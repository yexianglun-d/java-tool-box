package com.undernine.utils.spring.repeat;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class LocalRepeatSubmitStoreTest {

    @Test
    void removesExpiredEntriesWhenCapacityIsReached() throws Exception {
        LocalRepeatSubmitStore store = new LocalRepeatSubmitStore(1);

        assertThat(store.acquire("submit:1", Duration.ofMillis(100))).isTrue();
        assertThat(store.acquire("submit:2", Duration.ofSeconds(1))).isFalse();

        Thread.sleep(150L);

        assertThat(store.acquire("submit:2", Duration.ofSeconds(1))).isTrue();
        assertThat(store.size()).isEqualTo(1);
    }
}
