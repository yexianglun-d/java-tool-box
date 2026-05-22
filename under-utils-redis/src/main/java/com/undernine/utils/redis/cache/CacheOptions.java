package com.undernine.utils.redis.cache;

import java.time.Duration;
import java.util.Objects;

/**
 * Cache-aside 模板配置。
 * <p>
 * {@link #ttl()} 表示正常业务值写入 Redis 后的存活时间，{@link #nullTtl()} 表示空值占位的存活时间。
 * 为了让调用方在领域代码中表达得更明确，也提供 {@link #valueTtl()}、{@link #nullValueTtl()}
 * 和 {@link #nullValueCacheEnabled()} 作为等价别名。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class CacheOptions {

    private static final String DEFAULT_KEY_PREFIX = "under-utils:cache:";
    private static final String DEFAULT_REBUILD_LOCK_KEY_PREFIX = "under-utils:cache:rebuild-lock:";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);
    private static final Duration DEFAULT_NULL_TTL = Duration.ofSeconds(30);
    private static final Duration DEFAULT_JITTER = Duration.ZERO;
    private static final Duration DEFAULT_LOCK_WAIT_TIME = Duration.ofSeconds(1);
    private static final Duration DEFAULT_LOCK_LEASE_TIME = Duration.ofSeconds(30);

    private final Duration ttl;
    private final Duration nullTtl;
    private final Duration jitter;
    private final boolean cacheNull;
    private final String keyPrefix;
    private final boolean rebuildLockEnabled;
    private final String rebuildLockKeyPrefix;
    private final Duration lockWaitTime;
    private final Duration lockLeaseTime;

    private CacheOptions(Builder builder) {
        this.ttl = requirePositive(builder.ttl, "ttl");
        this.nullTtl = requirePositive(builder.nullTtl, "nullTtl");
        this.jitter = requireNotNegative(builder.jitter, "jitter");
        this.cacheNull = builder.cacheNull;
        this.keyPrefix = builder.keyPrefix == null ? "" : builder.keyPrefix;
        this.rebuildLockEnabled = builder.rebuildLockEnabled;
        this.rebuildLockKeyPrefix = builder.rebuildLockKeyPrefix == null ? "" : builder.rebuildLockKeyPrefix;
        this.lockWaitTime = requireNotNegative(builder.lockWaitTime, "lockWaitTime");
        this.lockLeaseTime = requirePositive(builder.lockLeaseTime, "lockLeaseTime");
    }

    public static CacheOptions defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
            .ttl(ttl)
            .nullTtl(nullTtl)
            .jitter(jitter)
            .cacheNull(cacheNull)
            .keyPrefix(keyPrefix)
            .rebuildLockEnabled(rebuildLockEnabled)
            .rebuildLockKeyPrefix(rebuildLockKeyPrefix)
            .lockWaitTime(lockWaitTime)
            .lockLeaseTime(lockLeaseTime);
    }

    public Duration ttl() {
        return ttl;
    }

    /**
     * 正常业务值的 Redis TTL。
     *
     * @return 正常业务值 TTL
     */
    public Duration valueTtl() {
        return ttl();
    }

    public Duration nullTtl() {
        return nullTtl;
    }

    /**
     * 空值占位的 Redis TTL。
     *
     * @return 空值 TTL
     */
    public Duration nullValueTtl() {
        return nullTtl();
    }

    public Duration jitter() {
        return jitter;
    }

    public boolean cacheNull() {
        return cacheNull;
    }

    /**
     * 是否缓存空值占位。
     *
     * @return true 表示缓存空值占位
     */
    public boolean nullValueCacheEnabled() {
        return cacheNull();
    }

    public String keyPrefix() {
        return keyPrefix;
    }

    public boolean rebuildLockEnabled() {
        return rebuildLockEnabled;
    }

    public String rebuildLockKeyPrefix() {
        return rebuildLockKeyPrefix;
    }

    public Duration lockWaitTime() {
        return lockWaitTime;
    }

    public Duration lockLeaseTime() {
        return lockLeaseTime;
    }

    private static Duration requirePositive(Duration duration, String name) {
        Objects.requireNonNull(duration, name + " must not be null");
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return duration;
    }

    private static Duration requireNotNegative(Duration duration, String name) {
        Objects.requireNonNull(duration, name + " must not be null");
        if (duration.isNegative()) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
        return duration;
    }

    /**
     * CacheOptions 构造器。
     */
    public static final class Builder {

        private Duration ttl = DEFAULT_TTL;
        private Duration nullTtl = DEFAULT_NULL_TTL;
        private Duration jitter = DEFAULT_JITTER;
        private boolean cacheNull = true;
        private String keyPrefix = DEFAULT_KEY_PREFIX;
        private boolean rebuildLockEnabled = true;
        private String rebuildLockKeyPrefix = DEFAULT_REBUILD_LOCK_KEY_PREFIX;
        private Duration lockWaitTime = DEFAULT_LOCK_WAIT_TIME;
        private Duration lockLeaseTime = DEFAULT_LOCK_LEASE_TIME;

        private Builder() {
        }

        public Builder ttl(Duration ttl) {
            this.ttl = ttl;
            return this;
        }

        /**
         * 设置正常业务值的 Redis TTL，等价于 {@link #ttl(Duration)}。
         *
         * @param valueTtl 正常业务值 TTL
         * @return 当前构建器
         */
        public Builder valueTtl(Duration valueTtl) {
            return ttl(valueTtl);
        }

        public Builder nullTtl(Duration nullTtl) {
            this.nullTtl = nullTtl;
            return this;
        }

        /**
         * 设置空值占位的 Redis TTL，等价于 {@link #nullTtl(Duration)}。
         *
         * @param nullValueTtl 空值 TTL
         * @return 当前构建器
         */
        public Builder nullValueTtl(Duration nullValueTtl) {
            return nullTtl(nullValueTtl);
        }

        public Builder jitter(Duration jitter) {
            this.jitter = jitter;
            return this;
        }

        public Builder cacheNull(boolean cacheNull) {
            this.cacheNull = cacheNull;
            return this;
        }

        /**
         * 设置是否缓存空值占位，等价于 {@link #cacheNull(boolean)}。
         *
         * @param enabled true 表示缓存空值占位
         * @return 当前构建器
         */
        public Builder nullValueCacheEnabled(boolean enabled) {
            return cacheNull(enabled);
        }

        public Builder keyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
            return this;
        }

        public Builder rebuildLockEnabled(boolean rebuildLockEnabled) {
            this.rebuildLockEnabled = rebuildLockEnabled;
            return this;
        }

        public Builder rebuildLockKeyPrefix(String rebuildLockKeyPrefix) {
            this.rebuildLockKeyPrefix = rebuildLockKeyPrefix;
            return this;
        }

        public Builder lockWaitTime(Duration lockWaitTime) {
            this.lockWaitTime = lockWaitTime;
            return this;
        }

        public Builder lockLeaseTime(Duration lockLeaseTime) {
            this.lockLeaseTime = lockLeaseTime;
            return this;
        }

        public CacheOptions build() {
            return new CacheOptions(this);
        }
    }
}
