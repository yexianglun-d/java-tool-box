# Under-Utils Redis

基于 Redisson 的基础设施模式模块，供 Spring 应用和 starter 使用。

本模块要求业务项目提供已配置的 `RedissonClient`，不会自行创建 Redis 连接。

## 依赖

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-redis</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 主要 API

| API | 说明 |
|-----|------|
| `DistributedLockTemplate` | 在 Redisson lock 下执行回调，并确保释放锁。 |
| `RedisRateLimitStore` | 分布式 `RateLimitStore` 实现。 |
| `RedisRepeatSubmitStore` | 分布式 `RepeatSubmitStore` 实现。 |
| `CacheAsideTemplate` | cache-aside 读穿模板，支持空值缓存、TTL 抖动和重建锁。 |
| `LogicalExpireCacheTemplate` | 热点 key 缓存模板，逻辑过期后先返回旧值并后台刷新。 |
| `CacheValueCodec` | cache 模板共享的序列化边界。 |

## 分布式锁

```java
Long orderId = distributedLockTemplate.execute(
        "order:create:" + command.requestNo(),
        1,
        30,
        TimeUnit.SECONDS,
        () -> orderService.create(command)
);
```

无法获取锁时，模板抛出 `DistributedLockException`。

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

行为：

- 缓存命中时不调用 loader。
- loader 返回 null 时，可以用更短 TTL 缓存空值占位。
- TTL jitter 用于降低集中失效。
- 重建锁用于降低缓存击穿。
- loader 异常会向外传播，不写入缓存。

## 逻辑过期缓存

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

行为：

- key 不存在时同步加载。
- 逻辑未过期时直接返回缓存值。
- 逻辑过期时立即返回旧值，并提交后台刷新。
- `physicalTtl` 必须大于 `logicalTtl`，否则旧值无法作为兜底窗口。
- 配置 `LogicalExpireCacheRefreshFailureHandler` 后，后台刷新失败会回调处理器。

## 限流和防重复提交 store

`RedisRateLimitStore` 和 `RedisRepeatSubmitStore` 实现了 `under-utils-spring` 中的 store 接口。

通常由 `under-utils-starter` 在以下配置下装配：

```yaml
under:
  utils:
    web:
      rate-limit:
        store: redis
      repeat-submit:
        store: redis
```

Redis 或 Redisson 异常不会被吞掉。如果业务需要 fail-open 或兜底策略，请在应用内提供自定义 store。

## 集成测试

Redis 缓存模板的真实 Redis 行为由 `under-utils-test` 通过 Testcontainers 覆盖：

```bash
mvn -Pintegration-tests -pl under-utils-test -am test -Dtest=RedisCacheTemplateIntegrationTest
```
