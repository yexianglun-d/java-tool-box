package com.undernine.utils.starter.autoconfigure;

import com.undernine.utils.ai.AiClient;
import com.undernine.utils.ai.AiClientOptions;
import com.undernine.utils.ai.AiClientProvider;
import com.undernine.utils.ai.AiProviderNames;
import com.undernine.utils.ai.OpenAiCompatibleAiClient;
import com.undernine.utils.starter.properties.UnderUtilsAiProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

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

    /**
     * 创建默认 AI client。
     *
     * @param properties AI 配置
     * @param aiClientProviders 自定义 AI client provider
     * @return AI client
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "under.utils.ai", name = "enabled", havingValue = "true")
    public AiClient aiClient(UnderUtilsAiProperties properties,
                             ObjectProvider<AiClientProvider> aiClientProviders) {
        String provider = AiClientProvider.normalize(properties.getProvider());
        AiClientOptions options = buildOptions(properties);
        AiClientProvider customProvider = aiClientProviders.stream()
                .filter(candidate -> candidate.supports(provider))
                .findFirst()
                .orElse(null);
        if (customProvider != null) {
            return customProvider.create(options);
        }
        if (AiProviderNames.OPENAI_COMPATIBLE.equals(provider)) {
            return new OpenAiCompatibleAiClient(options);
        }
        throw new IllegalArgumentException("unsupported AI provider: " + properties.getProvider());
    }

    private AiClientOptions buildOptions(UnderUtilsAiProperties properties) {
        return AiClientOptions.builder()
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
                .build();
    }
}
