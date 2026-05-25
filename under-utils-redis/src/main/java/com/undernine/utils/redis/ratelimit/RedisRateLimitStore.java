package com.undernine.utils.redis.ratelimit;

import com.undernine.utils.spring.ratelimit.RateLimitStore;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateLimiterConfig;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Objects;

/**
 * 基于 Redisson 的分布式限流存储。
 * <p>
 * 使用 Redis 中的 {@link RRateLimiter} 共享额度，适合多实例应用。Redis 不可用时 Redisson
 * 调用会向外抛出运行时异常，业务可通过自定义 {@link RateLimitStore} 实现降级策略。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisRateLimitStore implements RateLimitStore {

    private static final String DEFAULT_PREFIX = "under-utils:rate-limit:";

    private final RedissonClient redissonClient;
    private final String keyPrefix;

    public RedisRateLimitStore(RedissonClient redissonClient) {
        this(redissonClient, DEFAULT_PREFIX);
    }

    public RedisRateLimitStore(RedissonClient redissonClient, String keyPrefix) {
        this.redissonClient = Objects.requireNonNull(redissonClient, "redissonClient must not be null");
        this.keyPrefix = keyPrefix == null ? DEFAULT_PREFIX : keyPrefix;
    }

    @Override
    public boolean tryAcquire(String key, int limit, Duration window) {
        if (limit <= 0) {
            return false;
        }
        Objects.requireNonNull(window, "window must not be null");
        long windowMillis = Math.max(1L, window.toMillis());
        RRateLimiter limiter = redissonClient.getRateLimiter(keyPrefix + key);
        if (!limiter.trySetRate(RateType.OVERALL, limit, windowMillis, RateIntervalUnit.MILLISECONDS)
                && shouldUpdateRate(limiter, limit, windowMillis)) {
            limiter.setRate(RateType.OVERALL, limit, windowMillis, RateIntervalUnit.MILLISECONDS);
        }
        limiter.expireIfNotSet(window.plusSeconds(1));
        return limiter.tryAcquire();
    }

    private boolean shouldUpdateRate(RRateLimiter limiter, int limit, long windowMillis) {
        RateLimiterConfig config = limiter.getConfig();
        return config == null
                || config.getRateType() != RateType.OVERALL
                || !Objects.equals(config.getRate(), (long) limit)
                || !Objects.equals(config.getRateInterval(), windowMillis);
    }
}
