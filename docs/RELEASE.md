# Release Guide

Under-Utils publishes to Maven Central through Central Publisher Portal. The project does not use the legacy OSSRH / nexus-staging flow.

Official references:

- <https://central.sonatype.org/publish/publish-portal-maven/>
- <https://central.sonatype.org/register/namespace/>
- <https://central.sonatype.org/publish/requirements/>
- <https://central.sonatype.org/publish/requirements/gpg/>

## Prerequisites

- Central Portal account.
- Verified namespace: `io.github.yexianglun-d`.
- Central Portal user token.
- GPG private key, public key published to a supported key server, and passphrase.
- Updated `CHANGELOG.md`, README, samples, and API review notes for the release.

Never commit Central tokens, GPG private keys, passphrases, or generated release bundles.

## Local Checks

```bash
mvn test
mvn -Prelease -DskipTests package
mvn -Prelease,sign-artifacts -Dgpg.skip=true -DskipTests verify
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

The `central-publish` profile skips upload by default. `docs/central-dry-run-settings.xml` only provides placeholder credentials because the Central plugin still requires a server entry during the deploy lifecycle.

`under-utils-samples` participates in build verification but is excluded from Central publication.

## Credentials

For local publishing, configure a `central` server in `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>${env.CENTRAL_TOKEN_USERNAME}</username>
      <password>${env.CENTRAL_TOKEN_PASSWORD}</password>
    </server>
  </servers>
</settings>
```

The server id must match `central.publishing.server.id`.

## Manual Publish

Upload a bundle and wait for Central validation:

```bash
mvn -Prelease,sign-artifacts,central-publish \
  -Dgpg.sign=true \
  -Dcentral.skipPublishing=false \
  -Dcentral.autoPublish=false \
  -Dcentral.waitUntil=validated \
  -DskipTests \
  deploy
```

After validation, review the deployment in Central Portal and click Publish manually.

Maven Central artifacts cannot be changed or removed after publishing. Fixes require a new version.

## GitHub Actions

`.github/workflows/release.yml` runs the same publish flow through a manual workflow dispatch.

Required repository secrets:

- `CENTRAL_TOKEN_USERNAME`
- `CENTRAL_TOKEN_PASSWORD`
- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`

The workflow should stay attached to the protected `maven-central` environment. Use `validated` mode for normal releases so the final Publish click remains manual. Use `published` only when the version, namespace, signatures, changelog, and rollback plan are already confirmed.
