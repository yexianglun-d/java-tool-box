package com.undernine.utils.spring.annotation;

import com.undernine.utils.spring.enums.OperationType;

import java.lang.annotation.*;

/**
 * 操作日志注解。
 * <p>
 * 该注解仅提供兼容维护的轻量日志能力，不提供审计持久化、敏感字段过滤或异步投递。
 * 新项目建议基于 {@code OperationContext} 自行接入审计系统、消息队列或日志平台。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 * @deprecated 轻量日志切面保留为兼容 API，不作为 Under-Utils 后续工程模式主线能力演进。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated(since = "1.0.0")
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
    boolean recordParams() default false;

    /**
     * 是否记录返回结果
     */
    boolean recordResult() default false;
}
