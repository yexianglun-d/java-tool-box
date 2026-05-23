# 贡献指南

感谢你愿意改进 Under-Utils。

本项目接收的是可复用工程模式封装，不接收为了增加方法数量而扩张的通用工具集合。

## 项目边界

适合提交的能力通常满足：

- 解决跨服务重复出现的基础设施问题。
- 有清晰模块边界，不把单个应用的业务规则写进公共库。
- 明确失败语义、线程安全假设和外部依赖要求。
- 可以通过单元测试或可复现集成测试验证。
- 不重复 JDK、Spring、Hutool、Apache Commons、Guava 已成熟覆盖的 API。

适合的方向：

- 请求上下文传播。
- 限流和防重复提交。
- Redis 分布式锁和缓存重建模板。
- OpenAPI 客户端治理。
- 安全分页和审计填充。
- 导入任务流程。

通常不适合的方向：

- `StringUtils.isBlank`、`DateUtils.format`、`CollectionUtils.map` 等小工具方法。
- 绑定订单、支付、会员、营销等单一产品域的流程。
- 没有错误处理和测试的薄封装。

## 开始之前

较大的改动请先创建 issue，说明：

- 要解决的重复场景。
- 为什么现有库或当前模块不足以覆盖。
- 计划放入的模块、public API、配置 key 和失败语义。
- 兼容性影响：patch-compatible、minor-compatible、breaking 或 deprecation-only。
- Redis、数据库、线程池、时钟、网络等运行时假设。
- 测试计划。

小型修复、测试和文档改进可以直接提交 PR。

## 本地环境

要求：

- Java 21
- Maven 3.9+
- Docker，仅用于 Testcontainers 集成测试

常用命令：

```bash
mvn -DskipTests compile
mvn test
mvn -Prelease -DskipTests package
```

集成测试：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

Central Portal dry run：

```bash
mvn -s docs/central-dry-run-settings.xml \
  -Prelease,central-publish \
  -Dcentral.publishing.server.id=central-dry-run \
  -Dcentral.skipPublishing=true \
  -Dgpg.skip=true \
  -DskipTests \
  deploy
```

## Pull Request

提交 review 前请确认：

- 变更有清晰模块归属。
- public API 行为不明显时，已补充文档。
- public API 变更遵循 [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md)。
- 避免在非 major 版本引入破坏性变更；如有安全或正确性例外，必须说明迁移路径。
- deprecated API 有替代方案或迁移说明。
- 行为变更包含测试。
- 默认 `mvn test` 不依赖 Redis、MySQL、Docker 或私有基础设施。
- `mvn -Prelease -DskipTests package` 仍能生成 sources 和 javadocs。
- 面向用户的变更已更新 README、模块文档或 `CHANGELOG.md`。
- 没有提交构建产物、本地路径、token、私钥、内部报告或生产日志。

## 提交信息

中文提交信息可以使用。保持简短，说明结果：

```text
完善 Redis 缓存模板文档
修复 OpenAPI 重试异常处理
补充 MyBatis 安全分页测试
```

## Review 关注点

维护者会重点看：

- 变更是否符合项目边界。
- API 是否能稳定维护。
- 兼容性影响分类是否准确。
- 依赖和自动装配是否有不可接受的副作用。
- 失败处理、资源释放和并发边界是否明确。
- 是否避免了与成熟工具库的低价值重复。
