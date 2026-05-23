# Patch 版本发布说明模板

用于 `1.0.x` 这类 patch 版本。patch 版本应保持源码兼容，不改变默认副作用和已文档化失败语义。

## 标题

`v1.0.x`

## 摘要

一句话说明本次修复或小幅增强解决了什么问题。

## 变更

- 修复：
- 文档：
- 测试：
- 其他兼容增强：

## 兼容性

- public API：
- 配置 key：
- 默认行为：
- 迁移要求：

## 验证

```bash
mvn test
mvn -Prelease -DskipTests package
```

## 发布后检查

- Maven Central 构件可搜索。
- GitHub Actions 发布任务成功。
- README / CHANGELOG 已更新。
