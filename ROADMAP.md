# Roadmap

本路线图用于公开说明 Under-Utils 的演进方向。具体优先级会根据 issue、真实业务复用场景和维护成本调整。

## GitHub 首发前

- 清理过时的内部进度文档、占位代码和重复说明。
- 补齐 `README.md`、`QUICK_START.md`、`CHANGELOG.md`、`CONTRIBUTING.md`、`SECURITY.md`、`CODE_OF_CONDUCT.md`。
- 收敛模块定位，明确不做 Hutool 式大而全通用工具集合。
- 保证默认 Maven reactor 不依赖本地 MySQL、Redis、Docker 等外部环境。
- 为 samples 保留无外部依赖的默认启动路径，并提供 Redis profile 示例。

## 首个稳定 Release

- 梳理 public API，减少发布后需要破坏性调整的命名和配置项。
- 为核心工程模式能力补齐边界文档和失败语义说明。
- 建立 GitHub Actions CI，覆盖默认 `mvn test`、编译检查和 markdown 基础检查。
- 评估 Maven Central 发布所需的 groupId、签名、sources、javadocs 和 release 流程。
- 将 `under-utils-test` 中依赖本地 MySQL 的验证迁移到 Testcontainers 或独立的集成测试 profile。

## 后续方向

- 强化 Redis 缓存模板的可观测性，包括刷新失败、锁等待、缓存空值和逻辑过期状态指标。
- 扩展 OpenAPI 客户端的认证、签名、幂等键和业务错误解码示例。
- 补充 MyBatis 安全分页、审计填充和多数据库场景示例。
- 完善导入任务模板的异步任务、进度查询和错误导出扩展点。
- 根据真实使用反馈决定是否保留或收缩 `under-utils-core` 中的历史基础工具。

## 不在路线图内

- 复制 Hutool、Apache Commons、Guava 已成熟覆盖的通用工具方法。
- 引入强业务领域模型，例如订单、支付、营销、会员等单一业务线流程。
- 为了数量增加模块或 API，但没有清晰复用边界和测试覆盖。
