package com.undernine.utils.samples.ai;

import com.undernine.utils.ai.AiException;
import com.undernine.utils.ai.AiClient;
import com.undernine.utils.ai.ChatStream;
import com.undernine.utils.ai.ChatStreamEvent;
import com.undernine.utils.ai.ChatRequest;
import com.undernine.utils.ai.ChatResponse;
import com.undernine.utils.ai.StreamingAiClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/samples/ai")
public class AiSampleController {

    private final ObjectProvider<AiClient> aiClientProvider;

    public AiSampleController(ObjectProvider<AiClient> aiClientProvider) {
        this.aiClientProvider = aiClientProvider;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        AiClient aiClient = aiClientProvider.getIfAvailable();
        return Map.of(
                "enabled", aiClient != null,
                "streaming", aiClient instanceof StreamingAiClient
        );
    }

    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    public ChatSampleResponse chat(@RequestBody ChatSampleCommand command) {
        AiClient aiClient = aiClientProvider.getIfAvailable();
        if (aiClient == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI client is not enabled");
        }
        String prompt = requireText(command == null ? null : command.prompt(), "prompt");
        String systemPrompt = trimToNull(command == null ? null : command.systemPrompt());
        ChatResponse response = aiClient.chat(buildRequest(prompt, systemPrompt));
        return new ChatSampleResponse(
                response.text(),
                response.getModel(),
                response.getFinishReason(),
                response.getRequestId(),
                response.getResponseId(),
                response.getModelFingerprint(),
                response.getDuration() == null ? null : response.getDuration().toMillis(),
                response.getUsage() == null ? null : response.getUsage().getTotalTokens()
        );
    }

    @PostMapping("/chat/stream")
    public SseEmitter streamChat(@RequestBody ChatSampleCommand command) {
        AiClient aiClient = aiClientProvider.getIfAvailable();
        if (aiClient == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI client is not enabled");
        }
        if (!(aiClient instanceof StreamingAiClient streamingAiClient)) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "AI client does not support streaming");
        }
        String prompt = requireText(command == null ? null : command.prompt(), "prompt");
        String systemPrompt = trimToNull(command == null ? null : command.systemPrompt());
        ChatRequest request = buildRequest(prompt, systemPrompt);
        SseEmitter emitter = new SseEmitter(0L);
        CompletableFuture.runAsync(() -> streamToEmitter(streamingAiClient, request, emitter));
        return emitter;
    }

    private void streamToEmitter(StreamingAiClient aiClient, ChatRequest request, SseEmitter emitter) {
        try (ChatStream stream = aiClient.streamChat(request)) {
            for (ChatStreamEvent event : stream) {
                emitter.send(SseEmitter.event()
                        .name(event.isDone() ? "done" : "delta")
                        .data(new ChatStreamSampleEvent(
                                event.text(),
                                event.isDone(),
                                event.getModel(),
                                event.getFinishReason(),
                                event.getResponseId(),
                                event.getModelFingerprint(),
                                event.getUsage() == null ? null : event.getUsage().getTotalTokens()
                        )));
            }
            emitter.complete();
        } catch (AiException e) {
            emitter.completeWithError(new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "AI stream failed: " + e.getErrorType(), e));
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    private ChatRequest buildRequest(String prompt, String systemPrompt) {
        ChatRequest.Builder request = ChatRequest.builder();
        if (systemPrompt != null) {
            request.system(systemPrompt);
        }
        return request.user(prompt).build();
    }

    private static String requireText(String value, String fieldName) {
        String text = trimToNull(value);
        if (text == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must not be blank");
        }
        return text;
    }

    private static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    public record ChatSampleCommand(String prompt, String systemPrompt) {
    }

    public record ChatSampleResponse(String text, String model, String finishReason, String requestId,
                                     String responseId, String modelFingerprint, Long durationMillis,
                                     Integer totalTokens) {
    }

    public record ChatStreamSampleEvent(String text, boolean done, String model, String finishReason,
                                        String responseId, String modelFingerprint, Integer totalTokens) {
    }
}
