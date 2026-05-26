package com.undernine.utils.redis.cache;

import java.util.concurrent.atomic.LongAdder;

/**
 * 基于 {@link LongAdder} 的缓存操作指标聚合器。
 * <p>
 * 可单独作为 {@link CacheOperationObserver} 使用，也会被缓存模板默认内置，用于零配置读取基础缓存指标。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.2
 * @since 1.0.2
 */
public final class CountingCacheOperationObserver implements CacheOperationObserver {

    private final LongAdder hitCount = new LongAdder();
    private final LongAdder missCount = new LongAdder();
    private final LongAdder loadSuccessCount = new LongAdder();
    private final LongAdder loadFailureCount = new LongAdder();
    private final LongAdder writeCount = new LongAdder();
    private final LongAdder lockAcquiredCount = new LongAdder();
    private final LongAdder lockRejectedCount = new LongAdder();
    private final LongAdder refreshSubmittedCount = new LongAdder();
    private final LongAdder refreshSuccessCount = new LongAdder();
    private final LongAdder refreshFailureCount = new LongAdder();

    @Override
    public void onHit(CacheOperationEvent event) {
        hitCount.increment();
    }

    @Override
    public void onMiss(CacheOperationEvent event) {
        missCount.increment();
    }

    @Override
    public void onLoadSuccess(CacheOperationEvent event) {
        loadSuccessCount.increment();
    }

    @Override
    public void onLoadFailure(CacheOperationEvent event) {
        loadFailureCount.increment();
    }

    @Override
    public void onWrite(CacheOperationEvent event) {
        writeCount.increment();
    }

    @Override
    public void onLockAcquired(CacheOperationEvent event) {
        lockAcquiredCount.increment();
    }

    @Override
    public void onLockRejected(CacheOperationEvent event) {
        lockRejectedCount.increment();
    }

    @Override
    public void onRefreshSubmitted(CacheOperationEvent event) {
        refreshSubmittedCount.increment();
    }

    @Override
    public void onRefreshSuccess(CacheOperationEvent event) {
        refreshSuccessCount.increment();
    }

    @Override
    public void onRefreshFailure(CacheOperationEvent event) {
        refreshFailureCount.increment();
    }

    /**
     * 返回当前计数快照。
     *
     * @return 缓存指标快照
     */
    public CacheMetrics snapshot() {
        return new CacheMetrics(
            hitCount.sum(),
            missCount.sum(),
            loadSuccessCount.sum(),
            loadFailureCount.sum(),
            writeCount.sum(),
            lockAcquiredCount.sum(),
            lockRejectedCount.sum(),
            refreshSubmittedCount.sum(),
            refreshSuccessCount.sum(),
            refreshFailureCount.sum()
        );
    }

    /**
     * 重置所有计数。
     * <p>
     * 并发写入场景下，{@link LongAdder#reset()} 本身不提供严格原子快照语义；该方法适合周期性归零或测试场景。
     * </p>
     */
    public void reset() {
        hitCount.reset();
        missCount.reset();
        loadSuccessCount.reset();
        loadFailureCount.reset();
        writeCount.reset();
        lockAcquiredCount.reset();
        lockRejectedCount.reset();
        refreshSubmittedCount.reset();
        refreshSuccessCount.reset();
        refreshFailureCount.reset();
    }
}
