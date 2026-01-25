package com.undernine.utils.spring.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 方法执行时间统计切面
 * <p>
 * 拦截带有 {@link TimeLog} 注解的方法，统计执行时间并记录日志。
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>自动记录方法执行时间</li>
 *   <li>慢方法自动以 WARN 级别记录（默认阈值1000ms）</li>
 *   <li>即使方法抛出异常也会记录执行时间</li>
 *   <li>支持自定义操作描述和慢方法阈值</li>
 * </ul>
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class TimeLogAspect {

    /**
     * 环绕通知：统计方法执行时间
     *
     * @param joinPoint 连接点
     * @param timeLog   TimeLog 注解
     * @return 方法返回值
     * @throws Throwable 异常
     */
    @Around("@annotation(timeLog)")
    public Object around(ProceedingJoinPoint joinPoint, TimeLog timeLog) throws Throwable {
        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        // 获取操作描述
        String description = timeLog.value();
        if (description == null || description.trim().isEmpty()) {
            description = "未命名";
        }

        // 获取慢方法阈值
        long slowThreshold = timeLog.slowThreshold();

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        try {
            // 执行目标方法
            return joinPoint.proceed();
        } finally {
            // 计算执行时间
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // 记录日志
            String logMessage = String.format("%s.%s(%s) 执行耗时: %dms",
                    className, methodName, description, executionTime);

            if (executionTime > slowThreshold) {
                // 慢方法，使用 WARN 级别
                log.warn("【慢方法】{} (阈值: {}ms)", logMessage, slowThreshold);
            } else {
                // 正常方法，使用 DEBUG 级别
                log.debug(logMessage);
            }
        }
    }
}
