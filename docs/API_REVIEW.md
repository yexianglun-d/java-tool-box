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

## 后续待审

- 复核 `under-utils-spring` 中 `OperationLog`、`Retry`、`TimeLog` 等历史 AOP 能力是否保留、迁移或标记为兼容维护。
- 复核 `under-utils-core` 中历史基础工具的 README 表达，避免被误解为 Hutool 式工具集合主线。
- 为关键注解属性补充更明确的失败语义和集群环境说明。
- Maven Central namespace、正式 deploy 仓库和 GPG 密钥托管仍需单独确认。
