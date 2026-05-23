package com.undernine.utils.redis.cache;

import java.util.Objects;

/**
 * 缓存操作观测事件。
 *
 * @author Under-Utils Team
 * @version 1.0.1
 * @since 1.0.1
 */
public final class CacheOperationEvent {

    private final CacheOperationType operationType;
    private final String key;
    private final String cacheKey;
    private final boolean nullValue;
    private final long durationNanos;
    private final Throwable error;

    /**
     * 构造缓存操作事件。
     *
     * @param operationType 模板类型
     * @param key           业务 key
     * @param cacheKey      实际缓存 key
     * @param nullValue     是否为空值占位
     * @param durationNanos 操作耗时，未知时为 0
     * @param error         失败异常
     */
    public CacheOperationEvent(CacheOperationType operationType, String key, String cacheKey,
                               boolean nullValue, long durationNanos, Throwable error) {
        if (durationNanos < 0L) {
            throw new IllegalArgumentException("durationNanos must be greater than or equal to 0");
        }
        this.operationType = Objects.requireNonNull(operationType, "operationType must not be null");
        this.key = key;
        this.cacheKey = cacheKey;
        this.nullValue = nullValue;
        this.durationNanos = durationNanos;
        this.error = error;
    }

    /**
     * 创建无耗时、无异常的事件。
     *
     * @param operationType 模板类型
     * @param key           业务 key
     * @param cacheKey      实际缓存 key
     * @param nullValue     是否为空值占位
     * @return 缓存操作事件
     */
    public static CacheOperationEvent of(CacheOperationType operationType, String key, String cacheKey,
                                         boolean nullValue) {
        return new CacheOperationEvent(operationType, key, cacheKey, nullValue, 0L, null);
    }

    /**
     * 创建带耗时的事件。
     *
     * @param operationType 模板类型
     * @param key           业务 key
     * @param cacheKey      实际缓存 key
     * @param nullValue     是否为空值占位
     * @param durationNanos 操作耗时
     * @return 缓存操作事件
     */
    public static CacheOperationEvent timed(CacheOperationType operationType, String key, String cacheKey,
                                            boolean nullValue, long durationNanos) {
        return new CacheOperationEvent(operationType, key, cacheKey, nullValue, durationNanos, null);
    }

    /**
     * 创建失败事件。
     *
     * @param operationType 模板类型
     * @param key           业务 key
     * @param cacheKey      实际缓存 key
     * @param durationNanos 操作耗时
     * @param error         失败异常
     * @return 缓存操作事件
     */
    public static CacheOperationEvent failed(CacheOperationType operationType, String key, String cacheKey,
                                             long durationNanos, Throwable error) {
        return new CacheOperationEvent(operationType, key, cacheKey, false, durationNanos, error);
    }

    public CacheOperationType getOperationType() {
        return operationType;
    }

    public String getKey() {
        return key;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public boolean isNullValue() {
        return nullValue;
    }

    public long getDurationNanos() {
        return durationNanos;
    }

    public Throwable getError() {
        return error;
    }
}
