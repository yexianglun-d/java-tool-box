package com.undernine.utils.http.openapi;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.Objects;

/**
 * 开放平台客户端配置。
 * <p>
 * 当前配置保持与底层 HTTP 模块一致，超时和重试间隔字段均使用毫秒。
 * </p>
 */
@Data
@Builder(toBuilder = true)
public class OpenApiClientOptions {

    /**
     * 连接超时时间，单位毫秒。
     */
    @Builder.Default
    private int connectTimeout = 5000;

    /**
     * 读取超时时间，单位毫秒。
     */
    @Builder.Default
    private int readTimeout = 10000;

    /**
     * 写入超时时间，单位毫秒。
     */
    @Builder.Default
    private int writeTimeout = 10000;

    /**
     * 最大重试次数，不包含首次调用。
     */
    @Builder.Default
    private int maxRetries = 0;

    /**
     * 重试等待间隔，单位毫秒。
     */
    @Builder.Default
    private long retryInterval = 1000;

    /**
     * 是否允许同步客户端在重试前阻塞当前线程等待 {@link #retryInterval}。
     * <p>
     * 默认关闭，避免在 Web 请求线程中因为重试等待占用容器线程。需要退避、熔断或异步调度的场景，
     * 建议在调用方的客户端治理层实现。
     * </p>
     */
    @Builder.Default
    private boolean blockingRetryDelayEnabled = false;

    /**
     * 透传 traceId 时使用的请求头名称。
     */
    @Builder.Default
    private String traceHeaderName = "X-Trace-Id";

    /**
     * 透传幂等键时使用的请求头名称。
     */
    @Builder.Default
    private String idempotencyHeaderName = "Idempotency-Key";

    /**
     * 是否输出客户端治理日志。
     */
    @Builder.Default
    private boolean loggingEnabled = false;

    /**
     * 返回默认配置。
     *
     * @return 默认配置
     */
    public static OpenApiClientOptions defaultOptions() {
        return OpenApiClientOptions.builder().build();
    }

    /**
     * 连接超时时间。
     *
     * @return 连接超时时间
     */
    public Duration getConnectTimeoutDuration() {
        return Duration.ofMillis(connectTimeout);
    }

    /**
     * 读取超时时间。
     *
     * @return 读取超时时间
     */
    public Duration getReadTimeoutDuration() {
        return Duration.ofMillis(readTimeout);
    }

    /**
     * 写入超时时间。
     *
     * @return 写入超时时间
     */
    public Duration getWriteTimeoutDuration() {
        return Duration.ofMillis(writeTimeout);
    }

    /**
     * 重试等待间隔。
     *
     * @return 重试等待间隔
     */
    public Duration getRetryIntervalDuration() {
        return Duration.ofMillis(retryInterval);
    }

    private static int toIntMillis(Duration duration, String fieldName) {
        long millis = toMillis(duration, fieldName);
        return Math.toIntExact(millis);
    }

    private static long toMillis(Duration duration, String fieldName) {
        long millis = Objects.requireNonNull(duration, fieldName + " must not be null").toMillis();
        if (millis < 0L) {
            throw new IllegalArgumentException(fieldName + " must not be negative");
        }
        return millis;
    }

    /**
     * OpenAPI 客户端配置构建器扩展。
     */
    public static class OpenApiClientOptionsBuilder {

        /**
         * 设置连接超时时间。
         *
         * @param timeout 连接超时时间
         * @return 当前构建器
         */
        public OpenApiClientOptionsBuilder connectTimeoutDuration(Duration timeout) {
            return connectTimeout(toIntMillis(timeout, "connectTimeout"));
        }

        /**
         * 设置读取超时时间。
         *
         * @param timeout 读取超时时间
         * @return 当前构建器
         */
        public OpenApiClientOptionsBuilder readTimeoutDuration(Duration timeout) {
            return readTimeout(toIntMillis(timeout, "readTimeout"));
        }

        /**
         * 设置写入超时时间。
         *
         * @param timeout 写入超时时间
         * @return 当前构建器
         */
        public OpenApiClientOptionsBuilder writeTimeoutDuration(Duration timeout) {
            return writeTimeout(toIntMillis(timeout, "writeTimeout"));
        }

        /**
         * 设置重试等待间隔。
         *
         * @param interval 重试等待间隔
         * @return 当前构建器
         */
        public OpenApiClientOptionsBuilder retryIntervalDuration(Duration interval) {
            return retryInterval(toMillis(interval, "retryInterval"));
        }
    }
}
