# 路线图

本路线图用于说明 Under-Utils 的演进方向。优先级会根据 issue、真实使用反馈和维护成本调整。

## 已完成

- `v1.0.0` 已发布到 Maven Central，坐标命名空间为 `io.github.yexianglun-d`。
- GitHub Actions CI 覆盖默认编译、默认测试和 Testcontainers 集成测试。
- 接入 Central Portal 发布流程、GPG 签名 profile、sources 和 javadocs 生成。
- 清理占位模块、空占位类和过时内部规划文档。
- 明确 Under-Utils 不做 Hutool 式通用工具集合。
- 将依赖 Docker 的集成测试收口到 `under-utils-test`，通过 `integration-tests` profile 启用。
- 保证 `under-utils-samples` 默认无 Redis/MySQL 也能启动。
- 补齐 GitHub 社区文件：许可证、贡献指南、安全策略、行为准则、Issue 模板和 PR 模板。
- 将公开文档收敛为社区维护文档写法，并补齐 Redis、starter、biz 模块 README。
- 补充 starter 自动装配测试，覆盖 Redis store 切换和用户自定义 Bean 退让。
- 补充 Redis Testcontainers 测试，覆盖 cache-aside 和逻辑过期缓存行为。
- 补充 public API 兼容性策略，明确变更、弃用、配置迁移和 Release Notes 要求。
- 拆分 `under-utils-spring-starter` 与 `under-utils-redis-starter`，旧 `under-utils-starter` 保留为兼容聚合入口。

## 近期计划

- 持续维护 API Review，配置 key、异常语义和 starter 默认行为发生变化时同步记录。
- 增加 API 兼容性检查，避免 patch/minor 版本误删 public API。
- 继续收缩 `under-utils-core` 历史工具方法的扩张倾向。

## 后续方向

- 改进 Redis 缓存模板可观测性，包括刷新失败、锁等待、空值缓存和逻辑过期状态。
- 扩展 OpenAPI 示例：签名、幂等、token 刷新和业务错误解码。
- 补充 MyBatis 审计填充和多数据库假设示例。
- 扩展导入任务模板的异步执行、进度查询和错误导出能力。
- 评估将历史 JSON 工具迁移到独立模块，降低 `under-utils-core` 对 Jackson 的默认依赖。

## 非目标

- 重建 Hutool、Apache Commons、Guava 或 JDK 已成熟覆盖的工具方法。
- 为了模块数量而新增没有测试复用边界的模块。
- 写入订单、支付、营销、会员等产品域专属流程。
