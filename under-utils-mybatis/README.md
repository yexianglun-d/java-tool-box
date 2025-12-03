# Under-Utils MyBatis 模块

> MyBatis-Plus 增强工具集，开箱即用的企业级数据访问层解决方案

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/undernineplaces/under-utils)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

## ✨ 核心特性

| 特性 | 说明 | 优势 |
|------|------|------|
| 🚀 **BaseEntity** | 统一基础实体类 | 包含 ID、时间戳、操作人、逻辑删除等通用字段 |
| 🔄 **自动填充** | 元数据自动填充 | 创建/更新时间、操作人自动记录，无需手动设置 |
| 📄 **统一分页** | PageQuery/PageResult | 规范化分页参数和返回格式，前后端对接更简单 |
| 🗑️ **逻辑删除** | 自动过滤已删除数据 | 删除操作变更新，数据可追溯可恢复 |
| 🔒 **安全防护** | 防止全表更新/删除 | 避免误操作导致的数据灾难 |
| 🎯 **Lambda 查询** | 类型安全的条件构造 | 编译时检查，重构友好 |

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.undernineplaces</groupId>
    <artifactId>under-utils-mybatis</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置数据源

**application.yml**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_db?useUnicode=true&characterEncoding=utf8
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 打印 SQL
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

### 3. 创建实体类

**继承 BaseEntity 即可获得通用字段**

```java
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    private String username;
    private String email;
    private String phone;
    private Integer status;  // 0-禁用 1-启用
}
```

**对应数据库表**

```sql
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY COMMENT '主键',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  email VARCHAR(100) COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '手机号',
  status INT DEFAULT 1 COMMENT '状态',
  create_time DATETIME COMMENT '创建时间',
  update_time DATETIME COMMENT '更新时间',
  create_by BIGINT COMMENT '创建人',
  update_by BIGINT COMMENT '更新人',
  deleted INT DEFAULT 0 COMMENT '删除标记'
) COMMENT='用户表';
```

### 4. 配置 MyBatis-Plus

```java
@Configuration
public class MybatisConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return MybatisPlusConfig.mybatisPlusInterceptor(DbType.MYSQL);
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new DefaultMetaObjectHandler() {
            @Override
            protected Long getUserId() {
                // TODO: 从 Spring Security 获取当前用户 ID
                // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                // return ((UserDetails) auth.getPrincipal()).getId();
                return 1L; // 测试默认值
            }
        };
    }
}
```

### 5. 创建 Mapper

```java
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 继承 BaseMapper 即可，无需写任何方法
}
```

## 📖 使用示例

### CRUD 操作

```java
@Service
@RequiredArgsConstructor
public class SysUserService {
    
    private final SysUserMapper userMapper;
    
    // ✅ 创建（ID、创建时间、创建人自动填充）
    public Long create(SysUser user) {
        userMapper.insert(user);
        return user.getId();
    }
    
    // ✅ 更新（更新时间、更新人自动填充）
    public void update(SysUser user) {
        userMapper.updateById(user);
    }
    
    // ✅ 逻辑删除（只更新 deleted 字段）
    public void delete(Long id) {
        userMapper.deleteById(id);
    }
    
    // ✅ 查询（自动过滤 deleted=1 的记录）
    public SysUser getById(Long id) {
        return userMapper.selectById(id);
    }
}
```

### 条件查询

```java
// Lambda 查询（类型安全）
public List<SysUser> findActiveUsers(String keyword) {
    return userMapper.selectList(
        new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getStatus, 1)
            .like(StringUtils.hasText(keyword), SysUser::getUsername, keyword)
            .orderByDesc(SysUser::getCreateTime)
    );
}
```

### 分页查询

```java
public PageResult<SysUser> pageUsers(PageQuery pageQuery, String keyword) {
    LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
        .like(StringUtils.hasText(keyword), SysUser::getUsername, keyword)
        .orderByDesc(SysUser::getCreateTime);
    
    IPage<SysUser> page = userMapper.selectPage(pageQuery.toPage(), wrapper);
    return PageResult.of(page);
}
```

**Controller 调用**

```java
@GetMapping("/users")
public PageResult<SysUser> page(PageQuery pageQuery, String keyword) {
    return userService.pageUsers(pageQuery, keyword);
}

// 请求: GET /users?current=1&size=10&keyword=admin
```

**返回示例**

```json
{
  "records": [{"id": 123, "username": "admin", "createTime": "2024-12-03T10:00:00"}],
  "total": 1,
  "current": 1,
  "size": 10,
  "pages": 1
}
```

## 🔧 高级特性

### 乐观锁

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {
    @Version  // 添加版本号字段
    private Integer version;
    // ...
}

// 更新时自动使用乐观锁
// SQL: UPDATE sys_user SET ..., version = version + 1 WHERE id = ? AND version = ?
userMapper.updateById(user);
```

### 自定义排序

```java
// 单字段排序
PageQuery.of(1, 10).orderByDesc("create_time")

// 多字段排序
PageQuery.of(1, 10)
    .orderByDesc("status")
    .orderByAsc("username")
```

### 获取当前用户

**方式 1：从 ThreadLocal 获取**
```java
@Override
protected Long getUserId() {
    return UserContext.getCurrentUserId();
}
```

**方式 2：从 Spring Security 获取**
```java
@Override
protected Long getUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof UserDetails) {
        return ((YourUserDetails) auth.getPrincipal()).getId();
    }
    return null;
}
```

## ⚠️ 注意事项

| 项目 | 说明 |
|------|------|
| 🔤 **字段命名** | 数据库使用下划线（`create_time`），Java 使用驼峰（`createTime`），自动映射 |
| 🗑️ **逻辑删除** | `deleted` 字段必须为 `INT` 类型，0=未删除，1=已删除 |
| 📄 **分页限制** | 默认单页最大 1000 条，防止大数据量查询 |
| 🚫 **安全防护** | 禁止无条件的全表 UPDATE/DELETE 操作 |
| 🆔 **主键生成** | 使用雪花算法，无需数据库自增 |

## 🧪 测试用例

完整的集成测试位于 `under-utils-test` 模块：
- ✅ BaseEntity 自动填充测试
- ✅ 分页查询测试
- ✅ 逻辑删除测试
- ✅ 条件查询测试
- ✅ 更新自动填充测试

运行测试：
```bash
mvn test -Dtest=MybatisIntegrationTest
```

## 📦 依赖版本

| 依赖 | 版本 |
|------|------|
| Spring Boot | 3.1.11 |
| MyBatis-Plus | 3.5.7 |
| JDK | 21+ |

## 📚 相关文档

- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [项目整体规范](../README.md)

## 📄 License

MIT License - 详见 [LICENSE](../LICENSE) 文件
