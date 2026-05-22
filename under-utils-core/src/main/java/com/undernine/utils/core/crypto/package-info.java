/**
 * 摘要与加解密历史工具兼容包。
 * <p>
 * {@link com.undernine.utils.core.crypto.MD5Utils}、
 * {@link com.undernine.utils.core.crypto.SHA256Utils} 和
 * {@link com.undernine.utils.core.crypto.AESUtils} 仅保留兼容维护。
 * 安全相关新代码应优先使用 JDK JCA、HMAC、AES/GCM、专用密码哈希方案或统一加密服务。
 * </p>
 *
 * <h2>安全边界</h2>
 * <ul>
 *     <li>MD5 不适合密码、签名或安全校验。</li>
 *     <li>SHA-256 摘要不能替代 BCrypt、PBKDF2、Argon2 等密码存储方案。</li>
 *     <li>AES CBC/ECB 工具不提供认证加密、密钥轮换、KMS 集成或密文版本治理。</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core.crypto;
