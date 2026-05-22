# Under-Utils Core

## 📖 模块介绍

`under-utils-core` 是 Under-Utils 的无框架依赖基础模块，承载历史基础工具和轻量核心抽象。后续新增能力需要符合根 README 中的工程模式准入标准，不再扩张为 Hutool 式大而全工具集合。

## 🎯 设计原则

- **无框架依赖**：仅依赖 JDK，不依赖任何第三方框架
- **单一职责**：每个工具类只负责一个领域
- **线程安全**：所有工具方法都是静态的、无状态的
- **完善文档**：每个公共方法都有详细的 JavaDoc

## 📦 功能模块

### 字符串工具 (string)

**StringUtils** - 字符串处理工具类

- `isEmpty(String)` - 判断字符串是否为空
- `isNotEmpty(String)` - 判断字符串是否非空
- `isBlank(String)` - 判断字符串是否为空白
- `isNotBlank(String)` - 判断字符串是否非空白
- `trim(String)` - 安全的 trim 操作
- `trimToNull(String)` - trim 后为空则返回 null
- `trimToEmpty(String)` - trim 后为空则返回空字符串
- `defaultIfEmpty(String, String)` - 为空时返回默认值
- `defaultIfBlank(String, String)` - 为空白时返回默认值

### 日期时间工具 (time)

**LocalDateTimeUtils** - 基于 Java 8+ 的日期时间工具类

- `format(LocalDateTime)` - 格式化日期时间
- `format(LocalDateTime, String)` - 使用指定格式格式化
- `parseDateTime(String)` - 解析字符串为日期时间
- `tryParseDateTime(String)` - 安全解析，失败返回 null
- `now()` - 获取当前时间字符串
- `today()` - 获取当前日期字符串

### JSON 工具 (json)

**JsonUtils** - JSON 序列化与反序列化工具

- `toJson(Object)` - 对象转 JSON 字符串
- `toPrettyJson(Object)` - 对象转格式化 JSON 字符串
- `fromJson(String, Class<T>)` - JSON 转对象
- `fromJson(String, TypeReference<T>)` - JSON 转泛型对象（List、Map 等）
- `tryToJson(Object)` - 安全的序列化（失败返回 null）
- `tryFromJson(String, Class<T>)` - 安全的反序列化（失败返回 null）
- `isValidJson(String)` - 判断是否为有效的 JSON 格式

### 加密工具 (crypto)

**MD5Utils** - MD5 摘要工具

- `md5(String)` - 计算 MD5 值（32 位小写）
- `md5Upper(String)` - 计算 MD5 值（32 位大写）
- `md5Short(String)` - 计算 MD5 值（16 位小写）
- `md5WithSalt(String, String)` - 加盐 MD5
- `verify(String, String)` - 验证 MD5 值

**SHA256Utils** - SHA-256 摘要工具

- `sha256(String)` - 计算 SHA-256 值（64 位小写）
- `sha256Upper(String)` - 计算 SHA-256 值（64 位大写）
- `sha256WithSalt(String, String)` - 加盐 SHA-256
- `sha256Multiple(String, int)` - 多次哈希增强安全性
- `verify(String, String)` - 验证 SHA-256 值

**AESUtils** - AES 加密解密工具

- `generateKey()` / `generateKey(int)` - 生成随机密钥
- `generateIV()` - 生成随机 IV（初始化向量）
- `encrypt(String, String, String)` - AES 加密（CBC 模式）
- `decrypt(String, String, String)` - AES 解密（CBC 模式）
- `encryptECB(String, String)` - AES 加密（ECB 模式，不推荐）
- `encryptBytes(byte[], String, String)` - 字节数组加密

### ID 生成工具 (id)

**UUIDUtils** - UUID 生成工具

- `randomUUID()` - 生成标准 UUID（带连字符）
- `randomUUIDNoDash()` - 生成 UUID（不带连字符）
- `shortUUID()` - 生成短 UUID（22 位 Base62 编码）
- `nameUUIDFromString(String)` - 基于名称生成确定性 UUID
- `timeBasedUUID()` - 生成基于时间戳的有序 UUID
- `isValidUUID(String)` - 验证 UUID 格式
- `toBytes(String)` / `fromBytes(byte[])` - UUID 与字节数组转换

**IdGenerator** - 分布式 ID 生成工具（雪花算法）

- `nextId()` - 生成唯一 ID（long 类型）
- `nextIdStr()` - 生成唯一 ID（字符串类型）
- `parseId(long)` - 解析 ID，获取时间戳、机器 ID 等信息
- 支持自定义数据中心 ID 和机器 ID
- 线程安全，高性能（单机百万级/秒）

### 集合工具 (collection)

**CollectionUtils** - 集合处理工具

- `isEmpty(Collection)` / `isNotEmpty(Collection)` - 判断集合是否为空
- `isEmpty(Map)` / `isNotEmpty(Map)` - 判断 Map 是否为空
- `size(Collection)` / `size(Map)` - 获取集合/Map 大小
- `getFirst(List)` / `getLast(List)` - 安全获取第一个/最后一个元素
- `get(List, index)` - 安全获取指定索引的元素（越界返回 null）
- `partition(List, batchSize)` - 列表分批处理
- `distinct(Collection)` - 集合去重（保持顺序）
- `map(Collection, Function)` - 集合转换（映射）
- `toList(T...)` - 数组转 List
- `union(Collection, Collection)` - 集合并集
- `intersection(Collection, Collection)` - 集合交集
- `subtract(Collection, Collection)` - 集合差集
- `contains(Collection, element)` - 判断集合是否包含元素

### 金额计算工具 (money)

**MoneyUtils** - 金额计算工具

- `yuan2Fen(BigDecimal)` - 元转分
- `fen2Yuan(Long)` - 分转元
- `add(BigDecimal, BigDecimal)` - 金额相加
- `add(BigDecimal...)` - 多个金额相加
- `subtract(BigDecimal, BigDecimal)` - 金额相减
- `multiply(BigDecimal, BigDecimal)` - 金额相乘
- `divide(BigDecimal, BigDecimal)` - 金额相除
- `compare(BigDecimal, BigDecimal)` - 比较金额大小
- `equals(BigDecimal, BigDecimal)` - 判断金额是否相等
- `greaterThan/lessThan/greaterThanOrEqual/lessThanOrEqual` - 大小比较
- `isZero/isPositive/isNegative` - 判断金额状态
- `format(BigDecimal)` - 格式化金额显示（千分位）
- `formatWithSymbol(BigDecimal)` - 格式化金额（带货币符号）
- `max/min/abs/negate` - 最大值、最小值、绝对值、相反数

### 参数校验工具 (validation)

**ValidationUtils** - 参数校验工具

- `isPhone(String)` - 校验手机号（中国大陆 11 位）
- `isEmail(String)` - 校验邮箱格式
- `isIdCard(String)` - 校验身份证号（18 位，含校验位验证）
- `isUrl(String)` - 校验 URL 格式
- `isIpv4(String)` - 校验 IPv4 地址
- `isChinese(String)` - 判断是否全中文
- `containsChinese(String)` - 判断是否包含中文
- `isInteger(String)` - 判断是否为整数
- `isDecimal(String)` - 判断是否为小数
- `isNumber(String)` - 判断是否为数字（整数或小数）
- `isInRange(Number, min, max)` - 判断数值是否在范围内
- `isLengthInRange(String, min, max)` - 判断字符串长度是否在范围内
- `isAlpha(String)` - 判断是否只包含字母
- `isAlphanumeric(String)` - 判断是否只包含字母和数字
- `matches(String, regex)` - 判断是否匹配正则表达式

### IO 工具 (io)

**IOUtils** - IO 操作工具（待实现）

## 💡 使用示例

### 字符串工具示例

```java
import com.undernine.utils.core.string.StringUtils;

// 判空
if (StringUtils.isEmpty(name)) {
    System.out.println("名称为空");
}

// 默认值
String displayName = StringUtils.defaultIfBlank(name, "匿名用户");

// 安全 trim
String trimmed = StringUtils.trimToNull(input);
```

### 日期时间工具示例

```java
import com.undernine.utils.core.time.LocalDateTimeUtils;
import java.time.LocalDateTime;

// 格式化当前时间
String nowStr = LocalDateTimeUtils.now();
System.out.println(nowStr); // 输出：2024-12-02 17:13:00

// 解析字符串
LocalDateTime dateTime = LocalDateTimeUtils.parseDateTime("2024-12-02 17:13:00");

// 安全解析（不抛异常）
LocalDateTime result = LocalDateTimeUtils.tryParseDateTime(userInput);
if (result == null) {
    System.out.println("日期格式错误");
}
```

### JSON 工具示例

```java
import com.undernine.utils.core.json.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;

// 对象转 JSON
User user = new User("John", 25);
String json = JsonUtils.toJson(user);
System.out.println(json); // 输出：{"name":"John","age":25}

// JSON 转对象
String json = "{\"name\":\"John\",\"age\":25}";
User user = JsonUtils.fromJson(json, User.class);

// JSON 转 List
String jsonArray = "[{\"name\":\"John\"},{\"name\":\"Jane\"}]";
List<User> users = JsonUtils.fromJson(jsonArray, new TypeReference<List<User>>() {});

// 安全的序列化/反序列化（不抛异常）
String json = JsonUtils.tryToJson(user);
User user = JsonUtils.tryFromJson(invalidJson, User.class); // 失败返回 null

// 判断 JSON 格式是否有效
boolean valid = JsonUtils.isValidJson("{\"name\":\"John\"}"); // true
```

### 集合工具示例

```java
import com.undernine.utils.core.collection.CollectionUtils;
import java.util.Arrays;
import java.util.List;

// 判空
List<String> list = Arrays.asList("a", "b", "c");
if (CollectionUtils.isNotEmpty(list)) {
    System.out.println("列表不为空");
}

// 安全获取元素
String first = CollectionUtils.getFirst(list); // "a"
String last = CollectionUtils.getLast(list); // "c"
String element = CollectionUtils.get(list, 10); // null（越界返回 null）

// 列表分批（分页）
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
List<List<Integer>> batches = CollectionUtils.partition(numbers, 3);
// 结果：[[1, 2, 3], [4, 5, 6], [7]]

// 集合去重
List<String> withDuplicates = Arrays.asList("a", "b", "a", "c", "b");
List<String> distinct = CollectionUtils.distinct(withDuplicates);
// 结果：["a", "b", "c"]

// 集合转换
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
List<Integer> lengths = CollectionUtils.map(names, String::length);
// 结果：[5, 3, 7]

// 集合运算
List<String> list1 = Arrays.asList("a", "b", "c");
List<String> list2 = Arrays.asList("b", "c", "d");
List<String> union = CollectionUtils.union(list1, list2); // ["a", "b", "c", "d"]
List<String> intersection = CollectionUtils.intersection(list1, list2); // ["b", "c"]
List<String> subtract = CollectionUtils.subtract(list1, list2); // ["a"]
```

### 参数校验工具示例

```java
import com.undernine.utils.core.validation.ValidationUtils;

// 手机号校验
boolean validPhone = ValidationUtils.isPhone("13812345678"); // true
boolean invalidPhone = ValidationUtils.isPhone("12345678901"); // false

// 邮箱校验
boolean validEmail = ValidationUtils.isEmail("user@example.com"); // true

// 身份证号校验（含校验位验证）
boolean validIdCard = ValidationUtils.isIdCard("110101199003074796"); // true

// URL 校验
boolean validUrl = ValidationUtils.isUrl("https://www.example.com"); // true

// IP 地址校验
boolean validIp = ValidationUtils.isIpv4("192.168.1.1"); // true

// 中文校验
boolean isChinese = ValidationUtils.isChinese("中文"); // true
boolean containsChinese = ValidationUtils.containsChinese("abc中文"); // true

// 数字校验
boolean isInteger = ValidationUtils.isInteger("123"); // true
boolean isDecimal = ValidationUtils.isDecimal("123.45"); // true
boolean isNumber = ValidationUtils.isNumber("123.45"); // true

// 范围校验
boolean inRange = ValidationUtils.isInRange(50, 1, 100); // true
boolean lengthOk = ValidationUtils.isLengthInRange("hello", 1, 10); // true

// 字母和数字校验
boolean isAlpha = ValidationUtils.isAlpha("abc"); // true
boolean isAlphanumeric = ValidationUtils.isAlphanumeric("abc123"); // true

// 自定义正则表达式
boolean matches = ValidationUtils.matches("abc123", "^[a-z0-9]+$"); // true
```

### 金额计算工具示例

```java
import com.undernine.utils.core.money.MoneyUtils;
import java.math.BigDecimal;

// 元转分 / 分转元
Long fen = MoneyUtils.yuan2Fen(new BigDecimal("10.50")); // 1050
BigDecimal yuan = MoneyUtils.fen2Yuan(1050L); // 10.50

// 金额计算（避免精度问题）
BigDecimal sum = MoneyUtils.add(new BigDecimal("10.50"), new BigDecimal("20.30")); // 30.80
BigDecimal diff = MoneyUtils.subtract(new BigDecimal("30.50"), new BigDecimal("10.30")); // 20.20
BigDecimal product = MoneyUtils.multiply(new BigDecimal("10.50"), new BigDecimal("2")); // 21.00
BigDecimal quotient = MoneyUtils.divide(new BigDecimal("10.00"), new BigDecimal("3")); // 3.33

// 多个金额相加
BigDecimal total = MoneyUtils.add(
    new BigDecimal("10.50"),
    new BigDecimal("20.30"),
    new BigDecimal("5.00")
); // 35.80

// 金额比较
boolean isEqual = MoneyUtils.equals(new BigDecimal("10.50"), new BigDecimal("10.50")); // true
boolean isGreater = MoneyUtils.greaterThan(new BigDecimal("20.00"), new BigDecimal("10.00")); // true

// 金额状态判断
boolean isZero = MoneyUtils.isZero(new BigDecimal("0.00")); // true
boolean isPositive = MoneyUtils.isPositive(new BigDecimal("10.50")); // true

// 格式化显示
String formatted = MoneyUtils.format(new BigDecimal("12345.67")); // "12,345.67"
String withSymbol = MoneyUtils.formatWithSymbol(new BigDecimal("12345.67")); // "¥12,345.67"

// 最大值/最小值
BigDecimal max = MoneyUtils.max(new BigDecimal("10.50"), new BigDecimal("20.30")); // 20.30
BigDecimal min = MoneyUtils.min(new BigDecimal("10.50"), new BigDecimal("20.30")); // 10.50
```

### 加密工具示例

```java
import com.undernine.utils.core.crypto.MD5Utils;
import com.undernine.utils.core.crypto.SHA256Utils;

// MD5 摘要
String md5 = MD5Utils.md5("hello world"); // 5eb63bbbe01eeed093cb22bb8f5acdc3
String md5Upper = MD5Utils.md5Upper("hello world"); // 5EB63BBBE01EEED093CB22BB8F5ACDC3
String md5Short = MD5Utils.md5Short("hello world"); // e01eeed093cb22bb (16位)

// MD5 加盐（提高安全性）
String password = "myPassword";
String salt = "randomSalt123";
String md5WithSalt = MD5Utils.md5WithSalt(password, salt);

// MD5 验证
boolean isValid = MD5Utils.verify("hello world", md5); // true

// SHA-256 摘要（更安全）
String sha256 = SHA256Utils.sha256("hello world");
// b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9

// SHA-256 加盐
String sha256WithSalt = SHA256Utils.sha256WithSalt(password, salt);

// SHA-256 多次哈希（增强安全性，防暴力破解）
String strongHash = SHA256Utils.sha256Multiple(password, 10000);

// SHA-256 验证
boolean sha256Valid = SHA256Utils.verify("hello world", sha256); // true

// AES 对称加密
String aesKey = AESUtils.generateKey(); // 生成随机密钥
String aesIV = AESUtils.generateIV(); // 生成随机 IV

// 加密
String plainText = "sensitive data";
String encrypted = AESUtils.encrypt(plainText, aesKey, aesIV);
System.out.println("加密后: " + encrypted);

// 解密
String decrypted = AESUtils.decrypt(encrypted, aesKey, aesIV);
System.out.println("解密后: " + decrypted); // sensitive data
```

### ID 生成工具示例

```java
import com.undernine.utils.core.id.UUIDUtils;
import com.undernine.utils.core.id.IdGenerator;

// UUID 生成
String uuid = UUIDUtils.randomUUID(); // 550e8400-e29b-41d4-a716-446655440000
String uuidNoDash = UUIDUtils.randomUUIDNoDash(); // 550e8400e29b41d4a716446655440000
String shortUuid = UUIDUtils.shortUUID(); // 7NLCAyd0K4mQT0wgXGJ8Pq (22位)

// 基于名称的 UUID（确定性，相同输入产生相同输出）
String nameUuid1 = UUIDUtils.nameUUIDFromString("test");
String nameUuid2 = UUIDUtils.nameUUIDFromString("test");
// nameUuid1 equals nameUuid2

// 时间有序 UUID（适合数据库索引）
String timeUuid = UUIDUtils.timeBasedUUID();

// UUID 验证
boolean valid = UUIDUtils.isValidUUID("550e8400-e29b-41d4-a716-446655440000"); // true

// 雪花算法 ID 生成器
IdGenerator idGenerator = new IdGenerator(1, 1); // 数据中心ID=1, 机器ID=1
long id = idGenerator.nextId(); // 1234567890123456789
String idStr = idGenerator.nextIdStr(); // "1234567890123456789"

// 解析 ID 信息
IdGenerator.IdInfo info = idGenerator.parseId(id);
System.out.println("时间戳: " + info.getTimestamp());
System.out.println("数据中心ID: " + info.getDatacenterId());
System.out.println("机器ID: " + info.getWorkerId());
System.out.println("序列号: " + info.getSequence());
```

## 🔧 依赖配置

在项目的 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.undernineplaces</groupId>
    <artifactId>under-utils-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 📝 开发规范

### 工具类规范

1. 所有工具类必须使用 `final` 修饰
2. 必须提供私有构造方法并抛出 `UnsupportedOperationException`
3. 所有方法必须是 `static`
4. 方法必须是无状态的、线程安全的

### 命名规范

- 工具类：`XxxUtils`
- 方法名：准确描述功能，如 `isEmpty`、`format`、`parse`
- 参数名：具有描述性，如 `str`、`dateTime`、`pattern`

### 文档规范

- 每个公共方法必须有 JavaDoc
- JavaDoc 必须包含：方法用途、参数说明、返回值说明、异常说明

### 测试规范

- 每个工具类必须有对应的测试类
- 测试类命名：`XxxUtilsTest`
- 必须覆盖：正常场景、边界场景、异常场景

## 🤝 贡献指南

请参考项目根目录的 [CONTRIBUTING.md](../CONTRIBUTING.md)。

## 📄 许可证

本项目采用 MIT 许可证，详见 [LICENSE](../LICENSE)。
