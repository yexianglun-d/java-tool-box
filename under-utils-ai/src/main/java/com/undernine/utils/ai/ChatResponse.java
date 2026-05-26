package com.undernine.utils.ai;

/**
 * 文本对话响应。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public final class ChatResponse {

    private final String text;
    private final String model;
    private final String finishReason;
    private final String requestId;
    private final TokenUsage usage;

    /**
     * 创建文本对话响应。
     *
     * @param text 文本结果
     * @param model 模型名称
     * @param finishReason 结束原因
     * @param requestId 请求 ID
     * @param usage token 用量
     */
    public ChatResponse(String text, String model, String finishReason, String requestId, TokenUsage usage) {
        this.text = text;
        this.model = model;
        this.finishReason = finishReason;
        this.requestId = requestId;
        this.usage = usage;
    }

    /**
     * 文本结果。
     *
     * @return 文本结果
     */
    public String text() {
        return text;
    }

    /**
     * 文本结果。
     *
     * @return 文本结果
     */
    public String getText() {
        return text;
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
     * 请求 ID。
     *
     * @return 请求 ID
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * token 用量。
     *
     * @return token 用量
     */
    public TokenUsage getUsage() {
        return usage;
    }

    @Override
    public String toString() {
        return "ChatResponse{"
                + "textLength=" + (text == null ? 0 : text.length())
                + ", model='" + model + '\''
                + ", finishReason='" + finishReason + '\''
                + ", requestId='" + requestId + '\''
                + ", usage=" + usage
                + '}';
    }
}
