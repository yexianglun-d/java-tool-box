package com.undernine.utils.spring.aspect;

import com.undernine.utils.spring.annotation.RateLimit;
import com.undernine.utils.spring.exception.BizException;
import com.undernine.utils.spring.key.DefaultOperationKeyResolver;
import com.undernine.utils.spring.key.OperationKeyResolver;
import com.undernine.utils.spring.ratelimit.LocalRateLimitStore;
import com.undernine.utils.spring.ratelimit.RateLimitStore;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 接口限流切面。
 * <p>
 * 解析操作 key 后向 {@link RateLimitStore} 请求访问额度；获取失败时抛出
 * {@link BizException}。未注入存储时会懒加载 {@link LocalRateLimitStore}，starter 会按配置替换为
 * 本地或 Redis 存储。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect implements AutoCloseable {

    private volatile RateLimitStore rateLimitStore;
    private volatile boolean defaultStoreOwned;
    private OperationKeyResolver operationKeyResolver = new DefaultOperationKeyResolver();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        String key = operationKeyResolver.resolve(point, rateLimit.namespace(), rateLimit.key());
        Duration window = Duration.ofSeconds(Math.max(1, rateLimit.period()));
        String message = rateLimit.message();

        if (!getRateLimitStore().tryAcquire(key, rateLimit.limit(), window)) {
            log.warn("【接口限流】请求被限流: {}", key);
            throw new BizException(message);
        }
        
        return point.proceed();
    }

    @Autowired(required = false)
    public synchronized void setRateLimitStore(RateLimitStore rateLimitStore) {
        if (rateLimitStore != null) {
            closeDefaultStore();
            this.rateLimitStore = rateLimitStore;
            this.defaultStoreOwned = false;
        }
    }

    @Autowired(required = false)
    public void setOperationKeyResolver(OperationKeyResolver operationKeyResolver) {
        if (operationKeyResolver != null) {
            this.operationKeyResolver = operationKeyResolver;
        }
    }

    @Override
    public synchronized void close() {
        closeDefaultStore();
    }

    private RateLimitStore getRateLimitStore() {
        RateLimitStore store = rateLimitStore;
        if (store != null) {
            return store;
        }
        synchronized (this) {
            if (rateLimitStore == null) {
                rateLimitStore = new LocalRateLimitStore();
                defaultStoreOwned = true;
            }
            return rateLimitStore;
        }
    }

    private void closeDefaultStore() {
        if (defaultStoreOwned && rateLimitStore instanceof AutoCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception ex) {
                log.warn("Failed to close default rate limit store", ex);
            }
        }
        defaultStoreOwned = false;
    }
}
