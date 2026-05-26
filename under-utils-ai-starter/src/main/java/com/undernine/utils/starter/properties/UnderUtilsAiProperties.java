package com.undernine.utils.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AI 自动装配配置。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
@ConfigurationProperties(prefix = "under.utils.ai")
public class UnderUtilsAiProperties {

    /**
     * 是否启用默认 AiClient 自动装配。
     */
    private boolean enabled = false;

    /**
     * 协议类型。当前仅支持 openai-compatible。
     */
    private String provider = "openai-compatible";

    /**
     * 模型服务基础地址，例如 https://api.example.com/v1。
     */
    private String baseUrl;

    /**
     * API key。为空时不会发送 Authorization header。
     */
    private String apiKey;

    /**
     * 默认模型名称。
     */
    private String model;

    /**
     * Chat Completions 路径。
     */
    private String chatCompletionsPath = "/chat/completions";

    /**
     * HTTP 超时时间。
     */
    private Duration timeout = Duration.ofSeconds(30);

    /**
     * 最大重试次数。
     */
    private int maxRetries = 0;

    /**
     * 重试间隔。
     */
    private Duration retryInterval = Duration.ZERO;

    /**
     * 默认采样温度。
     */
    private Double temperature;

    /**
     * 默认最大输出 token 数。
     */
    private Integer maxTokens;

    /**
     * 默认请求头。
     */
    private Map<String, String> headers = new LinkedHashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getChatCompletionsPath() {
        return chatCompletionsPath;
    }

    public void setChatCompletionsPath(String chatCompletionsPath) {
        this.chatCompletionsPath = chatCompletionsPath;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Duration getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(Duration retryInterval) {
        this.retryInterval = retryInterval;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers == null ? new LinkedHashMap<>() : new LinkedHashMap<>(headers);
    }
}
