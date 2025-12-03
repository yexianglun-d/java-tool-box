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
     * 提示消息
     */
    String message() default "访问过于频繁，请稍后再试";
}
