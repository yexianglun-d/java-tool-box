package com.undernine.utils.redis.cache;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MicrometerCacheOperationObserverTest {

    @Test
    void recordsOperationCountersAndDurationsWithoutCacheKeyTags() {
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        MicrometerCacheOperationObserver observer = new MicrometerCacheOperationObserver(
                meterRegistry,
                ObservationRegistry.create()
        );
        CacheOperationEvent event = CacheOperationEvent.timed(CacheOperationType.CACHE_ASIDE,
                "user:1", "app:cache:user:1", false, TimeUnit.MILLISECONDS.toNanos(12));

        observer.onHit(event);

        assertThat(meterRegistry.get(MicrometerCacheOperationObserver.DEFAULT_COUNTER_NAME)
                .tag("cache.type", "cache-aside")
                .tag("cache.operation", "hit")
                .tag("cache.outcome", "success")
                .tag("cache.null", "false")
                .tag("exception", "none")
                .counter()
                .count()).isEqualTo(1D);
        assertThat(meterRegistry.get(MicrometerCacheOperationObserver.DEFAULT_TIMER_NAME)
                .tag("cache.operation", "hit")
                .timer()
                .totalTime(TimeUnit.MILLISECONDS)).isEqualTo(12D);
        assertThat(meterRegistry.find(MicrometerCacheOperationObserver.DEFAULT_COUNTER_NAME)
                .tag("key", "user:1")
                .counter()).isNull();
        assertThat(meterRegistry.find(MicrometerCacheOperationObserver.DEFAULT_COUNTER_NAME)
                .tag("cache.key", "app:cache:user:1")
                .counter()).isNull();
    }

    @Test
    void recordsFailureOutcomeAndExceptionTag() {
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        MicrometerCacheOperationObserver observer = new MicrometerCacheOperationObserver(meterRegistry);
        CacheOperationEvent event = CacheOperationEvent.failed(CacheOperationType.LOGICAL_EXPIRE,
                "user:2", "app:logical:user:2", TimeUnit.MILLISECONDS.toNanos(5),
                new IllegalStateException("source failed"));

        observer.onRefreshFailure(event);

        assertThat(meterRegistry.get(MicrometerCacheOperationObserver.DEFAULT_COUNTER_NAME)
                .tag("cache.type", "logical-expire")
                .tag("cache.operation", "refresh")
                .tag("cache.outcome", "failure")
                .tag("exception", "IllegalStateException")
                .counter()
                .count()).isEqualTo(1D);
    }

    @Test
    void rejectsBlankMetricNames() {
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

        assertThatThrownBy(() -> new MicrometerCacheOperationObserver(
                meterRegistry,
                ObservationRegistry.NOOP,
                " ",
                MicrometerCacheOperationObserver.DEFAULT_TIMER_NAME,
                MicrometerCacheOperationObserver.DEFAULT_OBSERVATION_NAME
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("counterName must not be blank");
    }
}
