package com.undernine.utils.http.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HttpConfig 测试类
 *
 * @author deng
 */
class HttpConfigTest {

    @Test
    void testDefaultConfig() {
        HttpConfig config = HttpConfig.defaultConfig();

        assertThat(config.getConnectTimeout()).isEqualTo(5000);
        assertThat(config.getReadTimeout()).isEqualTo(10000);
        assertThat(config.getWriteTimeout()).isEqualTo(10000);
        assertThat(config.getMaxRetries()).isEqualTo(0);
        assertThat(config.getRetryInterval()).isEqualTo(1000);
        assertThat(config.isFollowRedirects()).isTrue();
        assertThat(config.getMaxConnections()).isEqualTo(200);
        assertThat(config.getMaxConnectionsPerRoute()).isEqualTo(20);
        assertThat(config.getKeepAliveTime()).isEqualTo(60000);
        assertThat(config.isLoggingEnabled()).isFalse();
        assertThat(config.isVerifySsl()).isTrue();
    }

    @Test
    void testBuilderWithCustomValues() {
        HttpConfig config = HttpConfig.builder()
                .connectTimeout(3000)
                .readTimeout(8000)
                .writeTimeout(8000)
                .maxRetries(3)
                .retryInterval(2000)
                .followRedirects(false)
                .maxConnections(100)
                .maxConnectionsPerRoute(10)
                .keepAliveTime(30000)
                .loggingEnabled(true)
                .verifySsl(false)
                .build();

        assertThat(config.getConnectTimeout()).isEqualTo(3000);
        assertThat(config.getReadTimeout()).isEqualTo(8000);
        assertThat(config.getWriteTimeout()).isEqualTo(8000);
        assertThat(config.getMaxRetries()).isEqualTo(3);
        assertThat(config.getRetryInterval()).isEqualTo(2000);
        assertThat(config.isFollowRedirects()).isFalse();
        assertThat(config.getMaxConnections()).isEqualTo(100);
        assertThat(config.getMaxConnectionsPerRoute()).isEqualTo(10);
        assertThat(config.getKeepAliveTime()).isEqualTo(30000);
        assertThat(config.isLoggingEnabled()).isTrue();
        assertThat(config.isVerifySsl()).isFalse();
    }

    @Test
    void testAddDefaultHeader() {
        HttpConfig config = HttpConfig.builder().build();
        config.addDefaultHeader("User-Agent", "MyApp/1.0");
        config.addDefaultHeader("Accept", "application/json");

        assertThat(config.getDefaultHeaders()).containsEntry("User-Agent", "MyApp/1.0");
        assertThat(config.getDefaultHeaders()).containsEntry("Accept", "application/json");
    }

    @Test
    void testAddDefaultHeaderWithNullMap() {
        HttpConfig config = HttpConfig.builder()
                .defaultHeaders(null)
                .build();

        config.addDefaultHeader("User-Agent", "MyApp/1.0");

        assertThat(config.getDefaultHeaders()).containsEntry("User-Agent", "MyApp/1.0");
    }

    @Test
    void testBuilderWithDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "MyApp/1.0");
        headers.put("Accept", "application/json");

        HttpConfig config = HttpConfig.builder()
                .defaultHeaders(headers)
                .build();

        assertThat(config.getDefaultHeaders()).containsAllEntriesOf(headers);
    }

    @Test
    void testBuilderChaining() {
        HttpConfig config = HttpConfig.builder()
                .connectTimeout(3000)
                .readTimeout(8000)
                .build();

        HttpConfig updatedConfig = config.addDefaultHeader("User-Agent", "MyApp/1.0");

        assertThat(updatedConfig).isSameAs(config);
        assertThat(config.getDefaultHeaders()).containsEntry("User-Agent", "MyApp/1.0");
    }

    @Test
    void shouldSupportDurationBuilderAndDefaultHeaderChaining() {
        HttpConfig config = HttpConfig.builder()
                .connectTimeoutDuration(Duration.ofSeconds(3))
                .readTimeoutDuration(Duration.ofSeconds(8))
                .writeTimeoutDuration(Duration.ofSeconds(9))
                .retryIntervalDuration(Duration.ofMillis(250))
                .keepAliveTimeDuration(Duration.ofSeconds(30))
                .addDefaultHeader("User-Agent", "MyApp/1.0")
                .defaultHeader("Accept", "application/json")
                .build();

        assertThat(config.getConnectTimeout()).isEqualTo(3000);
        assertThat(config.getReadTimeout()).isEqualTo(8000);
        assertThat(config.getWriteTimeout()).isEqualTo(9000);
        assertThat(config.getRetryInterval()).isEqualTo(250L);
        assertThat(config.getKeepAliveTime()).isEqualTo(30000L);
        assertThat(config.getConnectTimeoutDuration()).isEqualTo(Duration.ofSeconds(3));
        assertThat(config.getDefaultHeaders())
                .containsEntry("User-Agent", "MyApp/1.0")
                .containsEntry("Accept", "application/json");
    }

    @Test
    void shouldCopyConfigWithToBuilder() {
        HttpConfig original = HttpConfig.builder()
                .connectTimeout(1000)
                .addDefaultHeader("User-Agent", "MyApp/1.0")
                .build();

        HttpConfig copied = original.toBuilder()
                .readTimeoutDuration(Duration.ofSeconds(2))
                .addDefaultHeader("Accept", "application/json")
                .build();

        assertThat(copied.getConnectTimeout()).isEqualTo(1000);
        assertThat(copied.getReadTimeout()).isEqualTo(2000);
        assertThat(copied.getDefaultHeaders())
                .containsEntry("User-Agent", "MyApp/1.0")
                .containsEntry("Accept", "application/json");
        assertThat(original.getDefaultHeaders()).doesNotContainKey("Accept");
    }
}
