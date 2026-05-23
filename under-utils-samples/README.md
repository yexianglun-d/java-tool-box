# Under-Utils Samples

Runnable Spring Boot sample application for Under-Utils.

The default profile does not require Redis or a database. It covers request context propagation, local rate limit and duplicate-submit guards, OpenAPI client usage, safe pagination request building, and CSV import workflow.

## Start

From the repository root:

```bash
mvn -pl under-utils-samples -am spring-boot:run
```

Default port: `18080`.

## Requests

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

## Redis Profile

Start Redis:

```bash
cd under-utils-samples
docker compose up -d
```

Run the app with the `redis` profile:

```bash
cd ..
mvn -pl under-utils-samples -am spring-boot:run -Dspring-boot.run.profiles=redis
```

The profile creates a sample `RedissonClient` from:

```yaml
samples:
  redis:
    address: redis://127.0.0.1:6379
    database: 0
    password:
```

Redis endpoints:

```bash
curl http://localhost:18080/samples/redis/status
curl http://localhost:18080/samples/redis/lock
curl http://localhost:18080/samples/redis/cache-aside
curl http://localhost:18080/samples/redis/logical-cache
```

Stop Redis:

```bash
cd under-utils-samples
docker compose down
```
