# Under-Utils

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](#环境要求)
[![Maven](https://img.shields.io/badge/Maven-3.9%2B-C71A36.svg)](#环境要求)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.x-6DB33F.svg)](#环境要求)
[![CI](https://github.com/yexianglun-d/java-tool-box/actions/workflows/ci.yml/badge.svg)](https://github.com/yexianglun-d/java-tool-box/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.yexianglun-d/under-utils-starter.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.yexianglun-d/under-utils-starter)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Under-Utils 是一组面向 Java 21 / Spring Boot 项目的工程模式工具包，用来沉淀业务系统里反复出现、实现细节多、容易写散的基础设施代码。

这个项目不定位为 Hutool、Apache Commons 或 Guava 的替代品。新增能力应解决可复用的工程问题，并且有明确行为可以测试，例如请求上下文传播、限流、防重复提交、Redis 分布式锁、缓存重建、OpenAPI 客户端治理、安全分页、审计填充和导入任务流程。

当前稳定版本：`1.0.0`。

## 项目边界

适合进入本项目的能力，应有清晰复用边界，并能封装足够多的重复复杂度。

适合的方向：

- 跨服务复用的基础设施模式，而不是单个应用的业务规则。
- 具备明确失败语义、资源边界和测试覆盖的能力。
- 发布后 API 可以保持稳定。
- 外部系统依赖被收口在小而清晰的接口后面。

不适合的方向：

- JDK、Spring、Hutool、Apache Commons、Guava 已成熟覆盖的小工具方法。
- 只服务单个业务线的一次性流程。
- 没有错误处理、并发边界或测试覆盖的快捷封装。

`under-utils-core` 中保留了一些历史静态工具类，用于兼容已有调用；它们不是后续新增能力的主线。

## 模块

| 模块 | 说明 |
|------|------|
| `under-utils-bom` | 统一管理 Under-Utils 模块和相关依赖版本。 |
| `under-utils-core` | 低耦合基础能力，例如雪花 ID、金额工具；历史静态工具仅做兼容维护。 |
| `under-utils-spring` | Spring Web 上下文传播、限流/防重抽象、返回结果、异常处理和 JSON 脱敏。 |
| `under-utils-redis` | 基于 Redisson 的分布式锁、限流/防重存储、cache-aside、逻辑过期缓存模板和缓存观测 SPI。 |
| `under-utils-http` | HTTP 便捷调用与 OpenAPI 客户端治理，包括 token 刷新、签名、trace/idempotency header、错误解码和重试。 |
| `under-utils-mybatis` | MyBatis-Plus 安全分页、排序白名单、审计填充和分页结果封装。 |
| `under-utils-biz` | 可复用业务流程模板，目前主要是 CSV 导入、异步导入进度查询和错误导出。 |
| `under-utils-starter` | Spring Boot 自动装配入口。 |
| `under-utils-samples` | 可运行示例工程，不作为正式 Maven 库构件发布。 |
| `under-utils-test` | Testcontainers 集成测试模块，仅通过 `integration-tests` profile 启用。 |

## 环境要求

- Java 21
- Maven 3.9+
- Spring Boot 3.1.x
- Docker，仅在运行 Testcontainers 集成测试或 Redis 示例环境时需要

## 安装

建议先引入 BOM，再按需引入 starter 或单模块：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.yexianglun-d</groupId>
            <artifactId>under-utils-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>io.github.yexianglun-d</groupId>
        <artifactId>under-utils-starter</artifactId>
    </dependency>
</dependencies>
```

本地开发：

```bash
git clone https://github.com/yexianglun-d/java-tool-box.git
cd java-tool-box
mvn test
```

## Starter 示例

starter 默认使用本地内存状态存储。切换到 Redis 前，业务项目需要先提供 `RedissonClient`。

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

限流和防重复提交默认失败语义是拒绝请求并抛出 `BizException`，异常消息来自注解配置。本地 store 只在当前 JVM 内生效，多实例部署应使用 Redis 或自定义 `RateLimitStore` / `RepeatSubmitStore`。

## 使用示例

请求上下文传播：

```java
OperationContext context = OperationContextHolder.getContext();
String traceId = context == null ? null : context.getTraceId();

Runnable task = OperationContextSnapshot.capture().wrap(() -> {
    OperationContext asyncContext = OperationContextHolder.getContext();
});
```

限流和防重复提交：

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

Redis cache-aside：

```java
UserProfile profile = cacheAsideTemplate.get(
        "user:profile:" + userId,
        UserProfile.class,
        () -> userRepository.findProfile(userId)
);
```

异步导入任务：

```java
AsyncImportTaskTemplate importTasks = new AsyncImportTaskTemplate(executor);
String taskId = importTasks.submit(rows, handler);
ImportProgress progress = importTasks.findProgress(taskId).orElseThrow();
```

安全分页：

```java
SortFieldMapping mapping = SortFieldMapping.builder()
        .add("createdAt", "create_time")
        .add("status", "status")
        .build();

SafePageQuery query = SafePageQuery.of(page, size)
        .orderByDesc("createdAt");
```

## 示例工程

```bash
mvn -pl under-utils-samples -am spring-boot:run
```

示例工程默认端口为 `18080`，默认不依赖 Redis 或 MySQL。Redis 示例和请求样例见 [under-utils-samples/README.md](under-utils-samples/README.md)。

## 验证

默认检查：

```bash
mvn -DskipTests compile
mvn test
```

发布构件检查：

```bash
mvn -Prelease -DskipTests package
```

Testcontainers 集成测试：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

发布流程见 [docs/RELEASE.md](docs/RELEASE.md)。

## 贡献

提交新能力前请先阅读 [CONTRIBUTING.md](CONTRIBUTING.md)。核心判断是：这个能力是否属于可复用工程模式，而不是应用代码、框架已有能力或成熟工具库已有能力。

public API 变更遵循 [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md)。除非有明确的安全或正确性例外，patch 和 minor 版本应保持源码兼容。

## 许可证

Under-Utils 使用 [MIT License](LICENSE)。
