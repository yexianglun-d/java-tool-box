package com.undernine.utils.ai;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenAiCompatibleAiClientTest {

    private static final String API_KEY = "sk-test-secret";

    private MockWebServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void shouldCallOpenAiCompatibleChatCompletions() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "id": "chatcmpl-001",
                          "model": "demo-model",
                          "choices": [
                            {
                              "message": {
                                "role": "assistant",
                                "content": "你好，Under-Utils"
                              },
                              "finish_reason": "stop"
                            }
                          ],
                          "usage": {
                            "prompt_tokens": 7,
                            "completion_tokens": 5,
                            "total_tokens": 12
                          }
                        }
                        """));
        AiClient client = client();

        ChatResponse response = client.chat(ChatRequest.builder()
                .system("你是一个简洁的助手")
                .user("打个招呼")
                .temperature(0.2D)
                .maxTokens(64)
                .requestId("req-001")
                .build());

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/v1/chat/completions");
        assertThat(request.getHeader("Authorization")).isEqualTo("Bearer " + API_KEY);
        assertThat(request.getHeader("X-Request-Id")).isEqualTo("req-001");
        String requestBody = request.getBody().readUtf8();
        assertThat(requestBody).contains("\"model\":\"demo-model\"");
        assertThat(requestBody).contains("\"role\":\"system\"");
        assertThat(requestBody).contains("\"role\":\"user\"");
        assertThat(requestBody).contains("\"temperature\":0.2");
        assertThat(requestBody).contains("\"max_tokens\":64");
        assertThat(response.text()).isEqualTo("你好，Under-Utils");
        assertThat(response.getModel()).isEqualTo("demo-model");
        assertThat(response.getRequestId()).isEqualTo("chatcmpl-001");
        assertThat(response.getFinishReason()).isEqualTo("stop");
        assertThat(response.getUsage().getTotalTokens()).isEqualTo(12);
    }

    @Test
    void shouldSupportSimpleUserMessageShortcut() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("""
                        {
                          "id": "chatcmpl-002",
                          "model": "demo-model",
                          "choices": [
                            {
                              "message": {
                                "role": "assistant",
                                "content": "done"
                              },
                              "finish_reason": "stop"
                            }
                          ]
                        }
                        """));

        ChatResponse response = client().chat("ping");

        assertThat(response.text()).isEqualTo("done");
    }

    @Test
    void shouldAllowRequestModelOverrideAndExtraBody() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("""
                        {
                          "id": "chatcmpl-003",
                          "model": "other-model",
                          "choices": [
                            {
                              "message": {
                                "role": "assistant",
                                "content": "ok"
                              }
                            }
                          ]
                        }
                        """));

        client().chat(ChatRequest.builder()
                .user("ping")
                .model("other-model")
                .extraBody("top_p", 0.9D)
                .build());

        RecordedRequest request = server.takeRequest();
        String requestBody = request.getBody().readUtf8();
        assertThat(requestBody).contains("\"model\":\"other-model\"");
        assertThat(requestBody).contains("\"top_p\":0.9");
    }

    @Test
    void shouldMapAuthenticationFailureWithoutLeakingApiKey() {
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "error": {
                            "message": "invalid api key",
                            "type": "authentication_error",
                            "code": "invalid_api_key"
                          }
                        }
                        """));

        assertThatThrownBy(() -> client().chat(ChatRequest.user("hello")))
                .isInstanceOfSatisfying(AiException.class, ex -> {
                    assertThat(ex.getErrorType()).isEqualTo(AiErrorType.AUTHENTICATION);
                    assertThat(ex.getStatusCode()).isEqualTo(401);
                    assertThat(ex.getErrorCode()).isEqualTo("invalid_api_key");
                    assertThat(ex.isRetryable()).isFalse();
                    assertThat(ex.getMessage()).doesNotContain(API_KEY);
                    assertThat(ex.getMessage()).doesNotContain("Bearer");
                    assertThat(ex.getMessage()).doesNotContain("hello");
                });
    }

    @Test
    void shouldMapRateLimitAsRetryable() {
        server.enqueue(new MockResponse()
                .setResponseCode(429)
                .setBody("""
                        {
                          "error": {
                            "message": "too many requests",
                            "code": "rate_limit"
                          }
                        }
                        """));

        assertThatThrownBy(() -> client().chat(ChatRequest.user("hello")))
                .isInstanceOfSatisfying(AiException.class, ex -> {
                    assertThat(ex.getErrorType()).isEqualTo(AiErrorType.RATE_LIMIT);
                    assertThat(ex.getStatusCode()).isEqualTo(429);
                    assertThat(ex.isRetryable()).isTrue();
                });
    }

    @Test
    void shouldMapServerErrorAsRetryable() {
        server.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{}"));

        assertThatThrownBy(() -> client().chat(ChatRequest.user("hello")))
                .isInstanceOfSatisfying(AiException.class, ex -> {
                    assertThat(ex.getErrorType()).isEqualTo(AiErrorType.SERVER_ERROR);
                    assertThat(ex.getStatusCode()).isEqualTo(500);
                    assertThat(ex.isRetryable()).isTrue();
                });
    }

    @Test
    void shouldMapInvalidSuccessBodyToParseFailure() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"choices\":[]}"));

        assertThatThrownBy(() -> client().chat(ChatRequest.user("hello")))
                .isInstanceOfSatisfying(AiException.class, ex -> {
                    assertThat(ex.getErrorType()).isEqualTo(AiErrorType.RESPONSE_PARSE);
                    assertThat(ex.getStatusCode()).isEqualTo(200);
                    assertThat(ex.isRetryable()).isFalse();
                    assertThat(ex.getMessage()).doesNotContain("hello");
                });
    }

    @Test
    void shouldMapTimeoutFailure() {
        server.enqueue(new MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE));
        AiClient client = AiClient.builder()
                .baseUrl(server.url("/v1").toString())
                .apiKey(API_KEY)
                .model("demo-model")
                .timeout(Duration.ofMillis(50))
                .build();

        assertThatThrownBy(() -> client.chat(ChatRequest.user("hello")))
                .isInstanceOfSatisfying(AiException.class, ex -> {
                    assertThat(ex.getErrorType()).isEqualTo(AiErrorType.TIMEOUT);
                    assertThat(ex.getStatusCode()).isZero();
                    assertThat(ex.isRetryable()).isTrue();
                });
    }

    @Test
    void shouldHideSensitiveValuesInToString() {
        AiClientOptions options = AiClientOptions.builder()
                .baseUrl("https://api.example.com/v1")
                .apiKey(API_KEY)
                .model("demo-model")
                .header("Authorization", "Bearer other-secret")
                .build();
        ChatRequest request = ChatRequest.user("secret prompt");
        ChatResponse response = new ChatResponse("secret output", "demo-model", "stop", "req-001", null);

        assertThat(options.toString()).doesNotContain(API_KEY);
        assertThat(options.toString()).doesNotContain("other-secret");
        assertThat(request.toString()).doesNotContain("secret prompt");
        assertThat(response.toString()).doesNotContain("secret output");
    }

    @Test
    void shouldRejectEmptyMessages() {
        assertThatThrownBy(() -> ChatRequest.builder().build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("messages must not be empty");
    }

    private AiClient client() {
        return AiClient.builder()
                .baseUrl(server.url("/v1").toString())
                .apiKey(API_KEY)
                .model("demo-model")
                .timeout(Duration.ofSeconds(2))
                .retryInterval(Duration.ZERO)
                .build();
    }
}
