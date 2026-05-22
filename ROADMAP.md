# Roadmap

本路线图用于公开说明 Under-Utils 的演进方向。具体优先级会根据 issue、真实业务复用场景和维护成本调整。

## GitHub 首发前

- 清理过时的内部进度文档、占位代码和重复说明。
- 补齐 `README.md`、`QUICK_START.md`、`CHANGELOG.md`、`CONTRIBUTING.md`、`SECURITY.md`、`CODE_OF_CONDUCT.md`。
- 收敛模块定位，明确不做 Hutool 式大而全通用工具集合。
- 保证默认 Maven reactor 不依赖本地 MySQL、Redis、Docker 等外部环境。
- 为 samples 保留无外部依赖的默认启动路径，并提供 Redis profile 示例。
- 将 `under-utils-test` 迁移到 Testcontainers，让 CI 可以执行真实 MySQL 集成测试。
- 补齐 Maven 发布元信息，建立 sources、javadocs 与可选 GPG 签名的发布构件验证链路。
- 接入 Central Publisher Portal 发布插件，建立本地 bundle dry run 和手动发布工作流。

## 首个稳定 Release

- 梳理 public API，减少发布后需要破坏性调整的命名和配置项。
- 持续维护发布前 API 审计记录，优先处理类名冲突、配置 key、异常语义和高风险默认值。
- 为核心工程模式能力补齐边界文档和失败语义说明。
- 建立 GitHub Actions CI，覆盖默认 `mvn test`、编译检查和 markdown 基础检查。
- Maven 坐标收敛为 GitHub namespace `io.github.yexianglun-d`，正式发布前完成 Central Portal token 和 GPG 私钥托管。
- 扩大 Testcontainers 集成测试覆盖面，优先补齐 Redis 缓存模板和 starter 自动装配验证。

## 后续方向

- 强化 Redis 缓存模板的可观测性，包括刷新失败、锁等待、缓存空值和逻辑过期状态指标。
- 扩展 OpenAPI 客户端的认证、签名、幂等键和业务错误解码示例。
- 补充 MyBatis 安全分页、审计填充和多数据库场景示例。
- 完善导入任务模板的异步任务、进度查询和错误导出扩展点。
- 根据真实使用反馈继续收缩 `under-utils-core` 中的历史基础工具，避免低复杂度工具方法重新扩张。

## 不在路线图内

- 复制 Hutool、Apache Commons、Guava 已成熟覆盖的通用工具方法。
- 引入强业务领域模型，例如订单、支付、营销、会员等单一业务线流程。
- 为了数量增加模块或 API，但没有清晰复用边界和测试覆盖。
