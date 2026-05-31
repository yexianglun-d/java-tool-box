package com.undernine.utils.redis.cache;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 将缓存操作事件桥接到 Micrometer 的 observer。
 * <p>
 * 该适配器不会把业务 key 或实际 cache key 写入 tag，避免制造高基数指标和潜在敏感信息泄漏。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.2
 * @since 1.0.2
 */
public final class MicrometerCacheOperationObserver implements CacheOperationObserver {

    public static final String DEFAULT_COUNTER_NAME = "under.utils.redis.cache.operations";
    public static final String DEFAULT_TIMER_NAME = "under.utils.redis.cache.duration";
    public static final String DEFAULT_OBSERVATION_NAME = "under.utils.redis.cache";

    private final MeterRegistry meterRegistry;
    private final ObservationRegistry observationRegistry;
    private final String counterName;
    private final String timerName;
    private final String observationName;

    public MicrometerCacheOperationObserver(MeterRegistry meterRegistry) {
        this(meterRegistry, ObservationRegistry.NOOP);
    }

    public MicrometerCacheOperationObserver(MeterRegistry meterRegistry, ObservationRegistry observationRegistry) {
        this(meterRegistry, observationRegistry, DEFAULT_COUNTER_NAME, DEFAULT_TIMER_NAME, DEFAULT_OBSERVATION_NAME);
    }

    public MicrometerCacheOperationObserver(MeterRegistry meterRegistry,
                                            ObservationRegistry observationRegistry,
                                            String counterName,
                                            String timerName,
                                            String observationName) {
        this.meterRegistry = Objects.requireNonNull(meterRegistry, "meterRegistry must not be null");
        this.observationRegistry = observationRegistry == null ? ObservationRegistry.NOOP : observationRegistry;
        this.counterName = requireText(counterName, "counterName");
        this.timerName = requireText(timerName, "timerName");
        this.observationName = requireText(observationName, "observationName");
    }

    @Override
    public void onHit(CacheOperationEvent event) {
        record("hit", "success", event);
    }

    @Override
    public void onMiss(CacheOperationEvent event) {
        record("miss", "success", event);
    }

    @Override
    public void onLoadSuccess(CacheOperationEvent event) {
        record("load", "success", event);
    }

    @Override
    public void onLoadFailure(CacheOperationEvent event) {
        record("load", "failure", event);
    }

    @Override
    public void onWrite(CacheOperationEvent event) {
        record("write", "success", event);
    }

    @Override
    public void onLockAcquired(CacheOperationEvent event) {
        record("lock", "acquired", event);
    }

    @Override
    public void onLockRejected(CacheOperationEvent event) {
        record("lock", "rejected", event);
    }

    @Override
    public void onRefreshSubmitted(CacheOperationEvent event) {
        record("refresh", "submitted", event);
    }

    @Override
    public void onRefreshSuccess(CacheOperationEvent event) {
        record("refresh", "success", event);
    }

    @Override
    public void onRefreshFailure(CacheOperationEvent event) {
        record("refresh", "failure", event);
    }

    private void record(String operation, String outcome, CacheOperationEvent event) {
        CacheOperationEvent safeEvent = Objects.requireNonNull(event, "event must not be null");
        Tags tags = Tags.of(
                "cache.type", normalize(safeEvent.getOperationType().name()),
                "cache.operation", operation,
                "cache.outcome", outcome,
                "cache.null", Boolean.toString(safeEvent.isNullValue()),
                "exception", exceptionName(safeEvent.getError())
        );

        Counter.builder(counterName)
                .description("Under-Utils Redis cache operation count")
                .tags(tags)
                .register(meterRegistry)
                .increment();

        if (safeEvent.getDurationNanos() > 0L) {
            Timer.builder(timerName)
                    .description("Under-Utils Redis cache operation duration")
                    .tags(tags)
                    .register(meterRegistry)
                    .record(safeEvent.getDurationNanos(), TimeUnit.NANOSECONDS);
        }

        Observation observation = Observation.createNotStarted(observationName, observationRegistry)
                .contextualName("redis cache " + operation)
                .lowCardinalityKeyValue("cache.type", normalize(safeEvent.getOperationType().name()))
                .lowCardinalityKeyValue("cache.operation", operation)
                .lowCardinalityKeyValue("cache.outcome", outcome)
                .lowCardinalityKeyValue("cache.null", Boolean.toString(safeEvent.isNullValue()))
                .lowCardinalityKeyValue("exception", exceptionName(safeEvent.getError()));
        if (safeEvent.getError() != null) {
            observation.error(safeEvent.getError());
        }
        observation.start();
        observation.stop();
    }

    private static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT).replace('_', '-');
    }

    private static String exceptionName(Throwable error) {
        return error == null ? "none" : error.getClass().getSimpleName();
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
