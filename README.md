# Under-Utils

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](#requirements)
[![Maven](https://img.shields.io/badge/Maven-3.9%2B-C71A36.svg)](#requirements)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.x-6DB33F.svg)](#module-matrix)
[![Status](https://img.shields.io/badge/Status-Pre--release-orange.svg)](CHANGELOG.md)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Under-Utils 是一组面向 Java 21 / Spring Boot 项目的工程模式工具包。它不定位为 Hutool、Apache Commons 或 Guava 的替代品，也不追求堆叠大量低复杂度静态工具方法；项目更关注业务系统里反复出现、实现细节多、容易写散的工程能力，例如请求操作上下文、幂等与限流、分布式锁、缓存重建、OpenAPI 调用、安全分页排序、审计填充和导入任务模板。

当前项目处于 GitHub 开源首发前的质量收口阶段，API 和模块边界仍可能在第一个稳定 Release 前调整。重要变更见 [CHANGELOG.md](CHANGELOG.md)，公开路线图见 [ROADMAP.md](ROADMAP.md)。

## Project Scope

适合进入 Under-Utils 的能力通常具备这些特征：

| 标准 | 要求 |
|------|------|
| 复杂性 | 内部包含流程编排、状态管理、异常边界、资源释放、并发控制或外部系统交互 |
| 重复性 | 能在多个业务项目或多个模块中复用，能减少明显重复代码 |
| 边界清晰 | 有明确输入输出和职责边界，不把单一业务规则硬编码进公共模块 |
| 可测试 | 核心分支可以通过单元测试或集成测试验证，异常路径可覆盖 |
| 不冲突 | 不重复建设 Hutool、Apache Commons、Guava 已成熟覆盖的基础工具方法 |
| 可演进 | API 命名稳定，配置项可解释，后续扩展不需要频繁破坏调用方 |

不建议新增的能力：

- `StringUtils.isBlank`、`DateUtils.format`、`CollectionUtils.isEmpty` 这类通用小工具方法。
- 只服务单个业务线、带强业务语义的流程。
- 依赖不稳定外部系统且无法抽象边界的封装。
- 没有测试、没有失败语义、只有“方便一下”的快捷方法。

`under-utils-core` 中已有的历史基础工具会保持兼容维护，但新增能力会按上面的准入标准收口，避免项目继续向“大而全工具箱”扩张。

## Module Matrix

| 模块 | 定位 | 典型能力 |
|------|------|----------|
| `under-utils-bom` | 统一依赖版本管理 | Spring Boot、MyBatis-Plus、Redisson、OkHttp、Jackson 等版本收口 |
| `under-utils-core` | 无框架耦合的基础能力承载层 | 历史基础工具、轻量核心抽象；新增能力需要符合工程模式准入标准 |
| `under-utils-spring` | Spring Web 横切能力 | `OperationContext`、上下文传播、`@RateLimit`、`@PreventRepeat`、`OperationKeyResolver` |
| `under-utils-redis` | Redis 工程模式 | `DistributedLockTemplate`、Redis 限流/防重、`CacheAsideTemplate`、`LogicalExpireCacheTemplate` |
| `under-utils-http` | HTTP / OpenAPI 客户端 | `OpenApiClient`、token 注入、请求签名、trace/idempotency header、业务错误解码、重试 |
| `under-utils-mybatis` | MyBatis-Plus 增强 | `SafePageQuery`、`SortFieldMapping`、`AuditorProvider`、`DefaultMetaObjectHandler`、分页结果 |
| `under-utils-biz` | 可复用业务流程模板 | `ImportTaskTemplate`、`CsvImportRowReader`、逐行解析、校验失败收集、导入统计 |
| `under-utils-starter` | Spring Boot 自动装配 | Web 横切、Redis 状态存储、分布式锁、缓存模板、逻辑过期缓存自动配置 |
| `under-utils-samples` | 可运行示例工程 | starter 接入、上下文传播、限流防重、OpenAPI、本地导入、Redis 可选示例 |
| `under-utils-test` | 手工集成验证模块 | 依赖本地 MySQL 等外部环境，通过 `integration-tests` profile 启用，不进入默认构建 |

## Requirements

- Java 21
- Maven 3.9+
- Spring Boot 3.1.x
- 可选：Docker，用于运行 samples 的 Redis 示例环境
- 可选：MySQL，用于运行 `under-utils-test` 手工集成验证

## Installation

发布到 Maven 仓库前，可以先从源码安装到本地仓库：

```bash
git clone https://github.com/yexianglun-d/java-tool-box.git
cd java-tool-box
mvn clean install
```

业务项目推荐先引入 BOM，再按需引入 starter 或单模块依赖：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.undernineplaces</groupId>
            <artifactId>under-utils-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>com.undernineplaces</groupId>
        <artifactId>under-utils-starter</artifactId>
    </dependency>
</dependencies>
```

## Quick Start

starter 会在条件满足时自动装配：

- `OperationContextFilter`、`OperationContextTaskDecorator`
- `RateLimitAspect`、`PreventRepeatAspect`
- local / redis 两种 `RateLimitStore`、`RepeatSubmitStore`
- `DistributedLockTemplate`
- `CacheAsideTemplate`
- `LogicalExpireCacheTemplate`

典型配置：

```yaml
under:
  utils:
    web:
      operation-context:
        enabled: true
        task-decorator-enabled: true
      rate-limit:
        enabled: true
        store: redis
      repeat-submit:
        enabled: true
        store: redis
    redis:
      lock-enabled: true
      lock-key-prefix: "order:lock:"
      cache:
        enabled: true
        ttl: 5m
        null-ttl: 30s
        jitter: 10s
        cache-null: true
        key-prefix: "app:cache:"
        rebuild-lock-enabled: true
      logical-cache:
        enabled: true
        logical-ttl: 5m
        physical-ttl: 30m
        cache-null: true
        key-prefix: "app:logical-cache:"
```

如果限流、防重提交使用 Redis 存储，业务项目需要提供 `RedissonClient`。如果不配置 `store: redis`，默认使用本地内存存储，适合单实例或测试环境。

运行示例工程：

```bash
mvn -pl under-utils-samples -am spring-boot:run
```

默认端口为 `18080`，无 Redis/MySQL 也可以启动基础示例。Redis 场景、接口清单和请求样例见 [under-utils-samples/README.md](under-utils-samples/README.md)。更完整的上手步骤见 [QUICK_START.md](QUICK_START.md)。

## Usage Examples

请求上下文与异步传播：

```java
OperationContext context = OperationContextHolder.getContext();
String traceId = context == null ? null : context.getTraceId();

Runnable task = OperationContextSnapshot.capture().wrap(() -> {
    OperationContext asyncContext = OperationContextHolder.getContext();
    // 在异步线程继续使用 traceId、userId、tenantId
});
```

限流与防重复提交：

```java
@RateLimit(limit = 10, period = 60, message = "请求过于频繁")
@PostMapping("/sms/send")
public void sendSms(@RequestBody SendSmsCommand command) {
    smsService.send(command);
}

@PreventRepeat(timeout = 5, message = "请勿重复提交")
@PostMapping("/orders")
public Long createOrder(@RequestBody CreateOrderCommand command) {
    return orderService.create(command);
}
```

分布式锁：

```java
Long orderId = distributedLockTemplate.execute(
        "order:create:" + command.requestNo(),
        1,
        30,
        TimeUnit.SECONDS,
        () -> orderService.create(command)
);
```

Cache-Aside：

```java
UserProfile profile = cacheAsideTemplate.get(
        "user:profile:" + userId,
        UserProfile.class,
        () -> userRepository.findProfile(userId)
);
```

安全分页排序：

```java
SortFieldMapping mapping = SortFieldMapping.builder()
        .add("createdAt", "create_time")
        .add("status", "status")
        .build();

SafePageQuery query = SafePageQuery.of(page, size)
        .orderByDesc("createdAt");
```

## Development

常用命令：

```bash
mvn -DskipTests compile
mvn test
mvn -pl under-utils-samples -am test
```

`under-utils-test` 依赖本地 MySQL 等外部环境，不进入默认父工程构建。如需运行：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

详细说明见 [under-utils-test/README.md](under-utils-test/README.md)。

## Community

- 贡献指南：[CONTRIBUTING.md](CONTRIBUTING.md)
- 行为准则：[CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- 安全漏洞报告：[SECURITY.md](SECURITY.md)
- 变更记录：[CHANGELOG.md](CHANGELOG.md)
- 公开路线图：[ROADMAP.md](ROADMAP.md)

提交功能前建议先开 issue 说明场景、边界和复用价值。新增能力需要附带测试，并说明为什么不属于 Hutool、Apache Commons 或 Guava 已覆盖的低复杂度通用工具。

## License

Under-Utils is released under the [MIT License](LICENSE).
