package com.undernine.utils.spring.repeat;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JVM 本地防重复提交存储。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class LocalRepeatSubmitStore implements RepeatSubmitStore {

    private final Map<String, Long> cache = new ConcurrentHashMap<>();

    @Override
    public boolean acquire(String key, Duration ttl) {
        long now = System.currentTimeMillis();
        long ttlMillis = Math.max(1L, ttl.toMillis());
        cleanExpired(now);

        Long expireAt = cache.putIfAbsent(key, now + ttlMillis);
        if (expireAt == null) {
            return true;
        }
        if (expireAt <= now && cache.replace(key, expireAt, now + ttlMillis)) {
            return true;
        }
        return false;
    }

    @Override
    public void release(String key) {
        cache.remove(key);
    }

    /**
     * 清空本地状态，主要用于测试或主动重置。
     */
    public void clear() {
        cache.clear();
    }

    private void cleanExpired(long now) {
        cache.entrySet().removeIf(entry -> entry.getValue() <= now);
    }
}
