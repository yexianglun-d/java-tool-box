package com.undernine.utils.spring.context;

/**
 * 当前租户提供器。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface CurrentTenantProvider {

    /**
     * 获取当前租户标识。
     *
     * @return 当前租户标识，无法获取时返回 null
     */
    String getCurrentTenantId();
}
