/**
 * Spring 注解包
 * <p>
 * 提供常用的 Spring AOP 注解，用于操作日志、防重复提交、接口限流、重试机制、敏感信息脱敏等功能。
 * </p>
 *
 * <h2>核心注解</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.annotation.OperationLog} - 操作日志注解</li>
 *     <li>{@link com.undernine.utils.spring.annotation.PreventRepeat} - 防重复提交注解</li>
 *     <li>{@link com.undernine.utils.spring.annotation.RateLimit} - 接口限流注解</li>
 *     <li>{@link com.undernine.utils.spring.annotation.Retry} - 重试机制注解</li>
 *     <li>{@link com.undernine.utils.spring.annotation.Sensitive} - 敏感信息脱敏注解</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>1. 操作日志</h3>
 * <pre>{@code
 * @OperationLog(
 *     module = "用户管理",
 *     type = OperationType.CREATE,
 *     content = "创建用户",
 *     recordParams = true,
 *     recordResult = true
 * )
 * @PostMapping("/users")
 * public Result<User> createUser(@RequestBody User user) {
 *     return Result.success(userService.create(user));
 * }
 * }</pre>
 *
 * <h3>2. 防重复提交</h3>
 * <pre>{@code
 * @PreventRepeat(timeout = 5, timeUnit = TimeUnit.SECONDS, message = "请勿重复提交")
 * @PostMapping("/orders")
 * public Result<Order> createOrder(@RequestBody Order order) {
 *     return Result.success(orderService.create(order));
 * }
 * }</pre>
 *
 * <h3>3. 接口限流</h3>
 * <pre>{@code
 * @RateLimit(limit = 10, period = 60, message = "访问过于频繁")
 * @GetMapping("/api/data")
 * public Result<List<Data>> getData() {
 *     return Result.success(dataService.list());
 * }
 * }</pre>
 *
 * <h3>4. 重试机制</h3>
 * <pre>{@code
 * @Retry(maxAttempts = 3, delay = 1000, exceptions = {IOException.class, TimeoutException.class})
 * public void sendEmail(String to, String content) {
 *     emailService.send(to, content);
 * }
 * }</pre>
 *
 * <h3>5. 敏感信息脱敏</h3>
 * <pre>{@code
 * public class User {
 *     @Sensitive(type = SensitiveType.MOBILE_PHONE)
 *     private String phone;  // 输出：138****5678
 *
 *     @Sensitive(type = SensitiveType.ID_CARD)
 *     private String idCard; // 输出：320***********1234
 *
 *     @Sensitive(type = SensitiveType.EMAIL)
 *     private String email;  // 输出：a***@example.com
 * }
 * }</pre>
 *
 * <h2>配置要求</h2>
 * <ul>
 *     <li>确保对应的切面类被 Spring 扫描到（使用 @Component 或 @Aspect）</li>
 *     <li>需要启用 AspectJ 自动代理：@EnableAspectJAutoProxy</li>
 *     <li>敏感信息脱敏需要使用 Jackson 进行 JSON 序列化</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>注解只对 Spring 管理的 Bean 有效</li>
 *     <li>防重复提交和限流使用内存缓存，集群环境建议使用 Redis</li>
 *     <li>重试机制会阻塞当前线程，不适用于高并发场景</li>
 *     <li>操作日志默认记录请求参数，敏感信息需注意脱敏</li>
 * </ul>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring.annotation;
