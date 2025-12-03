package com.undernine.utils.spring.aspect;

import com.undernine.utils.spring.annotation.PreventRepeat;
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

/**
 * 防重复提交切面
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class PreventRepeatAspect {

    private static final Map<String, Long> REQUEST_CACHE = new ConcurrentHashMap<>();

    @Around("@annotation(preventRepeat)")
    public Object around(ProceedingJoinPoint point, PreventRepeat preventRepeat) throws Throwable {
        String key = generateKey(point);
        long now = System.currentTimeMillis();
        long expireTime = preventRepeat.timeUnit().toMillis(preventRepeat.timeout());
        
        Long lastRequestTime = REQUEST_CACHE.get(key);
        if (lastRequestTime != null && (now - lastRequestTime) < expireTime) {
            log.warn("【防重复提交】请求被拒绝: {}", key);
            throw new BizException(preventRepeat.message());
        }
        
        REQUEST_CACHE.put(key, now);
        
        // 清理过期数据
        cleanExpiredCache(now, expireTime);
        
        return point.proceed();
    }

    private String generateKey(ProceedingJoinPoint point) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String uri = attrs != null ? attrs.getRequest().getRequestURI() : "";
        String methodName = point.getSignature().getName();
        String userId = getCurrentUserId();
        return String.format("%s:%s:%s", userId, uri, methodName);
    }

    private String getCurrentUserId() {
        // TODO: 集成实际认证系统获取用户ID
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            return request.getRemoteAddr(); // 临时用IP
        }
        return "anonymous";
    }

    private void cleanExpiredCache(long now, long expireTime) {
        REQUEST_CACHE.entrySet().removeIf(entry -> (now - entry.getValue()) > expireTime);
    }
}
