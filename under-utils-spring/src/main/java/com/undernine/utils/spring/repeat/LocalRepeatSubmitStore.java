package com.undernine.utils.spring.repeat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JVM 本地防重复提交存储。
 * <p>
 * 适合单实例应用或本地开发。该实现不在多个 JVM 之间共享 key，多实例部署时应替换为 Redis 等分布式实现。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class LocalRepeatSubmitStore implements RepeatSubmitStore, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(LocalRepeatSubmitStore.class);

    private static final int DEFAULT_MAX_ENTRIES = 100_000;
    private static final Duration DEFAULT_CLEANUP_INTERVAL = Duration.ofSeconds(1);
    private static final AtomicLong THREAD_SEQUENCE = new AtomicLong();

    private final Map<String, Long> cache = new ConcurrentHashMap<>();
    private final int maxEntries;
    private final long cleanupIntervalMillis;
    private final ScheduledExecutorService cleanupExecutor;
    private final ScheduledFuture<?> cleanupFuture;
    private final AtomicLong nextCleanupAt = new AtomicLong();
    private final AtomicBoolean closed = new AtomicBoolean();

    public LocalRepeatSubmitStore() {
        this(DEFAULT_MAX_ENTRIES, DEFAULT_CLEANUP_INTERVAL);
    }

    public LocalRepeatSubmitStore(int maxEntries) {
        this(maxEntries, DEFAULT_CLEANUP_INTERVAL);
    }

    public LocalRepeatSubmitStore(int maxEntries, Duration cleanupInterval) {
        if (maxEntries <= 0) {
            throw new IllegalArgumentException("maxEntries must be greater than 0");
        }
        Duration interval = normalizeCleanupInterval(cleanupInterval);
        this.maxEntries = maxEntries;
        this.cleanupIntervalMillis = Math.max(1L, interval.toMillis());
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(
                daemonThreadFactory("under-local-repeat-submit-cleanup-"));
        this.cleanupFuture = cleanupExecutor.scheduleWithFixedDelay(
                this::cleanupExpiredSafely,
                cleanupIntervalMillis,
                cleanupIntervalMillis,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public boolean acquire(String key, Duration ttl) {
        long now = System.currentTimeMillis();
        long ttlMillis = Math.max(1L, ttl.toMillis());
        cleanupExpired(now, false);
        Long currentExpireAt = cache.get(key);
        if (currentExpireAt != null && currentExpireAt <= now) {
            cache.remove(key, currentExpireAt);
        }
        if (!cache.containsKey(key) && cache.size() >= maxEntries) {
            cleanupExpired(now, true);
            if (!cache.containsKey(key) && cache.size() >= maxEntries) {
                return false;
            }
        }

        Long expireAt = cache.putIfAbsent(key, now + ttlMillis);
        if (expireAt == null) {
            return true;
        }
        if (expireAt <= now && cache.replace(key, expireAt, now + ttlMillis)) {
            return true;
        }
        return false;
    }

    @Override
    public void release(String key) {
        cache.remove(key);
    }

    /**
     * 清空本地状态，主要用于测试或主动重置。
     */
    public void clear() {
        cache.clear();
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            cleanupFuture.cancel(false);
            cleanupExecutor.shutdownNow();
        }
    }

    int size() {
        return cache.size();
    }

    private void cleanupExpiredSafely() {
        try {
            cleanupExpired(System.currentTimeMillis(), true);
        } catch (RuntimeException ex) {
            log.warn("Failed to cleanup local repeat submit entries", ex);
        }
    }

    private void cleanupExpired(long now, boolean force) {
        if (!force) {
            long next = nextCleanupAt.get();
            if (now < next || !nextCleanupAt.compareAndSet(next,
                    now + cleanupIntervalMillis)) {
                return;
            }
        }
        cache.entrySet().removeIf(entry -> entry.getValue() <= now);
    }

    private Duration normalizeCleanupInterval(Duration cleanupInterval) {
        Duration interval = cleanupInterval == null ? DEFAULT_CLEANUP_INTERVAL : cleanupInterval;
        if (interval.isZero() || interval.isNegative()) {
            throw new IllegalArgumentException("cleanupInterval must be positive");
        }
        return interval;
    }

    private static ThreadFactory daemonThreadFactory(String namePrefix) {
        return runnable -> {
            Thread thread = new Thread(runnable, namePrefix + THREAD_SEQUENCE.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }
}
