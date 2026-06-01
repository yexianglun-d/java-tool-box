package com.undernine.utils.ai;

import java.util.Optional;
import java.util.Set;

/**
 * 按名称管理多个 AI client 的注册表。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.3
 */
public interface AiClientRegistry {

    /**
     * 默认客户端名称。
     *
     * @return 默认客户端名称
     */
    String getDefaultName();

    /**
     * 默认 AI client。
     *
     * @return 默认 AI client
     */
    default AiClient getDefaultClient() {
        return get(getDefaultName());
    }

    /**
     * 按名称获取 AI client。
     *
     * @param name 客户端名称
     * @return AI client
     * @throws java.util.NoSuchElementException 客户端不存在时抛出
     */
    AiClient get(String name);

    /**
     * 按名称查找 AI client。
     *
     * @param name 客户端名称
     * @return AI client
     */
    Optional<AiClient> find(String name);

    /**
     * 是否存在指定名称的 AI client。
     *
     * @param name 客户端名称
     * @return true 表示存在
     */
    default boolean contains(String name) {
        return find(name).isPresent();
    }

    /**
     * 已注册的客户端名称。
     *
     * @return 客户端名称集合
     */
    Set<String> names();

    /**
     * 按名称获取支持流式对话的 AI client。
     *
     * @param name 客户端名称
     * @return 支持流式对话的 AI client
     * @throws java.util.NoSuchElementException 客户端不存在时抛出
     * @throws IllegalStateException 客户端不支持流式对话时抛出
     */
    default StreamingAiClient getStreaming(String name) {
        AiClient client = get(name);
        if (client instanceof StreamingAiClient streamingAiClient) {
            return streamingAiClient;
        }
        throw new IllegalStateException("AI client '" + normalizeName(name) + "' does not support streaming");
    }

    /**
     * 获取默认的流式 AI client。
     *
     * @return 支持流式对话的默认 AI client
     */
    default StreamingAiClient getDefaultStreaming() {
        return getStreaming(getDefaultName());
    }

    /**
     * 规范化客户端名称。
     *
     * @param name 客户端名称
     * @return 规范化后的客户端名称
     */
    static String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("AI client name must not be blank");
        }
        return name.trim();
    }
}
