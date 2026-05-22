# Quick Start

本文档面向第一次接触 Under-Utils 的开发者，帮助你在本地完成构建、运行示例工程，并理解如何在业务项目中接入。

## Requirements

- Java 21
- Maven 3.9+
- 可选：Docker，用于运行 samples 的 Redis 示例，以及 `under-utils-test` Testcontainers 集成验证

## Build From Source

```bash
git clone https://github.com/yexianglun-d/java-tool-box.git
cd java-tool-box
mvn clean install
```

只做编译检查：

```bash
mvn -DskipTests compile
```

运行默认单元测试：

```bash
mvn test
```

默认构建不包含 `under-utils-test`，因为该模块会通过 Testcontainers 启动临时 MySQL 容器，需要 Docker 环境。

## Add To Your Project

发布到 Maven 仓库前，请先在本地执行 `mvn clean install`。业务项目推荐引入 BOM 后再引入 starter：

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

也可以按需引入单模块：

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

## Release Build Check

项目尚未发布到 Maven Central。正式发布前可以先验证发布构件：

```bash
mvn -Prelease -DskipTests package
```

该命令会生成 sources 与 javadocs，用于检查发布构件链路是否可用。GPG 签名不默认启用；准备好本机或 CI 的 GPG 环境后再执行：

```bash
mvn -Prelease,sign-artifacts -Dgpg.sign=true -DskipTests verify
```

Central Portal 发布链路可通过以下命令做本地 dry run，默认不会上传：

```bash
mvn -s docs/central-dry-run-settings.xml \
  -Prelease,central-publish \
  -Dcentral.publishing.server.id=central-dry-run \
  -Dcentral.skipPublishing=true \
  -Dgpg.skip=true \
  -DskipTests \
  deploy
```

完整流程见 [Release Guide](docs/RELEASE.md)。`under-utils-samples` 用于示例和开发体验验证，不作为正式库模块发布。

## Enable Starter Features

本地内存状态存储适合单实例开发环境：

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

集群环境可以切换到 Redis 存储，并由业务项目提供 `RedissonClient`：

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

限流和防重复提交的失败语义：

- `@RateLimit` 超过窗口额度时抛出 `BizException`，异常消息来自注解 `message`。
- `@PreventRepeat` 在窗口内重复提交时抛出 `BizException`，异常消息来自注解 `message`。
- `store: local` 只在当前 JVM 内生效，适合单实例或测试环境。
- `store: redis` 跨实例共享状态，要求业务项目提供 `RedissonClient`；Redis 不可用时 Redisson 异常会向外传播，生产环境如需降级应自定义 `RateLimitStore` / `RepeatSubmitStore`。

## Run Samples

基础示例不依赖 Redis/MySQL：

```bash
mvn -pl under-utils-samples -am spring-boot:run
```

默认端口为 `18080`。接口清单和请求样例见 [under-utils-samples/README.md](under-utils-samples/README.md)。

运行 Redis 示例环境：

```bash
cd under-utils-samples
docker compose up -d
cd ..
mvn -pl under-utils-samples -am spring-boot:run -Dspring-boot.run.profiles=redis
```

如果本地没有 Docker，也可以接入已有 Redis，并按 samples 文档调整连接配置。

## Integration Tests

`under-utils-test` 是集成验证模块，不是默认 release 模块。它使用 Testcontainers 启动临时 MySQL 容器，不需要你本地安装 MySQL，但需要 Docker 可用。

运行命令：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

只验证 MyBatis 集成测试：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test -Dtest=MybatisIntegrationTest
```

## Troubleshooting

Maven 依赖下载慢时，可以在 `~/.m2/settings.xml` 配置你所在网络环境可用的镜像。

IDE 无法识别模块时，先确认项目根目录的 `pom.xml` 已作为 Maven Project 导入，然后重新刷新 Maven。

`under-utils-test` 运行失败时，先确认是否启用了 `integration-tests` profile，以及本地 Docker 是否可用。
