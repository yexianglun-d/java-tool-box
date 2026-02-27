---
inclusion: always
---

# Under-Utils 项目开发规则

## 项目概述
- **项目名称**: Under-Utils - 企业级 Java 工具库集合
- **JDK 版本**: 21
- **构建工具**: Maven 3.x
- **Spring Boot 版本**: 3.2.5
- **项目类型**: Maven 多模块项目
- **包名前缀**: `com.undernine.utils`
- **作者信息**: `deng`

## 开发风格
- **要求**: 需保证编写的代码与当前系统高度统一：
- **1**: 每当某一个模块编写完成后需创建package-info.java 文件以完整详细说明当前模块内容以及使用方式，不能够编写重复的功能模块包括模块中的功能
- **2**: 每个模块下面需创建readme-模块名.md 文档来说明当前模块的开发进度
- **3**: 测试这块非必要情况下在模块下中进行创建测试类
## 模块结构与职责

### 核心模块
1. **under-utils-bom** - BOM 依赖管理模块（无业务代码）
2. **under-utils-core** - 基础工具模块（仅依赖 JDK）
3. **under-utils-mybatis** - MyBatis/MyBatis-Plus 增强模块
4. **under-utils-spring** - Spring 通用组件模块
5. **under-utils-http** - HTTP 封装模块
6. **under-utils-redis** - Redis 封装模块
7. **under-utils-starter** - Spring Boot Starter
8. **under-utils-biz** - 业务组件模块
9. **under-utils-placeholder** - 占位符模块
10. **under-utils-test** - 测试模块

### 模块依赖原则
- 避免循环依赖
- 通用工具优先放入 under-utils-core
- 框架相关工具放入对应模块
- 业务相关组件放入 under-utils-biz

## 编码规范

### Java 代码规范
- 遵循阿里巴巴 Java 开发手册
- 使用 4 空格缩进，禁止使用 Tab
- 类名使用大驼峰命名法（PascalCase）
- 方法名和变量名使用小驼峰命名法（camelCase）
- 常量使用全大写加下划线（UPPER_SNAKE_CASE）
- 编码统一使用 UTF-8
- 强制使用 `@Override` 注解

### 工具类开发规范
- 工具类必须使用 `final` 修饰
- 必须提供私有构造方法并抛出 `UnsupportedOperationException`
- 所有方法必须是 `static`
- 方法必须是无状态的、线程安全的
- 禁止在工具类中直接写业务逻辑

**工具类模板**:
```java
public final class XxxUtils {
    
    private XxxUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * 方法说明
     *
     * @param param 参数说明
     * @return 返回值说明
     */
    public static String someMethod(String param) {
        // 实现逻辑
        return param;
    }
}
```

### 命名规范

#### 包命名
- 顶层包名: `com.undernine.utils`
- 按领域划分子包，例如:
  - `com.undernine.utils.core.string`
  - `com.undernine.utils.core.time`
  - `com.undernine.utils.redis.lock`
  - `com.undernine.utils.http.client`

#### 类命名
- 工具类: `XxxUtils` (优先) 或 `XxxUtil`
- 配置类: `XxxConfig` / `XxxConfiguration`
- 处理器类: `XxxHandler`
- 异常类: `XxxException`
- 禁止使用含义不明确的命名: `CommonUtils`、`BaseHelper`、`TmpUtils`

#### 方法命名
- 判断方法: `isEmpty`、`isNotEmpty`、`isBlank`、`isValid`
- 格式化方法: `format`
- 解析方法: `parse`、`tryParse`（安全解析）
- 加密方法: `encrypt`、`decrypt`
- 生成方法: `generate`、`create`

### 注释规范
- 所有公共类和方法必须有 Javadoc 注释
- JavaDoc 必须包含: 方法用途、参数说明、返回值说明、异常说明
- 复杂逻辑需要添加行内注释说明
- 注释应该说明"为什么"而不仅仅是"是什么"

**JavaDoc 模板**:
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

### 异常处理规范
- 工具类内部禁止吞异常，必须明确处理或抛出运行时异常
- 自定义运行时异常统一使用 `BizException` 或更具体的异常类型
- 对外暴露的工具方法应提供带 `boolean` 返回的"安全方法"（如 `tryParseInt`）

## 依赖管理规范

### BOM 管理
- 所有版本号只允许出现在 `under-utils-bom` 模块中
- 子模块禁止直接填写第三方依赖版本号
- 如需升级依赖版本，必须通过 BOM 统一调整

### 关键依赖版本
- Spring Boot: 3.2.5
- MyBatis-Plus: 3.5.5
- Redisson: 3.27.2
- Jackson: 2.16.1
- Fastjson2: 2.0.47
- OkHttp: 4.12.0
- HttpClient5: 5.3
- Lombok: 1.18.30
- Guava: 33.0.0
- EasyExcel: 3.3.4

### 依赖引入要求
1. 所有子模块需通过 `<dependencyManagement>` 引入 BOM
2. 引入依赖时只写 groupId 和 artifactId，不填 version
3. 禁止在子模块 POM 中重复声明版本
4. 升级依赖时必须评估: 兼容性、CVE 安全风险、受影响模块

## Git 协作规范

### 分支策略
- `master`: 稳定可发布分支，仅合并正式 release
- `develop`: 开发主干分支，所有功能从此分支拉出
- `feature/*`: 功能开发分支
- `release/*`: 发布准备分支
- `hotfix/*`: 线上紧急修复分支

### 分支命名规范
- 功能分支: `feature/string-utils-enhance`
- 新模块分支: `feature/add-redis-lock-module`
- 修复分支: `hotfix/json-npe-fix`
- 发布分支: `release/1.0.0`
- 禁止使用: `test`、`tmp`、`mybranch` 等随意命名

### 提交规范
- 提交代码前确保编译通过
- 禁止提交未使用代码、调试代码（如 System.out.println）
- 提交信息要清晰描述修改内容

## 单元测试规范

### 测试要求
- 所有公共工具方法必须具备对应单元测试
- 使用 JUnit 5 + Mockito（如有需要）
- 测试用例需覆盖:
  - 正常场景
  - 边界场景（空值、极限值、异常输入）
  - 异常场景（无效参数等）

### 测试类命名
- 测试类命名: `XxxUtilsTest`
- 示例: `StringUtilsTest`、`LocalDateTimeUtilsTest`、`JsonUtilsTest`

## 文档规范

### 模块文档
- 每个模块必须至少包含 `README.md`
- README 必须包含: 模块介绍、使用示例、注意事项
- 可选: `CHANGELOG.md` 记录变更历史

### 根目录文档
- `README.md`: 项目规范与贡献指南
- `QUICK_START.md`: 快速开始指南
- `PROJECT_ANALYSIS.md`: 项目分析报告

## 版本管理规范

### 版本号格式
遵循语义化版本: `MAJOR.MINOR.PATCH`
- **MAJOR**: 存在不兼容的 API 变更
- **MINOR**: 新增功能，向下兼容
- **PATCH**: 问题修复或微小改动

### 发布流程
1. 确认 `develop` 状态稳定
2. 创建发布分支: `release/x.y.z`
3. 在发布分支上进行回归测试与 bug 修复
4. 合并到 `master` 与 `develop`
5. 在 `master` 打 Tag: `vx.y.z`
6. 使用 CI 进行打包与发布

## 安全规范
- 禁止在代码中硬编码明文密码、密钥、访问令牌
- HTTP 工具默认必须启用合理的超时配置
- 禁止提供可执行任意文件读写、命令执行的工具方法
- 对 JSON 反序列化等操作需评估安全风险
- 对外 API 返回或日志中不得泄露敏感信息

## 质量保障要求
- 引入统一的代码检查工具（如 CheckStyle / Spotless）
- CI 流程中必须包含: 编译检查、单元测试、静态代码扫描
- 禁止提交未使用代码、调试代码
- 所有公共 API 变更必须经过评审

## 特殊注意事项

### MyBatis 模块
- 实体类继承 `BaseEntity` 获得通用字段
- 使用 `PageQuery` 和 `PageResult` 进行分页
- 逻辑删除字段 `deleted` 必须为 INT 类型
- 数据库字段使用下划线，Java 使用驼峰，自动映射

### Spring 模块
- 使用 `Result<T>` 统一返回格式
- 业务异常使用 `BizException`
- 确保 `GlobalExceptionHandler` 被 Spring 扫描到

### Core 模块
- 仅依赖 JDK，不依赖任何第三方框架
- 所有方法必须是静态的、无状态的、线程安全的
