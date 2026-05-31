# Crypto 重新建模备忘

`under-utils-core` 当前保留 `AESUtils`、`MD5Utils` 和 `SHA256Utils`，只作为历史兼容工具。它们不再作为安全治理主线扩展，也不再通过新增重载模拟完整加密框架。

## 当前结论

- `1.x` 内不新增 `under-utils-crypto` 模块。
- `AESUtils` 继续保留兼容 API，ECB/CBC 均不作为推荐能力宣传。
- 新增加密能力前必须先明确威胁模型、算法选择、密钥/IV 管理、认证加密和依赖边界。
- 如果没有明确业务场景，优先建议用户直接使用 JCA、Bouncy Castle 或业务安全团队提供的封装。

## 重新进入条件

同时满足以下条件时，才重新打开 crypto 方向：

- 至少有两个以上项目重复出现相同加解密封装需求。
- 需求不是“再包一层 AES encrypt/decrypt”，而是需要统一模式、填充、IV、AAD、错误语义和脱敏日志。
- 能提供离线单元测试向量，覆盖成功、错误密钥、错误 IV、篡改密文、空输入和字符集边界。
- 依赖重量可控，不让 `under-utils-core` 被动引入新的加密 provider。

## 候选模块边界

| 模块 | 说明 |
|------|------|
| `under-utils-crypto` | 独立 crypto 模块，承载对称加密、摘要、签名等模型化 API。 |
| `under-utils-core` | 只保留历史兼容工具，不继续新增加密 public API。 |

## 候选 API 形态

```java
SymmetricCrypto crypto = SymmetricCrypto.builder()
        .algorithm(SymmetricAlgorithm.AES)
        .mode(CipherMode.GCM)
        .padding(CipherPadding.NO_PADDING)
        .key(key)
        .iv(iv)
        .build();

byte[] cipherText = crypto.encrypt(plainText);
byte[] plainText = crypto.decrypt(cipherText);
```

## 非目标

- 不在 `under-utils-core` 继续堆静态方法重载。
- 不默认启用 ECB。
- 不在异常、日志或 `toString()` 输出密钥、IV、完整明文或完整密文。
- 不替代企业 KMS、证书生命周期管理或业务安全审计系统。

## `2.0.0` 前置检查

- 列出所有 core crypto public API 的真实使用路径。
- 为 `AESUtils`、`MD5Utils`、`SHA256Utils` 保留迁移说明。
- 明确旧 API 是否继续保留、移入兼容包，还是在 major 版本删除。
