# API Review

本文件记录 Under-Utils 发布前 public API 审计结论，目标是在首个稳定 Release 前收敛命名、配置 key、异常语义和模块边界。

## 审计原则

- 优先稳定发布后难以修改的入口：包名、类名、方法签名、配置属性、注解属性和异常类型。
- 不把低复杂度通用工具方法作为新增方向，历史 core 工具只做兼容维护。
- starter 默认行为必须可解释，配置 key 一经发布尽量不破坏。
- 对存在安全边界的 API，默认路径应使用白名单、显式配置或封装模板。

## 第一轮结论

### HTTP

- 将底层 OkHttp 执行入口从 `OkHttpClient` 收敛为 `OkHttpRequestExecutor`，避免与 `okhttp3.OkHttpClient` 同名并误导使用者。
- `HttpRequest` 继续作为兼容的便捷请求模型，OpenAPI 治理能力仍以 `OpenApiClient`、`OpenApiRequest`、`OpenApiResponse` 为主线。

### Redis

- `LogicalExpireCacheOptions` 明确校验 `physicalTtl > logicalTtl`，避免逻辑过期缓存失去旧值兜底窗口。
- `LogicalExpireCacheTemplate.LogicalCachePayload` 收回为包内实现细节，不作为 public API 暴露。

### MyBatis

- `SafePageQuery` + `SortFieldMapping` 是对外推荐分页排序入口。
- `PageQuery` 直接接收数据库列名，已标记为不推荐 API，只适合内部可信代码，不建议暴露为 Web 入参。

### Starter

- `under.utils.*` 配置前缀保持稳定。
- Redis cache-aside 与 logical-cache 使用独立开关，共享 `CacheValueCodec`。
- GPG 签名、release profile 与 Testcontainers 集成测试已进入发布准备链路。

## 第二轮结论

### Spring Legacy AOP

- `OperationLog`、`Retry`、`TimeLog` 保留为兼容维护 API，并标记为 `@Deprecated`。
- `OperationLogAspect`、`RetryAspect`、`TimeLogAspect` 不再声明为 Spring `@Component`，避免直接扫描 `com.undernine.utils.spring` 时意外启用历史切面；仍需兼容时应显式 `@Import` 或声明为 `@Bean`。
- `OperationLog.recordParams` 默认值改为 `false`，降低误记录请求参数和敏感信息的风险；兼容旧行为需要显式设置 `recordParams = true`。
- `RetryAspect` 仍保留同步 sleep 语义，但会规整非法 `maxAttempts`、`delay` 和空异常列表，避免配置异常造成不可预期循环或等待。
- 新项目的审计、重试和耗时观测不再以轻量历史切面为主线，应优先使用业务统一审计、专用客户端治理、Micrometer 或 OpenTelemetry。

## 第三轮结论

### Core Historical Helpers

- `under-utils-core` 不再按“基础工具大全”表达，README 与包级 Javadoc 改为强调低耦合基础能力和历史工具兼容维护。
- `StringUtils`、`CollectionUtils`、`LocalDateTimeUtils`、`ValidationUtils`、`UUIDUtils`、`JsonUtils`、`MD5Utils`、`SHA256Utils`、`AESUtils` 标记为 `@Deprecated` 兼容 API，保留调用但不再扩展。
- `IdGenerator` 与 `MoneyUtils` 暂作为 core 主线保留能力：前者承担本地趋势递增 ID 生成，后者固化 BigDecimal 金额计算与分/元转换语义。
- `JsonUtils` 明确为历史兼容入口；复杂应用应注入业务自己的 `ObjectMapper` 或 codec，`getObjectMapper()` 标记为不推荐。
- `AESUtils.encryptECB` 与 `AESUtils.decryptECB` 标记为不推荐，ECB 仅保留历史兼容；MD5/SHA-256/AES 文档明确不作为推荐安全治理入口。
- 移除 `under-utils-core` POM 中未使用的 Lombok、SLF4J、Apache Commons Lang、Guava 和 Bouncy Castle 可选依赖，避免模块边界和开源定位被误读。

## 后续待审

- 为关键注解属性补充更明确的失败语义和集群环境说明。
- Maven Central namespace、正式 deploy 仓库和 GPG 密钥托管仍需单独确认。
