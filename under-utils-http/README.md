# Under-Utils HTTP

基于 OkHttp 的 HTTP 调用和 OpenAPI 客户端治理模块。

模块分为两层：

- `HttpRequest`、`HttpResponse`、`HttpUtils`：直接 HTTP 调用。
- `OpenApiClient`：封装开放平台常见治理能力，例如 token 注入、签名、trace header、幂等 key、业务错误解码和重试决策。
- `RefreshingAccessTokenProvider`：封装 access token 本地缓存、提前刷新和并发收敛。

## 依赖

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-http</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 直接 HTTP 调用

```java
HttpResponse response = HttpRequest.builder()
        .url("https://api.example.com/users")
        .method(HttpMethod.GET)
        .timeout(5000)
        .retry(1)
        .build()
        .execute();

if (response.isSuccess()) {
    String body = response.asString();
}
```

POST JSON：

```java
HttpResponse response = HttpRequest.builder()
        .url("https://api.example.com/orders")
        .method(HttpMethod.POST)
        .header("Content-Type", "application/json")
        .body(command)
        .build()
        .execute();
```

便捷 API：

```java
String body = HttpUtils.get("https://api.example.com/users");
String created = HttpUtils.postJson("https://api.example.com/orders", command);
```

全局默认配置：

```java
HttpConfig config = HttpConfig.builder()
        .connectTimeout(5000)
        .readTimeout(10000)
        .maxRetries(1)
        .retryInterval(500)
        .addDefaultHeader("User-Agent", "my-service/1.0")
        .build();

HttpUtils.setDefaultConfig(config);
```

## OpenAPI 客户端

当每次调用都需要统一 token、签名、幂等和业务错误处理时，使用 `DefaultOpenApiClient`。

```java
OpenApiClient client = new DefaultOpenApiClient(
        OpenApiClientOptions.builder()
                .connectTimeout(3000)
                .readTimeout(8000)
                .maxRetries(1)
                .retryInterval(300)
                .build(),
        request -> tokenService.getAccessToken(),
        request -> {
            request.header("X-Sign", signer.sign(request));
            request.query("timestamp", String.valueOf(System.currentTimeMillis()));
        },
        ApiErrorDecoder.httpStatus()
);

OpenApiResponse<OrderResult> response = client.execute(
        OpenApiRequest.builder()
                .url("https://open.example.com/orders")
                .method(HttpMethod.POST)
                .operationName("create-order")
                .traceId(traceId)
                .idempotencyKey(requestNo)
                .body(command)
                .build(),
        OrderResult.class
);
```

token 有有效期时，可以使用 `RefreshingAccessTokenProvider`：

```java
AccessTokenProvider tokenProvider = new RefreshingAccessTokenProvider(
        request -> {
            TokenResponse token = tokenGateway.refreshToken();
            return RefreshingAccessTokenProvider.AccessToken.of(
                    token.accessToken(),
                    token.expiresAt()
            );
        },
        Duration.ofSeconds(30)
);
```

`refreshAhead` 表示到期前多久开始刷新。该实现只负责当前 JVM 内的 token 缓存；多实例共享、分布式锁、持久化和上游刷新失败兜底应由业务项目在 `TokenFetcher` 中处理。

`OpenApiResponse` 会区分 HTTP 响应和治理层结果：

- `success` 是经过 HTTP 状态和业务错误解码后的结果。
- `statusCode` 是 HTTP 状态码；没有拿到响应时为 `0`。
- `errorCode`、`errorMessage`、`retryable` 来自配置的 `ApiErrorDecoder`。

## 业务错误解码

很多开放平台会用 HTTP 200 返回业务错误码。可以通过 `ApiErrorDecoder` 把规则从业务方法中移出。

```java
ApiErrorDecoder decoder = (request, httpResponse) -> {
    if (!httpResponse.isSuccess()) {
        return ApiErrorDecoder.httpStatus().decode(request, httpResponse);
    }

    Map<String, Object> body = JsonUtils.fromJson(
            httpResponse.asString(),
            new TypeReference<Map<String, Object>>() {
            }
    );
    String code = String.valueOf(body.get("code"));
    if ("OK".equals(code)) {
        return ApiErrorDecodeResult.success();
    }
    return ApiErrorDecodeResult.failure(
            code,
            String.valueOf(body.get("message")),
            "BUSY".equals(code)
    );
};
```

只有解码结果标记为 `retryable`，并且还有重试预算时，客户端才会重试。

## 异常

直接 HTTP API 会针对网络和超时失败抛出模块异常：

```java
try {
    String body = HttpUtils.get("https://api.example.com/users");
} catch (HttpTimeoutException e) {
    // timeout
} catch (HttpNetworkException e) {
    // network failure
} catch (HttpException e) {
    // other HTTP module failure
}
```

## 注意事项

- 生产环境不要关闭 SSL 校验。
- 非幂等请求应关闭或谨慎配置重试。
- 超时时间应按上游接口特点配置，全局默认只作为基线。
- 当 token、签名、幂等、trace 和业务错误处理在多个 service 方法中重复出现时，优先使用 `OpenApiClient`。
