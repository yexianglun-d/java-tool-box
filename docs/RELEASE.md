# Release Guide

本指南记录 Under-Utils 发布到 Maven Central 的准备项和执行命令。项目使用 Central Publisher Portal，不使用旧 OSSRH / nexus-staging 发布流程。

参考官方文档：

- <https://central.sonatype.org/publish/publish-portal-maven/>
- <https://central.sonatype.org/register/namespace/>
- <https://central.sonatype.org/publish/requirements/>
- <https://central.sonatype.org/publish/requirements/gpg/>

## Preconditions

发布前需要先完成这些外部准备：

- 在 <https://central.sonatype.com> 使用 GitHub 账号登录或注册 Central Portal。
- 确认 Maven Central namespace。当前坐标已收敛为 GitHub 个人 namespace `io.github.yexianglun-d`，需要在 Central Portal 中确认该 namespace 已属于当前 GitHub 账号。
- 在 Central Portal 生成 user token，并保存 token username / token password。
- 创建 GPG/PGP 签名密钥，将公钥发布到 Central 支持的 key server，并安全保存私钥和 passphrase。
- 确认 `CHANGELOG.md`、README、示例和 public API 审计已经反映本次版本。

## Local Verification

默认验证命令不会上传到 Maven Central：

```bash
mvn test
mvn -Prelease -DskipTests package
mvn -Prelease,sign-artifacts -Dgpg.skip=true -DskipTests verify
```

验证 Central Portal 插件和 deploy 生命周期：

```bash
mvn -s docs/central-dry-run-settings.xml \
  -Prelease,central-publish \
  -Dcentral.publishing.server.id=central-dry-run \
  -Dcentral.skipPublishing=true \
  -Dgpg.skip=true \
  -DskipTests \
  deploy
```

`central-publish` profile 默认设置 `central.skipPublishing=true`，因此不会上传到 Central Portal。Central 插件仍会读取 `settings.xml` server，`docs/central-dry-run-settings.xml` 只提供 dry run 用的占位凭据。真实 bundle 会在 `central.skipPublishing=false` 且凭据有效时由插件生成并上传；`under-utils-samples` 作为示例工程已从发布构件中排除。

## Credentials

本地发布前在 `~/.m2/settings.xml` 配置 Central Portal token：

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

`central` 需要和根 POM 中的 `central.publishing.server.id` 保持一致。不要把真实 token、GPG 私钥或 passphrase 写入仓库。

## Publish To Central Portal

上传并等待 Central Portal 校验通过：

```bash
mvn -Prelease,sign-artifacts,central-publish \
  -Dgpg.sign=true \
  -Dcentral.skipPublishing=false \
  -Dcentral.autoPublish=false \
  -Dcentral.waitUntil=validated \
  -DskipTests \
  deploy
```

该命令不会自动发布到 Maven Central。上传通过校验后，进入 Central Portal 的 deployments 页面人工检查并点击 Publish。

如果后续要在 CI 中自动发布，可以显式开启：

```bash
mvn -Prelease,sign-artifacts,central-publish \
  -Dgpg.sign=true \
  -Dcentral.skipPublishing=false \
  -Dcentral.autoPublish=true \
  -Dcentral.waitUntil=published \
  -DskipTests \
  deploy
```

只有在版本号、构件内容、namespace、签名和回滚策略全部确认后，才应使用自动发布。Maven Central 的已发布构件不可修改或删除，修复只能通过新版本完成。

## GitHub Actions Secrets

手动发布工作流 `.github/workflows/release.yml` 需要以下 repository secrets：

- `CENTRAL_TOKEN_USERNAME`
- `CENTRAL_TOKEN_PASSWORD`
- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`

建议将工作流绑定到受保护的 `maven-central` environment，由维护者人工批准后执行。默认工作流上传后等待 `validated`，仍需要在 Central Portal 手动 Publish。
