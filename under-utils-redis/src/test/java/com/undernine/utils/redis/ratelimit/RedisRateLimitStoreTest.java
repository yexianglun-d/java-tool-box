package com.undernine.utils.redis.ratelimit;

import org.junit.jupiter.api.Test;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateLimiterConfig;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedisRateLimitStoreTest {

    @Test
    void updatesExistingLimiterWhenConfigChanged() {
        RedissonClient redissonClient = mock(RedissonClient.class);
        RRateLimiter limiter = mock(RRateLimiter.class);
        when(redissonClient.getRateLimiter("under-utils:rate-limit:api")).thenReturn(limiter);
        when(limiter.trySetRate(RateType.OVERALL, 10L, 60_000L, RateIntervalUnit.MILLISECONDS)).thenReturn(false);
        when(limiter.getConfig()).thenReturn(new RateLimiterConfig(RateType.OVERALL, 30_000L, 5L));
        when(limiter.tryAcquire()).thenReturn(true);
        RedisRateLimitStore store = new RedisRateLimitStore(redissonClient);

        boolean acquired = store.tryAcquire("api", 10, Duration.ofMinutes(1));

        assertThat(acquired).isTrue();
        verify(limiter).setRate(RateType.OVERALL, 10L, 60_000L, RateIntervalUnit.MILLISECONDS);
        verify(limiter).expireIfNotSet(Duration.ofSeconds(61));
    }

    @Test
    void keepsExistingLimiterWhenConfigUnchanged() {
        RedissonClient redissonClient = mock(RedissonClient.class);
        RRateLimiter limiter = mock(RRateLimiter.class);
        when(redissonClient.getRateLimiter("under-utils:rate-limit:api")).thenReturn(limiter);
        when(limiter.trySetRate(RateType.OVERALL, 10L, 60_000L, RateIntervalUnit.MILLISECONDS)).thenReturn(false);
        when(limiter.getConfig()).thenReturn(new RateLimiterConfig(RateType.OVERALL, 60_000L, 10L));
        when(limiter.tryAcquire()).thenReturn(true);
        RedisRateLimitStore store = new RedisRateLimitStore(redissonClient);

        boolean acquired = store.tryAcquire("api", 10, Duration.ofMinutes(1));

        assertThat(acquired).isTrue();
        verify(limiter, never()).setRate(RateType.OVERALL, 10L, 60_000L, RateIntervalUnit.MILLISECONDS);
        verify(limiter).expireIfNotSet(Duration.ofSeconds(61));
    }
}
