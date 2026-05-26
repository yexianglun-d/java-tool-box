package com.undernine.utils.samples.ai;

import com.undernine.utils.ai.AiClient;
import com.undernine.utils.ai.ChatRequest;
import com.undernine.utils.ai.ChatResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/samples/ai")
public class AiSampleController {

    private final ObjectProvider<AiClient> aiClientProvider;

    public AiSampleController(ObjectProvider<AiClient> aiClientProvider) {
        this.aiClientProvider = aiClientProvider;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of("enabled", aiClientProvider.getIfAvailable() != null);
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
        ChatRequest.Builder request = ChatRequest.builder();
        if (systemPrompt != null) {
            request.system(systemPrompt);
        }
        request.user(prompt);
        ChatResponse response = aiClient.chat(request.build());
        return new ChatSampleResponse(
                response.text(),
                response.getModel(),
                response.getFinishReason(),
                response.getUsage() == null ? null : response.getUsage().getTotalTokens()
        );
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

    public record ChatSampleResponse(String text, String model, String finishReason, Integer totalTokens) {
    }
}
