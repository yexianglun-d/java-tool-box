# Under-Utils AI

AI 大模型基础调用封装模块，第一阶段只提供 OpenAI-compatible 文本对话客户端。

本模块目标是让业务项目只配置模型服务的基础参数，就能完成最常见的同步文本对话调用；它不是 Agent 框架，也不覆盖流式响应、工具调用、RAG、向量数据库或厂商私有全部参数。

## 依赖

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-ai</artifactId>
    <version>1.0.2-SNAPSHOT</version>
</dependency>
```

## 快速使用

```java
AiClient aiClient = AiClient.builder()
        .baseUrl("https://api.example.com/v1")
        .apiKey(apiKey)
        .model("your-model-name")
        .timeout(Duration.ofSeconds(30))
        .build();

ChatResponse response = aiClient.chat(ChatRequest.user("请总结这段文本"));
String text = response.text();
```

构建器会调用 `{baseUrl}/chat/completions`，请求体使用 OpenAI-compatible Chat Completions 结构：

```json
{
  "model": "your-model-name",
  "messages": [
    {
      "role": "user",
      "content": "请总结这段文本"
    }
  ]
}
```

## 多轮消息

```java
ChatResponse response = aiClient.chat(ChatRequest.builder()
        .system("你是一个简洁的助手")
        .user("请把下面内容压缩成三句话")
        .assistant("请提供原文")
        .user(content)
        .temperature(0.2D)
        .maxTokens(512)
        .requestId(requestId)
        .build());
```

`ChatRequest` 可以按请求覆盖默认模型，也可以通过 `extraBody` 透传少量兼容参数：

```java
ChatRequest request = ChatRequest.builder()
        .user("ping")
        .model("another-model")
        .extraBody("top_p", 0.9D)
        .build();
```

## 错误语义

模型调用失败统一抛出 `AiException`：

| 分类 | 触发场景 |
|------|----------|
| `AUTHENTICATION` | HTTP 401/403。 |
| `RATE_LIMIT` | HTTP 429。 |
| `SERVER_ERROR` | HTTP 5xx。 |
| `CLIENT_ERROR` | 其他 HTTP 4xx。 |
| `TIMEOUT` | 连接、读取或写入超时。 |
| `NETWORK` | 网络连接失败。 |
| `RESPONSE_PARSE` | 成功响应无法解析，或缺少 assistant 文本。 |

`AiException` 暴露 `statusCode`、`errorCode` 和 `retryable`，调用方可以据此决定重试或降级。异常信息不会包含 API key、Authorization header 或完整请求体。

## 安全边界

- `AiClientOptions.toString()` 会隐藏 API key，只显示是否已配置。
- `ChatRequest.toString()` 不输出完整 prompt，只输出消息数量和参数摘要。
- `ChatResponse.toString()` 不输出完整模型回复，只输出文本长度和元数据。
- 默认测试使用 `MockWebServer`，不会访问外网。

## 当前限制

- 只支持同步文本对话调用。
- 不支持流式响应。
- 不封装工具调用、函数调用、Agent 工作流和多步骤推理。
- 不提供 Spring Boot starter；如需自动装配，应在核心 API 稳定后进入独立 `under-utils-ai-starter`。
