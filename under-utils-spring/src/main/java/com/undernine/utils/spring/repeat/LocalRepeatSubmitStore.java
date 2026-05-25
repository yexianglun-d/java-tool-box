package com.undernine.utils.spring.repeat;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
public class LocalRepeatSubmitStore implements RepeatSubmitStore {

    private static final int DEFAULT_MAX_ENTRIES = 100_000;
    private static final long CLEANUP_INTERVAL_MILLIS = 1_000L;

    private final Map<String, Long> cache = new ConcurrentHashMap<>();
    private final int maxEntries;
    private final AtomicLong nextCleanupAt = new AtomicLong();

    public LocalRepeatSubmitStore() {
        this(DEFAULT_MAX_ENTRIES);
    }

    public LocalRepeatSubmitStore(int maxEntries) {
        if (maxEntries <= 0) {
            throw new IllegalArgumentException("maxEntries must be greater than 0");
        }
        this.maxEntries = maxEntries;
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

    int size() {
        return cache.size();
    }

    private void cleanupExpired(long now, boolean force) {
        if (!force) {
            long next = nextCleanupAt.get();
            if (now < next || !nextCleanupAt.compareAndSet(next, now + CLEANUP_INTERVAL_MILLIS)) {
                return;
            }
        }
        cache.entrySet().removeIf(entry -> entry.getValue() <= now);
    }
}
