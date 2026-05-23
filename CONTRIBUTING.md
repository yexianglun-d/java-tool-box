# Contributing

Thanks for taking the time to improve Under-Utils.

This project accepts code that packages repeated engineering patterns. It does not accept generic helper-method growth for its own sake.

## Project Fit

A feature is a good fit when it:

- Solves a repeated infrastructure problem across services.
- Has a clear module boundary and does not embed one application's business rules.
- Defines failure behavior, thread-safety assumptions, and external dependency requirements.
- Can be tested with unit tests or reproducible integration tests.
- Does not duplicate mature JDK, Spring, Hutool, Apache Commons, or Guava APIs.

Examples of suitable areas:

- Request context propagation.
- Rate limiting and duplicate-submit protection.
- Redis locks and cache rebuild templates.
- OpenAPI client governance.
- Safe pagination and audit filling.
- Import task workflows.

Examples that usually do not belong here:

- `StringUtils.isBlank`, `DateUtils.format`, `CollectionUtils.map`, or similar small helpers.
- A workflow tied to one product domain such as orders, payments, membership, or marketing.
- A wrapper with no defined error handling or tests.

## Before Starting

Open an issue first for non-trivial work. Include:

- The repeated scenario you want to solve.
- Why existing libraries or current modules are not enough.
- Proposed module, public API, configuration keys, and failure semantics.
- Compatibility impact: patch-compatible, minor-compatible, breaking, or deprecation-only.
- Runtime assumptions such as Redis, database, thread pool, clock, or network behavior.
- Test plan.

Small fixes, tests, and documentation improvements can go straight to a PR.

## Local Setup

Requirements:

- Java 21
- Maven 3.9+
- Docker only for Testcontainers integration tests

Common commands:

```bash
mvn -DskipTests compile
mvn test
mvn -Prelease -DskipTests package
```

Integration tests:

```bash
mvn -Pintegration-tests -pl under-utils-test -am test
```

Central Portal dry run:

```bash
mvn -s docs/central-dry-run-settings.xml \
  -Prelease,central-publish \
  -Dcentral.publishing.server.id=central-dry-run \
  -Dcentral.skipPublishing=true \
  -Dgpg.skip=true \
  -DskipTests \
  deploy
```

## Pull Requests

Before requesting review, check that:

- The change has a clear module owner.
- Public APIs are documented where the behavior is not obvious.
- Public API changes follow [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md).
- Breaking changes are avoided outside major releases unless a documented safety or security exception applies.
- Deprecated APIs include a replacement or migration note.
- Behavior changes include tests.
- Default `mvn test` does not require Redis, MySQL, Docker, or private infrastructure.
- `mvn -Prelease -DskipTests package` still generates sources and javadocs.
- README, module docs, or `CHANGELOG.md` are updated for user-facing changes.
- No build output, local path, token, private key, internal report, or production log is committed.

## Commit Messages

Chinese commit messages are fine. Keep them short and outcome-focused:

```text
完善 Redis 缓存模板文档
修复 OpenAPI 重试异常处理
补充 MyBatis 安全分页测试
```

## Review Focus

Maintainers will mainly check:

- Whether the change fits the project scope.
- Whether the API can stay stable.
- Whether the compatibility impact is correctly classified.
- Whether dependencies and auto-configuration have acceptable side effects.
- Whether failure handling, resource release, and concurrency boundaries are explicit.
- Whether the change avoids low-value overlap with existing utility libraries.
