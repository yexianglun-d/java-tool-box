package com.undernine.utils.redis.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redisson 分布式锁模板。
 * <p>
 * 封装获取锁、释放锁、处理中断和 lease time 等重复样板代码。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class DistributedLockTemplate {

    private static final String DEFAULT_PREFIX = "under-utils:lock:";

    private final RedissonClient redissonClient;
    private final String keyPrefix;

    public DistributedLockTemplate(RedissonClient redissonClient) {
        this(redissonClient, DEFAULT_PREFIX);
    }

    public DistributedLockTemplate(RedissonClient redissonClient, String keyPrefix) {
        this.redissonClient = Objects.requireNonNull(redissonClient, "redissonClient must not be null");
        this.keyPrefix = keyPrefix == null ? DEFAULT_PREFIX : keyPrefix;
    }

    /**
     * 获取锁并执行任务。
     *
     * @param key       业务 key
     * @param waitTime  等待锁时间
     * @param leaseTime 自动释放锁时间
     * @param unit      时间单位
     * @param supplier  业务逻辑
     * @param <T>       返回值类型
     * @return 业务返回值
     */
    public <T> T execute(String key, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "supplier must not be null");
        RLock lock = redissonClient.getLock(buildKey(key));
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, leaseTime, unit);
            if (!locked) {
                throw new DistributedLockException("Failed to acquire distributed lock: " + key);
            }
            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DistributedLockException("Interrupted while acquiring distributed lock: " + key, e);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 获取锁并执行无返回值任务。
     *
     * @param key       业务 key
     * @param waitTime  等待锁时间
     * @param leaseTime 自动释放锁时间
     * @param unit      时间单位
     * @param runnable  业务逻辑
     */
    public void execute(String key, long waitTime, long leaseTime, TimeUnit unit, Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable must not be null");
        execute(key, waitTime, leaseTime, unit, () -> {
            runnable.run();
            return null;
        });
    }

    private String buildKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("lock key must not be blank");
        }
        return keyPrefix + key.trim();
    }
}
