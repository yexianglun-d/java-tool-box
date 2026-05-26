package com.undernine.utils.spring.ratelimit;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.BooleanSupplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalRateLimitStoreTest {

    @Test
    void removesExpiredCountersWhenCapacityIsReached() throws Exception {
        try (LocalRateLimitStore store = new LocalRateLimitStore(1)) {
            assertThat(store.tryAcquire("user:1", 1, Duration.ofMillis(100))).isTrue();
            assertThat(store.tryAcquire("user:2", 1, Duration.ofSeconds(1))).isFalse();

            Thread.sleep(150L);

            assertThat(store.tryAcquire("user:2", 1, Duration.ofSeconds(1))).isTrue();
            assertThat(store.size()).isEqualTo(1);
        }
    }

    @Test
    void activelyRemovesExpiredCountersWithoutNewAccess() throws Exception {
        try (LocalRateLimitStore store = new LocalRateLimitStore(10, Duration.ofMillis(10))) {
            assertThat(store.tryAcquire("user:active", 1, Duration.ofMillis(20))).isTrue();
            assertThat(store.size()).isEqualTo(1);

            waitUntil(() -> store.size() == 0);

            assertThat(store.size()).isZero();
        }
    }

    @Test
    void rejectsInvalidCleanupInterval() {
        assertThatThrownBy(() -> new LocalRateLimitStore(10, Duration.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cleanupInterval must be positive");
    }

    private static void waitUntil(BooleanSupplier condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 1_000L;
        while (!condition.getAsBoolean() && System.currentTimeMillis() < deadline) {
            Thread.sleep(10L);
        }
    }
}
