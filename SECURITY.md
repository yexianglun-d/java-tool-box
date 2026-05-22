# Security Policy

## Supported Versions

Under-Utils 当前处于 GitHub 开源首发前的质量收口阶段，尚未发布稳定 Release。安全修复优先进入默认分支，并在后续 Release 中说明影响范围。

| Version | Supported |
|---------|-----------|
| `main` | Yes |
| Released versions before the first stable release | Best effort |

## Reporting a Vulnerability

请不要在公开 issue 中粘贴漏洞利用细节、密钥、内部地址、生产日志或可直接复现攻击的 payload。

推荐报告方式：

1. 使用 GitHub Security Advisories 私下报告漏洞。
2. 如果仓库尚未开启私有安全公告，请创建一个不包含利用细节的 issue，说明“需要私下报告安全问题”，维护者会建立私有沟通渠道。

报告内容建议包含：

- 受影响模块和版本。
- 漏洞类型和影响范围。
- 最小复现步骤或测试用例。
- 已知的临时缓解方案。
- 是否已经在公开环境中被利用。

维护者会尽快确认问题、评估影响范围，并在修复后通过 CHANGELOG 或 Release Notes 公开说明。
