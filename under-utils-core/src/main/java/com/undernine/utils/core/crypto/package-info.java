/**
 * 加密工具包
 * <p>
 * 提供常用的加密、摘要、编码工具类，包括 MD5、SHA-256、AES 等算法。
 * </p>
 *
 * <h2>主要工具类</h2>
 * <ul>
 *   <li>{@link com.undernine.utils.core.crypto.MD5Utils} - MD5 摘要工具类</li>
 *   <li>{@link com.undernine.utils.core.crypto.SHA256Utils} - SHA-256 摘要工具类</li>
 *   <li>{@link com.undernine.utils.core.crypto.AESUtils} - AES 加密工具类</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>MD5 摘要</h3>
 * <pre>{@code
 * // 计算字符串的 MD5 值
 * String md5 = MD5Utils.md5("hello world");
 *
 * // 计算加盐的 MD5 值
 * String md5WithSalt = MD5Utils.md5WithSalt("password", "randomSalt");
 *
 * // 验证 MD5 值
 * boolean isValid = MD5Utils.verify("hello world", md5);
 * }</pre>
 *
 * <h3>SHA-256 摘要</h3>
 * <pre>{@code
 * // 计算字符串的 SHA-256 值
 * String sha256 = SHA256Utils.sha256("hello world");
 *
 * // 计算加盐的 SHA-256 值
 * String sha256WithSalt = SHA256Utils.sha256WithSalt("password", "randomSalt");
 *
 * // 多次哈希（增强安全性）
 * String multiHash = SHA256Utils.sha256Multiple("password", 1000);
 * }</pre>
 *
 * <h3>AES 加密</h3>
 * <pre>{@code
 * // 生成密钥
 * String key = AESUtils.generateKey();
 *
 * // 加密
 * String encrypted = AESUtils.encrypt("hello world", key);
 *
 * // 解密
 * String decrypted = AESUtils.decrypt(encrypted, key);
 * }</pre>
 *
 * <h2>安全建议</h2>
 * <ul>
 *   <li><strong>MD5</strong>：已不安全，不建议用于密码加密，适用于文件校验、数据签名等非安全场景</li>
 *   <li><strong>SHA-256</strong>：安全性高，适用于密码加密、数字签名等安全场景</li>
 *   <li><strong>AES</strong>：对称加密算法，密钥长度 128 位，适用于数据加密</li>
 *   <li>密码加密建议使用 SHA-256 + 盐值 + 多次哈希</li>
 *   <li>密钥管理：密钥不应硬编码在代码中，应使用配置文件或密钥管理服务</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>所有方法都是空安全的，null 值不会抛出异常</li>
 *   <li>所有方法都是静态的、无状态的、线程安全的</li>
 *   <li>仅依赖 JDK，不依赖任何第三方框架</li>
 *   <li>加密失败时返回 null，不抛出异常</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core.crypto;
