package com.undernine.utils.samples.config;

import com.undernine.utils.redis.cache.CacheOperationEvent;
import com.undernine.utils.redis.cache.CacheOperationObserver;
import com.undernine.utils.redis.cache.CacheValueCodec;
import com.undernine.utils.redis.cache.JacksonCacheValueCodec;
import com.undernine.utils.spring.ratelimit.RateLimitStore;
import com.undernine.utils.spring.repeat.RepeatSubmitStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration(proxyBeanMethods = false)
@Profile("custom-store")
public class SampleCustomStoreConfiguration {

    @Bean
    public RateLimitStore sampleRateLimitStore() {
        return new WindowRateLimitStore();
    }

    @Bean
    public RepeatSubmitStore sampleRepeatSubmitStore() {
        return new WindowRepeatSubmitStore();
    }

    @Bean
    public CacheValueCodec sampleCacheValueCodec() {
        JacksonCacheValueCodec delegate = new JacksonCacheValueCodec();
        return new CacheValueCodec() {
            @Override
            public String encode(Object value) {
                return "sample-json:" + delegate.encode(value);
            }

            @Override
            public <T> T decode(String payload, Class<T> valueType) {
                String normalizedPayload = payload != null && payload.startsWith("sample-json:")
                        ? payload.substring("sample-json:".length())
                        : payload;
                return delegate.decode(normalizedPayload, valueType);
            }
        };
    }

    @Bean
    public CacheOperationObserver sampleCacheOperationObserver() {
        Logger log = LoggerFactory.getLogger("under-utils-samples-cache");
        return new CacheOperationObserver() {
            @Override
            public void onWrite(CacheOperationEvent event) {
                log.debug("cache write type={} key={} nullValue={}",
                        event.getOperationType(), event.getCacheKey(), event.isNullValue());
            }

            @Override
            public void onLoadFailure(CacheOperationEvent event) {
                log.warn("cache load failed type={} key={}",
                        event.getOperationType(), event.getCacheKey(), event.getError());
            }
        };
    }

    private static final class WindowRateLimitStore implements RateLimitStore {

        private final ConcurrentMap<String, WindowCounter> counters = new ConcurrentHashMap<>();

        @Override
        public boolean tryAcquire(String key, int limit, Duration window) {
            if (limit <= 0) {
                return false;
            }
            long now = System.currentTimeMillis();
            long windowMillis = Math.max(1L, window == null ? 1L : window.toMillis());
            AtomicBoolean allowed = new AtomicBoolean(false);
            counters.compute(key, (ignored, current) -> {
                WindowCounter counter = current == null || current.expiresAt <= now
                        ? new WindowCounter(now + windowMillis)
                        : current;
                if (counter.count < limit) {
                    counter.count++;
                    allowed.set(true);
                }
                return counter;
            });
            return allowed.get();
        }
    }

    private static final class WindowRepeatSubmitStore implements RepeatSubmitStore {

        private final ConcurrentMap<String, Long> expiresAtByKey = new ConcurrentHashMap<>();

        @Override
        public boolean acquire(String key, Duration ttl) {
            long now = System.currentTimeMillis();
            long ttlMillis = Math.max(1L, ttl == null ? 1L : ttl.toMillis());
            AtomicBoolean acquired = new AtomicBoolean(false);
            expiresAtByKey.compute(key, (ignored, currentExpiresAt) -> {
                if (currentExpiresAt != null && currentExpiresAt > now) {
                    return currentExpiresAt;
                }
                acquired.set(true);
                return now + ttlMillis;
            });
            return acquired.get();
        }

        @Override
        public void release(String key) {
            expiresAtByKey.remove(key);
        }
    }

    private static final class WindowCounter {

        private final long expiresAt;
        private int count;

        private WindowCounter(long expiresAt) {
            this.expiresAt = expiresAt;
        }
    }
}
