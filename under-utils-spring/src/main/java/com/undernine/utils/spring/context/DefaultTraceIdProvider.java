package com.undernine.utils.spring.context;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 默认链路 ID 提供器。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class DefaultTraceIdProvider implements TraceIdProvider {

    private static final String TRACE_ID = "traceId";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public String getTraceId() {
        OperationContext context = OperationContextHolder.getContext();
        if (context != null && isNotBlank(context.getTraceId())) {
            return context.getTraceId().trim();
        }

        String traceId = MDC.get(TRACE_ID);
        if (isNotBlank(traceId)) {
            return traceId.trim();
        }

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }

        HttpServletRequest request = attrs.getRequest();
        traceId = request.getHeader(TRACE_ID_HEADER);
        return isNotBlank(traceId) ? traceId.trim() : null;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
