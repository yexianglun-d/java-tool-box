package com.undernine.utils.ai;

/**
 * 对话消息角色。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public enum ChatRole {

    /**
     * 系统提示词。
     */
    SYSTEM("system"),

    /**
     * 用户消息。
     */
    USER("user"),

    /**
     * 模型回复。
     */
    ASSISTANT("assistant");

    private final String wireName;

    ChatRole(String wireName) {
        this.wireName = wireName;
    }

    /**
     * 协议中的角色名称。
     *
     * @return 协议角色名称
     */
    public String wireName() {
        return wireName;
    }
}
