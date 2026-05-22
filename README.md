# Under-Utils

Under-Utils 是一组面向 Java 21 / Spring Boot 项目的工程模式封装。它不是 Hutool 的替代品，也不主打 `StringUtils`、`DateUtils` 这类低复杂度工具方法；项目更关注在业务系统中反复出现、实现细节多、容易写散的能力，例如请求操作上下文、幂等与限流、分布式锁、缓存重建、OpenAPI 调用、分页安全排序、审计填充和导入任务模板。

适合放进 Under-Utils 的能力，通常具备这些特征：横跨多个项目重复出现、有清晰边界、能沉淀稳定 API、能用单元测试覆盖核心分支，并且不会和 Hutool、Apache Commons、Guava 等通用工具库形成低价值竞争。

## 模块矩阵

| 模块 | 定位 | 典型能力 |
|------|------|----------|
| `under-utils-bom` | 统一依赖版本管理 | Spring Boot、MyBatis-Plus、Redisson、OkHttp、Jackson 等版本收口 |
| `under-utils-core` | 无框架耦合的基础能力承载层 | 仅放入真正需要项目内统一的核心抽象，不扩展成泛用小工具集合 |
| `under-utils-spring` | Spring Web 横切能力 | `OperationContext`、`OperationContextFilter`、异步上下文传播、`@RateLimit`、`@PreventRepeat`、`OperationKeyResolver` |
| `under-utils-redis` | Redis 工程模式 | `DistributedLockTemplate`、`RedisRateLimitStore`、`RedisRepeatSubmitStore`、`CacheAsideTemplate`、`LogicalExpireCacheTemplate` |
| `under-utils-http` | HTTP / OpenAPI 客户端 | `OpenApiClient`、token 注入、请求签名、trace/idempotency header、业务错误解码、重试 |
| `under-utils-mybatis` | MyBatis-Plus 增强 | `SafePageQuery`、`SortFieldMapping`、`AuditorProvider`、`DefaultMetaObjectHandler`、分页结果 |
| `under-utils-biz` | 可复用业务流程模板 | `ImportTaskTemplate`、`CsvImportRowReader`、逐行解析、校验失败收集、导入统计 |
| `under-utils-starter` | Spring Boot 自动装配 | Web 横切、Redis 状态存储、分布式锁、缓存模板、逻辑过期缓存自动配置 |

## 快速开始

推荐业务项目先引入 BOM，再按需引入 starter 或单模块依赖。

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.undernineplaces</groupId>
            <artifactId>under-utils-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>com.undernineplaces</groupId>
        <artifactId>under-utils-starter</artifactId>
    </dependency>
</dependencies>
```

starter 会在条件满足时自动装配：

- `OperationContextFilter`、`OperationContextTaskDecorator`
- `RateLimitAspect`、`PreventRepeatAspect`
- local / redis 两种 `RateLimitStore`、`RepeatSubmitStore`
- `DistributedLockTemplate`
- `CacheAsideTemplate`
- `LogicalExpireCacheTemplate`

典型配置：

```yaml
under:
  utils:
    web:
      operation-context:
        enabled: true
        task-decorator-enabled: true
        order: -2147483548
      rate-limit:
        enabled: true
        store: redis
      repeat-submit:
        enabled: true
        store: redis
    redis:
      lock-enabled: true
      lock-key-prefix: "order:lock:"
      cache:
        enabled: true
        ttl: 5m
        null-ttl: 30s
        jitter: 10s
        cache-null: true
        key-prefix: "app:cache:"
        rebuild-lock-enabled: true
        rebuild-lock-key-prefix: "app:cache:rebuild:"
        lock-wait-time: 1s
        lock-lease-time: 30s
      logical-cache:
        enabled: true
        logical-ttl: 5m
        physical-ttl: 30m
        cache-null: true
        key-prefix: "app:logical-cache:"
        rebuild-lock-key-prefix: "app:logical-cache:rebuild:"
        core-pool-size: 2
        max-pool-size: 8
        queue-capacity: 1024
        thread-name-prefix: "app-logical-cache-"
```

如果限流、防重提交使用 Redis 存储，需要业务项目提供 `RedissonClient`。如果不配置 `store: redis`，默认使用本地内存存储，适合单实例或测试环境。

## 典型场景

### 请求上下文与异步传播

`OperationContextFilter` 在请求进入时构建 `OperationContext`，聚合 traceId、用户、租户、请求路径等信息。业务代码可以通过 `OperationContextHolder` 读取当前上下文，异步任务可以通过 starter 自动装配的 `OperationContextTaskDecorator` 或手动使用 `OperationContextSnapshot` 传播上下文。

```java
OperationContext context = OperationContextHolder.getContext();
String traceId = context == null ? null : context.getTraceId();

Runnable task = OperationContextSnapshot.capture().wrap(() -> {
    OperationContext asyncContext = OperationContextHolder.getContext();
    // 在异步线程继续使用 traceId、userId、tenantId
});
```

可通过实现 `CurrentUserProvider`、`CurrentTenantProvider`、`TraceIdProvider` 或 `OperationContextCustomizer` 接入业务自己的登录态、租户模型和 trace 规范。

### 限流与防重复提交

`@RateLimit` 和 `@PreventRepeat` 用于把接口频控、重复点击提交等横切逻辑从业务方法中移出。key 默认由 `OperationKeyResolver` 结合用户、租户、URI、方法参数等信息生成，也可以通过注解表达式定制。

```java
@RateLimit(limit = 10, period = 60, message = "请求过于频繁")
@PostMapping("/sms/send")
public void sendSms(@RequestBody SendSmsCommand command) {
    smsService.send(command);
}

@PreventRepeat(timeout = 5, message = "请勿重复提交")
@PostMapping("/orders")
public Long createOrder(@RequestBody CreateOrderCommand command) {
    return orderService.create(command);
}
```

单机可使用 `LocalRateLimitStore` / `LocalRepeatSubmitStore`；集群环境切换到 `RedisRateLimitStore` / `RedisRepeatSubmitStore`。

### 分布式锁

`DistributedLockTemplate` 封装 Redisson 锁获取、等待时间、租约时间、异常处理和释放流程，适合订单创建、库存扣减、定时任务互斥等场景。

```java
Long orderId = distributedLockTemplate.execute(
        "order:create:" + command.requestNo(),
        1,
        30,
        TimeUnit.SECONDS,
        () -> orderService.create(command)
);
```

锁 key 前缀可通过 `under.utils.redis.lock-key-prefix` 统一配置，业务侧只需要提供明确的业务唯一键。

### Cache-Aside 与逻辑过期缓存

`CacheAsideTemplate` 适合标准 cache-aside：先查缓存，未命中加载数据，写入缓存，并支持空值缓存、TTL 抖动和重建锁，降低击穿、穿透风险。

```java
UserProfile profile = cacheAsideTemplate.get(
        "user:profile:" + userId,
        UserProfile.class,
        () -> userRepository.findProfile(userId)
);
```

`LogicalExpireCacheTemplate` 适合热点数据：逻辑过期后先返回旧值，同时异步刷新缓存，避免高峰期大量请求阻塞在重建链路上。

```java
ProductSnapshot snapshot = logicalExpireCacheTemplate.get(
        "product:snapshot:" + productId,
        ProductSnapshot.class,
        () -> productRepository.loadSnapshot(productId)
);
```

### OpenAPI Client

`under-utils-http` 提供面向开放平台调用的 `OpenApiClient`，把 token 获取、签名、trace/idempotency header、业务错误解码和重试策略集中管理，避免每个接口手写重复拦截逻辑。

```java
OpenApiClient client = new DefaultOpenApiClient(
        OpenApiClientOptions.builder()
                .maxRetries(2)
                .build(),
        accessTokenProvider,
        requestSigner,
        apiErrorDecoder
);

OpenApiResponse<CreateOrderResponse> response = client.execute(
        OpenApiRequest.builder()
                .url("https://open.example.com/v1/orders")
                .method(HttpMethod.POST)
                .body(command)
                .traceId(traceId)
                .idempotencyKey(command.requestNo())
                .operationName("createOrder")
                .build(),
        CreateOrderResponse.class
);
```

业务错误通过 `ApiErrorDecoder` 统一转换为 `OpenApiException` 或可识别结果，调用方不用在每个接口里重复判断响应码和业务码。

### 安全分页与审计填充

`SafePageQuery` 面向外部请求排序场景。前端传入的是稳定的业务字段名，后端通过 `SortFieldMapping` 映射到允许排序的数据库字段，避免直接拼接外部字段造成 SQL 风险。

```java
SortFieldMapping mapping = SortFieldMapping.builder()
        .add("createdAt", "create_time")
        .add("status", "status")
        .build();

SafePageQuery query = SafePageQuery.of(page, size)
        .orderByDesc("createdAt");

Page<UserEntity> mybatisPage = query.buildPage(mapping);
```

`AuditorProvider` 用于接入当前操作人，配合 `DefaultMetaObjectHandler` 自动填充创建人、更新人、创建时间、更新时间等字段。

```java
@Bean
AuditorProvider auditorProvider() {
    return () -> SecurityContext.currentUserId();
}
```

### 导入任务模板与 CSV 行读取

`ImportTaskTemplate` 把导入流程拆成读取、空行判断、解析、校验、处理、错误收集和结果统计。`CsvImportRowReader` 负责 CSV 解析，业务只实现每行怎么转成领域对象、怎么校验、怎么落库。

```java
CsvImportRowReader reader = CsvImportRowReader.builder(csvReader)
        .hasHeader(true)
        .build();

ImportResult result = ImportTaskTemplate.create().execute(reader, new ImportRowHandler<CsvRow, UserImportCommand>() {
    @Override
    public UserImportCommand parse(CsvRow row, ImportRowContext context) {
        return new UserImportCommand(row.get("username"), row.get("phone"));
    }

    @Override
    public void process(UserImportCommand command, ImportRowContext context) {
        userImportService.importOne(command);
    }
});
```

模板会返回总行数、成功数、失败数、跳过数和行级错误，便于在后台任务、管理端导入和批处理接口中复用。

## 新增能力准入标准

新增能力进入 Under-Utils 前，需要同时满足以下标准：

| 标准 | 要求 |
|------|------|
| 复杂性 | 不是简单静态方法，内部包含流程编排、状态管理、异常边界、资源释放、并发控制或外部系统交互 |
| 重复性 | 至少能在多个业务项目或多个模块中复用，能减少明显重复代码 |
| 边界清晰 | 有明确输入输出和职责边界，不把业务规则硬编码进公共模块 |
| 可测试 | 核心分支可以通过单元测试或集成测试验证，异常路径可覆盖 |
| 不冲突 | 不重复建设 Hutool、Apache Commons、Guava 已经成熟覆盖的基础工具方法 |
| 可演进 | API 命名稳定，配置项可解释，后续扩展不需要破坏已有调用方 |

不建议加入的能力：

- `StringUtils.isBlank`、`DateUtils.format`、`CollectionUtils.isEmpty` 这类通用工具方法。
- 只服务单个业务线、带强业务语义的流程。
- 依赖不稳定外部系统且无法抽象边界的封装。
- 没有测试、没有失败语义、只有“方便一下”的快捷方法。

## 开发约束

- Java 版本：21。
- Maven 多模块工程，依赖版本由 `under-utils-bom` 收口。
- 优先保持模块职责清晰：Spring 横切放 `under-utils-spring`，Redis 模式放 `under-utils-redis`，OpenAPI 调用放 `under-utils-http`，MyBatis 增强放 `under-utils-mybatis`，业务流程模板放 `under-utils-biz`。
- starter 只做自动装配和默认配置，不承载业务实现。
