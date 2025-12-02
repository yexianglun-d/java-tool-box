# 《Under-Utils 工具库开发规范与贡献指南》（Draft v1.0）

## 📌 目录
1. 项目背景  
2. 项目目标  
3. 工程结构规范  
4. 模块划分规范  
5. 依赖与版本管理规范（BOM）  
6. 代码规范  
7. 工具类开发规范  
8. 命名规范  
9. Git 协作流程  
10. 分支策略  
11. Pull Request & Code Review 规范  
12. 单元测试规范  
13. 文档规范  
14. 版本号管理与发布流程  
15. 质量保障要求  
16. 安全规范  
17. 未来扩展规划（预留给后续完善）

---

## 1. 项目背景

Under-Utils 是一个由多人维护的 **企业级 Java 工具库集合**，旨在统一各项目常用工具方法、封装逻辑、业务组件，避免重复造轮子，提高研发效率，保证各项目基础框架的一致性。

本规范文档用于指导：

- 如何开发工具类模块  
- 如何参与项目协作  
- 如何管理依赖版本  
- 如何保证质量与安全  
- 如何发布可用版本  

---

## 2. 项目目标

- 形成一套稳定、可持续迭代的工具库体系  
- 支持多人协作，规范清晰、边界明确  
- 统一公司/个人所有项目的基础依赖版本  
- 提供可复用、可维护、可扩展的公共能力  
- 为后续开源或对外发布打基础，形成个人/团队技术品牌

---

## 3. 工程结构规范

当前推荐的整体工程结构如下：

```text
under-utils/                           <-- 顶层聚合工程（Parent）
├── pom.xml                            <-- 父 POM（统一依赖 & 插件管理）
│
├── under-utils-bom/                   <-- 依赖与版本统一管理（BOM）
│   └── pom.xml
│
├── under-utils-core/                  <-- 基础通用工具模块
│   └── src/main/java/com/undernine/utils/core/...
│
├── under-utils-http/                  <-- HTTP 相关封装模块
│   └── src/main/java/com/undernine/utils/http/...
│
├── under-utils-redis/                 <-- Redis 与分布式锁封装模块
│   └── src/main/java/com/undernine/utils/redis/...
│
├── under-utils-mybatis/               <-- MyBatis / MyBatis-Plus 增强模块
│   └── src/main/java/com/undernine/utils/mybatis/...
│
├── under-utils-spring/                <-- Spring 通用组件模块
│   └── src/main/java/com/undernine/utils/spring/...
│
├── under-utils-starter/               <-- Spring Boot Starter 自动配置模块
│   └── src/main/java/com/undernine/utils/starter/...
│
├── under-utils-biz/                   <-- 可复用业务组件模块
│   └── src/main/java/com/undernine/utils/biz/...
│
└── README.md                          <-- 本规范与整体说明文档
```

**工程结构要求：**

1. 每个模块必须具备独立的 `pom.xml`，遵循单一职责原则。  
2. 模块之间尽量避免循环依赖，必要依赖必须在文档中显式说明。  
3. 通用、无框架耦合的工具优先放入 `under-utils-core`。  
4. 与框架强相关的工具放入对应模块（如 Spring、MyBatis、Redis）。  
5. 业务相关的可复用逻辑（短信、Excel 导出等）统一放入 `under-utils-biz`。  
6. 新增模块时，请务必在 `under-utils-bom` 中声明版本号，且按照该项目进行统一命名。

---

## 4. 模块划分规范

### 4.1 模块职责说明

| 模块名                     | 职责定位                                      | 依赖上限                          |
|-------------------------|-------------------------------------------|-----------------------------------|
| under-utils-bom         | 统一版本与依赖管理 BOM                             | 无业务代码                        |
| under-utils-core        | 基础工具类：字符串、日期、金额、ID、加解密等                   | 仅依赖 JDK                        |
| under-utils-http        | HTTP 请求封装（HttpClient/OkHttp/RestTemplate） | 可依赖 HTTP 相关第三方库          |
| under-utils-redis       | Redis 操作封装、分布式锁                           | 依赖 Spring Data Redis / Redisson |
| under-utils-mybatis     | MyBatis / MyBatis-Plus 扩展、分页、元数据填充        | 依赖 MyBatis / MyBatis-Plus      |
| under-utils-spring      | Spring 通用组件（返回体、异常、工具等）                   | 依赖 Spring Framework             |
| under-utils-starter     | Spring Boot Starter 自动配置                  | 依赖 Spring Boot                  |
| under-utils-biz         | 可复用业务组件（短信、上传、Excel 等）                    | 可依赖具体业务相关第三方库        |
| under-utils-xxx  （新增模块） | 按照项目进行统一命名                                | 可依赖具体业务相关第三方库        |
### 4.2 新增模块要求

- 新增模块前必须经过评审（至少 1 人技术负责人确认）。  
- 模块必须有明确功能边界与描述（写入 README）。  
- 模块命名需简洁清晰，避免使用过于宽泛的名称（如 common、misc 等）。  

---

## 5. 依赖与版本管理规范（BOM）

项目采用统一 BOM 管理模式：

- 所有版本号只允许出现在 `under-utils-bom` 模块中。  
- 子模块禁止直接填写第三方依赖版本号。  
- 如需升级依赖版本，必须通过 BOM 统一调整。  

### 5.1 BOM 基本结构示例

```xml
<project>
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.undernineplaces</groupId>
    <artifactId>under-utils-bom</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <dependencyManagement>
        <dependencies>

            <!-- Spring Boot -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>3.3.1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- MyBatis Plus -->
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-boot-starter</artifactId>
                <version>3.5.6</version>
            </dependency>

            <!-- Redis -->
            <dependency>
                <groupId>org.redisson</groupId>
                <artifactId>redisson</artifactId>
                <version>3.36.0</version>
            </dependency>

            <!-- Jackson -->
            <dependency>
                <groupId>com.fasterxml.jackson.core</groupId>
                <artifactId>jackson-databind</artifactId>
                <version>2.17.2</version>
            </dependency>

            <!-- 其他依赖版本统一在此处管理 -->

        </dependencies>
    </dependencyManagement>
</project>
```

### 5.2 依赖管理要求

1. 所有子模块需通过 `<dependencyManagement>` 引入 BOM。  
2. 引入依赖时只写 groupId 和 artifactId，不填 version。  
3. 禁止在子模块 POM 中重复声明版本。  
4. 升级依赖时必须评估：兼容性、CVE 安全风险、受影响模块。  

---

## 6. 代码规范

### 6.1 基础要求

- JDK 版本：统一使用 **JDK 17** 或更高版本（视实际情况调整）。  
- 编码规范：统一使用 UTF-8。  
- 缩进风格：4 空格缩进，禁止使用 Tab。  
- 强制使用 `@Override` 注解。  

### 6.2 工具类通用规范

- 工具类必须使用 `final` 修饰，并提供私有构造方法：

```java
public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
```

- 所有工具方法必须为 `static`，不得依赖 Spring 容器状态。  
- 复杂方法必须拆分为多个小方法，避免超长方法。  
- 禁止在工具类中直接写业务逻辑。  

### 6.3 异常处理规范

- 工具类内部禁止吞异常，必须明确处理或抛出运行时异常。  
- 自定义运行时异常统一使用 `BizException` 或更具体的异常类型。  
- 对外暴露的工具方法应提供带 `boolean` 返回的“安全方法”（如 `tryParseInt`）。  

---

## 7. 工具类开发规范

### 7.1 单一职责原则

- 一个工具类只负责一个领域，例如：
  - `StringUtils`：字符串相关  
  - `LocalDateUtils`：日期时间相关  
  - `JsonUtils`：JSON 转换相关  
  - `CryptoUtils / AESUtils`：加解密相关  

### 7.2 方法命名规范

方法命名必须能够准确描述行为：

| 方法名        | 说明                 |
|---------------|----------------------|
| isEmpty       | 判断是否为空         |
| isNotEmpty    | 判断是否非空         |
| format        | 格式化日期/时间      |
| parse         | 解析字符串为对象     |
| encryptAES    | 使用 AES 加密        |
| decryptAES    | 使用 AES 解密        |
| generateId    | 生成分布式 ID        |

### 7.3 JavaDoc 要求

- 所有对外公开的工具方法必须编写 JavaDoc。  
- 包含：方法用途、参数说明、返回值说明、异常说明。  

示例：

```java
/**
 * 判断字符串是否为空（null 或去除首尾空格后长度为 0）。
 *
 * @param str 待判断字符串
 * @return 当字符串为 null 或空白时返回 true，否则返回 false
 */
public static boolean isEmpty(String str) {
    return str == null || str.trim().isEmpty();
}
```

---

## 8. 命名规范

### 8.1 包命名

- 顶层包名统一为：`com.undernine.utils`  
- 按领域划分子包，例如：

```text
com.undernine.utils.core.string
com.undernine.utils.core.time
com.undernine.utils.core.json
com.undernine.utils.redis.lock
com.undernine.utils.http.client
```

### 8.2 类命名

- 工具类：`XxxUtils`（优先）或 `XxxUtil`  
- 配置类：`XxxConfig` / `XxxConfiguration`  
- 处理器类：`XxxHandler`  
- 异常类：`XxxException`  

禁止出现含义不明确的命名，例如 `CommonUtils`、`BaseHelper`、`TmpUtils` 等。

---

## 9. Git 协作流程

推荐采用改进版 Git Flow：

- `master`：稳定可发布分支，仅合并正式 release。  
- `develop`：开发主干分支，所有功能从此分支拉出。  
- `feature/*`：功能开发分支。  
- `release/*`：发布准备分支，用于测试与 bug 修复。  
- `hotfix/*`：线上紧急修复分支。  

基本流程：

1. 从 `develop` 拉取新功能分支：`feature/xxx`  
2. 在功能分支开发与自测  
3. 提交 PR 到 `develop`，并通过 Code Review  
4. 发布前从 `develop` 创建 `release/x.y.z` 分支  
5. 验证通过后合并到 `master` 并打 Tag  

---

## 10. 分支命名规范

示例：

- 功能分支：`feature/string-utils-enhance`  
- 新模块分支：`feature/add-redis-lock-module`  
- 修复分支：`hotfix/json-npe-fix`  
- 发布分支：`release/1.0.0`  

要求：

- 分支名称必须包含明确含义  
- 禁止使用：`test`、`tmp`、`mybranch` 等随意命名  

---

## 11. Pull Request & Code Review 规范

每个 PR 必须至少包含以下内容：

1. 修改内容概述（What）  
2. 修改原因或背景（Why）  
3. 影响范围（Impact）  
4. 测试说明（How tested）  

### 11.1 审核要点

- 是否符合模块边界与工程结构规范  
- 是否按要求完善 JavaDoc 与注释  
- 是否具备充分的单元测试覆盖  
- 是否引入了新的第三方依赖，如有需说明理由  
- 是否存在潜在性能问题或线程安全问题  

---

## 12. 单元测试规范

- 所有公共工具方法必须具备对应单元测试。  
- 使用 JUnit 5 + Mockito（如有需要）。  
- 测试用例需覆盖：
  - 正常场景  
  - 边界场景（空值、极限值、异常输入）  
  - 异常场景（无效参数等）  

测试类命名规范：

```text
StringUtilsTest
LocalDateUtilsTest
RedisLockTest
JsonUtilsTest
```

---

## 13. 文档规范

每个模块必须至少包含：

- `README.md`：模块介绍、使用示例、注意事项。  
- `CHANGELOG.md`（可选）：记录该模块的变更历史。  

根目录 `README.md`（即本文件）用于：

- 介绍整个工具库工程  
- 说明模块划分与依赖管理方式  
- 描述协作与贡献流程  

---

## 14. 版本号管理与发布流程

版本号格式遵循：

```text
MAJOR.MINOR.PATCH
```

含义：

- **MAJOR**：存在不兼容的 API 变更  
- **MINOR**：新增功能，向下兼容  
- **PATCH**：问题修复或微小改动  

### 14.1 发布流程（示例）

1. 确认 `develop` 状态稳定  
2. 创建发布分支：`release/1.0.0`  
3. 在发布分支上进行回归测试与 bug 修复  
4. 合并到 `master` 与 `develop`  
5. 在 `master` 打 Tag：`v1.0.0`  
6. 使用 CI 进行打包与发布（如 Maven 私服、远程仓库等）  

---

## 15. 质量保障要求

- 引入统一的代码检查工具（如 CheckStyle / Spotless 等）。  
- CI 流程中必须包含：
  - 编译检查  
  - 单元测试  
  - 静态代码扫描（如 SonarQube，可选）  
- 禁止提交未使用代码、调试代码（如 System.out.println）。  
- 所有公共 API 变更必须经过评审。  

---

## 16. 安全规范

- 禁止在代码中硬编码明文密码、密钥、访问令牌。  
- HTTP 工具默认必须启用合理的超时配置。  
- 禁止提供可执行任意文件读写、命令执行的工具方法。  
- 对 JSON 反序列化等操作需评估安全风险，避免使用不安全的反序列化方式。  
- 对外 API 返回或日志中不得泄露敏感信息（如密钥、密码、身份证号完整信息等）。  

---

## 17. 未来扩展规划（预留给后续完善）

本章节可在后续由其他协作者或 AI 助手（如 Claude 等）继续补充与完善，例如：

- 增加 Observability（日志、链路追踪、指标）的统一工具模块  
- 增加 Cache（本地缓存 + 分布式缓存）统一封装与规范  
- 提供统一的异常码与错误字典支持  
- 构建在线文档站点（如基于 Docusaurus / VuePress 等）  
- 增加其他语言版本工具库（Node.js / Python 等）的约定与互通方案  

> 本文档为 Draft 版本，后续可在此基础上持续迭代、扩展和细化。
