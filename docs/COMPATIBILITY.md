# Compatibility Policy

This document defines how Under-Utils handles public API changes after `1.0.0`.

The goal is simple: users should be able to upgrade patch and minor versions without finding unrelated source changes, configuration rewrites, or starter behavior surprises.

## Versioning

Under-Utils follows semantic versioning with the following project rules.

| Version | Use For |
|---------|---------|
| Patch: `1.0.x` | Bug fixes, documentation, tests, internal refactors, and compatible behavior fixes. |
| Minor: `1.x.0` | New modules, new public APIs, new optional configuration, and compatible defaults. |
| Major: `2.0.0` | Planned breaking changes that remove or change public contracts. |

Patch releases must not require application source changes.

Minor releases may add APIs or configuration but should not remove or rename existing contracts.

Breaking changes are reserved for a major release unless the existing behavior is unsafe, impossible to support, or legally/security required to change. Those exceptions must be called out in `CHANGELOG.md` and release notes.

## Supported Lines

The repository mainly supports the latest released line and `main`.

| Line | Support |
|------|---------|
| `main` | Active development. |
| Latest `1.x` release | Bug fixes and security fixes as maintenance capacity allows. |
| Older release lines | Best effort only. |

## Public API

Treat the following as public once released:

- Maven coordinates: `groupId`, `artifactId`, module list, and published packaging.
- Public and protected Java types, constructors, methods, fields, annotations, enum constants, and checked/declared runtime exception contracts under `com.undernine.utils.*`.
- Annotation attributes and their defaults.
- Spring Boot configuration keys under `under.utils.*`, including default values and documented behavior.
- Starter auto-configured bean types and documented bean names.
- SPI interfaces intended for application implementation, such as stores, providers, codecs, decoders, signers, and handlers.
- Documented failure semantics, cache behavior, lock behavior, retry behavior, key generation behavior, and thread-safety assumptions.

The following are not public contracts:

- Package-private classes and members.
- Test classes and fixtures.
- `under-utils-samples` endpoint URLs and sample payloads.
- Internal implementation details not mentioned in README, module docs, JavaDoc, or API review notes.
- Build output and generated files.

## Compatible Changes

These changes are normally compatible:

- Adding a new class, method overload, constructor overload, enum type, module, or optional configuration key.
- Adding a default method to an SPI only when existing implementors continue to compile and run.
- Adding a Spring bean only when it backs off for user-defined beans and does not change existing wiring.
- Fixing behavior to match documented contracts.
- Tightening validation when the old behavior could produce invalid state or hidden data corruption.
- Improving documentation, tests, logging, or internal implementation.

When a compatible change may still affect runtime behavior, mention it in `CHANGELOG.md`.

## Breaking Changes

These changes are breaking and should wait for a major release:

- Removing or renaming a public type, method, constructor, field, annotation, enum constant, module, Maven coordinate, or configuration key.
- Changing method signatures, return types, generic bounds, checked exception declarations, or annotation attribute defaults.
- Making a previously public type or member less visible.
- Changing starter defaults in a way that enables new side effects or disables existing behavior.
- Replacing one required dependency with another when applications must change their setup.
- Changing serialized cache payload format without a migration or compatibility reader.
- Changing key generation, locking, retry, rate-limit, duplicate-submit, cache expiry, or error-decoding behavior in a way that invalidates existing assumptions.
- Removing a deprecated API before the next major version.

If a breaking change is unavoidable before a major version, the PR must explain why the exception is necessary and provide a migration path.

## Deprecation

Deprecation is the default path for replacing public APIs.

Rules:

- Mark Java APIs with `@Deprecated(since = "x.y.z", forRemoval = false)` when the source level allows it.
- Add JavaDoc that names the replacement or explains why there is no replacement.
- Keep deprecated APIs working for the rest of the current major version whenever possible.
- Add tests that protect compatibility behavior if the deprecated path still has active users.
- Record the deprecation in `CHANGELOG.md` and, when it affects API boundaries, `docs/API_REVIEW.md`.

Removal requires a major release by default. Security or correctness exceptions must be explicitly documented.

## Configuration Migration

Configuration keys are user-facing API.

When replacing a key:

- Keep the old key as an alias for at least one minor release when practical.
- Prefer warning logs over hard failures for the alias period.
- Document the old key, new key, default value, and migration example.
- Avoid changing default values in patch releases unless the old default is clearly broken or unsafe.

## Starter Auto-Configuration

Starter changes need extra care because they affect applications at startup.

PRs that add auto-configuration should verify:

- User-defined beans still win through `@ConditionalOnMissingBean` or an equivalent condition.
- Optional external systems such as Redis are only required when the feature is explicitly selected.
- Local defaults do not require Docker, Redis, MySQL, or private infrastructure.
- Failure mode is documented when a required bean is missing.
- Bean names are stable if they are documented.

## Pull Request Checklist

For any user-facing change, the PR should answer:

- Is this a public API change?
- Is it patch-compatible, minor-compatible, or breaking?
- Does it need deprecation first?
- Does it change a configuration key, default value, exception type, generated key, cache payload, or starter bean?
- Are README, module docs, `CHANGELOG.md`, and `docs/API_REVIEW.md` updated?
- Are compatibility tests included for the old path and the new path?

## Release Notes

Release notes should separate:

- Added compatible features.
- Fixed behavior.
- Deprecated APIs.
- Migration notes.
- Breaking changes, only for major releases or documented exceptions.
