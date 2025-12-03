package com.undernine.utils.spring.aspect;

import com.undernine.utils.spring.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 重试机制切面
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class RetryAspect {

    @Around("@annotation(retry)")
    public Object around(ProceedingJoinPoint point, Retry retry) throws Throwable {
        int maxAttempts = retry.maxAttempts();
        long delay = retry.delay();
        Class<? extends Throwable>[] exceptions = retry.exceptions();
        
        String methodName = point.getSignature().getName();
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return point.proceed();
            } catch (Throwable e) {
                if (attempt == maxAttempts || !shouldRetry(e, exceptions)) {
                    log.error("【重试失败】方法: {}, 尝试次数: {}/{}, 错误: {}", 
                        methodName, attempt, maxAttempts, e.getMessage());
                    throw e;
                }
                
                log.warn("【重试中】方法: {}, 尝试次数: {}/{}, 错误: {}, {}ms后重试", 
                    methodName, attempt, maxAttempts, e.getMessage(), delay);
                
                Thread.sleep(delay);
            }
        }
        
        throw new IllegalStateException("重试逻辑异常");
    }

    private boolean shouldRetry(Throwable e, Class<? extends Throwable>[] exceptions) {
        for (Class<? extends Throwable> exceptionClass : exceptions) {
            if (exceptionClass.isInstance(e)) {
                return true;
            }
        }
        return false;
    }
}
