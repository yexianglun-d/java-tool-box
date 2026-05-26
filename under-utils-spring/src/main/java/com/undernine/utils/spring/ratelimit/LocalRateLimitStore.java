package com.undernine.utils.spring.ratelimit;

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
 * JVM 本地限流存储。
 * <p>
 * 适合单实例应用或本地开发。该实现不在多个 JVM 之间共享计数，多实例部署时应替换为 Redis 等分布式实现。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class LocalRateLimitStore implements RateLimitStore, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(LocalRateLimitStore.class);

    private static final int DEFAULT_MAX_COUNTERS = 100_000;
    private static final Duration DEFAULT_CLEANUP_INTERVAL = Duration.ofSeconds(1);
    private static final AtomicLong THREAD_SEQUENCE = new AtomicLong();

    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();
    private final int maxCounters;
    private final long cleanupIntervalMillis;
    private final ScheduledExecutorService cleanupExecutor;
    private final ScheduledFuture<?> cleanupFuture;
    private final AtomicLong nextCleanupAt = new AtomicLong();
    private final AtomicBoolean closed = new AtomicBoolean();

    public LocalRateLimitStore() {
        this(DEFAULT_MAX_COUNTERS, DEFAULT_CLEANUP_INTERVAL);
    }

    public LocalRateLimitStore(int maxCounters) {
        this(maxCounters, DEFAULT_CLEANUP_INTERVAL);
    }

    public LocalRateLimitStore(int maxCounters, Duration cleanupInterval) {
        if (maxCounters <= 0) {
            throw new IllegalArgumentException("maxCounters must be greater than 0");
        }
        Duration interval = normalizeCleanupInterval(cleanupInterval);
        this.maxCounters = maxCounters;
        this.cleanupIntervalMillis = Math.max(1L, interval.toMillis());
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(
                daemonThreadFactory("under-local-rate-limit-cleanup-"));
        this.cleanupFuture = cleanupExecutor.scheduleWithFixedDelay(
                this::cleanupExpiredCountersSafely,
                cleanupIntervalMillis,
                cleanupIntervalMillis,
                TimeUnit.MILLISECONDS
        );
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

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            cleanupFuture.cancel(false);
            cleanupExecutor.shutdownNow();
        }
    }

    int size() {
        return counters.size();
    }

    private void cleanupExpiredCountersSafely() {
        try {
            cleanupExpiredCounters(System.currentTimeMillis(), true);
        } catch (RuntimeException ex) {
            log.warn("Failed to cleanup local rate limit counters", ex);
        }
    }

    private void cleanupExpiredCounters(long now, boolean force) {
        if (!force) {
            long next = nextCleanupAt.get();
            if (now < next || !nextCleanupAt.compareAndSet(next,
                    now + cleanupIntervalMillis)) {
                return;
            }
        }
        counters.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
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
