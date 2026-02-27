/**
 * Spring AOP 切面包
 * <p>
 * 提供各种注解的切面实现，包括操作日志、防重复提交、接口限流、重试机制等功能。
 * </p>
 *
 * <h2>核心切面</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.aspect.OperationLogAspect} - 操作日志切面</li>
 *     <li>{@link com.undernine.utils.spring.aspect.PreventRepeatAspect} - 防重复提交切面</li>
 *     <li>{@link com.undernine.utils.spring.aspect.RateLimitAspect} - 接口限流切面</li>
 *     <li>{@link com.undernine.utils.spring.aspect.RetryAspect} - 重试机制切面</li>
 * </ul>
 *
 * <h2>功能说明</h2>
 *
 * <h3>1. 操作日志切面</h3>
 * <p>拦截标注了 @OperationLog 的方法，记录操作信息：</p>
 * <ul>
 *     <li>操作模块、类型、内容</li>
 *     <li>当前用户、客户端 IP、请求 URI</li>
 *     <li>请求参数（可选）、返回结果（可选）</li>
 *     <li>执行耗时、成功/失败状态</li>
 * </ul>
 *
 * <h3>2. 防重复提交切面</h3>
 * <p>拦截标注了 @PreventRepeat 的方法，防止短时间内重复提交：</p>
 * <ul>
 *     <li>基于用户ID + URI + 方法名生成唯一键</li>
 *     <li>在指定时间窗口内拒绝重复请求</li>
 *     <li>使用内存缓存（ConcurrentHashMap）</li>
 *     <li>自动清理过期缓存</li>
 * </ul>
 *
 * <h3>3. 接口限流切面</h3>
 * <p>拦截标注了 @RateLimit 的方法，限制访问频率：</p>
 * <ul>
 *     <li>基于令牌桶算法实现</li>
 *     <li>支持按用户 + URI 维度限流</li>
 *     <li>可配置时间窗口和限流次数</li>
 *     <li>超过限制抛出 BizException</li>
 * </ul>
 *
 * <h3>4. 重试机制切面</h3>
 * <p>拦截标注了 @Retry 的方法，失败时自动重试：</p>
 * <ul>
 *     <li>支持配置最大重试次数</li>
 *     <li>支持配置重试延迟时间</li>
 *     <li>支持指定需要重试的异常类型</li>
 *     <li>记录每次重试的日志</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * @Configuration
 * @EnableAspectJAutoProxy
 * public class AopConfig {
 *     // 切面类会自动被扫描并注册
 * }
 *
 * @RestController
 * public class UserController {
 *
 *     @OperationLog(module = "用户管理", type = OperationType.CREATE, content = "创建用户")
 *     @PreventRepeat(timeout = 5)
 *     @RateLimit(limit = 10, period = 60)
 *     @PostMapping("/users")
 *     public Result<User> createUser(@RequestBody User user) {
 *         return Result.success(userService.create(user));
 *     }
 * }
 * }</pre>
 *
 * <h2>配置要求</h2>
 * <ul>
 *     <li>需要启用 AspectJ 自动代理：@EnableAspectJAutoProxy</li>
 *     <li>切面类需要被 Spring 扫描到（已使用 @Component）</li>
 *     <li>依赖 spring-boot-starter-aop</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>切面只对 Spring 管理的 Bean 有效，直接 new 的对象无效</li>
 *     <li>防重复提交和限流使用内存缓存，重启后数据丢失</li>
 *     <li>集群环境建议使用 Redis 实现分布式防重和限流</li>
 *     <li>重试机制会阻塞当前线程，不适用于高并发场景</li>
 *     <li>操作日志中的用户信息需要集成实际认证系统</li>
 * </ul>
 *
 * <h2>扩展建议</h2>
 * <ul>
 *     <li>将操作日志持久化到数据库或日志系统</li>
 *     <li>使用 Redis 实现分布式防重和限流</li>
 *     <li>集成实际的用户认证系统获取用户信息</li>
 *     <li>添加异步重试机制避免阻塞</li>
 * </ul>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring.aspect;
