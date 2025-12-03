# Under-Utils Spring 模块

Spring 通用组件模块，提供 Web 开发常用的基础组件。

## 核心功能

| 功能 | 说明 |
|------|------|
| 🎯 **统一返回结果** | `Result<T>` - 标准化 API 响应格式 |
| 📋 **状态码枚举** | `ResultCode` - 预定义常用业务状态码 |
| ⚠️ **业务异常** | `BizException` - 自定义业务异常 |
| 🛡️ **全局异常处理** | `GlobalExceptionHandler` - 统一异常拦截 |
| 🔧 **Spring 上下文工具** | `SpringContextHolder` - 非Spring管理类获取Bean |
| ⏱️ **AOP 时间统计** | `@TimeLog` - 方法执行时间统计与慢方法监控 |

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.undernineplaces</groupId>
    <artifactId>under-utils-spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 导入全局异常处理器

```java
@Configuration
@Import(GlobalExceptionHandler.class)
public class WebConfig {
}
```

或者直接扫描包路径：

```java
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.yourcompany",
    "com.undernine.utils.spring"
})
public class Application {
}
```

## 使用示例

### 统一返回结果

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    // ✅ 成功返回
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }
    
    // ❌ 失败返回
    @PostMapping
    public Result<Void> createUser(@RequestBody User user) {
        if (user.getUsername() == null) {
            return Result.fail("用户名不能为空");
        }
        userService.create(user);
        return Result.success();
    }
}
```

**响应格式**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin"
  },
  "timestamp": 1701612345678
}
```

### 业务异常处理

```java
@Service
public class UserService {
    
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return user;
    }
    
    public void updateStatus(Long id, Integer status) {
        if (status < 0 || status > 1) {
            throw new BizException(ResultCode.PARAM_ERROR, "状态值无效");
        }
        // ...
    }
}
```

**自动捕获并转换为标准响应**

```json
{
  "code": 10000,
  "message": "用户不存在",
  "timestamp": 1701612345678
}
```

### Spring 上下文工具

```java
// 在非Spring管理的类中获取Bean
public class SomeUtil {
    public void doSomething() {
        UserService userService = SpringContextHolder.getBean(UserService.class);
        userService.doSomething();
    }
}
```

###  AOP 时间统计

#### 1. 启用AOP

```java
@SpringBootApplication
@EnableAspectJAutoProxy  // 启用AOP
@ComponentScan(basePackages = {
    "com.yourcompany",
    "com.undernine.utils.spring"  // 扫描工具包
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### 2. 使用 @TimeLog 统计执行时间

```java
@Service
public class UserService {
    
    // 记录方法执行时间
    @TimeLog
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
    
    // 自定义描述和慢方法阈值
    @TimeLog(value = "批量查询用户", slowThreshold = 500)
    public List<User> listUsers(UserQuery query) {
        return userMapper.selectList(query);
    }
}
```

**日志输出示例：**

```
[DEBUG] UserService.getById(未命名) 执行耗时: 45ms
[WARN]  【慢方法】UserService.listUsers(批量查询用户) 执行耗时: 1523ms (阈值: 500ms)
```

**特性说明：**
- ✅ 自动记录方法执行时间
- ✅ 慢方法自动以 WARN 级别记录（默认阈值1000ms）
- ✅ 即使方法抛出异常也会记录执行时间
- ✅ 支持自定义操作描述和慢方法阈值

### 自定义状态码

```java
@RestController
public class OrderController {
    
    @PostMapping("/orders")
    public Result<Order> createOrder(@RequestBody OrderRequest request) {
        // 库存不足
        if (!stockService.check(request.getProductId())) {
            return Result.fail(40001, "库存不足");
        }
        
        Order order = orderService.create(request);
        return Result.success(order);
    }
}
```

## 内置状态码

### 成功状态 (2xx)

| 状态码 | 说明 |
|-------|------|
| 200 | 操作成功 |
| 201 | 创建成功 |

### 客户端错误 (4xx)

| 状态码 | 说明 |
|-------|------|
| 400 | 操作失败/参数错误 |
| 401 | 未授权，请登录 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 422 | 参数校验失败 |
| 429 | 请求过于频繁 |

### 服务端错误 (5xx)

| 状态码 | 说明 |
|-------|------|
| 500 | 服务器内部错误 |
| 503 | 服务暂不可用 |

### 业务错误 (10000+)

| 状态码 | 说明 |
|-------|------|
| 10000 | 业务处理失败 |
| 10001 | 数据不存在 |
| 10002 | 数据已存在 |
| 10003 | 数据状态异常 |
| 10004 | 操作过于频繁 |

## 全局异常处理

自动处理以下异常：

- `BizException` - 业务异常
- `MethodArgumentNotValidException` - 参数校验异常
- `BindException` - 参数绑定异常
- `HttpRequestMethodNotSupportedException` - 请求方法不支持
- `NoHandlerFoundException` - 404 异常
- `IllegalArgumentException` - 非法参数
- `NullPointerException` - 空指针异常
- `Exception` - 其他未捕获异常

## 注意事项

- ✅ 确保 `GlobalExceptionHandler` 被 Spring 扫描到
- ✅ `SpringContextHolder` 需要在 Spring 容器中注册
- ✅ 建议配合 `@Validated` 实现参数校验
- ✅ 业务异常使用 `BizException`，系统异常使用标准异常

## 依赖版本

| 依赖 | 版本 |
|------|------|
| Spring Framework | 6.x |
| Spring Boot | 3.1.11 |
| JDK | 21+ |

## 📄 License

MIT License - 详见 [LICENSE](../LICENSE) 文件
