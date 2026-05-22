package com.undernine.utils.spring.aspect;

import com.undernine.utils.spring.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 重试机制切面。
 * <p>
 * 仅提供兼容维护的同步重试能力。该类不再声明为 Spring 组件，业务如仍需使用，应显式
 * {@code @Import(RetryAspect.class)} 或注册为 {@code @Bean}。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 * @deprecated 同步重试切面保留为兼容 API，不作为 Under-Utils 后续工程模式主线能力演进。
 */
@Slf4j
@Aspect
@Deprecated(since = "1.0.0")
public class RetryAspect {

    @Around("@annotation(retry)")
    public Object around(ProceedingJoinPoint point, Retry retry) throws Throwable {
        int maxAttempts = Math.max(1, retry.maxAttempts());
        long delay = Math.max(0L, retry.delay());
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
                
                sleep(delay);
            }
        }
        
        throw new IllegalStateException("重试逻辑异常");
    }

    private boolean shouldRetry(Throwable e, Class<? extends Throwable>[] exceptions) {
        if (exceptions == null || exceptions.length == 0) {
            return false;
        }
        for (Class<? extends Throwable> exceptionClass : exceptions) {
            if (exceptionClass.isInstance(e)) {
                return true;
            }
        }
        return false;
    }

    private void sleep(long delay) throws Throwable {
        if (delay <= 0L) {
            return;
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }
}
