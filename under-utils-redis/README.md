# Under-Utils Redis

Redisson-backed infrastructure patterns used by Spring applications and the starter.

The module expects the application to provide a configured `RedissonClient`. It does not create Redis connections on its own.

## Dependency

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-redis</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Main APIs

| API | Purpose |
|-----|---------|
| `DistributedLockTemplate` | Executes a callback under a Redisson lock and releases it safely. |
| `RedisRateLimitStore` | Distributed `RateLimitStore` implementation. |
| `RedisRepeatSubmitStore` | Distributed `RepeatSubmitStore` implementation. |
| `CacheAsideTemplate` | Cache-aside read-through template with null caching, TTL jitter, and rebuild lock. |
| `LogicalExpireCacheTemplate` | Hot-key cache template that returns stale values while refreshing in the background. |
| `CacheValueCodec` | Serialization boundary shared by cache templates. |

## Distributed Lock

```java
Long orderId = distributedLockTemplate.execute(
        "order:create:" + command.requestNo(),
        1,
        30,
        TimeUnit.SECONDS,
        () -> orderService.create(command)
);
```

If the lock cannot be acquired, the template throws `DistributedLockException`.

## Cache-Aside

```java
CacheOptions options = CacheOptions.builder()
        .keyPrefix("app:cache:")
        .ttl(Duration.ofMinutes(5))
        .nullTtl(Duration.ofSeconds(30))
        .jitter(Duration.ofSeconds(10))
        .cacheNull(true)
        .rebuildLockEnabled(true)
        .build();

UserProfile profile = cacheAsideTemplate.getOrLoad(
        "user:profile:" + userId,
        UserProfile.class,
        options,
        key -> userRepository.findProfile(userId)
);
```

Behavior:

- A cache hit returns without calling the loader.
- A null load result can be cached with a shorter TTL.
- Jitter is added to reduce synchronized expiry.
- The rebuild lock limits cache stampede on misses.
- Loader exceptions propagate and do not write cache.

## Logical Expire Cache

```java
LogicalExpireCacheOptions options = LogicalExpireCacheOptions.builder()
        .keyPrefix("app:logical-cache:")
        .logicalTtl(Duration.ofMinutes(5))
        .physicalTtl(Duration.ofMinutes(30))
        .refreshExecutor(refreshExecutor)
        .build();

ProductView view = logicalExpireCacheTemplate.getOrLoad(
        "product:view:" + productId,
        ProductView.class,
        options,
        key -> productRepository.findView(productId)
);
```

Behavior:

- Missing keys are loaded synchronously.
- Fresh cached values are returned directly.
- Logically expired values are returned immediately and refreshed in the background.
- `physicalTtl` must be greater than `logicalTtl`, otherwise the old value cannot serve as a stale fallback.
- Refresh failures are reported through `LogicalExpireCacheRefreshFailureHandler` when configured.

## Rate Limit And Duplicate Submit Stores

`RedisRateLimitStore` and `RedisRepeatSubmitStore` implement the store interfaces from `under-utils-spring`.

They are usually wired by `under-utils-starter` when:

```yaml
under:
  utils:
    web:
      rate-limit:
        store: redis
      repeat-submit:
        store: redis
```

Redis or Redisson failures are not swallowed. If your service should fail open or use a fallback path, provide a custom store implementation in the application.

## Integration Tests

Redis-backed cache behavior is covered from the `under-utils-test` module with Testcontainers:

```bash
mvn -Pintegration-tests -pl under-utils-test -am test -Dtest=RedisCacheTemplateIntegrationTest
```
