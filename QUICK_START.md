# Under-Utils 快速开始指南

## 📋 前置要求

- JDK 17 或更高版本
- Maven 3.6+ 
- IDE（推荐 IntelliJ IDEA 或 Eclipse）

## 🚀 项目结构

```
under-utils/
├── pom.xml                          # 父 POM
├── README.md                        # 项目规范文档
├── QUICK_START.md                   # 本文件
├── .gitignore                       # Git 忽略配置
│
├── under-utils-bom/                 # BOM 依赖管理模块
│   └── pom.xml
│
├── under-utils-core/                # 核心工具模块
│   ├── pom.xml
│   ├── README.md
│   └── src/
│       ├── main/java/com/undernine/utils/core/
│       │   ├── string/              # 字符串工具
│       │   ├── time/                # 日期时间工具
│       │   ├── json/                # JSON 工具
│       │   ├── crypto/              # 加密工具
│       │   ├── id/                  # ID 生成工具
│       │   ├── collection/          # 集合工具
│       │   └── io/                  # IO 工具
│       └── test/java/               # 测试代码
│
├── under-utils-http/                # HTTP 封装模块
├── under-utils-redis/               # Redis 封装模块
├── under-utils-mybatis/             # MyBatis 增强模块
├── under-utils-spring/              # Spring 组件模块
├── under-utils-starter/             # Spring Boot Starter
├── under-utils-biz/                 # 业务组件模块
└── under-utils-placeholder/         # 占位符模块
```

## 🔧 编译项目

### 1. 编译所有模块

```bash
cd /Users/deng/Desktop/Java开发工具包/under-utils
mvn clean install
```

### 2. 仅编译某个模块

```bash
cd under-utils-core
mvn clean install
```

### 3. 跳过测试编译

```bash
mvn clean install -DskipTests
```

## 🧪 运行测试

### 运行所有测试

```bash
mvn test
```

### 运行某个模块的测试

```bash
cd under-utils-core
mvn test
```

### 运行单个测试类

```bash
mvn test -Dtest=StringUtilsTest
```

## 📦 使用工具库

### 在项目中引入

在你的项目 `pom.xml` 中添加：

```xml
<dependencyManagement>
    <dependencies>
        <!-- 引入 BOM -->
        <dependency>
            <groupId>com.undernineplaces</groupId>
            <artifactId>under-utils-bom</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- 引入核心模块 -->
    <dependency>
        <groupId>com.undernineplaces</groupId>
        <artifactId>under-utils-core</artifactId>
    </dependency>
</dependencies>
```

### 使用示例

```java
import com.undernine.utils.core.string.StringUtils;
import com.undernine.utils.core.time.LocalDateTimeUtils;

public class Example {
    public static void main(String[] args) {
        // 字符串工具
        String name = "  John  ";
        String trimmed = StringUtils.trim(name);  // "John"
        
        // 日期时间工具
        String now = LocalDateTimeUtils.now();  // "2024-12-02 17:13:00"
        System.out.println("当前时间: " + now);
    }
}
```

## 🛠️ IDE 配置

### IntelliJ IDEA

1. **导入项目**
   - File → Open → 选择 `under-utils` 目录
   - 选择 "Open as Maven Project"

2. **配置 JDK**
   - File → Project Structure → Project
   - 设置 SDK 为 JDK 17

3. **自动导入依赖**
   - Settings → Build, Execution, Deployment → Build Tools → Maven
   - 勾选 "Import Maven projects automatically"

4. **代码风格**
   - Settings → Editor → Code Style → Java
   - 缩进：4 空格

### Eclipse

1. **导入项目**
   - File → Import → Maven → Existing Maven Projects
   - 选择 `under-utils` 目录

2. **配置 JDK**
   - 右键项目 → Properties → Java Build Path
   - Libraries → 添加 JDK 17

## 📝 开发新功能

### 1. 创建新的工具类

在合适的模块下创建工具类，例如在 `under-utils-core`:

```java
package com.undernine.utils.core.xxx;

/**
 * XXX 工具类
 *
 * @author Your Name
 * @version 1.0.0
 * @since 1.0.0
 */
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

### 2. 编写单元测试

```java
package com.undernine.utils.core.xxx;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class XxxUtilsTest {
    
    @Test
    void testSomeMethod() {
        String result = XxxUtils.someMethod("test");
        assertThat(result).isEqualTo("test");
    }
}
```

### 3. 运行测试

```bash
mvn test -Dtest=XxxUtilsTest
```

## 📚 相关文档

- [README.md](README.md) - 完整的开发规范与贡献指南
- [under-utils-core/README.md](under-utils-core/README.md) - 核心模块文档

## ❓ 常见问题

### 1. Maven 依赖下载失败

**解决方案**：配置国内镜像

在 `~/.m2/settings.xml` 中添加：

```xml
<mirrors>
    <mirror>
        <id>aliyun</id>
        <mirrorOf>central</mirrorOf>
        <name>Aliyun Maven</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
</mirrors>
```

### 2. IDE 提示包名错误

**原因**：IDE 还未识别为 Maven 项目

**解决方案**：
- IntelliJ IDEA: 右键 `pom.xml` → Maven → Reload Project
- Eclipse: 右键项目 → Maven → Update Project

### 3. 编译错误：找不到符号

**原因**：可能是跨模块依赖问题

**解决方案**：
```bash
# 先安装被依赖的模块
cd under-utils-core
mvn clean install

# 再编译依赖它的模块
cd ../under-utils-http
mvn clean install
```

## 🎯 下一步

1. 阅读 [README.md](README.md) 了解完整的开发规范
2. 查看各模块的 README 了解具体功能
3. 开始编写你的工具类
4. 提交 Pull Request

## 📞 联系我们

如有问题，请提交 Issue 或联系项目维护者。
