package com.undernine.utils.ai;

import java.util.Locale;

/**
 * AI client provider 扩展点。
 * <p>
 * 业务侧如需接入非 OpenAI-compatible 协议，可以实现本接口并在 Spring 容器中声明 Bean；
 * 核心模块不会默认引入具体厂商 SDK。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public interface AiClientProvider {

    /**
     * provider 名称。
     *
     * @return provider 名称
     */
    String provider();

    /**
     * 当前 provider 是否支持给定名称。
     *
     * @param provider provider 名称
     * @return true 表示支持
     */
    default boolean supports(String provider) {
        return normalize(provider()).equals(normalize(provider));
    }

    /**
     * 创建 AI client。
     *
     * @param options AI client 配置
     * @return AI client
     */
    AiClient create(AiClientOptions options);

    /**
     * 规范化 provider 名称。
     *
     * @param provider provider 名称
     * @return 规范化名称
     */
    static String normalize(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            return AiProviderNames.OPENAI_COMPATIBLE;
        }
        return provider.trim().toLowerCase(Locale.ROOT);
    }
}
