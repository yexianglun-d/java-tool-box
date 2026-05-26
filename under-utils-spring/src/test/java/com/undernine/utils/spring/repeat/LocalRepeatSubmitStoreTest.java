package com.undernine.utils.spring.repeat;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.BooleanSupplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalRepeatSubmitStoreTest {

    @Test
    void removesExpiredEntriesWhenCapacityIsReached() throws Exception {
        try (LocalRepeatSubmitStore store = new LocalRepeatSubmitStore(1)) {
            assertThat(store.acquire("submit:1", Duration.ofMillis(100))).isTrue();
            assertThat(store.acquire("submit:2", Duration.ofSeconds(1))).isFalse();

            Thread.sleep(150L);

            assertThat(store.acquire("submit:2", Duration.ofSeconds(1))).isTrue();
            assertThat(store.size()).isEqualTo(1);
        }
    }

    @Test
    void activelyRemovesExpiredEntriesWithoutNewAccess() throws Exception {
        try (LocalRepeatSubmitStore store = new LocalRepeatSubmitStore(10, Duration.ofMillis(10))) {
            assertThat(store.acquire("submit:active", Duration.ofMillis(20))).isTrue();
            assertThat(store.size()).isEqualTo(1);

            waitUntil(() -> store.size() == 0);

            assertThat(store.size()).isZero();
        }
    }

    @Test
    void rejectsInvalidCleanupInterval() {
        assertThatThrownBy(() -> new LocalRepeatSubmitStore(10, Duration.ZERO))
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
