# JSON 模块迁移备忘

`JsonUtils` 已经作为 `under-utils-core` 的 public API 发布，并且当前会让 core 默认带入 Jackson。这个状态在 `1.x` 内继续保持，避免老用户升级后出现运行时缺类。

## 当前结论

- `1.x` 不移除 `under-utils-core` 对 Jackson 的默认依赖。
- 运行时模块内部已经逐步不再调用 `JsonUtils`，而是使用各自模块内的 Jackson mapper 或 codec 边界。
- `2.0.0` 才评估把 JSON 能力迁移到独立模块，例如 `under-utils-json`。

## 迁移目标

- 降低 `under-utils-core` 默认依赖重量。
- 让 JSON 能力以独立坐标表达，避免 core 用户为了 ID、金额或字符串工具被动引入 Jackson。
- 保留清晰迁移路径，避免 major 升级时用户无法判断该依赖哪个新模块。

## 候选模块边界

| 模块 | 说明 |
|------|------|
| `under-utils-json` | 承载 `JsonUtils` 后续替代 API、Jackson codec 和可选配置。 |
| `under-utils-core` | `2.0.0` 中只保留低耦合基础能力，不默认依赖 Jackson。 |
| `under-utils-http` / `under-utils-redis` / `under-utils-spring` | 继续通过各自模块内 codec/mapper 使用 JSON，不依赖 core JSON 工具。 |

## `2.0.0` 迁移方案草案

1. 在 `1.x` 末期发布迁移提示，说明 `JsonUtils` 将进入独立模块。
2. 新增 `under-utils-json`，先提供与旧 `JsonUtils` 等价的核心能力。
3. 在 `2.0.0` 中从 `under-utils-core` 移除 Jackson 依赖，并给出明确依赖替换示例。
4. 保留 `docs/COMPATIBILITY.md` 中的 breaking 说明和 release notes 迁移段落。

## 兼容示例

`1.x`：

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-core</artifactId>
    <version>1.x.y</version>
</dependency>
```

`2.0.0` 以后如果仍需历史 JSON 工具：

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-json</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 非目标

- 不在 patch 版本把 Jackson 改成 optional。
- 不把 JSON 迁移和 HTTP/Redis codec 行为变更混在一个版本里。
- 不把 Fastjson、Gson 等多实现适配作为第一目标；优先解决 core 依赖重量和迁移路径。

## 发布前检查

- `under-utils-core` README 和 `CHANGELOG.md` 标明迁移影响。
- `api-compat` 或单独兼容检查能识别删除/移动的 public API。
- `under-utils-http`、`under-utils-redis`、`under-utils-spring` 的 JSON 行为测试在迁移后仍通过。
