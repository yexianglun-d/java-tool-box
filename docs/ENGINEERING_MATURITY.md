# 工程成熟度推进

本文件用于跟踪 Under-Utils 从“功能可用”走向“可长期维护”的工程化事项。它不是功能清单，而是维护质量清单：生命周期怎么结束、指标怎么看、Bug 怎么防回归、CHANGELOG 怎么追溯、API 怎么保持稳定。

## 使用方式

- 每次开始一项成熟度改进前，先在本文件确认目标、范围和验收标准。
- 实现过程中同步更新状态，避免只完成代码而没有留下维护上下文。
- 合并前检查对应测试、文档和兼容性影响是否都已闭环。
- 与功能规划相关但还没有进入实现的事项，放到 [FUTURE_FEATURES.md](FUTURE_FEATURES.md)。

## 状态定义

| 状态 | 含义 |
|------|------|
| `待做` | 已确认有价值，但尚未开始设计或实现。 |
| `设计中` | 正在明确 API、模块边界、兼容性和验收标准。 |
| `实现中` | 已进入代码实现或测试补齐阶段。 |
| `已完成` | 代码、测试、文档和变更记录均已完成。 |
| `暂缓` | 当前阶段不做，保留原因和重新评估条件。 |

## 当前推进项

| 编号 | 事项 | 状态 | 主要模块 | 验收标准 |
|------|------|------|----------|----------|
| M-001 | 缓存内置命中率指标 | 已完成 | `under-utils-redis` | 调用方不需要手写聚合器即可读取 hit/miss/load/error 等基础指标；不强绑定 Micrometer；旧 `CacheOperationObserver` SPI 继续可用。 |
| M-002 | 本地状态主动清理机制 | 已完成 | `under-utils-spring`、`under-utils-biz` | 本地限流、防重复提交和异步任务状态具备可关闭、可配置的主动清理能力；不使用不可控的全局静态线程池；关闭应用时可释放资源。 |
| M-003 | Bug 回归测试追踪规范 | 已完成 | `tests`、`CONTRIBUTING.md` | 重要 Bug 修复必须新增独立回归测试；有 issue 编号时使用 issue 命名，无 issue 时使用 `Regression...Test` 或等价可追溯命名。 |
| M-004 | CHANGELOG 可追溯性 | 已完成 | `CHANGELOG.md`、`docs/releases` | 面向用户的变更记录说明原因、影响范围、兼容性分类和对应 issue/PR；无外部 issue 时说明来源，例如 review-doc 或 internal-review。 |
| M-005 | 高频 API 链式体验梳理 | 已完成 | `under-utils-http`、`under-utils-redis`、`under-utils-biz` | 高频构建路径支持自然链式调用或 builder；避免为了统计 `return this` 数量而制造无意义 API。 |
| M-006 | Crypto 方向重新建模 | 暂缓 | `under-utils-core`、候选 `under-utils-crypto` | `AESUtils` 仅做历史兼容维护；重新进入条件和候选模型见 [CRYPTO_REDESIGN.md](CRYPTO_REDESIGN.md)。 |

## 实施顺序建议

1. M-006：已明确暂缓原因、重新进入条件和候选模块边界；除非有明确业务需求，否则不在 `1.x` 内展开。

## 单项完成标准

一项成熟度改进标记为 `已完成` 前，至少满足：

- public API 或默认行为变化已记录兼容性影响。
- 关键路径有单元测试或可复现集成测试。
- Bug 修复类事项有独立回归测试。
- README、模块 README、`CHANGELOG.md` 或 `docs/API_REVIEW.md` 已按需更新。
- 默认测试不依赖 Redis、MySQL、Docker、外网或私有基础设施。
- 涉及 public API 的改动通过 `api-compat` 或说明无法自动检查的原因。

## 推荐验证命令

```bash
/usr/local/apache-maven-3.9.9/bin/mvn -q test
/usr/local/apache-maven-3.9.9/bin/mvn -Prelease -DskipTests package
/usr/local/apache-maven-3.9.9/bin/mvn -gs docs/central-dry-run-settings.xml -s docs/central-dry-run-settings.xml \
  -Papi-compat \
  -pl under-utils-core,under-utils-http,under-utils-ai,under-utils-ai-starter,under-utils-spring,under-utils-redis,under-utils-mybatis,under-utils-biz \
  -am \
  -DskipTests \
  verify
```
