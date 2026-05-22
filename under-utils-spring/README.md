# Under-Utils Spring 模块

Spring Web 横切能力模块，承载请求操作上下文、限流防重、统一异常响应和敏感字段脱敏等基础设施。推荐通过 `under-utils-starter` 使用自动装配；直接使用本模块时，建议显式 `@Import` 需要的组件，不建议扫描整个 `com.undernine.utils.spring` 包。

## 核心功能

| 功能 | 说明 |
|------|------|
| 🎯 **操作上下文** | `OperationContext` - traceId、tenantId、userId、请求信息和扩展属性传播 |
| 🚦 **限流防重** | `@RateLimit` / `@PreventRepeat` - 支持本地或 Redis 状态存储 |
| 🛡️ **统一异常处理** | `GlobalExceptionHandler` / `BizException` / `Result<T>` - 标准化 Web 错误响应 |
| 🔐 **敏感字段脱敏** | `@Sensitive` - Jackson 序列化时进行字段脱敏 |
| 🧩 **兼容维护 AOP** | `@OperationLog` / `@Retry` / `@TimeLog` - 轻量历史切面，不作为新增能力主线 |

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

如果需要限流、防重复提交、Redis 状态存储等工程模式能力，优先引入 `under-utils-starter` 并通过 `under.utils.*` 配置开关控制。

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

### 兼容维护 AOP

`@OperationLog`、`@Retry`、`@TimeLog` 是历史轻量切面，保留用于兼容维护。它们不会通过 starter 自动启用，也不建议作为新项目主线能力。确需使用时请显式注册对应切面：

```java
@Configuration
@EnableAspectJAutoProxy
@Import({
    OperationLogAspect.class,
    RetryAspect.class,
    TimeLogAspect.class
})
public class LegacyAopConfiguration {
}
```

使用 `@TimeLog` 统计执行时间：

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

日志输出示例：

```
[DEBUG] UserService.getById(未命名) 执行耗时: 45ms
[WARN]  【慢方法】UserService.listUsers(批量查询用户) 执行耗时: 1523ms (阈值: 500ms)
```

注意：

- `@OperationLog` 默认不记录请求参数，需要显式设置 `recordParams = true`。
- `@Retry` 使用当前线程同步 sleep，不适合高并发或需要退避、熔断、超时预算的外部调用治理。
- 新项目的耗时、重试和审计建议优先接入 Micrometer、OpenTelemetry、消息队列或业务统一审计平台。

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
