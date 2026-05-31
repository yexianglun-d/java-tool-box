package com.undernine.utils.ai;

/**
 * 支持流式文本对话的 AI client。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public interface StreamingAiClient {

    /**
     * 执行流式文本对话。
     * <p>
     * 返回值必须由调用方关闭，推荐使用 try-with-resources。
     * </p>
     *
     * @param request 对话请求
     * @return 流式响应
     */
    ChatStream streamChat(ChatRequest request);

    /**
     * 执行单条 user 消息流式文本对话。
     *
     * @param userMessage user 消息
     * @return 流式响应
     */
    default ChatStream streamChat(String userMessage) {
        return streamChat(ChatRequest.user(userMessage));
    }
}
