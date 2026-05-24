/**
 * Under-Utils HTTP 模块
 * <p>
 * 提供基于 OkHttp 的 HTTP 请求封装和 OpenAPI 客户端治理能力。
 * </p>
 *
 * <h2>核心功能</h2>
 * <ul>
 *   <li>统一的 HTTP 请求 API</li>
 *   <li>支持 GET、POST、PUT、DELETE、PATCH 等请求方法</li>
 *   <li>支持 JSON、表单、文件上传等多种请求格式</li>
 *   <li>完善的超时和重试机制</li>
 *   <li>异步请求支持</li>
 *   <li>灵活的配置选项</li>
 * </ul>
 *
 * <h2>快速开始</h2>
 * <pre>{@code
 * // 简单 GET 请求
 * String result = HttpUtils.get("https://api.example.com/users");
 *
 * // POST JSON 请求
 * User user = new User("John", 25);
 * String result = HttpUtils.postJson("https://api.example.com/users", user);
 *
 * // 使用构建器
 * HttpResponse response = HttpRequest.builder()
 *     .url("https://api.example.com/users")
 *     .method(HttpMethod.GET)
 *     .header("Authorization", "Bearer token")
 *     .param("page", "1")
 *     .timeout(5000)
 *     .build()
 *     .execute();
 * }</pre>
 *
 * <h2>主要类说明</h2>
 * <ul>
 *   <li>{@link com.undernine.utils.http.util.HttpUtils} - HTTP 工具类，提供便捷的静态方法</li>
 *   <li>{@link com.undernine.utils.http.request.HttpRequest} - HTTP 请求构建器</li>
 *   <li>{@link com.undernine.utils.http.response.HttpResponse} - HTTP 响应封装</li>
 *   <li>{@link com.undernine.utils.http.config.HttpConfig} - HTTP 配置类</li>
 *   <li>{@link com.undernine.utils.http.client.OkHttpRequestExecutor} - OkHttp 请求执行器</li>
 * </ul>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.http;
