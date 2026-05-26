package com.undernine.utils.ai;

/**
 * 模型 token 用量。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public final class TokenUsage {

    private final int promptTokens;
    private final int completionTokens;
    private final int totalTokens;

    /**
     * 创建 token 用量。
     *
     * @param promptTokens prompt token 数
     * @param completionTokens completion token 数
     * @param totalTokens 总 token 数
     */
    public TokenUsage(int promptTokens, int completionTokens, int totalTokens) {
        this.promptTokens = Math.max(promptTokens, 0);
        this.completionTokens = Math.max(completionTokens, 0);
        this.totalTokens = Math.max(totalTokens, 0);
    }

    /**
     * prompt token 数。
     *
     * @return prompt token 数
     */
    public int getPromptTokens() {
        return promptTokens;
    }

    /**
     * completion token 数。
     *
     * @return completion token 数
     */
    public int getCompletionTokens() {
        return completionTokens;
    }

    /**
     * 总 token 数。
     *
     * @return 总 token 数
     */
    public int getTotalTokens() {
        return totalTokens;
    }

    @Override
    public String toString() {
        return "TokenUsage{"
                + "promptTokens=" + promptTokens
                + ", completionTokens=" + completionTokens
                + ", totalTokens=" + totalTokens
                + '}';
    }
}
