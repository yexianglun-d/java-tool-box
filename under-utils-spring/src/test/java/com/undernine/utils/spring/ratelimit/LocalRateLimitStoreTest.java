package com.undernine.utils.spring.ratelimit;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class LocalRateLimitStoreTest {

    @Test
    void removesExpiredCountersWhenCapacityIsReached() throws Exception {
        LocalRateLimitStore store = new LocalRateLimitStore(1);

        assertThat(store.tryAcquire("user:1", 1, Duration.ofMillis(100))).isTrue();
        assertThat(store.tryAcquire("user:2", 1, Duration.ofSeconds(1))).isFalse();

        Thread.sleep(150L);

        assertThat(store.tryAcquire("user:2", 1, Duration.ofSeconds(1))).isTrue();
        assertThat(store.size()).isEqualTo(1);
    }
}
