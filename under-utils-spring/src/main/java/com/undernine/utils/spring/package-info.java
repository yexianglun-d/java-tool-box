/**
 * Spring Web 横切能力模块。
 * <p>
 * 本模块提供操作上下文、限流防重、统一异常响应、请求日志拦截和敏感字段脱敏等 Web 基础设施。
 * 推荐通过 {@code under-utils-spring-starter} 使用自动装配；直接依赖本模块时，应显式导入需要的 Bean，
 * 避免扫描整个 {@code com.undernine.utils.spring} 包。
 * </p>
 *
 * <h2>包结构</h2>
 * <ul>
 *     <li><b>context/</b> - 操作上下文、上下文快照和线程传播工具</li>
 *     <li><b>annotation/</b> - 限流、防重、敏感字段及兼容维护注解</li>
 *     <li><b>aspect/</b> - 限流、防重切面，以及兼容维护的日志/重试切面</li>
 *     <li><b>interceptor/</b> - HTTP 请求日志拦截器</li>
 *     <li><b>exception/</b> - 业务异常与统一异常处理</li>
 *     <li><b>result/</b> - Web 响应模型</li>
 *     <li><b>enums/</b> - 操作类型、敏感字段类型等枚举</li>
 * </ul>
 *
 * <h2>推荐入口</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.context.OperationContext} 统一承载 traceId、tenantId、userId 和请求信息。</li>
 *     <li>{@link com.undernine.utils.spring.annotation.PreventRepeat} 和
 *     {@link com.undernine.utils.spring.annotation.RateLimit} 封装重复提交与访问频率控制。</li>
 *     <li>{@link com.undernine.utils.spring.exception.GlobalExceptionHandler} 与
 *     {@link com.undernine.utils.spring.result.Result} 提供统一 Web 错误响应。</li>
 *     <li>{@link com.undernine.utils.spring.annotation.Sensitive} 支持 Jackson 序列化阶段字段脱敏。</li>
 * </ul>
 *
 * <h2>启用方式</h2>
 * <pre>{@code
 * @Configuration
 * @Import({
 *     OperationContextFilter.class,
 *     PreventRepeatAspect.class,
 *     RateLimitAspect.class
 * })
 * public class WebInfrastructureConfiguration {
 * }
 * }</pre>
 *
 * <h2>兼容维护能力</h2>
 * <p>
 * {@link com.undernine.utils.spring.annotation.OperationLog}、
 * {@link com.undernine.utils.spring.annotation.Retry} 和
 * {@link com.undernine.utils.spring.aop.TimeLog} 是历史轻量 AOP API，仅保留兼容维护。
 * 新项目的审计、重试和可观测性建议接入业务统一审计平台、专用客户端治理、Micrometer 或 OpenTelemetry。
 * </p>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>本地内存版防重和限流仅适合单机环境，集群环境应使用 Redis 存储实现。</li>
 *     <li>所有 AOP 能力只对 Spring 管理的 Bean 生效。</li>
 *     <li>{@code OperationLog} 默认不记录请求参数，如需兼容旧行为必须显式设置 {@code recordParams = true}。</li>
 *     <li>{@code Retry} 使用当前线程同步等待，不适合高并发或复杂外部调用治理。</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring;
