/**
 * Spring AOP 切面包。
 * <p>
 * 主线切面用于防重复提交和接口限流；历史日志、重试切面仅保留兼容维护。
 * 直接依赖本模块时，应显式 {@code @Import} 需要的切面或声明为 {@code @Bean}。
 * </p>
 *
 * <h2>主线切面</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.aspect.PreventRepeatAspect} - 基于
 *     {@link com.undernine.utils.spring.repeat.RepeatSubmitStore} 的防重复提交切面。</li>
 *     <li>{@link com.undernine.utils.spring.aspect.RateLimitAspect} - 基于
 *     {@link com.undernine.utils.spring.ratelimit.RateLimitStore} 的限流切面。</li>
 * </ul>
 *
 * <h2>兼容维护切面</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.aspect.OperationLogAspect} - 轻量操作日志输出，不提供审计持久化。</li>
 *     <li>{@link com.undernine.utils.spring.aspect.RetryAspect} - 当前线程同步重试，不提供退避、熔断和超时预算。</li>
 * </ul>
 *
 * <h2>启用示例</h2>
 * <pre>{@code
 * @Configuration
 * @EnableAspectJAutoProxy
 * @Import({PreventRepeatAspect.class, RateLimitAspect.class})
 * public class AopConfiguration {
 * }
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>切面只对 Spring 管理的 Bean 生效。</li>
 *     <li>本地存储的防重和限流只适合单机环境，集群环境应使用 Redis 存储实现。</li>
 *     <li>兼容维护切面不会通过 starter 自动启用，新项目不应把它们作为主线治理能力。</li>
 * </ul>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring.aspect;
