package com.undernine.utils.http.config;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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
@Builder
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
}
