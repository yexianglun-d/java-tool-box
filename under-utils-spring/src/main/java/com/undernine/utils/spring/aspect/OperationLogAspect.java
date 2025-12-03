package com.undernine.utils.spring.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.undernine.utils.spring.annotation.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 操作日志切面
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint point, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        String username = getCurrentUsername();
        String ip = getClientIp();
        String uri = getRequestUri();
        
        log.info("【操作日志】模块: {}, 类型: {}, 内容: {}, 用户: {}, IP: {}, URI: {}", 
            operationLog.module(), operationLog.type().getDescription(), 
            operationLog.content(), username, ip, uri);
        
        if (operationLog.recordParams()) {
            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                try {
                    log.info("【操作日志】请求参数: {}", MAPPER.writeValueAsString(args));
                } catch (Exception ignored) {}
            }
        }
        
        try {
            Object result = point.proceed();
            
            if (operationLog.recordResult() && result != null) {
                try {
                    log.info("【操作日志】返回结果: {}", MAPPER.writeValueAsString(result));
                } catch (Exception ignored) {}
            }
            
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("【操作日志】操作成功, 耗时: {}ms", elapsed);
            return result;
        } catch (Throwable e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("【操作日志】操作失败, 耗时: {}ms, 错误: {}", elapsed, e.getMessage());
            throw e;
        }
    }

    private String getCurrentUsername() {
        // TODO: 集成实际认证系统
        return "system";
    }

    private String getClientIp() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return "unknown";
        
        HttpServletRequest request = attrs.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip != null && ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }

    private String getRequestUri() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest().getRequestURI() : "unknown";
    }
}
