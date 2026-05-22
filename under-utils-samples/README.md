# Under-Utils Samples

`under-utils-samples` 是用于验证 Under-Utils 真实使用体验的 Spring Boot 示例工程。它不是测试聚合模块，而是给业务项目参考的最小可运行入口。

默认配置不依赖 Redis 或数据库，可以直接启动并体验 Spring Web 横切、OpenAPI 调用、本地限流/防重提交、MyBatis 安全分页参数构建和 CSV 导入任务模板。

## 启动

```bash
mvn -pl under-utils-samples -am spring-boot:run
```

默认端口：`18080`。

## 可直接运行的接口

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

curl -X POST http://localhost:18080/samples/openapi/orders \
  -H 'Content-Type: application/json' \
  -H 'X-Trace-Id: trace-openapi' \
  -d '{"requestNo":"req-openapi-1","skuId":"sku-1","quantity":1}'
```

## Redis 场景

默认 `application.yml` 关闭 Redis 缓存模板，所以无需 Redis 也能启动。要体验分布式锁、cache-aside 和逻辑过期缓存，需要提供 `RedissonClient` Bean，并启用 `redis` profile：

```bash
mvn -pl under-utils-samples -am spring-boot:run -Dspring-boot.run.profiles=redis
```

相关接口：

```bash
curl http://localhost:18080/samples/redis/status
curl http://localhost:18080/samples/redis/lock
curl http://localhost:18080/samples/redis/cache-aside
curl http://localhost:18080/samples/redis/logical-cache
```

如果没有 `RedissonClient`，接口会返回缺少 Bean 的说明，不会导致应用启动失败。
