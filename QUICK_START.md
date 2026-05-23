# Quick Start

This guide covers the fastest path to use Under-Utils from Maven Central, run the sample app, and execute the local test suite.

## Requirements

- Java 21
- Maven 3.9+
- Docker only if you run Testcontainers tests or Redis samples

## Add Dependencies

Use the BOM to keep module versions aligned:

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
```

For most Spring Boot applications, start with the starter:

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-starter</artifactId>
</dependency>
```

Single-module dependencies also work:

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-spring</artifactId>
</dependency>

<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-redis</artifactId>
</dependency>
```

## Starter Configuration

Local stores are the default and require no Redis:

```yaml
under:
  utils:
    web:
      operation-context:
        enabled: true
        task-decorator-enabled: true
      rate-limit:
        enabled: true
        store: local
      repeat-submit:
        enabled: true
        store: local
```

For multi-instance services, provide a `RedissonClient` and switch the state stores to Redis:

```yaml
under:
  utils:
    web:
      rate-limit:
        store: redis
      repeat-submit:
        store: redis
    redis:
      lock-enabled: true
      cache:
        enabled: true
        ttl: 5m
        null-ttl: 30s
        jitter: 10s
        cache-null: true
        rebuild-lock-enabled: true
      logical-cache:
        enabled: true
        logical-ttl: 5m
        physical-ttl: 30m
```

Important runtime behavior:

- `@RateLimit` throws `BizException` when the window quota is exceeded.
- `@PreventRepeat` throws `BizException` when the same key is submitted again inside the window.
- Local stores are JVM-local and should not be used for cluster-wide protection.
- Redisson failures propagate unless the application supplies a custom store with fallback behavior.

## Run The Sample App

```bash
git clone https://github.com/yexianglun-d/java-tool-box.git
cd java-tool-box
mvn -pl under-utils-samples -am spring-boot:run
```

Default port: `18080`.

Request examples are listed in [under-utils-samples/README.md](under-utils-samples/README.md).

Redis sample flow:

```bash
cd under-utils-samples
docker compose up -d
cd ..
mvn -pl under-utils-samples -am spring-boot:run -Dspring-boot.run.profiles=redis
```

## Build From Source

```bash
mvn -DskipTests compile
mvn test
```

The default reactor does not include `under-utils-test`, because that module starts containers.

Run integration tests explicitly:

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

Release artifact check:

```bash
mvn -Prelease -DskipTests package
```

Central Portal publishing is documented in [docs/RELEASE.md](docs/RELEASE.md).

## Troubleshooting

- If the IDE misses modules, import the root `pom.xml` as a Maven project and refresh.
- If dependency download is slow, configure a Maven mirror appropriate for your network.
- If `under-utils-test` fails before tests start, check Docker availability first.
