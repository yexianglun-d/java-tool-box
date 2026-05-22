package com.undernine.utils.spring.context;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 默认当前用户提供器。
 * <p>
 * 优先读取请求头 {@code X-User-Id}，没有时退化为客户端 IP，再没有时返回 {@code anonymous}。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class DefaultCurrentUserProvider implements CurrentUserProvider {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String ANONYMOUS = "anonymous";

    @Override
    public String getCurrentUserId() {
        OperationContext context = OperationContextHolder.getContext();
        if (context != null && isNotBlank(context.getUserId())) {
            return context.getUserId().trim();
        }

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return ANONYMOUS;
        }

        HttpServletRequest request = attrs.getRequest();
        String userId = request.getHeader(USER_ID_HEADER);
        if (isNotBlank(userId)) {
            return userId.trim();
        }

        String remoteAddr = request.getRemoteAddr();
        return isNotBlank(remoteAddr) ? remoteAddr.trim() : ANONYMOUS;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
