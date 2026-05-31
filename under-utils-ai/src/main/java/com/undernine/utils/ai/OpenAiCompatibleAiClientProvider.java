package com.undernine.utils.ai;

/**
 * OpenAI-compatible AI client provider。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public final class OpenAiCompatibleAiClientProvider implements AiClientProvider {

    @Override
    public String provider() {
        return AiProviderNames.OPENAI_COMPATIBLE;
    }

    @Override
    public AiClient create(AiClientOptions options) {
        return new OpenAiCompatibleAiClient(options);
    }
}
