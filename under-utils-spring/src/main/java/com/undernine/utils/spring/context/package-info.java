/**
 * Spring 上下文工具包
 * <p>
 * 提供 Spring 上下文工具类，用于在非 Spring 管理的类中获取 Bean。
 * </p>
 *
 * <h2>核心类</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.context.SpringContextHolder} - Spring 上下文持有者</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 在非 Spring 管理的类中获取 Bean
 * public class SomeUtil {
 *     public void doSomething() {
 *         // 根据类型获取 Bean
 *         UserService userService = SpringContextHolder.getBean(UserService.class);
 *         userService.doSomething();
 *
 *         // 根据名称和类型获取 Bean
 *         UserService service = SpringContextHolder.getBean("userService", UserService.class);
 *
 *         // 根据名称获取 Bean
 *         Object bean = SpringContextHolder.getBean("userService");
 *
 *         // 获取 ApplicationContext
 *         ApplicationContext context = SpringContextHolder.getApplicationContext();
 *     }
 * }
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>确保 {@link com.undernine.utils.spring.context.SpringContextHolder} 被 Spring 容器扫描到</li>
 *     <li>只能在 Spring 容器启动完成后使用</li>
 *     <li>不要在静态初始化块中使用</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring.context;
