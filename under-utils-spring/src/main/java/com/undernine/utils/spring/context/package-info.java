/**
 * Spring 操作上下文与容器访问工具包。
 * <p>
 * 该包提供一次请求或一次业务操作内的 traceId、租户、用户、请求信息和扩展属性承载模型，
 * 并提供 Servlet Filter、TaskDecorator、Executor 包装器等上下文传播能力。
 * {@link com.undernine.utils.spring.context.SpringContextHolder} 仍用于少量非 Spring 管理对象访问 Bean 的场景。
 * </p>
 *
 * <h2>核心类</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.context.OperationContext} - 不可变操作上下文</li>
 *     <li>{@link com.undernine.utils.spring.context.OperationContextHolder} - 当前线程上下文作用域</li>
 *     <li>{@link com.undernine.utils.spring.context.OperationContextSnapshot} - 异步任务上下文快照</li>
 *     <li>{@link com.undernine.utils.spring.context.OperationContextFilter} - Web 请求上下文初始化</li>
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
 *     <li>线程池场景应使用 {@link com.undernine.utils.spring.context.OperationContextTaskDecorator}
 *     或 {@link com.undernine.utils.spring.context.OperationContextExecutors} 传播上下文</li>
 *     <li>业务代码追加属性时应创建新的 {@link com.undernine.utils.spring.context.OperationContext}，
 *     避免在共享对象上维护可变状态</li>
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
