# Under-Utils Core

Low-coupling primitives used by other Under-Utils modules.

This module is not a general helper-method collection. Some historical static helpers remain for compatibility, but new additions should avoid overlap with the JDK, Spring, Hutool, Apache Commons, and Guava.

## Current Boundary

| API | Status | Notes |
|-----|--------|-------|
| `IdGenerator` | Active | Snowflake-style local ID generator. Requires unique worker/datacenter ids per node. |
| `MoneyUtils` | Active | Common BigDecimal money operations and yuan/fen conversion with fixed rounding behavior. |
| `JsonUtils` | Compatibility | Uses a module-owned `ObjectMapper`. Applications with their own Jackson configuration should prefer their own mapper or codec. |
| `StringUtils`, `CollectionUtils`, `LocalDateTimeUtils`, `ValidationUtils`, `UUIDUtils` | Compatibility | Existing methods are kept, but this is not an expansion path. |
| `MD5Utils`, `SHA256Utils`, `AESUtils` | Compatibility | Security-sensitive historical helpers. New security work should use application-approved crypto and key-management policy. |

## Dependency

```xml
<dependency>
    <groupId>io.github.yexianglun-d</groupId>
    <artifactId>under-utils-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Snowflake IDs

```java
IdGenerator generator = new IdGenerator(1, 1);

long id = generator.nextId();
String idText = generator.nextIdStr();

IdGenerator.IdInfo info = generator.parseId(id);
long timestamp = info.getTimestamp();
long datacenterId = info.getDatacenterId();
long workerId = info.getWorkerId();
```

Notes:

- `datacenterId` and `workerId` must be in `0..31`.
- The generator depends on system clock monotonicity and rejects clock rollback.
- Multi-node deployments must allocate node ids outside this library.

## Money Helpers

```java
Long fen = MoneyUtils.yuan2Fen(new BigDecimal("10.50"));
BigDecimal yuan = MoneyUtils.fen2Yuan(1050L);

BigDecimal total = MoneyUtils.add(
        new BigDecimal("10.50"),
        new BigDecimal("20.30")
);

BigDecimal avg = MoneyUtils.divide(total, new BigDecimal("3"));
String display = MoneyUtils.formatWithSymbol(total);
```

Defaults:

- Scale: `2`.
- Rounding: `RoundingMode.HALF_UP`.

For multi-currency, tax, accounting, or precision-sensitive domains, define an application money model instead of relying on generic helpers.

## JSON Compatibility Helper

```java
String json = JsonUtils.toJson(payload);
Payload parsed = JsonUtils.fromJson(json, Payload.class);
```

`JsonUtils.getObjectMapper()` returns a shared mapper and should not be reconfigured by callers.

## Crypto Notes

- `MD5Utils` is not suitable for password storage, signatures, or security checks.
- `SHA256Utils` is a digest helper, not a password hashing solution.
- `AESUtils` does not provide key rotation, KMS integration, ciphertext versioning, or authenticated encryption policy.
- `AESUtils.encryptECB` and `AESUtils.decryptECB` are retained only for old callers. Do not use ECB mode in new code.

## Contribution Rule

Do not add low-complexity helper methods here. New core APIs should define a reusable engineering boundary, failure behavior, and tests.
