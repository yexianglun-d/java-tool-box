# Under-Utils Samples

Under-Utils 的 Spring Boot 可运行示例工程。

默认 profile 不需要 Redis 或数据库，覆盖请求上下文传播、本地限流/防重提交、OpenAPI 客户端用法、安全分页参数构建和 CSV 导入流程。

## 启动

从仓库根目录执行：

```bash
mvn -pl under-utils-samples -am spring-boot:run
```

默认端口：`18080`。

## 请求样例

```bash
curl http://localhost:18080/samples/context/current \
  -H 'X-User-Id: u1001' \
  -H 'X-Tenant-Id: t1001' \
  -H 'X-Trace-Id: trace-sample'

curl http://localhost:18080/samples/context/async \
  -H 'X-User-Id: u1001' \
  -H 'X-Tenant-Id: t1001' \
  -H 'X-Trace-Id: trace-sample'

curl -X POST http://localhost:18080/samples/guard/sms \
  -H 'Content-Type: application/json' \
  -d '{"phone":"13800000000","templateCode":"LOGIN"}'

curl -X POST http://localhost:18080/samples/guard/orders \
  -H 'Content-Type: application/json' \
  -d '{"requestNo":"req-1001","skuId":"sku-1","quantity":1}'

curl 'http://localhost:18080/samples/mybatis/page?current=1&size=20&sort=createdAt&direction=desc'

curl -X POST http://localhost:18080/samples/import/users \
  -H 'Content-Type: text/plain' \
  --data-binary $'username,phone\nAlice,13800000000\n,13900000000\nBob,'

TASK_ID=$(curl -s -X POST http://localhost:18080/samples/import/users/async \
  -H 'Content-Type: text/plain' \
  --data-binary $'username,phone\nAlice,13800000000\n,13900000000\nBob,' \
  | sed -E 's/.*"taskId":"([^"]+)".*/\1/')

curl "http://localhost:18080/samples/import/tasks/${TASK_ID}"
curl "http://localhost:18080/samples/import/tasks/${TASK_ID}/errors.csv"

curl -X POST http://localhost:18080/samples/openapi/orders \
  -H 'Content-Type: application/json' \
  -H 'X-Trace-Id: trace-openapi' \
  -d '{"requestNo":"req-openapi-1","skuId":"sku-1","quantity":1}'

curl -X POST http://localhost:18080/samples/openapi/orders/envelope \
  -H 'Content-Type: application/json' \
  -H 'X-Trace-Id: trace-openapi' \
  -d '{"requestNo":"req-openapi-2","skuId":"sku-1","quantity":0}'
```

## Redis Profile

启动 Redis：

```bash
cd under-utils-samples
docker compose up -d
```

启用 `redis` profile 运行应用：

```bash
cd ..
mvn -pl under-utils-samples -am spring-boot:run -Dspring-boot.run.profiles=redis
```

该 profile 会根据以下配置创建示例 `RedissonClient`：

```yaml
samples:
  redis:
    address: redis://127.0.0.1:6379
    database: 0
    password:
```

Redis 相关接口：

```bash
curl http://localhost:18080/samples/redis/status
curl http://localhost:18080/samples/redis/lock
curl http://localhost:18080/samples/redis/cache-aside
curl http://localhost:18080/samples/redis/logical-cache
```

停止 Redis：

```bash
cd under-utils-samples
docker compose down
```

## 自定义存储 Profile

`custom-store` profile 展示如何替换 starter 的状态存储和缓存编解码边界：

```bash
mvn -pl under-utils-samples -am spring-boot:run -Dspring-boot.run.profiles=custom-store
```

该 profile 提供：

- 自定义 `RateLimitStore`
- 自定义 `RepeatSubmitStore`
- 自定义 `CacheValueCodec`
- 自定义 `CacheOperationObserver`

这些实现只用于演示 SPI 接入方式，不建议直接作为生产存储实现。
