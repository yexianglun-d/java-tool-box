# 快速开始

本文档说明如何从 Maven Central 引入 Under-Utils、运行示例工程，以及执行本地验证。

## 环境要求

- Java 21
- Maven 3.9+
- Docker，仅在运行 Testcontainers 集成测试或 Redis 示例时需要

## 添加依赖

先引入 BOM，统一各模块版本：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.yexianglun-d</groupId>
            <artifactId>under-utils-bom</artifactId>
            <version>1.0.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Spring Boot 项目通常从 starter 开始：

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-starter</artifactId>
</dependency>
```

也可以只引入单个模块：

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

## Starter 配置

本地状态存储不需要 Redis：

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

多实例服务应提供 `RedissonClient`，并切换到 Redis 存储：

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

运行时行为：

- `@RateLimit` 超过窗口额度时抛出 `BizException`。
- `@PreventRepeat` 在窗口内重复提交同一 key 时抛出 `BizException`。
- local store 只在当前 JVM 内生效，不适合作为集群级保护。
- Redisson 异常默认向外传播；如果业务需要降级，应提供自定义 store。

## 运行示例工程

```bash
git clone https://github.com/yexianglun-d/java-tool-box.git
cd java-tool-box
mvn -pl under-utils-samples -am spring-boot:run
```

默认端口：`18080`。

请求样例见 [under-utils-samples/README.md](under-utils-samples/README.md)。

Redis 示例流程：

```bash
cd under-utils-samples
docker compose up -d
cd ..
mvn -pl under-utils-samples -am spring-boot:run -Dspring-boot.run.profiles=redis
```

## 源码构建

```bash
mvn -DskipTests compile
mvn test
```

默认 Maven reactor 不包含 `under-utils-test`，因为该模块会启动容器。

显式运行集成测试：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

发布构件检查：

```bash
mvn -Prelease -DskipTests package
```

Maven Central 发布流程见 [docs/RELEASE.md](docs/RELEASE.md)。

## 常见问题

- IDE 没识别模块时，先确认导入的是根目录 `pom.xml`，然后刷新 Maven。
- 依赖下载慢时，可以配置适合当前网络环境的 Maven 镜像。
- `under-utils-test` 在启动前失败时，优先确认 Docker 是否可用。
