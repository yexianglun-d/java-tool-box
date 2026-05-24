# API Review

本文件记录 Under-Utils public API 审计结论，用于持续收敛命名、配置 key、异常语义和模块边界。

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

## 第四轮结论

### Spring Rate Limit And Repeat Submit

- 明确 `@RateLimit` 失败语义：超过窗口额度时抛出 `BizException`，消息来自 `message`；`limit <= 0` 等价于拒绝所有请求，`period <= 0` 按 1 秒窗口处理。
- 明确 `@PreventRepeat` 失败语义：同一 key 在窗口内重复提交时抛出 `BizException`，消息来自 `message`；`timeout <= 0` 按最小 1ms 处理。
- 明确 `releaseOnFailure` 只影响业务方法抛异常后的 key 释放；方法成功后 key 保持到窗口过期。
- 明确 key 解析语义：空 `key` 使用租户、用户、URI、方法名和参数摘要；SpEL 解析失败不会中断业务，会退回到表达式和参数摘要生成的兜底 key。
- 明确集群语义：local store 只在当前 JVM 内生效；多实例部署必须切换 Redis store 或自定义 `RateLimitStore` / `RepeatSubmitStore`。
- 明确 Redis 失败语义：Redisson 调用异常向外传播，如需 Redis 故障时放行或降级，应由业务自定义 store。

## 第五轮结论

### Maven Central Release

- 接入 Central Publisher Portal 的 Maven 发布链路，新增 `central-publish` profile；默认 `central.skipPublishing=true`，用于本地验证 deploy 生命周期，避免误上传。
- 发布插件固定为 `org.sonatype.central:central-publishing-maven-plugin:0.10.0`，不再引入旧 OSSRH / nexus-staging 发布路径。
- `central-publish` 通过 `excludeArtifacts` 排除 `under-utils-samples`，示例工程继续只参与构建和测试验证。
- 新增 `docs/RELEASE.md`，明确 namespace、Central Portal token、GPG/PGP 签名、手动校验发布和 CI secrets 要求。
- 新增手动触发的 GitHub Actions 发布工作流，默认上传后等待 `validated`，是否自动发布需要人工选择 `published` 模式。

## 第六轮结论

### Maven Coordinates

- Maven `groupId` 从 `com.undernineplaces` 收敛为 GitHub namespace `io.github.yexianglun-d`，避免依赖未持有域名的 DNS namespace 验证。
- Java 包名仍保持 `com.undernine.utils`，本轮只调整 Maven 坐标，不引入包名级破坏性迁移。

## 第七轮结论

### 1.0.0 Release

- `v1.0.0` 已发布到 Maven Central，发布源码 tag 固定在实际上传构件对应提交。
- `main` 分支进入 `1.0.1-SNAPSHOT` 开发周期，避免重复发布 Maven Central 已存在的 `1.0.0` 坐标。
- 发布后文档改为面向 Maven Central 使用，不再要求业务项目先本地 `mvn clean install`。

## 第八轮结论

### 文档与发布后测试覆盖

- README、快速开始、贡献指南、发布指南、路线图和模块 README 改为更接近社区维护文档的写法：少形容词，优先说明边界、安装、运行命令、失败语义和维护约束。
- 为 `under-utils-redis`、`under-utils-starter` 和 `under-utils-biz` 补齐模块 README，避免发布到 Maven Central 后缺少模块级入口说明。
- `under-utils-starter` 自动装配测试覆盖 Redis store 切换、缺少 `RedissonClient` 的失败路径，以及用户自定义 store、lock template、cache options/template 的退让行为。
- `LogicalExpireCacheOptions` 自动配置增加对用户自定义 `LogicalExpireCacheTemplate` 的退让，避免自定义模板场景下继续创建无用默认 options。
- `under-utils-test` 增加 Redis Testcontainers 集成测试，覆盖 cache-aside 命中复用、空值占位缓存和逻辑过期缓存后台刷新。

## 第九轮结论

### 兼容性策略

- 新增 `docs/COMPATIBILITY.md`，明确 `1.0.x`、`1.x.0` 和 `2.0.0` 的版本语义，patch/minor 版本默认保持源码兼容。
- 明确 public API 范围：Maven 坐标、public/protected Java API、注解属性、`under.utils.*` 配置 key、starter 自动装配 Bean、SPI 接口和已文档化的失败语义。
- 明确破坏性变更定义：删除/重命名 public API、修改签名或默认值、改变 starter 默认副作用、无迁移改变缓存 payload、改变 key/锁/缓存/错误解码语义等。
- 明确弃用流程：使用 `@Deprecated(since = "x.y.z", forRemoval = false)`，JavaDoc 说明替代方案，默认保留到下一 major 版本，并在 CHANGELOG/API Review 中记录。
- 将兼容性影响接入 README、贡献指南、PR 模板和功能建议模板，后续面向用户的变更需要显式标注 patch-compatible、minor-compatible、deprecation 或 breaking。

## 第十轮结论

### 文档语言

- 对外文档统一使用中文，保留 GitHub 开源项目常见的信息结构：边界、安装、配置、示例、兼容性、测试和贡献规则。
- 保留必要英文技术名词和代码标识，例如 Maven Central、public API、starter、SPI、Release Notes。
- Issue/PR 模板同步改为中文，降低中文贡献者提交成本。

## 第十一轮结论

### 1.0.1 增强 API

- `under-utils-biz` 新增 `AsyncImportTaskTemplate`、`ImportProgress`、`ImportProgressListener`、`ImportTaskStatus` 和 `ImportErrorExporter`。这些 API 只新增能力，不改变同步 `ImportTaskTemplate` 的既有返回结果和失败语义。
- `ImportOptions` 增加 `progressListener` 和 `toBuilder()`，默认 listener 为 no-op；listener 运行时异常会被记录并忽略，不影响导入主流程。
- `AsyncImportTaskTemplate` 默认使用 JVM 内存保存任务状态。跨实例查询、重启恢复和任务审计不进入默认实现，应由业务项目通过 `ImportProgressListener` 持久化。
- `under-utils-redis` 新增缓存观测 SPI：`CacheOperationObserver`、`CacheOperationEvent` 和 `CacheOperationType`。模板构造器新增 observer 重载，旧构造器继续可用并使用 no-op observer。
- starter 在存在 `CacheOperationObserver` Bean 时自动注入 cache-aside 与 logical-cache 模板，不新增默认 Bean，避免污染应用上下文。
- `under-utils-http` 新增 `RefreshingAccessTokenProvider`。它只负责当前 JVM 内 token 缓存、提前刷新和并发收敛；分布式 token 共享和刷新失败兜底仍属于业务实现边界。

## 第十二轮结论

### 1.0.1 Release Prep

- Maven reactor 版本从 `1.0.1-SNAPSHOT` 收敛为 `1.0.1`，用于 patch release 验证与 Central Portal 上传。
- 用户文档依赖版本同步更新为 `1.0.1`。
- `CHANGELOG.md` 将当前 Unreleased 内容归档到 `1.0.1`，并新增 `docs/releases/v1.0.1.md` 作为 GitHub Release Notes 草稿。

### 1.0.1 Release

- `v1.0.1` 已发布到 Maven Central，发布源码 tag 固定在实际上传构件对应提交。
- `main` 分支进入 `1.0.2-SNAPSHOT` 开发周期，避免重复发布 Maven Central 已存在的 `1.0.1` 坐标。

## 第十三轮结论

### Starter 轻量化

- 新增 `under-utils-spring-starter`，只提供请求上下文、限流、防重复提交、本地 store 和基础 SPI 自动装配，避免只接入 Spring 横切能力的项目被动引入 Redis/Redisson。
- 新增 `under-utils-redis-starter`，依赖 Spring starter 并承载 Redis store、分布式锁、缓存模板和缓存观测自动装配。
- `under-utils-starter` 保留为兼容聚合坐标，不删除旧 Maven artifact；旧用户可以继续使用，新用户按需选择轻量 starter。
- `under.utils.*` 配置 key 保持不变，本轮只调整自动装配模块边界，不改变现有默认行为和失败语义。

## 第十四轮结论

### API 兼容性门禁

- 新增 `api-compat` Maven profile，并接入 GitHub Actions CI，使用 japicmp 将当前构件和 `1.0.1` 已发布构件做 public API 对比。
- 默认纳入检查的模块为 `under-utils-core`、`under-utils-http`、`under-utils-spring`、`under-utils-redis`、`under-utils-mybatis` 和 `under-utils-biz`。
- 检查在 `verify` 阶段执行，并对二进制不兼容和源码不兼容修改失败退出。
- `under-utils-starter`、`under-utils-spring-starter` 和 `under-utils-redis-starter` 暂不纳入单 jar 对比。starter 拆分会产生类移动误报，兼容性先由旧聚合坐标、自动装配测试和配置 key 文档共同保证。
- 本地 Maven 如果配置了无法解析 japicmp 的镜像，可以使用 `docs/central-dry-run-settings.xml` 绕过全局镜像后再运行检查。

## 第十五轮结论

### Core JSON 解耦

- `JsonUtils` 继续留在 `under-utils-core`，只作为 `1.x` 兼容维护 API，不在 patch 版本移出 Jackson 依赖，避免老用户升级后缺少 transitive dependency。
- `under-utils-http`、`under-utils-spring` 和 `under-utils-redis` 内部不再调用 `JsonUtils`，改为模块内自有 Jackson mapper；异常仍沿用既有 unchecked 类型，避免改变用户可感知失败路径。
- 受影响模块显式声明 `jackson-datatype-jsr310`，确保脱离 `JsonUtils` 后仍支持 Java Time 类型。
- 本轮不是删除 core JSON，而是先降低运行时模块对历史工具入口的耦合。真正从 `under-utils-core` 移除 Jackson 需要等待 major 版本或提供明确迁移模块。

## 后续待审

- Redis 缓存观测事件是否需要进一步接入 Micrometer Observation 语义。当前只提供无依赖 SPI，避免强绑定监控栈。
