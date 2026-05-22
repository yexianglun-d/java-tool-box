# Contributing

感谢你愿意参与 Under-Utils。这个项目的核心目标不是做大而全工具箱，而是沉淀复杂、重复、跨项目复用的工程模式封装。

## Contribution Scope

适合提交的内容：

- 能在多个业务项目复用的工程能力，例如上下文传播、限流防重、分布式锁、缓存重建、OpenAPI 调用、安全分页、导入任务模板。
- 对现有模块的 bug 修复、测试补充、文档改进和示例完善。
- 能降低接入复杂度、增强失败语义或改善可观测性的改动。

不建议提交的内容：

- Hutool、Apache Commons、Guava 已成熟覆盖的低复杂度工具方法。
- 只服务单个业务线的强业务规则。
- 没有清晰边界、没有测试、没有失败语义的快捷封装。
- 与现有模块职责冲突的新模块或重复 API。

## Before You Start

较大的功能建议先创建 issue，说明：

- 你要解决的重复场景。
- 为什么现有 JDK、Spring、Hutool、Apache Commons、Guava 或项目现有模块不能直接覆盖。
- API 边界、失败语义、线程安全或外部依赖假设。
- 计划放入哪个模块，以及是否需要 starter 自动装配。

## Local Setup

要求：

- Java 21
- Maven 3.9+
- Docker，用于运行 `integration-tests` profile 下的 Testcontainers 集成测试

常用命令：

```bash
mvn -DskipTests compile
mvn test
mvn -pl under-utils-samples -am test
```

`under-utils-test` 是 Testcontainers 集成验证模块，不进入默认构建。如需运行：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

## Pull Request Checklist

提交 PR 前请确认：

- 变更有明确模块归属，没有把业务规则硬编码进公共模块。
- 新增 public API 有必要的 JavaDoc 或 README 示例。
- 新增能力包含单元测试；涉及外部系统的能力提供可复现的集成验证方式。
- 默认 `mvn test` 不依赖本地 Redis、MySQL、Docker 等外部环境。
- README、CHANGELOG 或模块文档已按需更新。
- 没有提交 IDE 临时文件、构建产物、个人路径或内部报告。

## Commit Message

提交信息建议使用简洁中文，说明变更结果，例如：

```text
完善 Redis 缓存模板文档
修复 OpenAPI 重试异常处理
补充 MyBatis 安全分页测试
```

## Review Principles

维护者会重点关注：

- 这个能力是否符合项目定位。
- API 是否清晰、稳定、可测试。
- 是否引入不必要的依赖或自动装配副作用。
- 失败语义、资源释放、并发边界是否明确。
- 是否会和现有成熟工具库形成低价值重复。
