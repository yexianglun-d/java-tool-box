package com.undernine.utils.ai;

import java.util.Objects;

/**
 * 对话消息。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public final class ChatMessage {

    private final ChatRole role;
    private final String content;

    private ChatMessage(ChatRole role, String content) {
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.content = requireText(content, "content");
    }

    /**
     * 创建 system 消息。
     *
     * @param content 消息内容
     * @return system 消息
     */
    public static ChatMessage system(String content) {
        return new ChatMessage(ChatRole.SYSTEM, content);
    }

    /**
     * 创建 user 消息。
     *
     * @param content 消息内容
     * @return user 消息
     */
    public static ChatMessage user(String content) {
        return new ChatMessage(ChatRole.USER, content);
    }

    /**
     * 创建 assistant 消息。
     *
     * @param content 消息内容
     * @return assistant 消息
     */
    public static ChatMessage assistant(String content) {
        return new ChatMessage(ChatRole.ASSISTANT, content);
    }

    /**
     * 消息角色。
     *
     * @return 消息角色
     */
    public ChatRole getRole() {
        return role;
    }

    /**
     * 消息内容。
     *
     * @return 消息内容
     */
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "ChatMessage{"
                + "role=" + role
                + ", contentLength=" + content.length()
                + '}';
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
