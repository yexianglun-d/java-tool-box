package com.undernine.utils.spring.repeat;

import java.time.Duration;

/**
 * 防重复提交存储接口。
 * <p>
 * 实现必须提供原子登记语义：同一 key 在 TTL 窗口内只能有一次登记成功。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RepeatSubmitStore {

    /**
     * 尝试登记一次提交。
     *
     * @param key 操作 key
     * @param ttl 重复提交判定窗口；实现应至少按 1 毫秒处理
     * @return true 表示首次提交，false 表示窗口内重复提交
     */
    boolean acquire(String key, Duration ttl);

    /**
     * 释放登记。
     *
     * @param key 操作 key
     */
    default void release(String key) {
        // Default no-op.
    }
}
