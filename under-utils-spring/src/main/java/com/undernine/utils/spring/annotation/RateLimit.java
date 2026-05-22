package com.undernine.utils.spring.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解。
 * <p>
 * 拦截方法执行前根据操作 key 尝试获取一次访问额度；超过窗口额度时抛出
 * {@link com.undernine.utils.spring.exception.BizException}，异常消息使用 {@link #message()}。
 * </p>
 * <p>
 * starter 默认使用 JVM 本地存储，只在当前实例内生效；集群环境应配置
 * {@code under.utils.web.rate-limit.store=redis} 并提供 {@code RedissonClient}。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 时间窗口内允许通过的次数。
     * <p>
     * 必须大于 0；小于等于 0 时当前实现会拒绝所有请求。
     * </p>
     */
    int limit() default 10;

    /**
     * 时间窗口，单位秒。
     * <p>
     * 小于等于 0 时会按 1 秒处理，避免产生无效窗口。
     * </p>
     */
    int period() default 60;

    /**
     * 限流命名空间。
     * <p>
     * 用于区分不同业务域，默认使用通用命名空间。
     * </p>
     */
    String namespace() default "rate-limit";

    /**
     * 限流 key 表达式。
     * <p>
     * 为空时使用默认规则：租户 + 用户 + URI + 方法 + 参数摘要。
     * 不为空时按 SpEL 解析，例如：#userId 或 #args[0].id。
     * 表达式解析失败时不会中断业务，会退回到表达式和方法参数摘要生成的稳定兜底 key。
     * </p>
     */
    String key() default "";

    /**
     * 超过限流额度时抛出的业务异常消息。
     */
    String message() default "访问过于频繁，请稍后再试";
}
