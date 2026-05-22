package com.undernine.utils.spring.context;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 默认当前租户提供器。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class DefaultCurrentTenantProvider implements CurrentTenantProvider {

    private static final String TENANT_ID_HEADER = "X-Tenant-Id";
    private static final String DEFAULT_TENANT = "default";

    @Override
    public String getCurrentTenantId() {
        OperationContext context = OperationContextHolder.getContext();
        if (context != null && isNotBlank(context.getTenantId())) {
            return context.getTenantId().trim();
        }

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return DEFAULT_TENANT;
        }

        HttpServletRequest request = attrs.getRequest();
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        return isNotBlank(tenantId) ? tenantId.trim() : DEFAULT_TENANT;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
