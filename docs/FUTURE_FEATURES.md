# 后续功能孵化

本文件用于记录尚未进入实现的新功能想法。它的目标是先把“为什么做、做到什么边界、什么不做”说清楚，再决定是否进入代码实现。

## 使用方式

- 新想法先进入本文件，不直接新增模块或 public API。
- 进入实现前，必须明确模块归属、依赖边界、配置项、失败语义、测试计划和兼容性影响。
- 已进入工程成熟度推进的事项，放到 [ENGINEERING_MATURITY.md](ENGINEERING_MATURITY.md)，不要在两个文件重复维护。

## 状态定义

| 状态 | 含义 |
|------|------|
| `想法` | 只有场景和方向，尚未确定是否适合进入项目。 |
| `候选` | 已确认与项目边界基本匹配，等待设计。 |
| `设计中` | 正在明确 API、依赖、配置、失败语义和验收标准。 |
| `实现中` | 已开始代码实现。 |
| `已发布` | 已进入正式版本并完成文档、测试和变更记录。 |
| `拒绝` | 不符合项目边界，或已有成熟库更适合承载。 |

## 功能准入标准

新功能进入实现前，应同时满足：

- 解决跨项目重复出现的工程问题，而不是单个业务应用的流程。
- 不只是对 JDK、Spring、Hutool、Apache Commons、Guava 或成熟生态库的低价值转包。
- 有明确的 public API、配置 key、失败语义和资源释放边界。
- 单元测试可以离线运行；涉及外部系统的测试必须可通过 mock server、Testcontainers 或独立 profile 复现。
- 依赖重量与模块定位匹配，不让轻量模块被动引入大依赖。
- 可以在 `1.x` 版本内保持源码兼容，或者明确作为 minor/major 能力规划。

## F-001 AI 大模型基础调用封装

状态：`实现中`

### 背景

后续可能需要提供一个面向 Java/Spring 项目的 AI 大模型基础封装：调用方只配置模型服务的基本参数，就能完成最常见的文本对话调用。

这个能力的重点不是做完整 Agent 框架，而是收敛不同业务项目重复编写的模型调用基础代码，例如 base URL、API key、model、timeout、retry、错误处理和日志脱敏。

### 目标

- 通过最少配置创建可用的 AI client。
- 支持 OpenAI-compatible HTTP API 作为第一阶段协议目标。
- 提供同步文本对话调用，优先覆盖最常见的 user/system/assistant message 场景。
- 统一超时、重试、错误响应解析、trace header 和敏感信息脱敏。
- core API 不强依赖 Spring；Spring Boot 自动装配放入独立 starter 或配置层。

### 非目标

- 不做模型训练、微调、数据标注或计费系统。
- 不做完整 Agent、工作流编排、工具调用市场或多步骤推理框架。
- 不内置向量数据库、RAG 检索链路或知识库管理。
- 不承诺覆盖所有模型厂商的全部私有参数。
- 不在日志、异常或 debug 输出中暴露 API key、Authorization header 或完整敏感 prompt。

### 候选模块

| 模块 | 说明 |
|------|------|
| `under-utils-ai` | 模型调用抽象、请求响应模型、错误类型、OpenAI-compatible 执行器。 |
| `under-utils-ai-starter` | Spring Boot 自动装配、配置属性和默认 `AiClient` Bean。 |

### 候选 API 草案

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

Spring Boot 配置草案：

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
      retry:
        max-attempts: 2
        backoff: 500ms
```

### 候选核心类型

| 类型 | 说明 |
|------|------|
| `AiClient` | 对外调用入口，提供 `chat` 等基础方法。 |
| `AiClientOptions` | base URL、API key、model、timeout、retry、headers 等配置。 |
| `ChatRequest` | message、temperature、max tokens、request id 等请求参数。 |
| `ChatMessage` | system/user/assistant 等角色消息。 |
| `ChatResponse` | 文本、模型名、token 用量、原始 request id 等响应信息。 |
| `AiException` | 统一模型调用异常，区分认证失败、限流、超时、服务端错误和响应解析失败。 |

### 第一阶段验收标准

- 只配置 base URL、API key 和 model 就能完成一次文本对话调用。
- API key 不会出现在日志、异常消息或 `toString()` 输出中。
- 单元测试通过 mock HTTP server 覆盖成功响应、401/429/5xx、超时和响应解析失败。
- 默认测试不访问外网。
- 文档提供 Java builder 和 Spring Boot YAML 两种用法。
- 依赖边界经过评估，不能让现有轻量模块被动引入 AI 或 HTTP 大依赖。
- 与 `under-utils-http` 的复用关系明确：可以复用执行器能力，但不能让 AI API 被 HTTP 内部模型绑死。

### 第一阶段实现记录

- 已新增 `under-utils-ai` 核心模块，提供 `AiClient`、`AiClientOptions`、`ChatRequest`、`ChatMessage`、`ChatResponse`、`TokenUsage`、`AiException` 和 `OpenAiCompatibleAiClient`。
- 第一阶段只实现同步文本对话，协议目标为 OpenAI-compatible Chat Completions。
- 复用 `under-utils-http` 的 `HttpRequest`、`HttpConfig` 和 `HttpResponse`，但 AI 模块对外不暴露 HTTP 内部请求/响应模型。
- 已用 `MockWebServer` 覆盖成功响应、认证失败、限流、服务端错误、超时、响应解析失败和敏感信息不进入 `toString()`。
- 已新增独立 `under-utils-ai-starter`，在 `under.utils.ai.enabled=true` 时按配置创建默认 `AiClient`；它不加入 `under-utils-starter` 聚合入口，避免普通 Spring/Redis 用户被动引入 AI 依赖。

### 待确认问题

- 第一阶段是否只支持 OpenAI-compatible 协议，还是同时支持国内模型厂商的原生 API。
- 是否需要流式响应；如果需要，应作为第二阶段能力，避免第一版 API 过重。
- 是否基于现有 `under-utils-http` 执行器实现，还是单独维护更贴合 AI 协议的 executor。
- 是否需要暴露 token 用量、模型指纹、请求 ID 等元数据。
- starter 默认是否创建 `AiClient` Bean，还是要求用户显式开启。

## 新功能记录模板

```markdown
## F-XXX 功能名称

状态：`想法`

### 背景

说明重复场景和当前痛点。

### 目标

- 目标 1
- 目标 2

### 非目标

- 不做什么

### 候选模块

说明模块归属和依赖边界。

### 候选 API 草案

给出最小可读示例。

### 第一阶段验收标准

- 可测试标准
- 文档标准
- 兼容性标准

### 待确认问题

- 尚未决定的问题
```
