/**
 * Spring 注解包。
 * <p>
 * 主线注解聚焦 Web 横切治理和序列化脱敏；历史日志和同步重试注解仅保留兼容维护。
 * </p>
 *
 * <h2>主线注解</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.annotation.PreventRepeat} - 防重复提交。</li>
 *     <li>{@link com.undernine.utils.spring.annotation.RateLimit} - 接口限流。</li>
 *     <li>{@link com.undernine.utils.spring.annotation.Sensitive} - Jackson 字段脱敏。</li>
 * </ul>
 *
 * <h2>兼容维护注解</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.annotation.OperationLog} - 轻量操作日志，默认不记录请求参数。</li>
 *     <li>{@link com.undernine.utils.spring.annotation.Retry} - 当前线程同步重试，不适合高并发或复杂外部调用治理。</li>
 * </ul>
 *
 * <h2>示例</h2>
 * <pre>{@code
 * @PreventRepeat(timeout = 5, timeUnit = TimeUnit.SECONDS, message = "请勿重复提交")
 * @RateLimit(limit = 10, period = 60, message = "访问过于频繁")
 * @PostMapping("/orders")
 * public Result<Order> createOrder(@RequestBody Order order) {
 *     return Result.success(orderService.create(order));
 * }
 * }</pre>
 *
 * <pre>{@code
 * public class User {
 *     @Sensitive(type = SensitiveType.MOBILE_PHONE)
 *     private String phone;
 *
 *     @Sensitive(type = SensitiveType.EMAIL)
 *     private String email;
 * }
 * }</pre>
 *
 * <h2>配置要求</h2>
 * <ul>
 *     <li>使用 starter 时通过 {@code under.utils.*} 配置启用对应能力。</li>
 *     <li>直接依赖本模块时，应显式导入对应切面或声明为 Bean，并启用 AspectJ 自动代理。</li>
 *     <li>敏感字段脱敏依赖 Jackson 序列化流程。</li>
 * </ul>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring.annotation;
