package com.undernine.utils.redis.cache;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 逻辑过期缓存访问模板。
 * <p>
 * 适用于热点 key：逻辑未过期时直接命中，逻辑过期时先返回旧值并提交后台刷新，缓存不存在时同步加载。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class LogicalExpireCacheTemplate {

    private static final Logger log = LoggerFactory.getLogger(LogicalExpireCacheTemplate.class);

    private static final String VALUE_PREFIX = "__UNDER_UTILS_LOGICAL_CACHE_VALUE_V1__:";

    private final RedissonClient redissonClient;
    private final CacheValueCodec valueCodec;
    private final LogicalExpireCacheOptions defaultOptions;
    private final CacheOperationObserver operationObserver;

    public LogicalExpireCacheTemplate(RedissonClient redissonClient) {
        this(redissonClient, new JacksonCacheValueCodec(), LogicalExpireCacheOptions.defaults());
    }

    public LogicalExpireCacheTemplate(RedissonClient redissonClient, CacheValueCodec valueCodec) {
        this(redissonClient, valueCodec, LogicalExpireCacheOptions.defaults());
    }

    public LogicalExpireCacheTemplate(
        RedissonClient redissonClient,
        CacheValueCodec valueCodec,
        LogicalExpireCacheOptions defaultOptions
    ) {
        this(redissonClient, valueCodec, defaultOptions, CacheOperationObserver.noop());
    }

    public LogicalExpireCacheTemplate(
        RedissonClient redissonClient,
        CacheValueCodec valueCodec,
        LogicalExpireCacheOptions defaultOptions,
        CacheOperationObserver operationObserver
    ) {
        this.redissonClient = Objects.requireNonNull(redissonClient, "redissonClient must not be null");
        this.valueCodec = Objects.requireNonNull(valueCodec, "valueCodec must not be null");
        this.defaultOptions = Objects.requireNonNull(defaultOptions, "defaultOptions must not be null");
        this.operationObserver = operationObserver == null ? CacheOperationObserver.noop() : operationObserver;
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
        LogicalExpireCacheOptions options,
        CacheLoadFunction<T, E> loader
    ) throws E {
        Objects.requireNonNull(valueType, "valueType must not be null");
        Objects.requireNonNull(loader, "loader must not be null");

        String normalizedKey = normalizeKey(key);
        LogicalExpireCacheOptions effectiveOptions = options == null ? defaultOptions : options;
        String cacheKey = buildCacheKey(normalizedKey, effectiveOptions);
        RBucket<String> bucket = redissonClient.getBucket(cacheKey);

        CacheLookup<T> cached = readCache(bucket, valueType);
        if (!cached.hit()) {
            observe(observer -> observer.onMiss(event(normalizedKey, cacheKey, false)));
            return loadMissingWithLock(normalizedKey, cacheKey, bucket, valueType, effectiveOptions, loader);
        }
        observe(observer -> observer.onHit(event(normalizedKey, cacheKey, cached.value() == null)));
        if (!cached.expired()) {
            return cached.value();
        }

        refreshInBackground(normalizedKey, cacheKey, bucket, valueType, effectiveOptions, loader);
        return cached.value();
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
        LogicalExpireCacheOptions options,
        CacheLoadFunction<T, E> loader
    ) throws E {
        return getOrLoad(key, valueType, options, loader);
    }

    private <T, E extends Throwable> T loadMissingWithLock(
        String key,
        String cacheKey,
        RBucket<String> bucket,
        Class<T> valueType,
        LogicalExpireCacheOptions options,
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
                    triggerRefreshIfExpired(key, cacheKey, bucket, valueType, options, loader, cachedAfterWait);
                    return cachedAfterWait.value();
                }
                throw new CacheRebuildLockException("Failed to acquire logical cache rebuild lock: " + cacheKey);
            }
            observe(observer -> observer.onLockAcquired(event(key, cacheKey, false)));

            CacheLookup<T> cachedAfterLock = readCache(bucket, valueType);
            if (cachedAfterLock.hit()) {
                observe(observer -> observer.onHit(event(key, cacheKey, cachedAfterLock.value() == null)));
                triggerRefreshIfExpired(key, cacheKey, bucket, valueType, options, loader, cachedAfterLock);
                return cachedAfterLock.value();
            }
            return loadAndCache(key, cacheKey, bucket, options, loader);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CacheRebuildLockException("Interrupted while acquiring logical cache rebuild lock: " + cacheKey, e);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private <T, E extends Throwable> void triggerRefreshIfExpired(
        String key,
        String cacheKey,
        RBucket<String> bucket,
        Class<T> valueType,
        LogicalExpireCacheOptions options,
        CacheLoadFunction<T, E> loader,
        CacheLookup<T> cached
    ) {
        if (cached.expired()) {
            refreshInBackground(key, cacheKey, bucket, valueType, options, loader);
        }
    }

    private <T, E extends Throwable> void refreshInBackground(
        String key,
        String cacheKey,
        RBucket<String> bucket,
        Class<T> valueType,
        LogicalExpireCacheOptions options,
        CacheLoadFunction<T, E> loader
    ) {
        try {
            observe(observer -> observer.onRefreshSubmitted(event(key, cacheKey, false)));
            options.refreshExecutor().execute(() -> {
                try {
                    boolean refreshed = refreshWithLock(key, cacheKey, bucket, valueType, options, loader);
                    if (refreshed) {
                        observe(observer -> observer.onRefreshSuccess(event(key, cacheKey, false)));
                    }
                } catch (Throwable error) {
                    handleRefreshFailure(key, cacheKey, options, error);
                }
            });
        } catch (RuntimeException e) {
            handleRefreshFailure(key, cacheKey, options, e);
        }
    }

    private <T, E extends Throwable> boolean refreshWithLock(
        String key,
        String cacheKey,
        RBucket<String> bucket,
        Class<T> valueType,
        LogicalExpireCacheOptions options,
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
                return false;
            }
            observe(observer -> observer.onLockAcquired(event(key, cacheKey, false)));

            CacheLookup<T> cachedAfterLock = readCache(bucket, valueType);
            if (cachedAfterLock.hit() && !cachedAfterLock.expired()) {
                return false;
            }
            loadAndCache(key, cacheKey, bucket, options, loader);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CacheRebuildLockException("Interrupted while refreshing logical cache: " + cacheKey, e);
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
        LogicalExpireCacheOptions options,
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
        if (loaded == null && !options.cacheNull()) {
            bucket.delete();
            return null;
        }

        String data = loaded == null ? null : Objects.requireNonNull(
            valueCodec.encode(loaded),
            "encoded cache value must not be null"
        );
        LogicalCachePayload payload = new LogicalCachePayload(
            data,
            loaded == null,
            System.currentTimeMillis() + options.logicalTtl().toMillis()
        );
        String encodedPayload = Objects.requireNonNull(
            valueCodec.encode(payload),
            "encoded logical cache payload must not be null"
        );
        bucket.set(VALUE_PREFIX + encodedPayload, options.physicalTtl());
        observe(observer -> observer.onWrite(event(key, cacheKey, loaded == null)));
        return loaded;
    }

    private <T> CacheLookup<T> readCache(RBucket<String> bucket, Class<T> valueType) {
        String cached = bucket.get();
        if (cached == null || !cached.startsWith(VALUE_PREFIX)) {
            return CacheLookup.miss();
        }

        LogicalCachePayload payload = valueCodec.decode(cached.substring(VALUE_PREFIX.length()), LogicalCachePayload.class);
        if (payload.getLogicalExpireAt() <= 0L) {
            return CacheLookup.miss();
        }
        if (payload.isNullValue()) {
            return CacheLookup.hit(null, payload.getLogicalExpireAt());
        }
        if (payload.getData() == null) {
            return CacheLookup.miss();
        }
        return CacheLookup.hit(valueCodec.decode(payload.getData(), valueType), payload.getLogicalExpireAt());
    }

    private void handleRefreshFailure(
        String key,
        String cacheKey,
        LogicalExpireCacheOptions options,
        Throwable error
    ) {
        log.warn("Failed to refresh logical cache for key {} ({})", key, cacheKey, error);
        observe(observer -> observer.onRefreshFailure(failedEvent(key, cacheKey, 0L, error)));
        LogicalExpireCacheRefreshFailureHandler failureHandler = options.refreshFailureHandler();
        if (failureHandler == null) {
            return;
        }

        try {
            failureHandler.handle(key, cacheKey, error);
        } catch (Throwable handlerError) {
            log.warn("Logical cache refresh failure handler failed for key {} ({})", key, cacheKey, handlerError);
        }
    }

    private String buildCacheKey(String key, LogicalExpireCacheOptions options) {
        return options.keyPrefix() + key;
    }

    private String normalizeKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("cache key must not be blank");
        }
        return key.trim();
    }

    private CacheOperationEvent event(String key, String cacheKey, boolean nullValue) {
        return CacheOperationEvent.of(CacheOperationType.LOGICAL_EXPIRE, key, cacheKey, nullValue);
    }

    private CacheOperationEvent timedEvent(String key, String cacheKey, boolean nullValue, long startedAt) {
        return CacheOperationEvent.timed(CacheOperationType.LOGICAL_EXPIRE, key, cacheKey, nullValue,
                System.nanoTime() - startedAt);
    }

    private CacheOperationEvent failedEvent(String key, String cacheKey, long startedAt, Throwable error) {
        long durationNanos = startedAt <= 0L ? 0L : System.nanoTime() - startedAt;
        return CacheOperationEvent.failed(CacheOperationType.LOGICAL_EXPIRE, key, cacheKey, durationNanos, error);
    }

    private void observe(Consumer<CacheOperationObserver> consumer) {
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

    private record CacheLookup<T>(boolean hit, T value, long logicalExpireAt) {

        boolean expired() {
            return logicalExpireAt <= System.currentTimeMillis();
        }

        static <T> CacheLookup<T> hit(T value, long logicalExpireAt) {
            return new CacheLookup<>(true, value, logicalExpireAt);
        }

        static <T> CacheLookup<T> miss() {
            return new CacheLookup<>(false, null, 0L);
        }
    }

    /**
     * 逻辑缓存载荷。data 为业务值经过 CacheValueCodec 编码后的字符串。
     */
    static final class LogicalCachePayload {

        private String data;
        private boolean nullValue;
        private long logicalExpireAt;

        public LogicalCachePayload() {
        }

        LogicalCachePayload(String data, boolean nullValue, long logicalExpireAt) {
            this.data = data;
            this.nullValue = nullValue;
            this.logicalExpireAt = logicalExpireAt;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public boolean isNullValue() {
            return nullValue;
        }

        public void setNullValue(boolean nullValue) {
            this.nullValue = nullValue;
        }

        public long getLogicalExpireAt() {
            return logicalExpireAt;
        }

        public void setLogicalExpireAt(long logicalExpireAt) {
            this.logicalExpireAt = logicalExpireAt;
        }
    }
}
