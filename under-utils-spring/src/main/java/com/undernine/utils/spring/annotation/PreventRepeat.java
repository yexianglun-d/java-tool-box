package com.undernine.utils.spring.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 防重复提交注解。
 * <p>
 * 拦截方法执行前登记一次操作 key；同一判定窗口内再次提交会抛出
 * {@link com.undernine.utils.spring.exception.BizException}，异常消息使用 {@link #message()}。
 * </p>
 * <p>
 * starter 默认使用 JVM 本地存储，只在当前实例内生效；集群环境应配置
 * {@code under.utils.web.repeat-submit.store=redis} 并提供 {@code RedissonClient}。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreventRepeat {

    /**
     * 防重复提交判定窗口。
     * <p>
     * 小于等于 0 时会按最小 1 毫秒处理，避免产生无效 TTL。
     * </p>
     */
    long timeout() default 3;

    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 防重复提交命名空间。
     */
    String namespace() default "repeat-submit";

    /**
     * 防重复提交 key 表达式。
     * <p>
     * 为空时使用默认规则：租户 + 用户 + URI + 方法 + 参数摘要。
     * 不为空时按 SpEL 解析，例如：#args[0].orderNo。
     * 表达式解析失败时不会中断业务，会退回到表达式和方法参数摘要生成的稳定兜底 key。
     * </p>
     */
    String key() default "";

    /**
     * 业务方法执行失败时是否释放 key。
     * <p>
     * 默认释放，便于失败后重试；方法执行成功后 key 不会立即释放，会等到判定窗口过期。
     * 如需严格阻断重复提交，可设置为 false，让失败请求也占用窗口。
     * </p>
     */
    boolean releaseOnFailure() default true;

    /**
     * 判定为重复提交时抛出的业务异常消息。
     */
    String message() default "请勿重复提交";
}
