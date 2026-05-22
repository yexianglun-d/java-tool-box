package com.undernine.utils.spring.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 当前操作上下文。
 * <p>
 * 用于在一次请求或一次业务操作内统一承载链路、租户、用户、请求和业务操作身份。
 * 实例不可变，业务需要追加属性时通过 {@link #toBuilder()} 或 {@link #withAttribute(String, Object)}
 * 创建新的上下文实例。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class OperationContext {

    private final String traceId;
    private final String tenantId;
    private final String userId;
    private final String requestMethod;
    private final String requestUri;
    private final String clientIp;
    private final String operationName;
    private final Map<String, Object> attributes;

    private OperationContext(Builder builder) {
        this.traceId = trimToNull(builder.traceId);
        this.tenantId = trimToNull(builder.tenantId);
        this.userId = trimToNull(builder.userId);
        this.requestMethod = trimToNull(builder.requestMethod);
        this.requestUri = trimToNull(builder.requestUri);
        this.clientIp = trimToNull(builder.clientIp);
        this.operationName = trimToNull(builder.operationName);
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(builder.attributes));
    }

    /**
     * 创建上下文构建器。
     *
     * @return 构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 基于已有上下文创建构建器。
     *
     * @param context 已有上下文
     * @return 构建器
     */
    public static Builder from(OperationContext context) {
        return new Builder(context);
    }

    /**
     * 复制当前上下文到构建器。
     *
     * @return 构建器
     */
    public Builder toBuilder() {
        return from(this);
    }

    /**
     * 追加或覆盖单个扩展属性。
     *
     * @param name  属性名
     * @param value 属性值
     * @return 新上下文实例
     */
    public OperationContext withAttribute(String name, Object value) {
        return toBuilder().attribute(name, value).build();
    }

    /**
     * 覆盖当前操作名称。
     *
     * @param operationName 操作名称
     * @return 新上下文实例
     */
    public OperationContext withOperationName(String operationName) {
        return toBuilder().operationName(operationName).build();
    }

    public String getTraceId() {
        return traceId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getOperationName() {
        return operationName;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public <T> T getAttribute(String name, Class<T> type) {
        Object value = attributes.get(name);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * OperationContext 构建器。
     */
    public static final class Builder {

        private String traceId;
        private String tenantId;
        private String userId;
        private String requestMethod;
        private String requestUri;
        private String clientIp;
        private String operationName;
        private final Map<String, Object> attributes = new LinkedHashMap<>();

        private Builder() {
        }

        private Builder(OperationContext context) {
            Objects.requireNonNull(context, "context must not be null");
            this.traceId = context.traceId;
            this.tenantId = context.tenantId;
            this.userId = context.userId;
            this.requestMethod = context.requestMethod;
            this.requestUri = context.requestUri;
            this.clientIp = context.clientIp;
            this.operationName = context.operationName;
            this.attributes.putAll(context.attributes);
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder requestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public Builder requestUri(String requestUri) {
            this.requestUri = requestUri;
            return this;
        }

        public Builder clientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public Builder operationName(String operationName) {
            this.operationName = operationName;
            return this;
        }

        public Builder attribute(String name, Object value) {
            attributes.put(Objects.requireNonNull(name, "attribute name must not be null"), value);
            return this;
        }

        public Builder attributes(Map<String, ?> attributes) {
            if (attributes != null) {
                attributes.forEach(this::attribute);
            }
            return this;
        }

        public OperationContext build() {
            return new OperationContext(this);
        }
    }
}
