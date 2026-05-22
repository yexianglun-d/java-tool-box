package com.undernine.utils.mybatis.handler;

/**
 * 当前审计用户提供者。
 * <p>
 * 由业务系统接入 ThreadLocal、网关上下文、认证上下文等能力后提供当前用户 ID。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface AuditorProvider {

    /**
     * 获取当前审计用户 ID。
     *
     * @return 当前用户 ID，无法获取时返回 null
     */
    Long getCurrentAuditor();
}
