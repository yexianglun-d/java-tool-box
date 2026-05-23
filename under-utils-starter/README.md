# Under-Utils Starter

Spring Boot auto-configuration for the Spring and Redis modules.

The starter backs off when the application provides its own infrastructure beans. It is the recommended entry point for Spring Boot services that want Under-Utils defaults without scanning every package manually.

## Dependency

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Auto-Configured Beans

Default beans:

- `CurrentUserProvider`
- `CurrentTenantProvider`
- `TraceIdProvider`
- `OperationKeyResolver`
- `OperationContextFilter`
- `OperationContextTaskDecorator`
- `RateLimitAspect`
- `PreventRepeatAspect`
- local `RateLimitStore`
- local `RepeatSubmitStore`

When a `RedissonClient` exists, Redis-backed beans can also be configured:

- `RedisRateLimitStore`
- `RedisRepeatSubmitStore`
- `DistributedLockTemplate`
- `CacheValueCodec`
- `CacheOptions`
- `CacheAsideTemplate`
- `LogicalExpireCacheOptions`
- `LogicalExpireCacheTemplate`

## Configuration

Local state stores:

```yaml
under:
  utils:
    web:
      operation-context:
        enabled: true
        task-decorator-enabled: true
      rate-limit:
        enabled: true
        store: local
      repeat-submit:
        enabled: true
        store: local
```

Redis state stores and cache templates:

```yaml
under:
  utils:
    web:
      rate-limit:
        store: redis
      repeat-submit:
        store: redis
    redis:
      lock-enabled: true
      cache:
        enabled: true
        ttl: 5m
        null-ttl: 30s
        jitter: 10s
        rebuild-lock-enabled: true
      logical-cache:
        enabled: true
        logical-ttl: 5m
        physical-ttl: 30m
```

## Backoff Rules

The starter does not replace application beans for the same role. Common backoff points include:

- `CurrentUserProvider`
- `CurrentTenantProvider`
- `TraceIdProvider`
- `OperationKeyResolver`
- `TaskDecorator`
- `RateLimitStore`
- `RepeatSubmitStore`
- `CacheValueCodec`
- `CacheOptions`
- `CacheAsideTemplate`
- `LogicalExpireCacheOptions`
- `LogicalExpireCacheTemplate`

This lets applications keep their own security context, key strategy, serialization codec, cache options, or fallback behavior.

## Notes

- Redis store selection requires a `RedissonClient`; the starter does not create one.
- Local stores are useful for single-instance development and tests only.
- Redis failures propagate through Redisson unless the application provides custom store beans.
- `under.utils.redis.logical-cache.physical-ttl` must be greater than `logical-ttl`.
