package com.undernine.utils.redis.lock;

/**
 * 分布式锁异常。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class DistributedLockException extends RuntimeException {

    public DistributedLockException(String message) {
        super(message);
    }

    public DistributedLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
