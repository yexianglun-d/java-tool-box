# Under-Utils Test

`under-utils-test` 是手工集成验证模块，用于在本地外部环境齐备时验证跨模块能力，不属于默认构建、发布和样例入口。

默认父工程不会构建该模块。原因是它依赖本地 MySQL，并会执行 `schema.sql` 初始化表结构；这类环境约束不应影响普通开发者执行 `mvn test` 或 `mvn compile`。

## 定位

该模块保留用于：

- 验证 MyBatis-Plus 与真实数据库的集成行为。
- 手工回归 core 基础工具的组合使用。
- 作为历史集成用例存放处，后续可迁移到更标准的 Testcontainers 或 profile 化集成测试。

不用于：

- 对外使用示例。对外示例统一放在 `under-utils-samples`。
- 默认 CI 单元测试。
- 发布产物。

## 本地环境

默认配置见 `src/main/resources/application.yml`：

- MySQL 地址：`localhost:3306`
- 数据库：`under_utils_test`
- 用户名：`root`
- 密码：`root`

运行前需要先创建数据库：

```sql
CREATE DATABASE under_utils_test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 运行

从仓库根目录启用 `integration-tests` profile：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

或直接指定模块 POM：

```bash
mvn -f under-utils-test/pom.xml test
```

如果本地没有 MySQL，请不要运行该模块；默认构建链路不会受影响。
