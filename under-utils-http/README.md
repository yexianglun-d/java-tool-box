# Under-Utils HTTP

HTTP helpers and OpenAPI client governance built on OkHttp.

The module has two layers:

- `HttpRequest`, `HttpResponse`, and `HttpUtils` for direct HTTP calls.
- `OpenApiClient` for repeated open-platform concerns such as token injection, signing, trace headers, idempotency keys, business error decoding, and retry decisions.

## Dependency

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-http</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Direct HTTP Calls

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

POST JSON:

```java
HttpResponse response = HttpRequest.builder()
        .url("https://api.example.com/orders")
        .method(HttpMethod.POST)
        .header("Content-Type", "application/json")
        .body(command)
        .build()
        .execute();
```

Convenience API:

```java
String body = HttpUtils.get("https://api.example.com/users");
String created = HttpUtils.postJson("https://api.example.com/orders", command);
```

Global defaults:

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

## OpenAPI Client

Use `DefaultOpenApiClient` when each request needs consistent platform-level behavior.

```java
OpenApiClient client = new DefaultOpenApiClient(
        OpenApiClientOptions.builder()
                .connectTimeout(3000)
                .readTimeout(8000)
                .maxRetries(1)
                .retryInterval(300)
                .build(),
        request -> tokenService.getAccessToken(),
        request -> request
                .header("X-Sign", signer.sign(request))
                .query("timestamp", String.valueOf(System.currentTimeMillis())),
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

`OpenApiResponse` separates transport status from the client governance result:

- `success` is the decoded result after HTTP status and business error handling.
- `statusCode` is the HTTP status code, or `0` when no response was received.
- `errorCode`, `errorMessage`, and `retryable` come from the configured `ApiErrorDecoder`.

## Business Error Decoding

Many open platforms return HTTP 200 with a business error code. Implement `ApiErrorDecoder` to keep that rule out of service methods.

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

Retry is only attempted when the decoded error is marked retryable and the configured retry budget allows it.

## Exceptions

Direct HTTP APIs throw module exceptions for network and timeout failures:

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

## Notes

- Do not disable SSL verification in production.
- Keep retry disabled or conservative for non-idempotent requests.
- Set timeouts per upstream. A shared global default should be a baseline, not the only control.
- Use `OpenApiClient` when token, signing, idempotency, trace, and business error rules would otherwise be repeated in every service method.
