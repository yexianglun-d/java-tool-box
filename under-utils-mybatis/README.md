# under-utils-mybatis

MyBatis / MyBatis-Plus 增强模块，提供分页、元数据自动填充、逻辑删除等常用功能。

## 功能特性

### 1. 基础实体类 (BaseEntity)
包含常用公共字段：
- `id` - 主键（雪花算法自动生成）
- `createTime` - 创建时间（自动填充）
- `updateTime` - 修改时间（自动填充）
- `createBy` - 创建人 ID（自动填充）
- `updateBy` - 修改人 ID（自动填充）
- `deleted` - 逻辑删除标记（0: 未删除, 1: 已删除）

### 2. 元数据自动填充 (DefaultMetaObjectHandler)
自动填充创建时间、修改时间、创建人、修改人等字段。

### 3. 分页功能
- `PageQuery` - 统一的分页查询参数
- `PageResult` - 统一的分页返回结果
- 支持排序、最大分页限制等

### 4. MyBatis-Plus 配置 (MybatisPlusConfig)
- 分页插件
- 乐观锁插件
- 防止全表更新删除插件

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.undernineplaces</groupId>
    <artifactId>under-utils-mybatis</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 实体类继承 BaseEntity

```java
import com.baomidou.mybatisplus.annotation.TableName;
import com.undernine.utils.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;
}
```

对应的数据库表：

```sql
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '修改人ID',
  `deleted` int DEFAULT '0' COMMENT '逻辑删除(0:未删除,1:已删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 3. 配置 MyBatis-Plus

```java
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.undernine.utils.mybatis.config.MybatisPlusConfig;
import com.undernine.utils.mybatis.handler.DefaultMetaObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfiguration {

    /**
     * MyBatis-Plus 拦截器（分页、乐观锁、防全表更新删除）
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return MybatisPlusConfig.mybatisPlusInterceptor(DbType.MYSQL);
    }

    /**
     * 元数据自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new DefaultMetaObjectHandler() {
            @Override
            protected Long getUserId() {
                // 从 ThreadLocal、Spring Security 等获取当前用户 ID
                // return UserContextHolder.getUserId();
                return null; // 默认实现
            }
        };
    }
}
```

### 4. 使用分页查询

```java
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.undernine.utils.mybatis.page.PageQuery;
import com.undernine.utils.mybatis.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 分页查询用户
     */
    public PageResult<User> pageUsers(PageQuery pageQuery, String username) {
        // 1. 构建查询条件
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null, User::getUsername, username);

        // 2. 执行分页查询（默认按创建时间倒序）
        IPage<User> page = userMapper.selectPage(
            pageQuery.orderByDesc("create_time").buildPage(),
            wrapper
        );

        // 3. 转换为统一分页结果
        return PageResult.of(page);
    }
}
```

### 5. Controller 使用示例

```java
import com.undernine.utils.mybatis.page.PageQuery;
import com.undernine.utils.mybatis.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户
     * GET /users?current=1&size=10&username=admin
     */
    @GetMapping
    public PageResult<User> pageUsers(PageQuery pageQuery, String username) {
        return userService.pageUsers(pageQuery, username);
    }
}
```

返回结果：

```json
{
  "records": [
    {
      "id": 1234567890,
      "username": "admin",
      "email": "admin@example.com",
      "createTime": "2024-12-02T18:30:00",
      "updateTime": "2024-12-02T18:30:00",
      "createBy": 1,
      "updateBy": 1,
      "deleted": 0
    }
  ],
  "total": 1,
  "current": 1,
  "size": 10,
  "pages": 1
}
```

## 高级用法

### 自定义用户 ID 获取

```java
@Bean
public MetaObjectHandler metaObjectHandler() {
    return new DefaultMetaObjectHandler() {
        @Override
        protected Long getUserId() {
            // 方式1: 从 ThreadLocal 获取
            return UserContextHolder.getUserId();
            
            // 方式2: 从 Spring Security 获取
            // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            //     return ((UserDetails) auth.getPrincipal()).getId();
            // }
            // return null;
        }
    };
}
```

### 自定义排序

```java
// 单个排序
PageQuery pageQuery = PageQuery.of(1L, 10L)
    .orderByDesc("create_time");

// 多个排序
PageQuery pageQuery = PageQuery.of(1L, 10L)
    .orderByDesc("create_time")
    .orderByAsc("id");

// 使用默认排序
List<OrderItem> defaultOrders = List.of(OrderItem.desc("update_time"));
Page<User> page = pageQuery.buildPage(defaultOrders);
```

### 乐观锁使用

```java
import com.baomidou.mybatisplus.annotation.Version;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    private String username;
    
    @Version // 添加 @Version 注解
    private Integer version;
}

// 更新时会自动使用乐观锁
// UPDATE user SET username = ?, version = version + 1 WHERE id = ? AND version = ?
userMapper.updateById(user);
```

## 注意事项

1. **数据库表字段命名**: 建议使用下划线命名（如 `create_time`），MyBatis-Plus 会自动转换为驼峰命名。

2. **逻辑删除**: 使用 `@TableLogic` 注解后，删除操作会自动转换为更新 `deleted` 字段。

3. **分页最大限制**: 默认单页最大 1000 条记录，可在配置中修改。

4. **防止全表更新删除**: 配置了 `BlockAttackInnerInterceptor` 后，不带条件的 update 和 delete 语句会被拦截。

## 依赖说明

本模块依赖：
- MyBatis-Plus Core
- MyBatis-Plus Extension
- under-utils-core（核心工具模块）
