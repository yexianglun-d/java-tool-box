package com.undernine.utils.spring.ratelimit;

import java.time.Duration;

/**
 * 限流存储接口。
 * <p>
 * 本地、Redis、网关等实现都可以接入该接口。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RateLimitStore {

    /**
     * 尝试获取一次访问额度。
     *
     * @param key    限流 key
     * @param limit  时间窗口内允许次数；小于等于 0 时实现应拒绝访问
     * @param window 时间窗口；实现应至少按 1 毫秒处理
     * @return true 表示允许访问，false 表示已超过限制
     */
    boolean tryAcquire(String key, int limit, Duration window);
}
