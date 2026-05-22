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
     * @param limit  时间窗口内允许次数
     * @param window 时间窗口
     * @return true 表示允许访问，false 表示已超过限制
     */
    boolean tryAcquire(String key, int limit, Duration window);
}
