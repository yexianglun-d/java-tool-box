# Changelog

本文件记录 Under-Utils 的重要变更。项目当前处于 MVP 能力集与发布前质量收口阶段，尚未正式发布稳定版。

## [Unreleased]

### Added

- 新增 GitHub Actions CI，覆盖默认编译、默认测试和 `under-utils-test` Testcontainers 集成测试。
- 新增 Maven 发布准备配置，包含开源元信息、release 构件 profile、sources/javadocs 生成和可选 GPG 签名 profile。
- 新增发布前 API 审计记录 `docs/API_REVIEW.md`，跟踪 public API、配置 key 和模块边界收口结论。
- `under-utils-test` 新增 Testcontainers MySQL 集成测试依赖，集成验证不再依赖本地 MySQL。
- 新增 GitHub 开源社区文件：`LICENSE`、`CONTRIBUTING.md`、`SECURITY.md`、`CODE_OF_CONDUCT.md`、Issue 模板和 Pull Request 模板。
- 新增公开路线图 `ROADMAP.md`，替代内部阶段分析文档。
- 新增 Spring 操作上下文体系：`OperationContext`、`OperationContextFilter`、`OperationContextHolder`、`OperationContextSnapshot`、`OperationContextTaskDecorator`、`OperationContextExecutors`。
- 新增操作身份 SPI：`CurrentUserProvider`、`CurrentTenantProvider`、`TraceIdProvider`、`OperationContextCustomizer`。
- 新增操作 key 解析抽象：`OperationKeyResolver` 与默认实现，支持基于租户、用户、URI、方法参数摘要及 SpEL 生成限流/防重 key。
- 新增限流与防重复提交存储抽象：`RateLimitStore`、`RepeatSubmitStore`，并提供本地内存实现。
- 新增 Redis 工程模式能力：
  - `DistributedLockTemplate`
  - `RedisRateLimitStore`
  - `RedisRepeatSubmitStore`
  - `CacheAsideTemplate`
  - `LogicalExpireCacheTemplate`
- 新增 OpenAPI 客户端封装：
  - `OpenApiClient`
  - `DefaultOpenApiClient`
  - `OpenApiRequest`
  - `OpenApiResponse`
  - `OpenApiClientOptions`
  - `AccessTokenProvider`
  - `RequestSigner`
  - `ApiErrorDecoder`
- 新增 MyBatis-Plus 增强：
  - `SafePageQuery`
  - `SortFieldMapping`
  - `AuditorProvider`
  - `DefaultMetaObjectHandler` 构造注入审计用户能力
- 新增业务导入任务模板：
  - `ImportTaskTemplate`
  - `ImportRowHandler`
  - `ImportOptions`
  - `ImportResult`
  - `CsvImportRowReader`
  - `CsvRow`
- 新增 `under-utils-starter` 自动装配能力，覆盖 Web 横切、本地/Redis 状态存储、分布式锁、cache-aside、逻辑过期缓存等。
- 新增 `under-utils-samples` 可运行示例工程，覆盖上下文传播、限流防重、OpenAPI、本地导入、MyBatis 安全分页和 Redis 可选示例。
- 新增 samples Redis 验证环境：`docker-compose.yml` 与 `redis` profile 下的 `RedissonClient` 示例配置。

### Changed

- 重写根 README，明确项目定位：Under-Utils 不做 Hutool 替代品，不主打低复杂度通用工具方法，而主打复杂、重复、高工程价值的模式封装。
- 重写 `QUICK_START.md`，移除本地绝对路径、过时 JDK 版本和占位模块说明，改为 GitHub 开源上手流程。
- 更新 POM 描述，弱化“常用工具方法”表述，强调工程模式封装。
- 父工程 Maven Compiler Plugin 开启 `parameters`，让 Spring 在干净编译后可直接读取方法参数名。
- `under-utils-samples` 标记为不参与 Maven deploy，保留构建验证但避免作为正式库模块发布。
- HTTP 模块底层 OkHttp 执行入口从 `OkHttpClient` 收敛为 `OkHttpRequestExecutor`，避免与三方库类型同名。
- `LogicalExpireCacheOptions` 增加 `physicalTtl > logicalTtl` 校验，确保逻辑过期缓存保留旧值兜底窗口。
- `LogicalExpireCacheTemplate.LogicalCachePayload` 收回为包内实现细节，不再作为 public API 暴露。
- `PageQuery` 标记为不推荐 API，前端可控排序场景应使用 `SafePageQuery` 与 `SortFieldMapping`。
- `OperationLog`、`Retry`、`TimeLog` 及对应历史切面标记为兼容维护 API，不再作为 Spring 模块新增能力主线。
- `OperationLog.recordParams` 默认值改为 `false`，需要记录请求参数时必须显式开启。
- `OperationLogAspect`、`RetryAspect`、`TimeLogAspect` 不再声明为 Spring `@Component`，兼容使用时需显式 `@Import` 或声明为 `@Bean`。
- `RetryAspect` 对非法 `maxAttempts`、负数 `delay` 和空异常类型进行防御处理，保留同步重试语义但降低错误配置风险。
- `under-utils-core` README 与包级 Javadoc 收敛为低耦合基础能力和历史工具兼容维护，不再按常用工具大全表达。
- `StringUtils`、`CollectionUtils`、`LocalDateTimeUtils`、`ValidationUtils`、`UUIDUtils`、`JsonUtils`、`MD5Utils`、`SHA256Utils`、`AESUtils` 标记为兼容维护 API，不再扩展低复杂度工具方法。
- `JsonUtils.getObjectMapper()` 标记为不推荐，避免外部修改共享 ObjectMapper 影响全局序列化行为。
- `AESUtils.encryptECB` 与 `AESUtils.decryptECB` 标记为不推荐，ECB 模式仅保留历史兼容。
- 移除 `under-utils-core` 中未使用的 Lombok、SLF4J、Apache Commons Lang、Guava 和 Bouncy Castle 可选依赖。
- 补充 `@RateLimit`、`@PreventRepeat`、`RateLimitStore`、`RepeatSubmitStore` 和 starter store 配置的失败语义、key 解析语义和集群环境说明。
- 收紧 starter 自动装配条件，用户自定义 `TaskDecorator`、`CacheValueCodec`、`CacheOptions`、`CacheAsideTemplate` 等 Bean 时自动退让。
- `CacheValueCodec` 改为 cache-aside 与 logical-cache 共享的 Redis 缓存基础设施，仅在相关能力启用时自动装配。
- Redis cache options 增加兼容别名，改善 `ttl/nullTtl/cacheNull` 与 value/null value 语义的可读性。
- 为 OpenAPI、Redis cache、Spring context、Biz import task、MyBatis page 等包补充边界说明和关键 Javadoc。
- 父工程增加 `spring-boot-maven-plugin` 版本管理，支持从根工程运行 samples 模块。
- `under-utils-test` 改为 Testcontainers 集成验证模块，通过 `integration-tests` profile 启用，不再进入默认构建链路。
- MyBatis 集成测试改为动态注入临时 MySQL 容器 datasource，并在每个测试前清理测试表。
- `CoreUtilsIntegrationTest` 移除不必要的 Spring 上下文启动。

### Removed

- 删除无实际职责的 `under-utils-placeholder` 占位模块。
- 删除各模块残留的空 `Placeholder.java`。
- 删除过时的内部分析、开发进度、测试报告和设计草稿文档。
- 删除 `under-utils-biz` 中的占位类，改由导入任务模板承载真实业务流程封装能力。

### Verified

- `mvn -pl under-utils-samples -am test`
- `mvn -DskipTests compile`
- `mvn clean test`
- `mvn -pl under-utils-core test`
- `mvn -pl under-utils-spring -am test`
- `mvn -pl under-utils-http,under-utils-redis,under-utils-mybatis,under-utils-starter -am test`
- `mvn -pl under-utils-spring,under-utils-redis,under-utils-starter -am test`
- `mvn test`
- `mvn -Prelease -DskipTests package`
- `mvn -Prelease,sign-artifacts -Dgpg.skip=true -DskipTests verify`
- `mvn -Pintegration-tests -pl under-utils-test -am -DskipTests test-compile`
- `git diff --check`
- samples 已在无 Redis/MySQL 的默认配置下完成 Spring Boot 启动与核心接口验证。

### Known Limitations

- 当前机器无 `docker` 命令，Redis samples 和 Testcontainers 集成测试无法在本机执行；相关验证需要在具备 Docker 的环境或 GitHub Actions 中运行。
