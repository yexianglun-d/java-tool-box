# Under-Utils Core

## 📖 模块介绍

`under-utils-core` 是 Under-Utils 工具库的核心模块，提供基础的、无框架依赖的通用工具类。

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

**JsonUtils** - JSON 序列化与反序列化工具（待实现）

### 加密工具 (crypto)

**AESUtils** - AES 加密解密工具（待实现）  
**MD5Utils** - MD5 摘要工具（待实现）  
**SHA256Utils** - SHA-256 摘要工具（待实现）

### ID 生成工具 (id)

**IdGenerator** - 分布式 ID 生成工具（待实现）  
**UUIDUtils** - UUID 生成工具（待实现）

### 集合工具 (collection)

**CollectionUtils** - 集合处理工具（待实现）

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

请参考项目根目录的 [README.md](../README.md) 中的贡献规范。

## 📄 许可证

本项目采用 MIT 许可证。
