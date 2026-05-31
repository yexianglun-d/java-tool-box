package com.undernine.utils.ai;

import java.time.Duration;

/**
 * AI 响应元数据。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public final class AiResponseMetadata {

    private final String provider;
    private final String requestId;
    private final String responseId;
    private final String modelFingerprint;
    private final Duration duration;

    private AiResponseMetadata(Builder builder) {
        this.provider = trimToNull(builder.provider);
        this.requestId = trimToNull(builder.requestId);
        this.responseId = trimToNull(builder.responseId);
        this.modelFingerprint = trimToNull(builder.modelFingerprint);
        this.duration = builder.duration;
    }

    /**
     * 创建响应元数据构建器。
     *
     * @return 构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * provider 名称。
     *
     * @return provider 名称
     */
    public String getProvider() {
        return provider;
    }

    /**
     * 调用方传入的请求 ID。
     *
     * @return 请求 ID
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * 模型服务返回的响应 ID。
     *
     * @return 响应 ID
     */
    public String getResponseId() {
        return responseId;
    }

    /**
     * 模型指纹。
     *
     * @return 模型指纹
     */
    public String getModelFingerprint() {
        return modelFingerprint;
    }

    /**
     * 请求耗时。
     *
     * @return 请求耗时
     */
    public Duration getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "AiResponseMetadata{"
                + "provider='" + provider + '\''
                + ", requestId='" + requestId + '\''
                + ", responseId='" + responseId + '\''
                + ", modelFingerprint='" + modelFingerprint + '\''
                + ", duration=" + duration
                + '}';
    }

    private static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    /**
     * 响应元数据构建器。
     */
    public static final class Builder {

        private String provider;
        private String requestId;
        private String responseId;
        private String modelFingerprint;
        private Duration duration;

        private Builder() {
        }

        public Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder responseId(String responseId) {
            this.responseId = responseId;
            return this;
        }

        public Builder modelFingerprint(String modelFingerprint) {
            this.modelFingerprint = modelFingerprint;
            return this;
        }

        public Builder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public AiResponseMetadata build() {
            return new AiResponseMetadata(this);
        }
    }
}
