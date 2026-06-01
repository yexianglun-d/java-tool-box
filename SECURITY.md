# 安全策略 / Security Policy

## 支持版本

安全修复会先进入 `main`。如果受影响代码已经发布，修复会进入下一个 patch 版本。

| 版本线 | 支持策略 |
|--------|----------|
| `main` | 支持 |
| `1.0.x` | 尽力维护 |

## 报告安全问题

请不要在公开 issue 中粘贴漏洞利用细节、密钥、内部地址、生产日志或可直接复现攻击的 payload。

推荐方式：

1. 如果仓库启用了 GitHub Security Advisories，请通过私有安全公告报告。
2. 如果暂未启用私有报告，请创建一个不包含漏洞细节的公开 issue，只说明需要私下报告安全问题；维护者会转到私有渠道沟通。

报告时建议提供：

- 受影响模块和版本。
- 漏洞类型和影响范围。
- 最小复现步骤或失败测试。
- 已知缓解方案。
- 是否已发现公开利用迹象。

维护者会确认问题、评估影响、准备修复，并在 Release Notes 或 `CHANGELOG.md` 中说明结果。

---

## Supported Versions

Security fixes are applied to `main` first. If affected code has already been released, the fix will be included in the next patch version.

| Version line | Support policy |
|--------------|----------------|
| `main` | Supported |
| `1.0.x` | Best-effort maintenance |

## Reporting a Security Issue

Do not paste exploit details, secrets, internal addresses, production logs, or directly reproducible attack payloads into public issues.

Recommended reporting process:

1. If GitHub Security Advisories are enabled for this repository, report the issue through a private security advisory.
2. If private reporting is not enabled yet, create a public issue without vulnerability details and state only that you need a private channel to report a security issue. Maintainers will continue the discussion privately.

When reporting, please include:

- Affected module and version.
- Vulnerability type and impact scope.
- Minimal reproduction steps or a failing test.
- Known mitigation options.
- Whether public exploitation has been observed.

Maintainers will confirm the issue, assess the impact, prepare a fix, and describe the outcome in Release Notes or `CHANGELOG.md`.
