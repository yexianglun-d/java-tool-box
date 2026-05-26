## 摘要

- 

## 类型

- [ ] Bug 修复
- [ ] 新工程模式能力
- [ ] 文档
- [ ] 重构
- [ ] 测试
- [ ] 构建/CI

## 影响范围

- 影响模块：
- public API 变更：
- 兼容性影响：<!-- patch-compatible / minor-compatible / deprecation / breaking / none -->
- 外部依赖或运行时假设：
- 追溯来源：<!-- issue / PR / review-doc / internal-review / user-report / none -->

## 检查项

- [ ] 变更符合 Under-Utils 项目边界，不是 Hutool 式通用工具方法扩张。
- [ ] public API 变更遵循 `docs/COMPATIBILITY.md`。
- [ ] 已避免破坏性变更，或已说明例外原因和迁移路径。
- [ ] deprecated API 已提供替代方案或迁移说明。
- [ ] 行为变更已补充或更新测试。
- [ ] Bug 修复已补独立回归测试；有 issue 编号时，测试名或方法名可追溯到该编号。
- [ ] 无 issue 编号的修复已在测试、PR 或 CHANGELOG 中标明来源类型，例如 review-doc、internal-review 或 user-report。
- [ ] 默认 `mvn test` 不依赖 Redis、MySQL、Docker 或其他外部服务。
- [ ] 面向用户的变更已更新文档或 `CHANGELOG.md`，并说明原因、影响范围和兼容性分类。
- [ ] 未提交构建产物、本地路径、凭据或内部报告。

## 验证

```bash
# 粘贴本次执行过的命令
```
