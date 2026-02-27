# Under-Utils-HTTP 模块设计文档

## 模块概述

under-utils-http 是一个企业级 HTTP 客户端工具库，提供统一、易用、功能强大的 HTTP 请求封装。

**设计目标**：
- 统一的 API 接口，屏蔽底层实现差异
- 支持多种 HTTP 客户端（OkHttp、HttpClient5）
- 开箱即用，简化 HTTP 调用
- 线程安全，高性能
- 完善的异常处理和重试机制

---

## 核心能力清单

### 1. 基础 HTTP 请求能力

#### 1.1 请求方法支持
- ✅ GET 请求
- ✅ POST 请求（表单、JSON、文件上传）
- ✅ PUT 请求
- ✅ DELETE 请求
- ✅ PATCH 请求
- ✅ HEAD 请求
- ✅ OPTIONS 请求

#### 1.2 请求参数处理
- ✅ URL 参数（Query Parameters）
- ✅ 请求头（Headers）
- ✅ 请求体（Body）
  - JSON 格式
  - 表单格式（application/x-www-form-urlencoded）
  - 多部分表单（multipart/form-data）
  - 原始字节流
  - XML 格式

#### 1.3 响应处理
- ✅ 响应状态码获取
- ✅ 响应头获取
- ✅ 响应体解析
  - 字符串
  - JSON 对象
  - 字节数组
  - 文件下载
- ✅ 响应编码处理（UTF-8、GBK 等）

---

### 2. 高级功能

#### 2.1 超时配置
- ✅ 连接超时（Connect Timeout）
- ✅ 读取超时（Read Timeout）
- ✅ 写入超时（Write Timeout）
- ✅ 全局默认超时配置
- ✅ 单次请求自定义超时

#### 2.2 重试机制
- ✅ 自动重试（可配置次数）
- ✅ 重试间隔（固定间隔、指数退避）
- ✅ 重试条件（网络异常、超时、特定状态码）
- ✅ 幂等性检查（仅对 GET、PUT、DELETE 重试）

#### 2.3 拦截器
- ✅ 请求拦截器（Request Interceptor）
  - 添加通用请求头（如 User-Agent、Authorization）
  - 请求日志记录
  - 请求签名
- ✅ 响应拦截器（Response Interceptor）
  - 响应日志记录
  - 统一异常处理
  - 响应数据解密

#### 2.4 连接池管理
- ✅ 连接池配置（最大连接数、最大空闲连接数）
- ✅ 连接保活（Keep-Alive）
- ✅ 连接复用
- ✅ 连接超时清理

---

### 3. 安全特性

#### 3.1 HTTPS 支持
- ✅ SSL/TLS 支持
- ✅ 自定义 SSL 证书
- ✅ 忽略 SSL 证书验证（开发环境）
- ✅ 双向认证（Mutual TLS）

#### 3.2 代理支持
- ✅ HTTP 代理
- ✅ HTTPS 代理
- ✅ SOCKS 代理
- ✅ 代理认证

#### 3.3 认证支持
- ✅ Basic Authentication
- ✅ Bearer Token
- ✅ OAuth 2.0
- ✅ 自定义认证头

---

### 4. 文件操作

#### 4.1 文件上传
- ✅ 单文件上传
- ✅ 多文件上传
- ✅ 文件 + 表单参数混合上传
- ✅ 上传进度监听
- ✅ 大文件分片上传（可选）

#### 4.2 文件下载
- ✅ 文件下载到本地
- ✅ 下载进度监听
- ✅ 断点续传（可选）
- ✅ 流式下载（大文件）

---

### 5. 异步与并发

#### 5.1 异步请求
- ✅ 异步 GET/POST/PUT/DELETE
- ✅ CompletableFuture 支持
- ✅ 回调函数支持
- ✅ 异步批量请求

#### 5.2 并发控制
- ✅ 并发请求限制
- ✅ 请求队列管理
- ✅ 线程池配置

---

### 6. 工具类与辅助功能

#### 6.1 URL 工具
- ✅ URL 编码/解码
- ✅ URL 参数拼接
- ✅ URL 解析

#### 6.2 Cookie 管理
- ✅ Cookie 存储
- ✅ Cookie 自动携带
- ✅ Cookie 持久化

#### 6.3 日志与监控
- ✅ 请求日志（可配置级别）
- ✅ 响应日志
- ✅ 性能监控（请求耗时统计）
- ✅ 异常日志

---

## 模块结构设计

```
under-utils-http/
├── src/main/java/com/undernine/utils/http/
│   ├── client/                    # HTTP 客户端实现
│   │   ├── HttpClient.java        # 统一 HTTP 客户端接口
│   │   ├── OkHttpClientImpl.java  # OkHttp 实现
│   │   └── ApacheHttpClientImpl.java # Apache HttpClient 实现
│   │
│   ├── request/                   # 请求相关
│   │   ├── HttpRequest.java       # 请求构建器
│   │   ├── HttpMethod.java        # HTTP 方法枚举
│   │   └── RequestBody.java       # 请求体封装
│   │
│   ├── response/                  # 响应相关
│   │   ├── HttpResponse.java      # 响应封装
│   │   └── ResponseHandler.java   # 响应处理器
│   │
│   ├── config/                    # 配置相关
│   │   ├── HttpConfig.java        # HTTP 配置类
│   │   ├── RetryConfig.java       # 重试配置
│   │   └── TimeoutConfig.java     # 超时配置
│   │
│   ├── interceptor/               # 拦截器
│   │   ├── Interceptor.java       # 拦截器接口
│   │   ├── LoggingInterceptor.java # 日志拦截器
│   │   └── RetryInterceptor.java  # 重试拦截器
│   │
│   ├── exception/                 # 异常处理
│   │   ├── HttpException.java     # HTTP 异常基类
│   │   ├── TimeoutException.java  # 超时异常
│   │   └── NetworkException.java  # 网络异常
│   │
│   ├── util/                      # 工具类
│   │   ├── UrlUtils.java          # URL 工具
│   │   ├── HttpUtils.java         # HTTP 通用工具
│   │   └── CookieUtils.java       # Cookie 工具
│   │
│   └── package-info.java          # 包说明文档
│
└── src/test/java/                 # 单元测试
```

---

## 核心类设计

### 1. HttpClient 接口（统一客户端）

```java
public interface HttpClient {
    // 同步请求
    HttpResponse get(String url);
    HttpResponse post(String url, Object body);
    HttpResponse put(String url, Object body);
    HttpResponse delete(String url);
    
    // 异步请求
    CompletableFuture<HttpResponse> getAsync(String url);
    CompletableFuture<HttpResponse> postAsync(String url, Object body);
    
    // 文件操作
    HttpResponse upload(String url, File file);
    void download(String url, File targetFile);
    
    // 配置
    HttpClient config(HttpConfig config);
    HttpClient addInterceptor(Interceptor interceptor);
}
```

### 2. HttpRequest 构建器

```java
public class HttpRequest {
    public static Builder builder() { ... }
    
    public static class Builder {
        public Builder url(String url);
        public Builder method(HttpMethod method);
        public Builder header(String name, String value);
        public Builder headers(Map<String, String> headers);
        public Builder param(String name, String value);
        public Builder params(Map<String, String> params);
        public Builder body(Object body);
        public Builder timeout(int timeout);
        public Builder retry(int maxRetries);
        public HttpRequest build();
    }
}
```

### 3. HttpResponse 响应封装

```java
public class HttpResponse {
    private int statusCode;
    private Map<String, String> headers;
    private String body;
    private byte[] bodyBytes;
    
    public boolean isSuccess();
    public String asString();
    public <T> T asObject(Class<T> clazz);
    public byte[] asBytes();
    public void saveToFile(File file);
}
```

---

## 使用示例

### 示例 1：简单 GET 请求

```java
// 方式 1：静态方法
String result = HttpUtils.get("https://api.example.com/users");

// 方式 2：构建器
HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/users")
    .method(HttpMethod.GET)
    .header("Authorization", "Bearer token")
    .timeout(5000)
    .build()
    .execute();
```

### 示例 2：POST JSON 请求

```java
User user = new User("John", 25);

HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/users")
    .method(HttpMethod.POST)
    .header("Content-Type", "application/json")
    .body(user)
    .build()
    .execute();

User createdUser = response.asObject(User.class);
```

### 示例 3：文件上传

```java
File file = new File("/path/to/file.jpg");

HttpResponse response = HttpRequest.builder()
    .url("https://api.example.com/upload")
    .method(HttpMethod.POST)
    .file("file", file)
    .param("description", "My photo")
    .build()
    .execute();
```

### 示例 4：异步请求

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

### 示例 5：配置全局客户端

```java
HttpConfig config = HttpConfig.builder()
    .connectTimeout(5000)
    .readTimeout(10000)
    .maxRetries(3)
    .retryInterval(1000)
    .addInterceptor(new LoggingInterceptor())
    .build();

HttpClient client = HttpClientFactory.create(config);
HttpResponse response = client.get("https://api.example.com/users");
```

---

## 技术选型

### 底层 HTTP 客户端

**方案 1：OkHttp（推荐）**
- ✅ 性能优异
- ✅ 连接池管理完善
- ✅ 支持 HTTP/2
- ✅ 拦截器机制强大
- ✅ 社区活跃

**方案 2：Apache HttpClient 5**
- ✅ 功能全面
- ✅ 稳定可靠
- ✅ 企业级应用广泛
- ✅ 配置灵活

**最终选择**：
- 默认使用 **OkHttp**（性能更好）
- 提供 **HttpClient 5** 作为备选
- 通过统一接口屏蔽底层差异

---

## 开发优先级

### 阶段 1：核心功能（必须）
1. ✅ HttpClient 接口定义
2. ✅ OkHttp 实现
3. ✅ GET/POST/PUT/DELETE 基础方法
4. ✅ HttpRequest 构建器
5. ✅ HttpResponse 响应封装
6. ✅ 超时配置
7. ✅ 异常处理

### 阶段 2：高级功能（重要）
1. ✅ 重试机制
2. ✅ 拦截器支持
3. ✅ 文件上传/下载
4. ✅ 异步请求
5. ✅ 日志记录

### 阶段 3：扩展功能（可选）
1. ✅ Apache HttpClient 实现
2. ✅ HTTPS 自定义证书
3. ✅ 代理支持
4. ✅ Cookie 管理
5. ✅ 进度监听

---

## 质量保障

### 单元测试
- 所有公共方法必须有单元测试
- 测试覆盖率目标：80%+
- Mock 外部 HTTP 服务

### 集成测试
- 真实 HTTP 请求测试
- 使用 WireMock 或 MockWebServer

### 性能测试
- 并发请求测试
- 连接池性能测试
- 内存泄漏检测

---

## 文档要求

1. ✅ README.md - 模块介绍和快速开始
2. ✅ readme-http.md - 开发进度文档
3. ✅ 所有类和方法的 JavaDoc
4. ✅ 所有子包的 package-info.java
5. ✅ 使用示例代码

---

**设计完成时间**: 2025-01-30  
**设计者**: Under-Utils Team
