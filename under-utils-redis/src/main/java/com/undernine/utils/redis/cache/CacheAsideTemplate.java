package com.undernine.utils.redis.cache;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Cache-aside 访问模板。
 * <p>
 * 封装读取缓存、未命中加载源数据、空值缓存、TTL 抖动和缓存重建锁等重复工程模式。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CacheAsideTemplate {

    private static final Logger log = LoggerFactory.getLogger(CacheAsideTemplate.class);

    private static final String NULL_VALUE = "__UNDER_UTILS_CACHE_NULL_V1__";
    private static final String VALUE_PREFIX = "__UNDER_UTILS_CACHE_VALUE_V1__:";

    private final RedissonClient redissonClient;
    private final CacheValueCodec valueCodec;
    private final CacheOptions defaultOptions;
    private final CountingCacheOperationObserver metricsObserver;
    private final CacheOperationObserver operationObserver;

    public CacheAsideTemplate(RedissonClient redissonClient) {
        this(redissonClient, new JacksonCacheValueCodec(), CacheOptions.defaults());
    }

    public CacheAsideTemplate(RedissonClient redissonClient, CacheValueCodec valueCodec) {
        this(redissonClient, valueCodec, CacheOptions.defaults());
    }

    public CacheAsideTemplate(RedissonClient redissonClient, CacheValueCodec valueCodec, CacheOptions defaultOptions) {
        this(redissonClient, valueCodec, defaultOptions, CacheOperationObserver.noop());
    }

    public CacheAsideTemplate(RedissonClient redissonClient, CacheValueCodec valueCodec, CacheOptions defaultOptions,
                              CacheOperationObserver operationObserver) {
        this.redissonClient = Objects.requireNonNull(redissonClient, "redissonClient must not be null");
        this.valueCodec = Objects.requireNonNull(valueCodec, "valueCodec must not be null");
        this.defaultOptions = Objects.requireNonNull(defaultOptions, "defaultOptions must not be null");
        this.metricsObserver = new CountingCacheOperationObserver();
        this.operationObserver = operationObserver == null ? CacheOperationObserver.noop() : operationObserver;
    }

    /**
     * 返回当前缓存指标快照。
     *
     * @return 缓存指标快照
     */
    public CacheMetrics getMetrics() {
        return metricsObserver.snapshot();
    }

    /**
     * 重置内置指标计数。
     */
    public void resetMetrics() {
        metricsObserver.reset();
    }

    public <T, E extends Throwable> T getOrLoad(
        String key,
        Class<T> valueType,
        CacheLoadFunction<T, E> loader
    ) throws E {
        return getOrLoad(key, valueType, defaultOptions, loader);
    }

    public <T, E extends Throwable> T getOrLoad(
        String key,
        Class<T> valueType,
        CacheOptions options,
        CacheLoadFunction<T, E> loader
    ) throws E {
        Objects.requireNonNull(valueType, "valueType must not be null");
        Objects.requireNonNull(loader, "loader must not be null");

        String normalizedKey = normalizeKey(key);
        CacheOptions effectiveOptions = options == null ? defaultOptions : options;
        String cacheKey = buildCacheKey(normalizedKey, effectiveOptions);
        RBucket<String> bucket = redissonClient.getBucket(cacheKey);

        CacheLookup<T> cached = readCache(bucket, valueType);
        if (cached.hit()) {
            observe(observer -> observer.onHit(event(normalizedKey, cacheKey, cached.value() == null)));
            return cached.value();
        }
        observe(observer -> observer.onMiss(event(normalizedKey, cacheKey, false)));

        if (!effectiveOptions.rebuildLockEnabled()) {
            return loadAndCache(normalizedKey, cacheKey, bucket, effectiveOptions, loader);
        }

        return rebuildWithLock(normalizedKey, cacheKey, bucket, valueType, effectiveOptions, loader);
    }

    public <T, E extends Throwable> T get(
        String key,
        Class<T> valueType,
        CacheLoadFunction<T, E> loader
    ) throws E {
        return getOrLoad(key, valueType, loader);
    }

    public <T, E extends Throwable> T get(
        String key,
        Class<T> valueType,
        CacheOptions options,
        CacheLoadFunction<T, E> loader
    ) throws E {
        return getOrLoad(key, valueType, options, loader);
    }

    private <T, E extends Throwable> T rebuildWithLock(
        String key,
        String cacheKey,
        RBucket<String> bucket,
        Class<T> valueType,
        CacheOptions options,
        CacheLoadFunction<T, E> loader
    ) throws E {
        RLock lock = redissonClient.getLock(options.rebuildLockKeyPrefix() + cacheKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(
                options.lockWaitTime().toMillis(),
                options.lockLeaseTime().toMillis(),
                TimeUnit.MILLISECONDS
            );
            if (!locked) {
                observe(observer -> observer.onLockRejected(event(key, cacheKey, false)));
                CacheLookup<T> cachedAfterWait = readCache(bucket, valueType);
                if (cachedAfterWait.hit()) {
                    observe(observer -> observer.onHit(event(key, cacheKey, cachedAfterWait.value() == null)));
                    return cachedAfterWait.value();
                }
                throw new CacheRebuildLockException("Failed to acquire cache rebuild lock: " + cacheKey);
            }
            observe(observer -> observer.onLockAcquired(event(key, cacheKey, false)));

            CacheLookup<T> cachedAfterLock = readCache(bucket, valueType);
            if (cachedAfterLock.hit()) {
                observe(observer -> observer.onHit(event(key, cacheKey, cachedAfterLock.value() == null)));
                return cachedAfterLock.value();
            }
            return loadAndCache(key, cacheKey, bucket, options, loader);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CacheRebuildLockException("Interrupted while acquiring cache rebuild lock: " + cacheKey, e);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private <T, E extends Throwable> T loadAndCache(
        String key,
        String cacheKey,
        RBucket<String> bucket,
        CacheOptions options,
        CacheLoadFunction<T, E> loader
    ) throws E {
        long startedAt = System.nanoTime();
        T loaded;
        try {
            loaded = loader.load(key);
            observe(observer -> observer.onLoadSuccess(timedEvent(key, cacheKey, loaded == null, startedAt)));
        } catch (Throwable error) {
            observe(observer -> observer.onLoadFailure(failedEvent(key, cacheKey, startedAt, error)));
            throwAs(error);
            return null;
        }
        if (loaded == null) {
            if (options.cacheNull()) {
                bucket.set(NULL_VALUE, ttlWithJitter(options.nullTtl(), options.jitter()));
                observe(observer -> observer.onWrite(event(key, cacheKey, true)));
            }
            return null;
        }

        String payload = Objects.requireNonNull(valueCodec.encode(loaded), "encoded cache value must not be null");
        bucket.set(VALUE_PREFIX + payload, ttlWithJitter(options.ttl(), options.jitter()));
        observe(observer -> observer.onWrite(event(key, cacheKey, false)));
        return loaded;
    }

    private <T> CacheLookup<T> readCache(RBucket<String> bucket, Class<T> valueType) {
        String cached = bucket.get();
        if (cached == null) {
            return CacheLookup.miss();
        }
        if (NULL_VALUE.equals(cached)) {
            return CacheLookup.hit(null);
        }
        if (!cached.startsWith(VALUE_PREFIX)) {
            return CacheLookup.hit(valueCodec.decode(cached, valueType));
        }
        return CacheLookup.hit(valueCodec.decode(cached.substring(VALUE_PREFIX.length()), valueType));
    }

    private String buildCacheKey(String key, CacheOptions options) {
        return options.keyPrefix() + key;
    }

    private String normalizeKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("cache key must not be blank");
        }
        return key.trim();
    }

    private Duration ttlWithJitter(Duration ttl, Duration jitter) {
        long jitterMillis = jitter.toMillis();
        if (jitterMillis <= 0L) {
            return ttl;
        }
        long extraMillis = ThreadLocalRandom.current().nextLong(jitterMillis + 1L);
        return ttl.plusMillis(extraMillis);
    }

    private CacheOperationEvent event(String key, String cacheKey, boolean nullValue) {
        return CacheOperationEvent.of(CacheOperationType.CACHE_ASIDE, key, cacheKey, nullValue);
    }

    private CacheOperationEvent timedEvent(String key, String cacheKey, boolean nullValue, long startedAt) {
        return CacheOperationEvent.timed(CacheOperationType.CACHE_ASIDE, key, cacheKey, nullValue,
                System.nanoTime() - startedAt);
    }

    private CacheOperationEvent failedEvent(String key, String cacheKey, long startedAt, Throwable error) {
        return CacheOperationEvent.failed(CacheOperationType.CACHE_ASIDE, key, cacheKey,
                System.nanoTime() - startedAt, error);
    }

    private void observe(Consumer<CacheOperationObserver> consumer) {
        try {
            consumer.accept(metricsObserver);
        } catch (RuntimeException ex) {
            log.warn("Cache metrics observer failed", ex);
        }
        try {
            consumer.accept(operationObserver);
        } catch (RuntimeException ex) {
            log.warn("Cache operation observer failed", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends Throwable> void throwAs(Throwable error) throws E {
        throw (E) error;
    }

    private record CacheLookup<T>(boolean hit, T value) {

        static <T> CacheLookup<T> hit(T value) {
            return new CacheLookup<>(true, value);
        }

        static <T> CacheLookup<T> miss() {
            return new CacheLookup<>(false, null);
        }
    }
}
