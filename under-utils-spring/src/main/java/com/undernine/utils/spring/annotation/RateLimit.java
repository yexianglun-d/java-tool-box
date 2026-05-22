package com.undernine.utils.spring.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
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
     * 限流次数
     */
    int limit() default 10;

    /**
     * 时间窗口(秒)
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
     * </p>
     */
    String key() default "";

    /**
     * 提示消息
     */
    String message() default "访问过于频繁，请稍后再试";
}
