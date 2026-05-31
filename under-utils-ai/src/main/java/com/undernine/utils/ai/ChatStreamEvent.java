package com.undernine.utils.ai;

/**
 * 流式文本对话事件。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public final class ChatStreamEvent {

    private final String text;
    private final String role;
    private final String model;
    private final String finishReason;
    private final TokenUsage usage;
    private final AiResponseMetadata metadata;
    private final boolean done;

    /**
     * 创建流式文本对话事件。
     *
     * @param text 增量文本
     * @param role 消息角色
     * @param model 模型名称
     * @param finishReason 结束原因
     * @param usage token 用量
     * @param metadata 响应元数据
     * @param done 是否结束事件
     */
    public ChatStreamEvent(String text, String role, String model, String finishReason,
                           TokenUsage usage, AiResponseMetadata metadata, boolean done) {
        this.text = text;
        this.role = role;
        this.model = model;
        this.finishReason = finishReason;
        this.usage = usage;
        this.metadata = metadata;
        this.done = done;
    }

    /**
     * 增量文本。
     *
     * @return 增量文本
     */
    public String text() {
        return text;
    }

    /**
     * 增量文本。
     *
     * @return 增量文本
     */
    public String getText() {
        return text;
    }

    /**
     * 增量文本，等价于 {@link #text()}。
     *
     * @return 增量文本
     */
    public String delta() {
        return text();
    }

    /**
     * 消息角色。
     *
     * @return 消息角色
     */
    public String getRole() {
        return role;
    }

    /**
     * 模型名称。
     *
     * @return 模型名称
     */
    public String getModel() {
        return model;
    }

    /**
     * 结束原因。
     *
     * @return 结束原因
     */
    public String getFinishReason() {
        return finishReason;
    }

    /**
     * token 用量。
     *
     * @return token 用量
     */
    public TokenUsage getUsage() {
        return usage;
    }

    /**
     * 响应元数据。
     *
     * @return 响应元数据
     */
    public AiResponseMetadata getMetadata() {
        return metadata;
    }

    /**
     * 调用方传入的请求 ID。
     *
     * @return 请求 ID
     */
    public String getRequestId() {
        return metadata == null ? null : metadata.getRequestId();
    }

    /**
     * 模型服务返回的响应 ID。
     *
     * @return 响应 ID
     */
    public String getResponseId() {
        return metadata == null ? null : metadata.getResponseId();
    }

    /**
     * 模型指纹。
     *
     * @return 模型指纹
     */
    public String getModelFingerprint() {
        return metadata == null ? null : metadata.getModelFingerprint();
    }

    /**
     * 是否包含非空文本增量。
     *
     * @return true 表示包含文本增量
     */
    public boolean hasText() {
        return text != null && !text.isEmpty();
    }

    /**
     * 是否结束事件。
     *
     * @return true 表示结束事件
     */
    public boolean isDone() {
        return done;
    }

    @Override
    public String toString() {
        return "ChatStreamEvent{"
                + "textLength=" + (text == null ? 0 : text.length())
                + ", role='" + role + '\''
                + ", model='" + model + '\''
                + ", finishReason='" + finishReason + '\''
                + ", usage=" + usage
                + ", metadata=" + metadata
                + ", done=" + done
                + '}';
    }
}
