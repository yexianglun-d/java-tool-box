package com.undernine.utils.spring.annotation;

import java.lang.annotation.*;

/**
 * 重试机制注解。
 * <p>
 * 该注解使用当前线程同步 sleep 重试，仅适合低并发、轻量级兼容场景。
 * OpenAPI 或外部系统调用建议使用具备退避、熔断、超时预算和可观测性的专用客户端治理能力。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 * @deprecated 同步重试切面保留为兼容 API，不作为 Under-Utils 后续工程模式主线能力演进。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated(since = "1.0.0")
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
