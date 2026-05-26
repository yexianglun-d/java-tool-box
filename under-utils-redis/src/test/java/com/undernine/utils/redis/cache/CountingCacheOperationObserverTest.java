package com.undernine.utils.redis.cache;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CountingCacheOperationObserverTest {

    @Test
    void snapshotReturnsCountersAndDerivedRates() {
        CountingCacheOperationObserver observer = new CountingCacheOperationObserver();
        CacheOperationEvent event = CacheOperationEvent.of(CacheOperationType.CACHE_ASIDE,
                "user:1", "cache:user:1", false);
        CacheOperationEvent failedEvent = CacheOperationEvent.failed(CacheOperationType.LOGICAL_EXPIRE,
                "user:2", "logical:user:2", 10L, new IllegalStateException("failed"));

        observer.onHit(event);
        observer.onHit(event);
        observer.onMiss(event);
        observer.onLoadSuccess(event);
        observer.onLoadFailure(failedEvent);
        observer.onWrite(event);
        observer.onLockAcquired(event);
        observer.onLockRejected(event);
        observer.onRefreshSubmitted(event);
        observer.onRefreshSuccess(event);
        observer.onRefreshFailure(failedEvent);

        CacheMetrics metrics = observer.snapshot();

        assertThat(metrics.getHitCount()).isEqualTo(2L);
        assertThat(metrics.getMissCount()).isEqualTo(1L);
        assertThat(metrics.getLookupCount()).isEqualTo(3L);
        assertThat(metrics.getHitRate()).isEqualTo(2D / 3D);
        assertThat(metrics.getMissRate()).isEqualTo(1D / 3D);
        assertThat(metrics.getLoadSuccessCount()).isEqualTo(1L);
        assertThat(metrics.getLoadFailureCount()).isEqualTo(1L);
        assertThat(metrics.getLoadCount()).isEqualTo(2L);
        assertThat(metrics.getWriteCount()).isEqualTo(1L);
        assertThat(metrics.getLockAcquiredCount()).isEqualTo(1L);
        assertThat(metrics.getLockRejectedCount()).isEqualTo(1L);
        assertThat(metrics.getRefreshSubmittedCount()).isEqualTo(1L);
        assertThat(metrics.getRefreshSuccessCount()).isEqualTo(1L);
        assertThat(metrics.getRefreshFailureCount()).isEqualTo(1L);
        assertThat(metrics.getErrorCount()).isEqualTo(2L);
    }

    @Test
    void resetClearsCounters() {
        CountingCacheOperationObserver observer = new CountingCacheOperationObserver();
        CacheOperationEvent event = CacheOperationEvent.of(CacheOperationType.CACHE_ASIDE,
                "user:1", "cache:user:1", false);

        observer.onHit(event);
        observer.onMiss(event);
        observer.reset();

        CacheMetrics metrics = observer.snapshot();
        assertThat(metrics.getLookupCount()).isZero();
        assertThat(metrics.getHitRate()).isZero();
        assertThat(metrics.getMissRate()).isZero();
    }
}
