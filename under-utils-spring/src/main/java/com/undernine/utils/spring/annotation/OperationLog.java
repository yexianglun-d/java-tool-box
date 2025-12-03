package com.undernine.utils.spring.annotation;

import com.undernine.utils.spring.enums.OperationType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 业务模块
     */
    String module() default "";

    /**
     * 操作类型
     */
    OperationType type() default OperationType.OTHER;

    /**
     * 操作内容描述
     */
    String content() default "";

    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;

    /**
     * 是否记录返回结果
     */
    boolean recordResult() default false;
}
