package com.undernine.utils.spring.aspect;

import com.undernine.utils.spring.annotation.RateLimit;
import com.undernine.utils.spring.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 接口限流切面
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    private static final Map<String, RateLimiter> LIMITER_CACHE = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        String key = generateKey(point);
        RateLimiter limiter = LIMITER_CACHE.computeIfAbsent(key, 
            k -> new RateLimiter(rateLimit.limit(), rateLimit.period() * 1000L));
        
        if (!limiter.tryAcquire()) {
            log.warn("【接口限流】请求被限流: {}", key);
            throw new BizException(rateLimit.message());
        }
        
        return point.proceed();
    }

    private String generateKey(ProceedingJoinPoint point) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String uri = attrs != null ? attrs.getRequest().getRequestURI() : "";
        String userId = getCurrentUserId();
        return String.format("%s:%s", userId, uri);
    }

    private String getCurrentUserId() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            return request.getRemoteAddr();
        }
        return "anonymous";
    }

    /**
     * 简单的令牌桶限流器
     */
    private static class RateLimiter {
        private final int limit;
        private final long period;
        private final AtomicInteger count;
        private volatile long lastRefillTime;

        public RateLimiter(int limit, long period) {
            this.limit = limit;
            this.period = period;
            this.count = new AtomicInteger(limit);
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean tryAcquire() {
            refill();
            if (count.get() > 0) {
                count.decrementAndGet();
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            if (now - lastRefillTime >= period) {
                count.set(limit);
                lastRefillTime = now;
            }
        }
    }
}
