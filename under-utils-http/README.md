# Under-Utils HTTP 模块

企业级 HTTP 客户端工具库，提供统一、易用、功能强大的 HTTP 请求封装。

## 特性

- ✅ 统一的 API 接口，屏蔽底层实现差异
- ✅ 支持多种 HTTP 方法（GET、POST、PUT、DELETE、PATCH 等）
- ✅ 支持多种请求格式（JSON、表单、文件上传）
- ✅ 完善的超时和重试机制
- ✅ 异步请求支持
- ✅ 灵活的配置选项
- ✅ 线程安全，高性能
- ✅ 基于 OkHttp 实现（性能优异）

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>com.undernineplaces</groupId>
    <artifactId>under-utils-http</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 基础使用

#### 1. 简单 GET 请求

```java
// 方式 1：使用工具类
String result = HttpUtils.get("https://api.example.com/users");

// 方式 2：使用构建器
HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/users")
    .method(HttpMethod.GET)
    .build()
    .execute();

String body = response.asString();
```

#### 2. GET 请求（带参数）

```java
Map<String, String> params = new HashMap<>();
params.put("page", "1");
params.put("size", "10");

String result = HttpUtils.get("https://api.example.com/users", params);
```

#### 3. POST JSON 请求

```java
User user = new User("John", 25);

// 方式 1：使用工具类
String result = HttpUtils.postJson("https://api.example.com/users", user);

// 方式 2：使用构建器
HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/users")
    .method(HttpMethod.POST)
    .header("Content-Type", "application/json")
    .body(user)
    .build()
    .execute();

User createdUser = response.asObject(User.class);
```

#### 4. POST 表单请求

```java
Map<String, String> formParams = new HashMap<>();
formParams.put("username", "john");
formParams.put("password", "123456");

String result = HttpUtils.postForm("https://api.example.com/login", formParams);
```

#### 5. 文件上传

```java
File file = new File("/path/to/file.jpg");

// 方式 1：简单上传
String result = HttpUtils.upload("https://api.example.com/upload", "file", file);

// 方式 2：带额外参数
Map<String, String> params = new HashMap<>();
params.put("description", "My photo");
String result = HttpUtils.upload("https://api.example.com/upload", "file", file, params);

// 方式 3：使用构建器
HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/upload")
    .method(HttpMethod.POST)
    .file("file", file)
    .formParam("description", "My photo")
    .build()
    .execute();
```

#### 6. 文件下载

```java
File targetFile = new File("/path/to/save/file.jpg");
HttpUtils.download("https://api.example.com/files/123", targetFile);
```

#### 7. 自定义请求头

```java
HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/users")
    .method(HttpMethod.GET)
    .header("Authorization", "Bearer your-token")
    .header("User-Agent", "MyApp/1.0")
    .build()
    .execute();
```

#### 8. 超时和重试

```java
HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/users")
    .method(HttpMethod.GET)
    .timeout(5000)        // 5 秒超时
    .retry(3)             // 最多重试 3 次
    .build()
    .execute();
```

#### 9. 异步请求

```java
HttpRequest.builder()
    .url("https://api.example.com/users")
    .method(HttpMethod.GET)
    .build()
    .executeAsync()
    .thenAccept(response -> {
        System.out.println("Response: " + response.asString());
    })
    .exceptionally(ex -> {
        System.err.println("Error: " + ex.getMessage());
        return null;
    });
```

#### 10. 全局配置

```java
// 配置全局默认设置
HttpConfig config = HttpConfig.builder()
    .connectTimeout(5000)
    .readTimeout(10000)
    .maxRetries(3)
    .retryInterval(1000)
    .loggingEnabled(true)
    .addDefaultHeader("User-Agent", "MyApp/1.0")
    .build();

HttpUtils.setDefaultConfig(config);

// 之后的所有请求都会使用这个配置
String result = HttpUtils.get("https://api.example.com/users");
```

## 高级功能

### 响应处理

```java
HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/users")
    .method(HttpMethod.GET)
    .build()
    .execute();

// 判断请求是否成功
if (response.isSuccess()) {
    // 获取响应体字符串
    String body = response.asString();
    
    // 解析为对象
    User user = response.asObject(User.class);
    
    // 解析为泛型对象
    List<User> users = response.asObject(new TypeReference<List<User>>() {});
    
    // 获取字节数组
    byte[] bytes = response.asBytes();
    
    // 保存到文件
    response.saveToFile(new File("/path/to/file"));
    
    // 获取响应头
    String contentType = response.getHeader("Content-Type");
    
    // 获取状态码
    int statusCode = response.getStatusCode();
}
```

### 自定义配置

```java
HttpConfig config = HttpConfig.builder()
    .connectTimeout(5000)           // 连接超时 5 秒
    .readTimeout(10000)             // 读取超时 10 秒
    .writeTimeout(10000)            // 写入超时 10 秒
    .maxRetries(3)                  // 最多重试 3 次
    .retryInterval(1000)            // 重试间隔 1 秒
    .followRedirects(true)          // 跟随重定向
    .maxConnections(200)            // 最大连接数 200
    .maxConnectionsPerRoute(20)     // 每个路由最大连接数 20
    .keepAliveTime(60000)           // 连接保活时间 60 秒
    .loggingEnabled(true)           // 启用日志
    .verifySsl(true)                // 验证 SSL 证书
    .addDefaultHeader("User-Agent", "MyApp/1.0")
    .build();

HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/users")
    .method(HttpMethod.GET)
    .config(config)
    .build()
    .execute();
```

### SSL 证书配置

```java
// 开发环境：忽略 SSL 证书验证（不推荐用于生产环境）
HttpConfig config = HttpConfig.builder()
    .verifySsl(false)
    .build();

HttpUtils.setDefaultConfig(config);
```

## 异常处理

```java
try {
    String result = HttpUtils.get("https://api.example.com/users");
} catch (HttpTimeoutException e) {
    // 处理超时异常
    System.err.println("Request timeout: " + e.getMessage());
} catch (HttpNetworkException e) {
    // 处理网络异常
    System.err.println("Network error: " + e.getMessage());
} catch (HttpException e) {
    // 处理其他 HTTP 异常
    System.err.println("HTTP error: " + e.getMessage());
}
```

## 最佳实践

### 1. 使用全局配置

```java
// 在应用启动时配置一次
HttpConfig config = HttpConfig.builder()
    .connectTimeout(5000)
    .readTimeout(10000)
    .maxRetries(3)
    .loggingEnabled(true)
    .addDefaultHeader("User-Agent", "MyApp/1.0")
    .build();

HttpUtils.setDefaultConfig(config);
```

### 2. 合理设置超时时间

```java
// 根据接口特点设置不同的超时时间
// 快速接口：3-5 秒
// 普通接口：10-15 秒
// 慢接口：30-60 秒
HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/slow-api")
    .timeout(30000)  // 30 秒
    .build()
    .execute();
```

### 3. 使用重试机制

```java
// 对于幂等请求（GET、PUT、DELETE）可以启用重试
HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/users")
    .method(HttpMethod.GET)
    .retry(3)  // 最多重试 3 次
    .build()
    .execute();
```

### 4. 异步处理耗时请求

```java
// 对于耗时的 HTTP 请求，使用异步方式避免阻塞
CompletableFuture<HttpResponse> future = HttpRequest.builder()
    .url("https://api.example.com/slow-api")
    .method(HttpMethod.GET)
    .build()
    .executeAsync();

future.thenAccept(response -> {
    // 处理响应
}).exceptionally(ex -> {
    // 处理异常
    return null;
});
```

## 注意事项

1. **SSL 证书验证**：生产环境必须启用 SSL 证书验证（`verifySsl(true)`）
2. **超时设置**：根据实际情况合理设置超时时间，避免请求长时间挂起
3. **重试机制**：仅对幂等请求启用重试，避免重复提交
4. **连接池**：合理配置连接池大小，避免资源浪费
5. **异常处理**：捕获并处理 HTTP 异常，提供友好的错误提示

## 依赖说明

- **OkHttp 4.12.0**：底层 HTTP 客户端实现
- **Jackson 2.16.1**：JSON 序列化/反序列化
- **SLF4J**：日志框架

## 更新日志

### v1.0.0 (2025-01-30)
- ✅ 初始版本发布
- ✅ 支持 GET、POST、PUT、DELETE、PATCH 等请求方法
- ✅ 支持 JSON、表单、文件上传等请求格式
- ✅ 完善的超时和重试机制
- ✅ 异步请求支持
- ✅ 基于 OkHttp 实现

## 许可证

MIT License

## 联系方式

- 作者：deng
- 项目：Under-Utils
- 版本：1.0.0
