package com.undernine.utils.spring.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求日志拦截器
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "REQUEST_START_TIME";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = getClientIp(request);
        
        log.info("【HTTP请求】{} {} 来自IP: {}", method, uri, ip);
        
        // 记录请求头
        if (log.isDebugEnabled()) {
            Map<String, String> headers = getHeaders(request);
            log.debug("【请求头】{}", headers);
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME);
        if (startTime != null) {
            long elapsed = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            
            log.info("【HTTP响应】{} {} - 状态码: {}, 耗时: {}ms", 
                request.getMethod(), request.getRequestURI(), status, elapsed);
            
            if (ex != null) {
                log.error("【请求异常】{}", ex.getMessage(), ex);
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip != null && ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        return headers;
    }
}
