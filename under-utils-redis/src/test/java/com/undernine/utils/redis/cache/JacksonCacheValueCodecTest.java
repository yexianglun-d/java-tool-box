package com.undernine.utils.redis.cache;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonCacheValueCodecTest {

    @Test
    void defaultCodecSupportsJavaTimeValues() {
        JacksonCacheValueCodec codec = new JacksonCacheValueCodec();
        TimedCacheValue value = new TimedCacheValue();
        value.setName("job");
        value.setCreatedAt(LocalDateTime.of(2026, 5, 24, 15, 30));

        String payload = codec.encode(value);
        TimedCacheValue decoded = codec.decode(payload, TimedCacheValue.class);

        assertThat(payload).contains("2026-05-24T15:30:00");
        assertThat(decoded.getName()).isEqualTo("job");
        assertThat(decoded.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 5, 24, 15, 30));
    }

    public static class TimedCacheValue {
        private String name;
        private LocalDateTime createdAt;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}
