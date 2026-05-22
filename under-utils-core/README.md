# Under-Utils Core

`under-utils-core` 是 Under-Utils 的低耦合基础模块。它不再按“常用工具大全”扩张，也不定位为 Hutool、Apache Commons 或 Guava 的替代品。

本模块现有静态工具会保留兼容维护，避免影响历史调用；新增能力必须符合根 README 的工程模式准入标准：复杂性高、重复实现成本高、边界清晰，并且不能只是对 JDK 或成熟三方库的轻量包装。

## 模块边界

| 分类 | 状态 | 说明 |
|------|------|------|
| `IdGenerator` | 主线保留 | 雪花算法 ID 生成器，适合需要本地生成趋势递增 ID 的场景 |
| `MoneyUtils` | 主线保留 | 固化 BigDecimal 金额计算、分/元转换和默认舍入语义 |
| `JsonUtils` | 兼容维护 | 使用内置 Jackson `ObjectMapper` 的历史入口，新项目建议使用应用自己的 mapper/codec |
| `StringUtils` / `CollectionUtils` / `LocalDateTimeUtils` / `ValidationUtils` / `UUIDUtils` | 兼容维护 | 与 JDK、Apache Commons、Guava、Bean Validation 或 Hutool 能力明显重叠，不再新增方法 |
| `MD5Utils` / `SHA256Utils` / `AESUtils` | 兼容维护 | 安全敏感历史工具，不再作为推荐加密/摘要入口 |

## 使用示例

### 雪花 ID

```java
import com.undernine.utils.core.id.IdGenerator;

IdGenerator generator = new IdGenerator(1, 1);

long id = generator.nextId();
String idText = generator.nextIdStr();

IdGenerator.IdInfo info = generator.parseId(id);
long timestamp = info.getTimestamp();
long datacenterId = info.getDatacenterId();
long workerId = info.getWorkerId();
```

注意：

- `datacenterId` 和 `workerId` 范围均为 `0-31`。
- 该实现依赖系统时钟，发生时钟回拨时会拒绝生成 ID。
- 多节点部署时必须保证节点标识唯一。

### 金额计算

```java
import com.undernine.utils.core.money.MoneyUtils;

import java.math.BigDecimal;

Long fen = MoneyUtils.yuan2Fen(new BigDecimal("10.50"));
BigDecimal yuan = MoneyUtils.fen2Yuan(1050L);

BigDecimal total = MoneyUtils.add(
    new BigDecimal("10.50"),
    new BigDecimal("20.30"),
    new BigDecimal("5.00")
);

BigDecimal avg = MoneyUtils.divide(total, new BigDecimal("3"));
String display = MoneyUtils.formatWithSymbol(total);
```

注意：

- 默认金额小数位为 `2`。
- 默认舍入模式为 `RoundingMode.HALF_UP`。
- 更复杂的币种、税费、会计科目或多精度场景，应封装业务自己的金额模型。

### JSON 兼容入口

```java
import com.undernine.utils.core.json.JsonUtils;

String json = JsonUtils.toJson(payload);
Payload parsed = JsonUtils.fromJson(json, Payload.class);
```

注意：

- `JsonUtils` 使用模块内置单例 `ObjectMapper`。
- Spring Boot 或复杂应用应优先注入应用自己的 `ObjectMapper`，避免序列化配置分叉。
- `JsonUtils.getObjectMapper()` 返回共享实例，不建议修改其配置。

## 兼容维护工具

以下类仍可使用，但不会继续扩展为工具方法集合：

- `StringUtils`
- `CollectionUtils`
- `LocalDateTimeUtils`
- `ValidationUtils`
- `UUIDUtils`
- `MD5Utils`
- `SHA256Utils`
- `AESUtils`

安全相关注意：

- `MD5Utils` 仅适合历史兼容或非安全校验，不适合密码、签名或安全校验。
- `SHA256Utils` 是摘要工具，不能替代 BCrypt、PBKDF2、Argon2 等密码存储方案。
- `AESUtils` 不提供认证加密、密钥轮换、KMS 集成或密文版本治理；新代码应优先使用 AES/GCM、JDK JCA 或统一加密服务。
- `AESUtils.encryptECB` 和 `AESUtils.decryptECB` 仅保留历史兼容，新代码不得使用 ECB 模式。

## 依赖配置

```xml
<dependency>
    <groupId>com.undernineplaces</groupId>
    <artifactId>under-utils-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 贡献规则

- 不新增 `XxxUtils.isEmpty`、`DateUtils.format`、`CollectionUtils.map` 这类低复杂度通用方法。
- 不复制 Hutool、Apache Commons、Guava、JDK 已成熟覆盖的工具 API。
- 新增能力必须说明工程场景、失败语义、线程/并发边界和测试覆盖。
- 安全相关能力必须明确算法限制、密钥管理方式和不适用场景。

更多规则请参考项目根目录的 [CONTRIBUTING.md](../CONTRIBUTING.md)。
