package com.undernine.utils.http.openapi;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiClientOptionsTest {

    @Test
    void shouldSupportDurationBuilderMethods() {
        OpenApiClientOptions options = OpenApiClientOptions.builder()
                .connectTimeoutDuration(Duration.ofSeconds(2))
                .readTimeoutDuration(Duration.ofSeconds(8))
                .writeTimeoutDuration(Duration.ofSeconds(9))
                .retryIntervalDuration(Duration.ofMillis(250))
                .build();

        assertThat(options.getConnectTimeout()).isEqualTo(2000);
        assertThat(options.getReadTimeout()).isEqualTo(8000);
        assertThat(options.getWriteTimeout()).isEqualTo(9000);
        assertThat(options.getRetryInterval()).isEqualTo(250L);
        assertThat(options.getRetryIntervalDuration()).isEqualTo(Duration.ofMillis(250));
    }

    @Test
    void shouldCopyOptionsWithToBuilder() {
        OpenApiClientOptions original = OpenApiClientOptions.builder()
                .connectTimeout(1000)
                .maxRetries(1)
                .build();

        OpenApiClientOptions copied = original.toBuilder()
                .readTimeoutDuration(Duration.ofSeconds(3))
                .retryIntervalDuration(Duration.ZERO)
                .build();

        assertThat(copied.getConnectTimeout()).isEqualTo(1000);
        assertThat(copied.getMaxRetries()).isEqualTo(1);
        assertThat(copied.getReadTimeout()).isEqualTo(3000);
        assertThat(copied.getRetryInterval()).isZero();
        assertThat(original.getReadTimeout()).isEqualTo(10000);
    }
}
