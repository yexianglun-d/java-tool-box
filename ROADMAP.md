# Roadmap

This roadmap is a working document. Priorities may change based on issues, real usage, and maintenance cost.

## Done

- Published `v1.0.0` to Maven Central under `io.github.yexianglun-d`.
- Added GitHub Actions CI for compile, default tests, and Testcontainers integration tests.
- Added Central Portal release workflow, GPG signing profile, sources and javadocs generation.
- Removed placeholder modules, empty placeholder classes, and obsolete internal planning documents.
- Clarified that Under-Utils is not a Hutool-style general utility collection.
- Moved Docker-dependent integration checks into `under-utils-test` behind the `integration-tests` profile.
- Kept `under-utils-samples` runnable without Redis or MySQL by default.
- Added GitHub community files: license, contributing guide, security policy, code of conduct, issue templates, and PR template.
- Reworked public docs into community-style maintenance docs with module-level READMEs for Redis, starter, and biz.
- Added starter auto-configuration tests for Redis store switching and user bean backoff.
- Added Redis Testcontainers coverage for cache-aside and logical-expire cache behavior.

## Near Term

- Write a compatibility policy for public API changes, deprecations, and release cadence.
- Keep API review notes current when configuration keys, exceptions, or starter defaults change.
- Add targeted examples for custom `RateLimitStore`, `RepeatSubmitStore`, and `CacheValueCodec`.

## Later

- Improve Redis cache observability around refresh failures, lock waits, null caching, and logical expiry.
- Expand OpenAPI examples for signing, idempotency, token refresh, and business error decoding.
- Add MyBatis examples for audit filling and multi-database assumptions.
- Extend the import workflow around async execution, progress query, and error export.
- Continue shrinking historical `under-utils-core` helper growth.

## Non-Goals

- Rebuilding Hutool, Apache Commons, Guava, or JDK helpers.
- Adding modules to increase package count without a tested reuse boundary.
- Encoding product-specific domains such as order, payment, marketing, or membership workflows.
