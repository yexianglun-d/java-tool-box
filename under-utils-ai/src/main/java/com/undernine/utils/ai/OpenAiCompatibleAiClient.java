package com.undernine.utils.ai;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.undernine.utils.core.json.JsonException;
import com.undernine.utils.http.config.HttpConfig;
import com.undernine.utils.http.exception.HttpException;
import com.undernine.utils.http.exception.HttpNetworkException;
import com.undernine.utils.http.exception.HttpTimeoutException;
import com.undernine.utils.http.request.HttpRequest;
import com.undernine.utils.http.response.HttpResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * OpenAI-compatible Chat Completions 客户端。
 * <p>
 * 当前实现只覆盖同步文本对话调用，不处理流式响应、工具调用和厂商私有完整参数。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public class OpenAiCompatibleAiClient implements AiClient {

    private final AiClientOptions options;
    private final HttpConfig httpConfig;

    /**
     * 创建 OpenAI-compatible AI client。
     *
     * @param options 客户端配置
     */
    public OpenAiCompatibleAiClient(AiClientOptions options) {
        this.options = Objects.requireNonNull(options, "options must not be null");
        this.httpConfig = buildHttpConfig(options);
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        ChatRequest chatRequest = Objects.requireNonNull(request, "request must not be null");
        HttpResponse response = execute(chatRequest);
        if (response.isFail()) {
            throw toStatusException(response);
        }
        return parseSuccess(response, chatRequest.getRequestId());
    }

    /**
     * 客户端配置。
     *
     * @return 客户端配置
     */
    public AiClientOptions getOptions() {
        return options;
    }

    private HttpResponse execute(ChatRequest request) {
        try {
            HttpRequest.Builder builder = HttpRequest.post(options.chatCompletionsUrl())
                    .config(httpConfig)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(toRequestBody(request));
            if (options.getApiKey() != null) {
                builder.header("Authorization", "Bearer " + options.getApiKey());
            }
            if (request.getRequestId() != null) {
                builder.header("X-Request-Id", request.getRequestId());
            }
            options.getHeaders().forEach(builder::header);
            return builder.execute();
        } catch (HttpTimeoutException e) {
            throw new AiException(AiErrorType.TIMEOUT, "AI request timed out", 0, null, true, e);
        } catch (HttpNetworkException e) {
            throw new AiException(AiErrorType.NETWORK, "AI request failed because of network error", 0, null, true, e);
        } catch (HttpException e) {
            throw new AiException(AiErrorType.UNKNOWN, "AI request failed", 0, null, false, e);
        }
    }

    private Map<String, Object> toRequestBody(ChatRequest request) {
        Map<String, Object> body = new LinkedHashMap<>(request.getExtraBody());
        body.put("model", request.getModel() == null ? options.getModel() : request.getModel());
        body.put("messages", toMessages(request.getMessages()));
        Double temperature = request.getTemperature() == null ? options.getTemperature() : request.getTemperature();
        Integer maxTokens = request.getMaxTokens() == null ? options.getMaxTokens() : request.getMaxTokens();
        if (temperature != null) {
            body.put("temperature", temperature);
        }
        if (maxTokens != null) {
            body.put("max_tokens", maxTokens);
        }
        return body;
    }

    private List<Map<String, String>> toMessages(List<ChatMessage> messages) {
        List<Map<String, String>> result = new ArrayList<>(messages.size());
        for (ChatMessage message : messages) {
            Map<String, String> item = new LinkedHashMap<>();
            item.put("role", message.getRole().wireName());
            item.put("content", message.getContent());
            result.add(item);
        }
        return result;
    }

    private ChatResponse parseSuccess(HttpResponse response, String fallbackRequestId) {
        ChatCompletionResponse result;
        try {
            result = response.asObject(ChatCompletionResponse.class);
        } catch (JsonException e) {
            throw new AiException(AiErrorType.RESPONSE_PARSE, "Failed to parse AI response", response.getStatusCode(),
                    null, false, e);
        }
        if (result == null || result.choices == null || result.choices.isEmpty()
                || result.choices.get(0).message == null) {
            throw new AiException(AiErrorType.RESPONSE_PARSE, "AI response does not contain assistant message",
                    response.getStatusCode(), null, false);
        }
        ChatChoice choice = result.choices.get(0);
        String text = choice.message.content;
        if (text == null) {
            throw new AiException(AiErrorType.RESPONSE_PARSE, "AI response assistant message content is missing",
                    response.getStatusCode(), null, false);
        }
        TokenUsage usage = null;
        if (result.usage != null) {
            usage = new TokenUsage(result.usage.promptTokens, result.usage.completionTokens, result.usage.totalTokens);
        }
        return new ChatResponse(text, result.model, choice.finishReason,
                result.id == null ? fallbackRequestId : result.id, usage);
    }

    private AiException toStatusException(HttpResponse response) {
        AiStatusError error = parseStatusError(response);
        AiErrorType type = classify(response.getStatusCode());
        boolean retryable = response.getStatusCode() == 429 || response.getStatusCode() >= 500;
        String code = error == null ? null : error.code;
        String message = "AI request failed with HTTP status " + response.getStatusCode();
        if (code != null && !code.trim().isEmpty()) {
            message += ", errorCode=" + code;
        }
        return new AiException(type, message, response.getStatusCode(), code, retryable);
    }

    private AiStatusError parseStatusError(HttpResponse response) {
        try {
            ErrorResponse body = response.asObject(ErrorResponse.class);
            return body == null ? null : body.error;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private AiErrorType classify(int statusCode) {
        if (statusCode == 401 || statusCode == 403) {
            return AiErrorType.AUTHENTICATION;
        }
        if (statusCode == 429) {
            return AiErrorType.RATE_LIMIT;
        }
        if (statusCode >= 500) {
            return AiErrorType.SERVER_ERROR;
        }
        if (statusCode >= 400) {
            return AiErrorType.CLIENT_ERROR;
        }
        return AiErrorType.UNKNOWN;
    }

    private HttpConfig buildHttpConfig(AiClientOptions options) {
        Duration timeout = options.getTimeout();
        return HttpConfig.builder()
                .connectTimeoutDuration(timeout)
                .readTimeoutDuration(timeout)
                .writeTimeoutDuration(timeout)
                .maxRetries(options.getMaxRetries())
                .retryIntervalDuration(options.getRetryInterval())
                .build();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static final class ErrorResponse {
        private AiStatusError error;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static final class AiStatusError {
        private String message;
        private String type;
        private String code;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static final class ChatCompletionResponse {
        private String id;
        private String model;
        private List<ChatChoice> choices;
        private Usage usage;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static final class ChatChoice {
        private ChatCompletionMessage message;
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static final class ChatCompletionMessage {
        private String role;
        private String content;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static final class Usage {
        @JsonProperty("prompt_tokens")
        private int promptTokens;
        @JsonProperty("completion_tokens")
        private int completionTokens;
        @JsonProperty("total_tokens")
        private int totalTokens;
    }
}
