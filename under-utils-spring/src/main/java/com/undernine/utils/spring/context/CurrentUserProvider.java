package com.undernine.utils.spring.context;

/**
 * 当前用户提供器。
 * <p>
 * 用于限流、防重复提交、审计日志、数据填充等横切能力获取当前用户标识。
 * 业务系统可以通过 Spring Bean 覆盖默认实现。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface CurrentUserProvider {

    /**
     * 获取当前用户标识。
     *
     * @return 当前用户标识，无法获取时返回 null
     */
    String getCurrentUserId();
}
