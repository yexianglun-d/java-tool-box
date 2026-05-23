# Under-Utils

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](#requirements)
[![Maven](https://img.shields.io/badge/Maven-3.9%2B-C71A36.svg)](#requirements)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.x-6DB33F.svg)](#requirements)
[![CI](https://github.com/yexianglun-d/java-tool-box/actions/workflows/ci.yml/badge.svg)](https://github.com/yexianglun-d/java-tool-box/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.yexianglun-d/under-utils-starter.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.yexianglun-d/under-utils-starter)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Under-Utils provides reusable Java 21 / Spring Boot building blocks for infrastructure code that tends to be rewritten across business services.

The project is intentionally not a Hutool, Apache Commons, or Guava replacement. New code should solve a repeated engineering problem with real behavior to test: request context propagation, rate limiting, duplicate-submit guards, Redis locks, cache rebuilds, OpenAPI client governance, safe pagination, audit filling, and import workflows.

Current stable version: `1.0.0`.

## Scope

Code belongs in this repository when it has a clear reuse boundary and hides enough repeated complexity to justify a library API.

Good candidates:

- Cross-service infrastructure patterns, not one application's business rules.
- Behavior with explicit failure semantics, resource boundaries, and tests.
- APIs that can remain stable after release.
- Integrations that keep external dependencies behind small, documented interfaces.

Poor candidates:

- Small helpers already covered by the JDK, Spring, Hutool, Apache Commons, or Guava.
- One-off business flows tied to a single product line.
- Convenience wrappers that do not define error handling, concurrency behavior, or test coverage.

`under-utils-core` still contains historical helper classes for compatibility. They are not the direction for new features.

## Modules

| Module | Purpose |
|--------|---------|
| `under-utils-bom` | Dependency version alignment for Under-Utils modules and related libraries. |
| `under-utils-core` | Low-coupling primitives such as snowflake IDs and money helpers; historical static helpers are compatibility-only. |
| `under-utils-spring` | Spring Web context propagation, rate-limit and duplicate-submit abstractions, result and exception helpers, JSON masking. |
| `under-utils-redis` | Redisson-backed locks, distributed rate-limit/duplicate-submit stores, cache-aside, and logical-expire cache templates. |
| `under-utils-http` | HTTP convenience APIs and OpenAPI client governance: token injection, signing hooks, trace/idempotency headers, error decoding, retry. |
| `under-utils-mybatis` | MyBatis-Plus helpers for safe pagination, sort-field whitelisting, audit filling, and page results. |
| `under-utils-biz` | Reusable workflow templates, currently focused on CSV import row processing and validation result collection. |
| `under-utils-starter` | Spring Boot auto-configuration for the Spring and Redis modules. |
| `under-utils-samples` | Runnable Spring Boot sample application. Not published as a Maven library artifact. |
| `under-utils-test` | Testcontainers integration tests. Enabled only through the `integration-tests` Maven profile. |

## Requirements

- Java 21
- Maven 3.9+
- Spring Boot 3.1.x
- Docker, only for Testcontainers integration tests and the Redis sample environment

## Installation

Use the BOM first, then add the modules you need:

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

For local development:

```bash
git clone https://github.com/yexianglun-d/java-tool-box.git
cd java-tool-box
mvn test
```

## Starter Example

The starter uses local in-memory stores by default. Switch to Redis only when the application provides a `RedissonClient`.

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

Rate limiting and duplicate-submit guards fail closed by throwing `BizException` with the annotation message. Local stores are JVM-local; multi-instance deployments should use Redis or a custom `RateLimitStore` / `RepeatSubmitStore`.

## Usage

Request context propagation:

```java
OperationContext context = OperationContextHolder.getContext();
String traceId = context == null ? null : context.getTraceId();

Runnable task = OperationContextSnapshot.capture().wrap(() -> {
    OperationContext asyncContext = OperationContextHolder.getContext();
});
```

Rate limiting and duplicate-submit guard:

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

Redis cache-aside:

```java
UserProfile profile = cacheAsideTemplate.get(
        "user:profile:" + userId,
        UserProfile.class,
        () -> userRepository.findProfile(userId)
);
```

Safe pagination:

```java
SortFieldMapping mapping = SortFieldMapping.builder()
        .add("createdAt", "create_time")
        .add("status", "status")
        .build();

SafePageQuery query = SafePageQuery.of(page, size)
        .orderByDesc("createdAt");
```

## Samples

```bash
mvn -pl under-utils-samples -am spring-boot:run
```

The sample app starts on port `18080` and does not require Redis or MySQL by default. Redis examples and request snippets are documented in [under-utils-samples/README.md](under-utils-samples/README.md).

## Verification

Default checks:

```bash
mvn -DskipTests compile
mvn test
```

Release artifact check:

```bash
mvn -Prelease -DskipTests package
```

Testcontainers integration tests:

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

Publishing notes are maintained in [docs/RELEASE.md](docs/RELEASE.md).

## Contributing

Before opening a PR for a new feature, please read [CONTRIBUTING.md](CONTRIBUTING.md). The main question is whether the change belongs in a shared engineering-pattern library rather than in an application, framework, or existing utility library.

Public API changes follow [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md). Patch and minor releases should remain source-compatible unless a documented safety or security exception requires otherwise.

## License

Under-Utils is released under the [MIT License](LICENSE).
