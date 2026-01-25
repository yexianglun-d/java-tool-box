/**
 * Spring AOP 工具包
 * <p>
 * 提供 AOP 相关的注解和切面，用于实现横切关注点功能。
 * </p>
 *
 * <h2>核心类</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.aop.TimeLog} - 时间统计注解</li>
 *     <li>{@link com.undernine.utils.spring.aop.TimeLogAspect} - 时间统计切面</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <h3>1. 启用 AOP</h3>
 * <pre>{@code
 * @SpringBootApplication
 * @EnableAspectJAutoProxy  // 启用 AOP
 * @ComponentScan(basePackages = {
 *     "com.yourcompany",
 *     "com.undernine.utils.spring"  // 扫描工具包
 * })
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * }</pre>
 *
 * <h3>2. 使用 @TimeLog 统计执行时间</h3>
 * <pre>{@code
 * @Service
 * public class UserService {
 *     // 记录方法执行时间
 *     @TimeLog
 *     public User getById(Long id) {
 *         return userMapper.selectById(id);
 *     }
 *
 *     // 自定义描述和慢方法阈值
 *     @TimeLog(value = "批量查询用户", slowThreshold = 500)
 *     public List<User> listUsers(UserQuery query) {
 *         return userMapper.selectList(query);
 *     }
 * }
 * }</pre>
 *
 * <h2>日志输出示例</h2>
 * <pre>
 * [DEBUG] UserService.getById(未命名) 执行耗时: 45ms
 * [WARN]  【慢方法】UserService.listUsers(批量查询用户) 执行耗时: 1523ms (阈值: 500ms)
 * </pre>
 *
 * <h2>特性说明</h2>
 * <ul>
 *     <li>自动记录方法执行时间</li>
 *     <li>慢方法自动以 WARN 级别记录（默认阈值 1000ms）</li>
 *     <li>即使方法抛出异常也会记录执行时间</li>
 *     <li>支持自定义操作描述和慢方法阈值</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring.aop;
