# Under-Utils Test

集成测试模块，用于验证需要真实外部服务的行为。

该模块不进入默认 Maven reactor，因为它会通过 Testcontainers 启动 Docker 容器。它不是对外示例工程，也不作为库模块发布。

## 覆盖范围

- MyBatis-Plus 与真实 MySQL 的集成行为。
- Redis 缓存模板与真实 Redis 的集成行为。
- 不应依赖开发者本地服务的跨模块集成检查。

## 环境要求

- Java 21
- Maven 3.9+
- Docker

## 运行

全部集成测试：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

仅 MyBatis：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test -Dtest=MybatisIntegrationTest
```

仅 Redis 缓存模板：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test -Dtest=RedisCacheTemplateIntegrationTest
```

如果本机没有 Docker，使用默认构建即可：

```bash
mvn test
```
