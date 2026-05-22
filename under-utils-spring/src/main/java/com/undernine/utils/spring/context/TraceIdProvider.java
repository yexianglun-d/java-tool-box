package com.undernine.utils.spring.context;

/**
 * 链路追踪 ID 提供器。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface TraceIdProvider {

    /**
     * 获取当前链路 ID。
     *
     * @return 当前链路 ID，无法获取时返回 null
     */
    String getTraceId();
}
