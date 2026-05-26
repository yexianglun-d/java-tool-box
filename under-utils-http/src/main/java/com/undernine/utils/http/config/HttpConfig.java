package com.undernine.utils.http.config;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP 配置类
 * <p>
 * 用于配置 HTTP 客户端的各项参数。
 * </p>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder(toBuilder = true)
public class HttpConfig {

    /**
     * 连接超时时间（毫秒），默认 5000ms
     */
    @Builder.Default
    private int connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒），默认 10000ms
     */
    @Builder.Default
    private int readTimeout = 10000;

    /**
     * 写入超时时间（毫秒），默认 10000ms
     */
    @Builder.Default
    private int writeTimeout = 10000;

    /**
     * 最大重试次数，默认 0（不重试）
     */
    @Builder.Default
    private int maxRetries = 0;

    /**
     * 重试间隔（毫秒），默认 1000ms
     */
    @Builder.Default
    private long retryInterval = 1000;

    /**
     * 是否跟随重定向，默认 true
     */
    @Builder.Default
    private boolean followRedirects = true;

    /**
     * 最大连接数，默认 200
     */
    @Builder.Default
    private int maxConnections = 200;

    /**
     * 每个路由的最大连接数，默认 20
     */
    @Builder.Default
    private int maxConnectionsPerRoute = 20;

    /**
     * 连接保活时间（毫秒），默认 60000ms
     */
    @Builder.Default
    private long keepAliveTime = 60000;

    /**
     * 默认请求头
     */
    @Builder.Default
    private Map<String, String> defaultHeaders = new HashMap<>();

    /**
     * 是否启用日志，默认 false
     */
    @Builder.Default
    private boolean loggingEnabled = false;

    /**
     * 是否验证 SSL 证书，默认 true
     */
    @Builder.Default
    private boolean verifySsl = true;

    /**
     * 添加默认请求头
     *
     * @param name  请求头名称
     * @param value 请求头值
     * @return 当前配置对象
     */
    public HttpConfig addDefaultHeader(String name, String value) {
        if (defaultHeaders == null) {
            defaultHeaders = new HashMap<>();
        } else if (!(defaultHeaders instanceof HashMap)) {
            defaultHeaders = new HashMap<>(defaultHeaders);
        }
        defaultHeaders.put(name, value);
        return this;
    }

    /**
     * 创建默认配置
     *
     * @return 默认配置对象
     */
    public static HttpConfig defaultConfig() {
        return HttpConfig.builder().build();
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
     * 重试间隔。
     *
     * @return 重试间隔
     */
    public Duration getRetryIntervalDuration() {
        return Duration.ofMillis(retryInterval);
    }

    /**
     * 连接保活时间。
     *
     * @return 连接保活时间
     */
    public Duration getKeepAliveTimeDuration() {
        return Duration.ofMillis(keepAliveTime);
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
     * HTTP 配置构建器扩展。
     */
    public static class HttpConfigBuilder {

        /**
         * 设置连接超时时间。
         *
         * @param timeout 连接超时时间
         * @return 当前构建器
         */
        public HttpConfigBuilder connectTimeoutDuration(Duration timeout) {
            return connectTimeout(toIntMillis(timeout, "connectTimeout"));
        }

        /**
         * 设置读取超时时间。
         *
         * @param timeout 读取超时时间
         * @return 当前构建器
         */
        public HttpConfigBuilder readTimeoutDuration(Duration timeout) {
            return readTimeout(toIntMillis(timeout, "readTimeout"));
        }

        /**
         * 设置写入超时时间。
         *
         * @param timeout 写入超时时间
         * @return 当前构建器
         */
        public HttpConfigBuilder writeTimeoutDuration(Duration timeout) {
            return writeTimeout(toIntMillis(timeout, "writeTimeout"));
        }

        /**
         * 设置重试间隔。
         *
         * @param interval 重试间隔
         * @return 当前构建器
         */
        public HttpConfigBuilder retryIntervalDuration(Duration interval) {
            return retryInterval(toMillis(interval, "retryInterval"));
        }

        /**
         * 设置连接保活时间。
         *
         * @param keepAliveTime 连接保活时间
         * @return 当前构建器
         */
        public HttpConfigBuilder keepAliveTimeDuration(Duration keepAliveTime) {
            return keepAliveTime(toMillis(keepAliveTime, "keepAliveTime"));
        }

        /**
         * 添加默认请求头。
         *
         * @param name  请求头名称
         * @param value 请求头值
         * @return 当前构建器
         */
        public HttpConfigBuilder addDefaultHeader(String name, String value) {
            if (defaultHeaders$value == null) {
                defaultHeaders$value = new HashMap<>();
            } else {
                defaultHeaders$value = new HashMap<>(defaultHeaders$value);
            }
            defaultHeaders$value.put(name, value);
            defaultHeaders$set = true;
            return this;
        }

        /**
         * 添加默认请求头。
         *
         * @param name  请求头名称
         * @param value 请求头值
         * @return 当前构建器
         */
        public HttpConfigBuilder defaultHeader(String name, String value) {
            return addDefaultHeader(name, value);
        }
    }
}
