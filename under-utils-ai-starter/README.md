# Under-Utils AI Starter

Spring Boot 自动装配入口，用于按配置创建默认 `AiClient`。

本 starter 只依赖 `under-utils-ai` 和 Spring Boot autoconfigure，不会被 `under-utils-starter` 聚合引入。需要 AI 能力的项目应显式引入该坐标。

## 依赖

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-ai-starter</artifactId>
    <version>1.0.2-SNAPSHOT</version>
</dependency>
```

## 配置

默认不会创建 `AiClient`，需要显式启用：

```yaml
under:
  utils:
    ai:
      enabled: true
      provider: openai-compatible
      base-url: https://api.example.com/v1
      api-key: ${AI_API_KEY}
      model: your-model-name
      timeout: 30s
      max-retries: 0
      retry-interval: 0ms
```

使用：

```java
@Service
public class SummaryService {

    private final AiClient aiClient;

    public SummaryService(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    public String summarize(String content) {
        return aiClient.chat(ChatRequest.user("请总结：" + content)).text();
    }
}
```

## 自动装配规则

- `under.utils.ai.enabled=true` 时才创建默认 `AiClient`。
- 默认内置 `openai-compatible` provider。
- 如需非兼容协议，可声明自定义 `AiClientProvider` Bean，并将 `under.utils.ai.provider` 设置为该 provider 名称。
- 用户自定义 `AiClient` Bean 时，自动装配会退让。
- `api-key` 为空时不会发送 Authorization header，适合本地兼容服务。

## 流式响应

默认创建的 `OpenAiCompatibleAiClient` 同时实现 `StreamingAiClient`，可用于 SSE 流式输出：

```java
if (aiClient instanceof StreamingAiClient streamingAiClient) {
    try (ChatStream stream = streamingAiClient.streamChat(ChatRequest.user("请逐步输出"))) {
        stream.stream()
                .filter(ChatStreamEvent::hasText)
                .forEach(event -> System.out.print(event.text()));
    }
}
```
