package com.undernine.utils.spring.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 防重复提交注解
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
     * 防重复提交的时间窗口
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
     * </p>
     */
    String key() default "";

    /**
     * 业务方法执行失败时是否释放 key。
     * <p>
     * 默认释放，便于失败后重试；如需严格阻断重复提交，可设置为 false。
     * </p>
     */
    boolean releaseOnFailure() default true;

    /**
     * 提示消息
     */
    String message() default "请勿重复提交";
}
