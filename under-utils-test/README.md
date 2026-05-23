# Under-Utils Test

Integration test module for behavior that needs real external services.

This module is excluded from the default Maven reactor because it starts Docker containers through Testcontainers. It is not a user-facing sample app and is not published as a library module.

## What It Covers

- MyBatis-Plus behavior against MySQL.
- Redis cache templates against Redis.
- Cross-module integration checks that should not depend on a developer's local services.

## Requirements

- Java 21
- Maven 3.9+
- Docker

## Run

All integration tests:

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

MyBatis only:

```bash
mvn -Pintegration-tests -pl under-utils-test -am test -Dtest=MybatisIntegrationTest
```

Redis cache templates only:

```bash
mvn -Pintegration-tests -pl under-utils-test -am test -Dtest=RedisCacheTemplateIntegrationTest
```

If Docker is unavailable, use the default build instead:

```bash
mvn test
```
