package com.undernine.utils.redis.cache;

/**
 * 缓存重建锁异常。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class CacheRebuildLockException extends RuntimeException {

    public CacheRebuildLockException(String message) {
        super(message);
    }

    public CacheRebuildLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
