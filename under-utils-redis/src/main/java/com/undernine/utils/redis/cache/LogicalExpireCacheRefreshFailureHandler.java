package com.undernine.utils.redis.cache;

/**
 * 逻辑过期缓存后台刷新失败处理器。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface LogicalExpireCacheRefreshFailureHandler {

    /**
     * 处理后台刷新异常。
     *
     * @param key      未加前缀的业务 key
     * @param cacheKey 实际 Redis key
     * @param error    后台刷新异常
     */
    void handle(String key, String cacheKey, Throwable error);
}
