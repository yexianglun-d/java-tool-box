# Minor 版本发布说明模板

用于 `1.x.0` 这类 minor 版本。minor 版本可以新增 API、配置项或模块能力，但默认应保持源码兼容。

## 标题

`v1.x.0`

## 摘要

说明本次新增的工程模式能力，以及它解决的重复复杂度。

## 新增能力

- 模块：
- 主要 API：
- 典型场景：
- 失败语义：

## 变更

- 文档：
- 测试：
- samples：
- 构建或发布：

## 兼容性

- 是否新增 public API：
- 是否新增配置 key：
- 是否调整默认行为：
- 是否包含 deprecated API：
- 迁移说明：

## 验证

```bash
mvn test
mvn -Prelease -DskipTests package
mvn -Pintegration-tests -pl under-utils-test -am test
```

## 发布后检查

- Maven Central 构件可搜索。
- GitHub Release Notes 已按本模板填写。
- CHANGELOG 与模块 README 已同步。
