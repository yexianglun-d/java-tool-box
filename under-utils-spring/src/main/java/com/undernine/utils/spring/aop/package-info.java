/**
 * 兼容维护的耗时日志 AOP 包。
 * <p>
 * {@link com.undernine.utils.spring.aop.TimeLog} 和
 * {@link com.undernine.utils.spring.aop.TimeLogAspect} 仅提供轻量方法耗时日志能力，
 * 不作为 Under-Utils 后续主线工程模式能力演进。新项目建议优先接入 Micrometer、
 * OpenTelemetry 或业务统一观测平台。
 * </p>
 *
 * <h2>启用示例</h2>
 * <pre>{@code
 * @Configuration
 * @EnableAspectJAutoProxy
 * @Import(TimeLogAspect.class)
 * public class TimeLogConfiguration {
 * }
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>{@code TimeLogAspect} 不再声明为 Spring 组件，必须显式导入或注册为 Bean。</li>
 *     <li>该能力只记录本地日志，不提供指标聚合、链路追踪或告警集成。</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring.aop;
