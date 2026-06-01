package com.undernine.utils.ai;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultAiClientRegistryTest {

    @Test
    void shouldResolveDefaultAndNamedClients() {
        AiClient defaultClient = request -> null;
        AiClient qwenClient = request -> null;
        Map<String, AiClient> clients = new LinkedHashMap<>();
        clients.put("default", defaultClient);
        clients.put("qwen", qwenClient);

        DefaultAiClientRegistry registry = new DefaultAiClientRegistry(clients, "default");

        assertThat(registry.getDefaultName()).isEqualTo("default");
        assertThat(registry.getDefaultClient()).isSameAs(defaultClient);
        assertThat(registry.get("qwen")).isSameAs(qwenClient);
        assertThat(registry.find("qwen")).containsSame(qwenClient);
        assertThat(registry.contains("missing")).isFalse();
        assertThat(registry.names()).containsExactly("default", "qwen");
    }

    @Test
    void shouldRejectMissingDefaultClient() {
        Map<String, AiClient> clients = Map.of("deepseek", request -> null);

        assertThatThrownBy(() -> new DefaultAiClientRegistry(clients, "default"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("default AI client does not exist");
    }

    @Test
    void shouldFailWhenNamedClientMissing() {
        DefaultAiClientRegistry registry = new DefaultAiClientRegistry(Map.of("default", request -> null), "default");

        assertThatThrownBy(() -> registry.get("qwen"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("qwen");
    }

    @Test
    void shouldExposeStreamingClientOnlyWhenSupported() {
        StreamingClient streamingClient = new StreamingClient();
        Map<String, AiClient> clients = new LinkedHashMap<>();
        clients.put("default", request -> null);
        clients.put("stream", streamingClient);
        DefaultAiClientRegistry registry = new DefaultAiClientRegistry(clients, "stream");

        assertThat(registry.getDefaultStreaming()).isSameAs(streamingClient);
        assertThat(registry.getStreaming("stream")).isSameAs(streamingClient);
        assertThatThrownBy(() -> registry.getStreaming("default"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("does not support streaming");
    }

    private static final class StreamingClient implements AiClient, StreamingAiClient {

        @Override
        public ChatResponse chat(ChatRequest request) {
            return null;
        }

        @Override
        public ChatStream streamChat(ChatRequest request) {
            return null;
        }
    }
}
