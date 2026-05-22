package com.undernine.utils.redis.repeat;

import com.undernine.utils.spring.repeat.RepeatSubmitStore;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Objects;

/**
 * 基于 Redisson 的分布式防重复提交存储。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisRepeatSubmitStore implements RepeatSubmitStore {

    private static final String DEFAULT_PREFIX = "under-utils:repeat-submit:";

    private final RedissonClient redissonClient;
    private final String keyPrefix;

    public RedisRepeatSubmitStore(RedissonClient redissonClient) {
        this(redissonClient, DEFAULT_PREFIX);
    }

    public RedisRepeatSubmitStore(RedissonClient redissonClient, String keyPrefix) {
        this.redissonClient = Objects.requireNonNull(redissonClient, "redissonClient must not be null");
        this.keyPrefix = keyPrefix == null ? DEFAULT_PREFIX : keyPrefix;
    }

    @Override
    public boolean acquire(String key, Duration ttl) {
        RBucket<String> bucket = redissonClient.getBucket(keyPrefix + key);
        return bucket.setIfAbsent("1", ttl);
    }

    @Override
    public void release(String key) {
        redissonClient.getBucket(keyPrefix + key).delete();
    }
}
