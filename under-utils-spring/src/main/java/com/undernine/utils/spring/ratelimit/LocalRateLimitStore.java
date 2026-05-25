package com.undernine.utils.spring.ratelimit;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JVM 本地限流存储。
 * <p>
 * 适合单实例应用或本地开发。该实现不在多个 JVM 之间共享计数，多实例部署时应替换为 Redis 等分布式实现。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class LocalRateLimitStore implements RateLimitStore {

    private static final int DEFAULT_MAX_COUNTERS = 100_000;
    private static final long CLEANUP_INTERVAL_MILLIS = 1_000L;

    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();
    private final int maxCounters;
    private final AtomicLong nextCleanupAt = new AtomicLong();

    public LocalRateLimitStore() {
        this(DEFAULT_MAX_COUNTERS);
    }

    public LocalRateLimitStore(int maxCounters) {
        if (maxCounters <= 0) {
            throw new IllegalArgumentException("maxCounters must be greater than 0");
        }
        this.maxCounters = maxCounters;
    }

    @Override
    public boolean tryAcquire(String key, int limit, Duration window) {
        if (limit <= 0) {
            return false;
        }
        long windowMillis = Math.max(1L, window.toMillis());
        long now = System.currentTimeMillis();
        cleanupExpiredCounters(now, false);
        if (!counters.containsKey(key) && counters.size() >= maxCounters) {
            cleanupExpiredCounters(now, true);
            if (!counters.containsKey(key) && counters.size() >= maxCounters) {
                return false;
            }
        }
        WindowCounter counter = counters.compute(key, (ignored, existing) -> {
            if (existing == null || existing.isExpired(now)) {
                return new WindowCounter(limit, now, windowMillis);
            }
            return existing;
        });
        return counter.tryAcquire(limit, windowMillis);
    }

    /**
     * 清空本地状态，主要用于测试或主动重置。
     */
    public void clear() {
        counters.clear();
    }

    int size() {
        return counters.size();
    }

    private void cleanupExpiredCounters(long now, boolean force) {
        if (!force) {
            long next = nextCleanupAt.get();
            if (now < next || !nextCleanupAt.compareAndSet(next, now + CLEANUP_INTERVAL_MILLIS)) {
                return;
            }
        }
        counters.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }

    private static class WindowCounter {
        private int count;
        private long windowStart;
        private long windowMillis;

        private WindowCounter(int limit, long now, long windowMillis) {
            this.count = limit;
            this.windowStart = now;
            this.windowMillis = windowMillis;
        }

        private synchronized boolean tryAcquire(int limit, long windowMillis) {
            long now = System.currentTimeMillis();
            if (now - windowStart >= windowMillis) {
                count = limit;
                windowStart = now;
                this.windowMillis = windowMillis;
            }
            if (count <= 0) {
                return false;
            }
            count--;
            return true;
        }

        private synchronized boolean isExpired(long now) {
            return now - windowStart >= windowMillis;
        }
    }
}
