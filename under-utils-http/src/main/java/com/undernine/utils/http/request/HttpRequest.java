package com.undernine.utils.http.request;

import com.undernine.utils.http.client.OkHttpClient;
import com.undernine.utils.http.config.HttpConfig;
import com.undernine.utils.http.enums.HttpMethod;
import com.undernine.utils.http.response.HttpResponse;
import lombok.Data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP 请求构建器
 * <p>
 * 使用构建器模式创建 HTTP 请求。
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * HttpResponse response = HttpRequest.builder()
 *     .url("https://api.example.com/users")
 *     .method(HttpMethod.GET)
 *     .header("Authorization", "Bearer token")
 *     .param("page", "1")
 *     .timeout(5000)
 *     .build()
 *     .execute();
 * }</pre>
 * </p>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class HttpRequest {

    /**
     * 请求 URL
     */
    private String url;

    /**
     * 请求方法
     */
    private HttpMethod method;

    /**
     * 请求头
     */
    private Map<String, String> headers;

    /**
     * URL 参数
     */
    private Map<String, String> params;

    /**
     * 请求体
     */
    private Object body;

    /**
     * 上传文件
     */
    private Map<String, File> files;

    /**
     * 表单参数
     */
    private Map<String, String> formParams;

    /**
     * 超时时间（毫秒）
     */
    private Integer timeout;

    /**
     * 最大重试次数
     */
    private Integer maxRetries;

    /**
     * HTTP 配置
     */
    private HttpConfig config;

    /**
     * 创建构建器
     *
     * @return 构建器实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 执行请求（同步）
     *
     * @return HTTP 响应
     */
    public HttpResponse execute() {
        OkHttpClient client = new OkHttpClient(config != null ? config : HttpConfig.defaultConfig());
        return client.execute(this);
    }

    /**
     * 执行请求（异步）
     *
     * @return CompletableFuture 包装的 HTTP 响应
     */
    public CompletableFuture<HttpResponse> executeAsync() {
        return CompletableFuture.supplyAsync(this::execute);
    }

    /**
     * HTTP 请求构建器
     */
    public static class Builder {
        private final HttpRequest request;

        public Builder() {
            this.request = new HttpRequest();
            this.request.headers = new HashMap<>();
            this.request.params = new HashMap<>();
            this.request.files = new HashMap<>();
            this.request.formParams = new HashMap<>();
        }

        /**
         * 设置请求 URL
         *
         * @param url 请求 URL
         * @return 构建器实例
         */
        public Builder url(String url) {
            request.url = url;
            return this;
        }

        /**
         * 设置请求方法
         *
         * @param method 请求方法
         * @return 构建器实例
         */
        public Builder method(HttpMethod method) {
            request.method = method;
            return this;
        }

        /**
         * 添加请求头
         *
         * @param name  请求头名称
         * @param value 请求头值
         * @return 构建器实例
         */
        public Builder header(String name, String value) {
            request.headers.put(name, value);
            return this;
        }

        /**
         * 批量添加请求头
         *
         * @param headers 请求头集合
         * @return 构建器实例
         */
        public Builder headers(Map<String, String> headers) {
            if (headers != null) {
                request.headers.putAll(headers);
            }
            return this;
        }

        /**
         * 添加 URL 参数
         *
         * @param name  参数名称
         * @param value 参数值
         * @return 构建器实例
         */
        public Builder param(String name, String value) {
            request.params.put(name, value);
            return this;
        }

        /**
         * 批量添加 URL 参数
         *
         * @param params 参数集合
         * @return 构建器实例
         */
        public Builder params(Map<String, String> params) {
            if (params != null) {
                request.params.putAll(params);
            }
            return this;
        }

        /**
         * 设置请求体
         *
         * @param body 请求体对象
         * @return 构建器实例
         */
        public Builder body(Object body) {
            request.body = body;
            return this;
        }

        /**
         * 添加上传文件
         *
         * @param name 文件参数名称
         * @param file 文件对象
         * @return 构建器实例
         */
        public Builder file(String name, File file) {
            request.files.put(name, file);
            return this;
        }

        /**
         * 添加表单参数
         *
         * @param name  参数名称
         * @param value 参数值
         * @return 构建器实例
         */
        public Builder formParam(String name, String value) {
            request.formParams.put(name, value);
            return this;
        }

        /**
         * 批量添加表单参数
         *
         * @param formParams 表单参数集合
         * @return 构建器实例
         */
        public Builder formParams(Map<String, String> formParams) {
            if (formParams != null) {
                request.formParams.putAll(formParams);
            }
            return this;
        }

        /**
         * 设置超时时间
         *
         * @param timeout 超时时间（毫秒）
         * @return 构建器实例
         */
        public Builder timeout(int timeout) {
            request.timeout = timeout;
            return this;
        }

        /**
         * 设置最大重试次数
         *
         * @param maxRetries 最大重试次数
         * @return 构建器实例
         */
        public Builder retry(int maxRetries) {
            request.maxRetries = maxRetries;
            return this;
        }

        /**
         * 设置 HTTP 配置
         *
         * @param config HTTP 配置
         * @return 构建器实例
         */
        public Builder config(HttpConfig config) {
            request.config = config;
            return this;
        }

        /**
         * 构建 HTTP 请求
         *
         * @return HTTP 请求对象
         */
        public HttpRequest build() {
            if (request.url == null || request.url.trim().isEmpty()) {
                throw new IllegalArgumentException("URL cannot be null or empty");
            }
            if (request.method == null) {
                request.method = HttpMethod.GET;
            }
            return request;
        }
    }
}
