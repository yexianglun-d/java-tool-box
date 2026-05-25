package com.undernine.utils.core.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 加密解密工具类。
 * <p>
 * 该类仅保留为兼容维护 API。当前实现主要覆盖 CBC/ECB 历史调用，不提供认证加密、密钥轮换、
 * KMS 集成或密文版本治理；新代码应优先使用 AES/GCM、JDK JCA 或统一加密服务。
 * </p>
 * <p>
     * 历史兼容能力：
 * <ul>
 *   <li>支持 AES-128/192/256 位密钥</li>
     *   <li>历史默认使用 AES/CBC/PKCS5Padding 模式</li>
 *   <li>支持自定义密钥和 IV（初始化向量）</li>
 *   <li>提供 Base64 编码的加密结果</li>
 * </ul>
 * </p>
 * <p>
 * 安全建议：
 * <ul>
 *   <li>使用 256 位密钥（需要 JCE Unlimited Strength Jurisdiction Policy）</li>
 *   <li>每次加密使用不同的 IV</li>
 *   <li>妥善保管密钥，不要硬编码在代码中</li>
 * </ul>
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 * @deprecated 历史加解密工具保留为兼容 API，不作为 Under-Utils 后续工程模式主线能力演进。
 */
@Deprecated(since = "1.0.0")
public final class AESUtils {

    /**
     * 算法名称
     */
    private static final String ALGORITHM = "AES";

    /**
     * 默认转换模式：AES/CBC/PKCS5Padding
     */
    private static final String DEFAULT_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    /**
     * ECB 模式（不推荐，不需要 IV）
     */
    private static final String ECB_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * 默认密钥长度（128 位）
     */
    private static final int DEFAULT_KEY_SIZE = 128;

    /**
     * IV 长度（128 位 = 16 字节）
     */
    private static final int IV_SIZE = 16;

    /**
     * 私有构造方法，防止实例化
     *
     * @throws UnsupportedOperationException 如果尝试实例化此类
     */
    private AESUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== 密钥生成 ====================

    /**
     * 生成随机 AES 密钥（128 位）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String key = AESUtils.generateKey();
     * }</pre>
     * </p>
     *
     * @return Base64 编码的密钥字符串
     */
    public static String generateKey() {
        return generateKey(DEFAULT_KEY_SIZE);
    }

    /**
     * 生成随机 AES 密钥（指定位数）。
     *
     * @param keySize 密钥长度（128、192 或 256 位）
     * @return Base64 编码的密钥字符串
     * @throws RuntimeException 如果密钥生成失败
     */
    public static String generateKey(int keySize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(keySize, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }

    /**
     * 生成随机 IV（初始化向量）。
     *
     * @return Base64 编码的 IV 字符串
     */
    public static String generateIV() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }

    // ==================== CBC 模式加密解密（历史兼容）====================

    /**
     * AES 加密（CBC 模式，Base64 编码）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String key = AESUtils.generateKey();
     * String iv = AESUtils.generateIV();
     * String encrypted = AESUtils.encrypt("hello world", key, iv);
     * }</pre>
     * </p>
     *
     * @param plainText 明文
     * @param key       Base64 编码的密钥
     * @param iv        Base64 编码的 IV
     * @return Base64 编码的密文
     * @throws IllegalArgumentException 如果参数为 null
     * @throws RuntimeException        如果加密失败
     * @deprecated CBC 模式仅保留历史兼容；新代码应使用认证加密或统一加密服务。
     */
    @Deprecated(since = "1.0.0")
    public static String encrypt(String plainText, String key, String iv) {
        if (plainText == null || key == null || iv == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);

            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encrypted = cipher.doFinal(plainBytes);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    /**
     * AES 解密（CBC 模式，Base64 编码）。
     *
     * @param cipherText Base64 编码的密文
     * @param key        Base64 编码的密钥
     * @param iv         Base64 编码的 IV
     * @return 明文
     * @throws IllegalArgumentException 如果参数为 null
     * @throws RuntimeException        如果解密失败
     * @deprecated CBC 模式仅保留历史兼容；新代码应使用认证加密或统一加密服务。
     */
    @Deprecated(since = "1.0.0")
    public static String decrypt(String cipherText, String key, String iv) {
        if (cipherText == null || key == null || iv == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            byte[] cipherBytes = Base64.getDecoder().decode(cipherText);

            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] decrypted = cipher.doFinal(cipherBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }

    // ==================== ECB 模式加密解密（不推荐，仅用于兼容）====================

    /**
     * AES 加密（ECB 模式，不需要 IV，不推荐使用）。
     * <p>
     * 注意：ECB 模式不安全，相同的明文块会产生相同的密文块，不建议在生产环境使用。
     * </p>
     *
     * @param plainText 明文
     * @param key       Base64 编码的密钥
     * @return Base64 编码的密文
     * @deprecated ECB 模式不安全，仅保留历史兼容；新代码不得使用。
     */
    @Deprecated(since = "1.0.0")
    public static String encryptECB(String plainText, String key) {
        if (plainText == null || key == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);

            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance(ECB_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encrypted = cipher.doFinal(plainBytes);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("AES ECB encryption failed", e);
        }
    }

    /**
     * AES 解密（ECB 模式，不需要 IV）。
     *
     * @param cipherText Base64 编码的密文
     * @param key        Base64 编码的密钥
     * @return 明文
     * @deprecated ECB 模式不安全，仅保留历史兼容；新代码不得使用。
     */
    @Deprecated(since = "1.0.0")
    public static String decryptECB(String cipherText, String key) {
        if (cipherText == null || key == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            byte[] cipherBytes = Base64.getDecoder().decode(cipherText);

            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance(ECB_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decrypted = cipher.doFinal(cipherBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES ECB decryption failed", e);
        }
    }

    // ==================== 字节数组加密解密 ====================

    /**
     * AES 加密（字节数组，CBC 模式）。
     *
     * @param plainBytes 明文字节数组
     * @param key        Base64 编码的密钥
     * @param iv         Base64 编码的 IV
     * @return 密文字节数组
     * @deprecated CBC 模式仅保留历史兼容；新代码应使用认证加密或统一加密服务。
     */
    @Deprecated(since = "1.0.0")
    public static byte[] encryptBytes(byte[] plainBytes, String key, String iv) {
        if (plainBytes == null || key == null || iv == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            byte[] ivBytes = Base64.getDecoder().decode(iv);

            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            return cipher.doFinal(plainBytes);
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    /**
     * AES 解密（字节数组，CBC 模式）。
     *
     * @param cipherBytes 密文字节数组
     * @param key         Base64 编码的密钥
     * @param iv          Base64 编码的 IV
     * @return 明文字节数组
     * @deprecated CBC 模式仅保留历史兼容；新代码应使用认证加密或统一加密服务。
     */
    @Deprecated(since = "1.0.0")
    public static byte[] decryptBytes(byte[] cipherBytes, String key, String iv) {
        if (cipherBytes == null || key == null || iv == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            byte[] ivBytes = Base64.getDecoder().decode(iv);

            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            return cipher.doFinal(cipherBytes);
        } catch (Exception e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }
}
