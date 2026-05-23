# Under-Utils Core

低耦合基础能力模块。

本模块不是通用工具方法集合。部分历史静态工具会继续保留用于兼容，但新增能力应避免与 JDK、Spring、Hutool、Apache Commons、Guava 重复。

## 当前边界

| API | 状态 | 说明 |
|-----|------|------|
| `IdGenerator` | 主线维护 | 雪花风格本地 ID 生成器，多节点需要自行保证 worker/datacenter 唯一。 |
| `MoneyUtils` | 主线维护 | 常见 BigDecimal 金额计算、分/元转换和固定舍入语义。 |
| `JsonUtils` | 兼容维护 | 使用模块内置 `ObjectMapper`；复杂应用应优先使用自己的 mapper 或 codec。 |
| `StringUtils`、`CollectionUtils`、`LocalDateTimeUtils`、`ValidationUtils`、`UUIDUtils` | 兼容维护 | 保留已有方法，不作为继续扩张方向。 |
| `MD5Utils`、`SHA256Utils`、`AESUtils` | 兼容维护 | 安全敏感历史工具，新安全能力应走业务统一加密和密钥管理策略。 |

## 依赖

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-core</artifactId>
    <version>1.0.1</version>
</dependency>
```

## 雪花 ID

```java
IdGenerator generator = new IdGenerator(1, 1);

long id = generator.nextId();
String idText = generator.nextIdStr();

IdGenerator.IdInfo info = generator.parseId(id);
long timestamp = info.getTimestamp();
long datacenterId = info.getDatacenterId();
long workerId = info.getWorkerId();
```

注意：

- `datacenterId` 和 `workerId` 取值范围为 `0..31`。
- 生成器依赖系统时钟单调前进，发生时钟回拨会拒绝生成 ID。
- 多节点部署时，节点 ID 分配不由本模块负责。

## 金额工具

```java
Long fen = MoneyUtils.yuan2Fen(new BigDecimal("10.50"));
BigDecimal yuan = MoneyUtils.fen2Yuan(1050L);

BigDecimal total = MoneyUtils.add(
        new BigDecimal("10.50"),
        new BigDecimal("20.30")
);

BigDecimal avg = MoneyUtils.divide(total, new BigDecimal("3"));
String display = MoneyUtils.formatWithSymbol(total);
```

默认语义：

- 小数位：`2`。
- 舍入模式：`RoundingMode.HALF_UP`。

多币种、税费、会计科目或精度敏感场景，应在业务侧定义明确的金额模型。

## JSON 兼容入口

```java
String json = JsonUtils.toJson(payload);
Payload parsed = JsonUtils.fromJson(json, Payload.class);
```

`JsonUtils.getObjectMapper()` 返回共享 mapper，调用方不应修改其配置。

## 加密相关说明

- `MD5Utils` 不适合密码存储、签名或安全校验。
- `SHA256Utils` 是摘要工具，不是密码哈希方案。
- `AESUtils` 不提供密钥轮换、KMS 集成、密文版本管理或认证加密策略。
- `AESUtils.encryptECB` 和 `AESUtils.decryptECB` 仅保留历史兼容，新代码不要使用 ECB 模式。

## 贡献规则

不要在本模块新增低复杂度工具方法。新增 core API 应定义清晰复用边界、失败行为和测试覆盖。
