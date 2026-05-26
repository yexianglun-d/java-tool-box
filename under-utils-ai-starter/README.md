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
- 当前 `provider` 只支持 `openai-compatible`。
- 用户自定义 `AiClient` Bean 时，自动装配会退让。
- `api-key` 为空时不会发送 Authorization header，适合本地兼容服务。
