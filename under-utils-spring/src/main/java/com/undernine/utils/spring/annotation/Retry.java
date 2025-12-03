package com.undernine.utils.spring.annotation;

import java.lang.annotation.*;

/**
 * 重试机制注解
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retry {

    /**
     * 最大重试次数
     */
    int maxAttempts() default 3;

    /**
     * 重试延迟(毫秒)
     */
    long delay() default 1000;

    /**
     * 需要重试的异常类型
     */
    Class<? extends Throwable>[] exceptions() default {Exception.class};
}
