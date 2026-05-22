package com.undernine.utils.spring.ratelimit;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JVM 本地限流存储。
 * <p>
 * 适合单实例应用或本地开发。多实例部署时应替换为 Redis 等分布式实现。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class LocalRateLimitStore implements RateLimitStore {

    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    @Override
    public boolean tryAcquire(String key, int limit, Duration window) {
        if (limit <= 0) {
            return false;
        }
        long windowMillis = Math.max(1L, window.toMillis());
        WindowCounter counter = counters.computeIfAbsent(key, ignored -> new WindowCounter(limit, windowMillis));
        return counter.tryAcquire(limit, windowMillis);
    }

    /**
     * 清空本地状态，主要用于测试或主动重置。
     */
    public void clear() {
        counters.clear();
    }

    private static class WindowCounter {
        private final AtomicInteger count;
        private volatile long windowStart;

        private WindowCounter(int limit, long windowMillis) {
            this.count = new AtomicInteger(limit);
            this.windowStart = System.currentTimeMillis();
        }

        private synchronized boolean tryAcquire(int limit, long windowMillis) {
            long now = System.currentTimeMillis();
            if (now - windowStart >= windowMillis) {
                count.set(limit);
                windowStart = now;
            }
            if (count.get() <= 0) {
                return false;
            }
            count.decrementAndGet();
            return true;
        }
    }
}
