package com.undernine.utils.spring.context;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 操作上下文自定义扩展点。
 * <p>
 * 业务系统可在请求进入时补充认证主体、灰度标识、业务线等上下文属性。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface OperationContextCustomizer {

    /**
     * 自定义操作上下文。
     *
     * @param builder 上下文构建器
     * @param request 当前 HTTP 请求
     */
    void customize(OperationContext.Builder builder, HttpServletRequest request);
}
