package com.undernine.utils.redis.cache;

/**
 * 缓存模板类型。
 *
 * @author Under-Utils Team
 * @version 1.0.1
 * @since 1.0.1
 */
public enum CacheOperationType {

    /**
     * Cache-aside 读穿缓存。
     */
    CACHE_ASIDE,

    /**
     * 逻辑过期缓存。
     */
    LOGICAL_EXPIRE
}
