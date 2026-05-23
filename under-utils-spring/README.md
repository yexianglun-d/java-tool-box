# Under-Utils Spring

Spring Web support for request context propagation, rate limiting, duplicate-submit guards, result objects, exception handling, and JSON field masking.

Prefer `under-utils-starter` when you want auto-configuration. Use this module directly when you want to wire only selected beans.

## Dependency

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Main APIs

| Area | APIs |
|------|------|
| Request context | `OperationContext`, `OperationContextFilter`, `OperationContextHolder`, `OperationContextSnapshot`, `OperationContextTaskDecorator` |
| Identity SPI | `CurrentUserProvider`, `CurrentTenantProvider`, `TraceIdProvider`, `OperationContextCustomizer` |
| Rate limit | `@RateLimit`, `RateLimitAspect`, `RateLimitStore`, `LocalRateLimitStore` |
| Duplicate submit | `@PreventRepeat`, `PreventRepeatAspect`, `RepeatSubmitStore`, `LocalRepeatSubmitStore` |
| Web response | `Result`, `ResultCode`, `BizException`, `GlobalExceptionHandler` |
| JSON masking | `@Sensitive`, `SensitiveJsonSerializer`, `DesensitizeUtils` |
| Compatibility AOP | `@OperationLog`, `@Retry`, `@TimeLog` and their aspects |

## Request Context

`OperationContextFilter` reads request headers and stores a per-request context in `OperationContextHolder`.

```java
OperationContext context = OperationContextHolder.getContext();
String traceId = context == null ? null : context.getTraceId();
```

For async work, capture and wrap the context:

```java
Runnable task = OperationContextSnapshot.capture().wrap(() -> {
    OperationContext asyncContext = OperationContextHolder.getContext();
});
```

`OperationContextTaskDecorator` can be attached to Spring task executors. The starter can configure it automatically unless the application already provides a `TaskDecorator`.

## Rate Limit And Duplicate Submit

```java
@RateLimit(limit = 10, period = 60, message = "请求过于频繁")
@PostMapping("/sms/send")
public void sendSms(@RequestBody SendSmsCommand command) {
    smsService.send(command);
}

@PreventRepeat(timeout = 5, timeUnit = TimeUnit.SECONDS, message = "请勿重复提交")
@PostMapping("/orders")
public Long createOrder(@RequestBody CreateOrderCommand command) {
    return orderService.create(command);
}
```

Runtime behavior:

- `@RateLimit` throws `BizException` when quota is exceeded.
- `limit <= 0` rejects all requests; `period <= 0` is treated as a 1-second window.
- `@PreventRepeat` throws `BizException` when the same key is acquired again before expiry.
- `timeout <= 0` is treated as the minimum 1ms window.
- If `releaseOnFailure = true`, the duplicate-submit key is released when the business method throws.

Key resolution:

- Empty `key` uses tenant, user, URI, method name, and method argument digest.
- Non-empty `key` is parsed as SpEL. Available variables include `#args`, `#userId`, `#tenantId`, `#traceId`, `#requestUri`, and `#context`.
- SpEL errors fall back to a deterministic key instead of interrupting the request.

Store selection:

- `LocalRateLimitStore` and `LocalRepeatSubmitStore` only protect the current JVM.
- Multi-instance services should use Redis stores from `under-utils-redis` or provide custom stores.
- Redis failures propagate unless a custom store implements fallback behavior.

Direct wiring requires `RateLimitAspect`, `PreventRepeatAspect`, `OperationKeyResolver`, and the relevant store beans. The starter is simpler for normal Spring Boot applications.

## Result And Exceptions

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }
}
```

Register `GlobalExceptionHandler` if you want Under-Utils to convert `BizException`, validation errors, 404s, method mismatch errors, and uncaught exceptions into `Result`.

```java
@Configuration
@Import(GlobalExceptionHandler.class)
public class WebConfiguration {
}
```

`Result` is a small response model, not a requirement. Applications with an existing response contract can keep their own model and still use the context, rate-limit, and duplicate-submit pieces.

## Sensitive Fields

Use `@Sensitive` with the provided Jackson serializer when a response field needs masking.

```java
public class UserView {

    @Sensitive(type = SensitiveType.PHONE)
    private String phone;
}
```

Make sure the serializer is registered in your Jackson configuration or use the starter path that wires the expected infrastructure.

## Compatibility AOP

`@OperationLog`, `@Retry`, and `@TimeLog` are retained for compatibility and are not the main direction for new code. They are not automatically enabled by the starter.

If you still need them, import the aspects explicitly:

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

Notes:

- `@OperationLog` does not record request parameters unless `recordParams = true`.
- `@Retry` uses synchronous sleep in the current thread.
- For production observability and retry governance, prefer the application's tracing, metrics, queue, and client-resilience stack.
