package com.undernine.utils.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 文本对话请求。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public final class ChatRequest {

    private final List<ChatMessage> messages;
    private final String model;
    private final Double temperature;
    private final Integer maxTokens;
    private final String requestId;
    private final Map<String, Object> extraBody;

    private ChatRequest(Builder builder) {
        if (builder.messages.isEmpty()) {
            throw new IllegalArgumentException("messages must not be empty");
        }
        this.messages = Collections.unmodifiableList(new ArrayList<>(builder.messages));
        this.model = trimToNull(builder.model);
        this.temperature = builder.temperature;
        this.maxTokens = builder.maxTokens;
        this.requestId = trimToNull(builder.requestId);
        this.extraBody = Collections.unmodifiableMap(new LinkedHashMap<>(builder.extraBody));
    }

    /**
     * 创建只有一条 user 消息的请求。
     *
     * @param content 用户消息
     * @return 对话请求
     */
    public static ChatRequest user(String content) {
        return builder().user(content).build();
    }

    /**
     * 创建请求构建器。
     *
     * @return 请求构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 基于当前请求创建构建器。
     *
     * @return 请求构建器
     */
    public Builder toBuilder() {
        return builder()
                .messages(messages)
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .requestId(requestId)
                .extraBody(extraBody);
    }

    /**
     * 对话消息。
     *
     * @return 对话消息
     */
    public List<ChatMessage> getMessages() {
        return messages;
    }

    /**
     * 本次请求覆盖的模型名称。
     *
     * @return 模型名称
     */
    public String getModel() {
        return model;
    }

    /**
     * 采样温度。
     *
     * @return 采样温度
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * 最大输出 token 数。
     *
     * @return 最大输出 token 数
     */
    public Integer getMaxTokens() {
        return maxTokens;
    }

    /**
     * 请求 ID。
     *
     * @return 请求 ID
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * 透传给 OpenAI-compatible API 的额外请求字段。
     *
     * @return 额外请求字段
     */
    public Map<String, Object> getExtraBody() {
        return extraBody;
    }

    @Override
    public String toString() {
        return "ChatRequest{"
                + "messageCount=" + messages.size()
                + ", model='" + model + '\''
                + ", temperature=" + temperature
                + ", maxTokens=" + maxTokens
                + ", requestId='" + requestId + '\''
                + ", extraBodyKeys=" + extraBody.keySet()
                + '}';
    }

    private static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    /**
     * 对话请求构建器。
     */
    public static final class Builder {

        private final List<ChatMessage> messages = new ArrayList<>();
        private String model;
        private Double temperature;
        private Integer maxTokens;
        private String requestId;
        private final Map<String, Object> extraBody = new LinkedHashMap<>();

        private Builder() {
        }

        /**
         * 添加消息。
         *
         * @param message 消息
         * @return 当前构建器
         */
        public Builder message(ChatMessage message) {
            this.messages.add(Objects.requireNonNull(message, "message must not be null"));
            return this;
        }

        /**
         * 批量添加消息。
         *
         * @param messages 消息列表
         * @return 当前构建器
         */
        public Builder messages(List<ChatMessage> messages) {
            if (messages != null) {
                messages.forEach(this::message);
            }
            return this;
        }

        /**
         * 添加 system 消息。
         *
         * @param content 消息内容
         * @return 当前构建器
         */
        public Builder system(String content) {
            return message(ChatMessage.system(content));
        }

        /**
         * 添加 user 消息。
         *
         * @param content 消息内容
         * @return 当前构建器
         */
        public Builder user(String content) {
            return message(ChatMessage.user(content));
        }

        /**
         * 添加 assistant 消息。
         *
         * @param content 消息内容
         * @return 当前构建器
         */
        public Builder assistant(String content) {
            return message(ChatMessage.assistant(content));
        }

        /**
         * 覆盖默认模型。
         *
         * @param model 模型名称
         * @return 当前构建器
         */
        public Builder model(String model) {
            this.model = model;
            return this;
        }

        /**
         * 设置采样温度。
         *
         * @param temperature 采样温度
         * @return 当前构建器
         */
        public Builder temperature(Double temperature) {
            if (temperature != null && temperature < 0D) {
                throw new IllegalArgumentException("temperature must not be negative");
            }
            this.temperature = temperature;
            return this;
        }

        /**
         * 设置最大输出 token 数。
         *
         * @param maxTokens 最大输出 token 数
         * @return 当前构建器
         */
        public Builder maxTokens(Integer maxTokens) {
            if (maxTokens != null && maxTokens <= 0) {
                throw new IllegalArgumentException("maxTokens must be greater than 0");
            }
            this.maxTokens = maxTokens;
            return this;
        }

        /**
         * 设置请求 ID。
         *
         * @param requestId 请求 ID
         * @return 当前构建器
         */
        public Builder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        /**
         * 添加额外请求字段。
         *
         * @param name 字段名
         * @param value 字段值
         * @return 当前构建器
         */
        public Builder extraBody(String name, Object value) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("extra body name must not be blank");
            }
            this.extraBody.put(name.trim(), value);
            return this;
        }

        /**
         * 批量添加额外请求字段。
         *
         * @param extraBody 额外请求字段
         * @return 当前构建器
         */
        public Builder extraBody(Map<String, Object> extraBody) {
            if (extraBody != null) {
                extraBody.forEach(this::extraBody);
            }
            return this;
        }

        /**
         * 构建请求。
         *
         * @return 对话请求
         */
        public ChatRequest build() {
            return new ChatRequest(this);
        }
    }
}
