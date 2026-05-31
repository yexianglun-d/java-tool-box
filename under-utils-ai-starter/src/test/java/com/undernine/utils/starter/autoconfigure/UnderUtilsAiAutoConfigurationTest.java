package com.undernine.utils.starter.autoconfigure;

import com.undernine.utils.ai.AiClient;
import com.undernine.utils.ai.AiClientOptions;
import com.undernine.utils.ai.AiClientProvider;
import com.undernine.utils.ai.ChatRequest;
import com.undernine.utils.ai.ChatResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class UnderUtilsAiAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(UnderUtilsAiAutoConfiguration.class));

    @Test
    void shouldNotCreateAiClientByDefault() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(AiClient.class));
    }

    @Test
    void shouldCreateOpenAiCompatibleClientWhenEnabled() throws IOException, InterruptedException {
        try (MockWebServer server = new MockWebServer()) {
            server.start();
            server.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody("""
                            {
                              "id": "chatcmpl-starter",
                              "model": "starter-model",
                              "choices": [
                                {
                                  "message": {
                                    "role": "assistant",
                                    "content": "starter-ok"
                                  }
                                }
                              ]
                            }
                            """));

            contextRunner
                    .withPropertyValues(
                            "under.utils.ai.enabled=true",
                            "under.utils.ai.base-url=" + server.url("/v1"),
                            "under.utils.ai.api-key=starter-secret",
                            "under.utils.ai.model=starter-model",
                            "under.utils.ai.timeout=2s",
                            "under.utils.ai.max-retries=0",
                            "under.utils.ai.headers.X-App-Id=demo"
                    )
                    .run(context -> {
                        assertThat(context).hasSingleBean(AiClient.class);
                        String text = context.getBean(AiClient.class)
                                .chat(ChatRequest.user("hello"))
                                .text();
                        assertThat(text).isEqualTo("starter-ok");
                    });

            RecordedRequest request = server.takeRequest();
            assertThat(request.getPath()).isEqualTo("/v1/chat/completions");
            assertThat(request.getHeader("Authorization")).isEqualTo("Bearer starter-secret");
            assertThat(request.getHeader("X-App-Id")).isEqualTo("demo");
        }
    }

    @Test
    void shouldBackOffWhenUserProvidesAiClient() {
        AiClient customClient = request -> null;

        contextRunner
                .withBean(AiClient.class, () -> customClient)
                .withPropertyValues(
                        "under.utils.ai.enabled=true",
                        "under.utils.ai.base-url=https://api.example.com/v1",
                        "under.utils.ai.api-key=secret",
                        "under.utils.ai.model=demo-model"
                )
                .run(context -> assertThat(context.getBean(AiClient.class)).isSameAs(customClient));
    }

    @Test
    void shouldUseCustomProviderWhenProviderMatches() {
        AiClientProvider customProvider = new AiClientProvider() {
            @Override
            public String provider() {
                return "native-vendor";
            }

            @Override
            public AiClient create(AiClientOptions options) {
                return request -> new ChatResponse("custom-provider", options.getModel(), "stop", "custom-001", null);
            }
        };

        contextRunner
                .withBean(AiClientProvider.class, () -> customProvider)
                .withPropertyValues(
                        "under.utils.ai.enabled=true",
                        "under.utils.ai.provider=native-vendor",
                        "under.utils.ai.base-url=https://api.example.com/v1",
                        "under.utils.ai.api-key=secret",
                        "under.utils.ai.model=native-model"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(AiClient.class);
                    assertThat(context.getBean(AiClient.class).chat(ChatRequest.user("hello")).text())
                            .isEqualTo("custom-provider");
                });
    }

    @Test
    void shouldFailFastForUnsupportedProvider() {
        contextRunner
                .withPropertyValues(
                        "under.utils.ai.enabled=true",
                        "under.utils.ai.provider=native-vendor",
                        "under.utils.ai.base-url=https://api.example.com/v1",
                        "under.utils.ai.api-key=secret",
                        "under.utils.ai.model=demo-model"
                )
                .run(context -> assertThat(context).hasFailed());
    }
}
