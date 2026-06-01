# 贡献指南 / Contributing Guide

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

## Bug 修复与回归测试

Bug 修复应留下可追溯防线，避免同类问题在后续重构中悄悄回归。

- 重要 Bug 修复必须补独立回归测试；测试应能在修复前失败、修复后通过。
- 有 issue 编号时，优先把编号写进测试类名或测试方法名，例如 `Issue1234Test`、`IssueGH1234Test` 或 `issue1234_shouldRejectUnsafeSort`。
- 没有外部 issue 时，使用 `Regression...Test` 或 `regression_...` 命名，并在 PR、`CHANGELOG.md` 或 `docs/API_REVIEW.md` 中说明来源，例如 `review-doc`、`internal-review` 或 `user-report`。
- 回归测试应覆盖触发条件、失败语义和边界值；不要只验证修复后的 happy path。
- 如果无法直接写自动化测试，PR 必须说明原因，并给出可复现的手动验证步骤。

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

public API 兼容性检查：

```bash
mvn -Papi-compat \
  -pl under-utils-core,under-utils-http,under-utils-spring,under-utils-redis,under-utils-mybatis,under-utils-biz \
  -am \
  -DskipTests \
  verify
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
- Bug 修复已补独立回归测试，并能追溯到 issue、review-doc、internal-review 或 user-report。
- public API 行为不明显时，已补充文档。
- public API 变更遵循 [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md)。
- 避免在非 major 版本引入破坏性变更；如有安全或正确性例外，必须说明迁移路径。
- 运行时模块的 public API 变更已通过 `api-compat` profile 检查，或已说明无法自动检查的原因。
- deprecated API 有替代方案或迁移说明。
- 行为变更包含测试。
- 默认 `mvn test` 不依赖 Redis、MySQL、Docker 或私有基础设施。
- `mvn -Prelease -DskipTests package` 仍能生成 sources 和 javadocs。
- 面向用户的变更已更新 README、模块文档或 `CHANGELOG.md`；`CHANGELOG.md` 应说明原因、影响范围、兼容性分类和来源编号或来源类型。
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

---

Thank you for helping improve Under-Utils.

This project accepts reusable engineering-pattern abstractions. It does not accept growth into a generic utility collection just to increase the number of methods.

## Project Scope

A suitable contribution usually meets these conditions:

- It solves an infrastructure problem that repeatedly appears across services.
- It has a clear module boundary and does not put business rules from a single application into a shared library.
- It defines failure semantics, thread-safety assumptions, and external dependency requirements.
- It can be verified by unit tests or reproducible integration tests.
- It does not duplicate APIs already well covered by the JDK, Spring, Hutool, Apache Commons, or Guava.

Good directions include:

- Request-context propagation.
- Rate limiting and repeat-submit protection.
- Redis distributed locks and cache rebuild templates.
- OpenAPI client governance.
- Safe pagination and audit filling.
- Import task workflows.

Usually poor fits include:

- Small utility methods such as `StringUtils.isBlank`, `DateUtils.format`, or `CollectionUtils.map`.
- Workflows tied to a single product domain such as orders, payments, memberships, or marketing.
- Thin wrappers without error handling or tests.

## Before You Start

For larger changes, please create an issue first and describe:

- The repeated scenario you want to solve.
- Why existing libraries or current modules are not enough.
- The planned module, public API, configuration keys, and failure semantics.
- Compatibility impact: patch-compatible, minor-compatible, breaking, or deprecation-only.
- Runtime assumptions such as Redis, database, thread pool, clock, or network dependencies.
- Test plan.

Small fixes, tests, and documentation improvements can be submitted directly as pull requests.

## Bug Fixes and Regression Tests

Bug fixes should leave a traceable guardrail so the same issue does not silently return during future refactoring.

- Important bug fixes must add an independent regression test. The test should fail before the fix and pass after the fix.
- If an issue number exists, prefer including it in the test class or method name, such as `Issue1234Test`, `IssueGH1234Test`, or `issue1234_shouldRejectUnsafeSort`.
- If there is no external issue, use `Regression...Test` or `regression_...` naming and describe the source in the pull request, `CHANGELOG.md`, or `docs/API_REVIEW.md`, such as `review-doc`, `internal-review`, or `user-report`.
- Regression tests should cover trigger conditions, failure semantics, and boundary values. Do not only verify the fixed happy path.
- If an automated test cannot be added directly, the pull request must explain why and provide reproducible manual verification steps.

## Local Environment

Requirements:

- Java 21
- Maven 3.9+
- Docker, only for Testcontainers integration tests

Common commands:

```bash
mvn -DskipTests compile
mvn test
mvn -Prelease -DskipTests package
```

Public API compatibility check:

```bash
mvn -Papi-compat \
  -pl under-utils-core,under-utils-http,under-utils-spring,under-utils-redis,under-utils-mybatis,under-utils-biz \
  -am \
  -DskipTests \
  verify
```

Integration tests:

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

Central Portal dry run:

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

Before requesting review, confirm that:

- The change has a clear module ownership.
- Bug fixes include an independent regression test and can be traced to an issue, `review-doc`, `internal-review`, or `user-report`.
- Public API behavior is documented when it is not obvious.
- Public API changes follow [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md).
- Breaking changes are avoided outside major versions. If a security or correctness exception exists, explain the migration path.
- Runtime-module public API changes have passed the `api-compat` profile, or the reason they cannot be checked automatically is documented.
- Deprecated APIs have alternatives or migration notes.
- Behavior changes include tests.
- The default `mvn test` does not depend on Redis, MySQL, Docker, or private infrastructure.
- `mvn -Prelease -DskipTests package` still generates sources and javadocs.
- User-facing changes update README, module documentation, or `CHANGELOG.md`. `CHANGELOG.md` should describe the reason, impact scope, compatibility category, and source issue or source type.
- No build artifacts, local paths, tokens, private keys, internal reports, or production logs are committed.

## Commit Messages

Chinese commit messages are acceptable. Keep messages short and describe the result:

```text
完善 Redis 缓存模板文档
修复 OpenAPI 重试异常处理
补充 MyBatis 安全分页测试
```

English commit messages are also acceptable:

```text
Improve Redis cache template docs
Fix OpenAPI retry exception handling
Add MyBatis safe pagination tests
```

## Review Focus

Maintainers will focus on:

- Whether the change fits the project scope.
- Whether the API can be maintained stably.
- Whether the compatibility impact category is accurate.
- Whether dependencies and auto-configuration introduce unacceptable side effects.
- Whether failure handling, resource release, and concurrency boundaries are clear.
- Whether the change avoids low-value duplication of mature utility libraries.
