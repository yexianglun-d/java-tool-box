package com.undernine.utils.redis.cache;

/**
 * 缓存操作指标快照。
 * <p>
 * 该对象是某一时刻的只读快照，后续缓存操作不会改变已经返回的实例。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.2
 * @since 1.0.2
 */
public final class CacheMetrics {

    private final long hitCount;
    private final long missCount;
    private final long loadSuccessCount;
    private final long loadFailureCount;
    private final long writeCount;
    private final long lockAcquiredCount;
    private final long lockRejectedCount;
    private final long refreshSubmittedCount;
    private final long refreshSuccessCount;
    private final long refreshFailureCount;

    CacheMetrics(
        long hitCount,
        long missCount,
        long loadSuccessCount,
        long loadFailureCount,
        long writeCount,
        long lockAcquiredCount,
        long lockRejectedCount,
        long refreshSubmittedCount,
        long refreshSuccessCount,
        long refreshFailureCount
    ) {
        this.hitCount = hitCount;
        this.missCount = missCount;
        this.loadSuccessCount = loadSuccessCount;
        this.loadFailureCount = loadFailureCount;
        this.writeCount = writeCount;
        this.lockAcquiredCount = lockAcquiredCount;
        this.lockRejectedCount = lockRejectedCount;
        this.refreshSubmittedCount = refreshSubmittedCount;
        this.refreshSuccessCount = refreshSuccessCount;
        this.refreshFailureCount = refreshFailureCount;
    }

    public long getHitCount() {
        return hitCount;
    }

    public long getMissCount() {
        return missCount;
    }

    public long getLookupCount() {
        return hitCount + missCount;
    }

    public double getHitRate() {
        long lookupCount = getLookupCount();
        return lookupCount == 0L ? 0D : (double) hitCount / lookupCount;
    }

    public double getMissRate() {
        long lookupCount = getLookupCount();
        return lookupCount == 0L ? 0D : (double) missCount / lookupCount;
    }

    public long getLoadSuccessCount() {
        return loadSuccessCount;
    }

    public long getLoadFailureCount() {
        return loadFailureCount;
    }

    public long getLoadCount() {
        return loadSuccessCount + loadFailureCount;
    }

    public long getWriteCount() {
        return writeCount;
    }

    public long getLockAcquiredCount() {
        return lockAcquiredCount;
    }

    public long getLockRejectedCount() {
        return lockRejectedCount;
    }

    public long getRefreshSubmittedCount() {
        return refreshSubmittedCount;
    }

    public long getRefreshSuccessCount() {
        return refreshSuccessCount;
    }

    public long getRefreshFailureCount() {
        return refreshFailureCount;
    }

    public long getErrorCount() {
        return loadFailureCount + refreshFailureCount;
    }
}
