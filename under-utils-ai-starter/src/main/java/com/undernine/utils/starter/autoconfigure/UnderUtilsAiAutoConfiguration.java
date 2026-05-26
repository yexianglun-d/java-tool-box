package com.undernine.utils.starter.autoconfigure;

import com.undernine.utils.ai.AiClient;
import com.undernine.utils.ai.AiClientOptions;
import com.undernine.utils.ai.OpenAiCompatibleAiClient;
import com.undernine.utils.starter.properties.UnderUtilsAiProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Locale;

/**
 * AI 模块自动装配。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
@AutoConfiguration
@ConditionalOnClass(AiClient.class)
@EnableConfigurationProperties(UnderUtilsAiProperties.class)
public class UnderUtilsAiAutoConfiguration {

    private static final String OPENAI_COMPATIBLE = "openai-compatible";

    /**
     * 创建默认 AI client。
     *
     * @param properties AI 配置
     * @return AI client
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "under.utils.ai", name = "enabled", havingValue = "true")
    public AiClient aiClient(UnderUtilsAiProperties properties) {
        String provider = normalizeProvider(properties.getProvider());
        if (!OPENAI_COMPATIBLE.equals(provider)) {
            throw new IllegalArgumentException("unsupported AI provider: " + properties.getProvider());
        }
        return new OpenAiCompatibleAiClient(AiClientOptions.builder()
                .baseUrl(properties.getBaseUrl())
                .apiKey(properties.getApiKey())
                .model(properties.getModel())
                .chatCompletionsPath(properties.getChatCompletionsPath())
                .timeout(properties.getTimeout())
                .maxRetries(properties.getMaxRetries())
                .retryInterval(properties.getRetryInterval())
                .temperature(properties.getTemperature())
                .maxTokens(properties.getMaxTokens())
                .headers(properties.getHeaders())
                .build());
    }

    private String normalizeProvider(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            return OPENAI_COMPATIBLE;
        }
        return provider.trim().toLowerCase(Locale.ROOT);
    }
}
