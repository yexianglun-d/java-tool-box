# Under-Utils Test

`under-utils-test` 是集成验证模块，用于验证跨模块能力和真实外部依赖交互。它不属于默认构建、发布和对外样例入口。

默认父工程不会构建该模块。原因是它通过 Testcontainers 启动临时 MySQL 容器；这类 Docker 环境约束不应影响普通开发者执行默认 `mvn test` 或 `mvn compile`。

## 定位

该模块用于：

- 验证 MyBatis-Plus 与真实 MySQL 的集成行为。
- 回归 core 基础工具的组合使用。
- 承载需要外部依赖但可以容器化复现的集成验证。

不用于：

- 对外使用示例。对外示例统一放在 `under-utils-samples`。
- 默认 Maven reactor 构建。
- 发布产物。

## 环境要求

- Java 21
- Maven 3.9+
- Docker

测试会自动启动 `mysql:8.0.33` 容器，并通过 `schema.sql` 初始化表结构。你不需要在本地安装 MySQL，也不需要创建数据库。

## 运行

从仓库根目录启用 `integration-tests` profile：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

只运行 MyBatis 集成测试：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test -Dtest=MybatisIntegrationTest
```

如果本地 Docker 不可用，请不要运行该模块；默认构建链路不会受影响。
