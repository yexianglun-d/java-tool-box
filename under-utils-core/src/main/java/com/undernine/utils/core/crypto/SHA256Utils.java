package com.undernine.utils.core.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA-256 摘要工具类。
 * <p>
 * 该类仅保留为兼容维护 API。SHA-256 是摘要算法，不是加密算法；新代码应按业务语义显式使用
 * JDK {@link java.security.MessageDigest}、HMAC 或专用密码哈希方案。
 * </p>
 * <p>
 * SHA-256 是 SHA-2 系列中的一种，提供 256 位（32 字节）的哈希值，
 * 安全性高于 MD5 和 SHA-1，但不能替代 BCrypt、PBKDF2、Argon2 等密码存储方案。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 * @deprecated 历史摘要工具保留为兼容 API，不作为 Under-Utils 后续工程模式主线能力演进。
 */
@Deprecated(since = "1.0.0")
public final class SHA256Utils {

    /**
     * SHA-256 算法名称
     */
    private static final String ALGORITHM = "SHA-256";

    /**
     * 十六进制字符
     */
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * 私有构造方法，防止实例化
     *
     * @throws UnsupportedOperationException 如果尝试实例化此类
     */
    private SHA256Utils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== 字符串 SHA-256 ====================

    /**
     * 计算字符串的 SHA-256 值（64 位小写十六进制）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String sha256 = SHA256Utils.sha256("hello world");
     * // 结果：b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9
     * }</pre>
     * </p>
     *
     * @param text 待加密的字符串
     * @return SHA-256 值（64 位小写十六进制），如果输入为 null 则返回 null
     */
    public static String sha256(String text) {
        if (text == null) {
            return null;
        }
        return sha256(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 计算字符串的 SHA-256 值（64 位大写十六进制）。
     *
     * @param text 待加密的字符串
     * @return SHA-256 值（64 位大写十六进制），如果输入为 null 则返回 null
     */
    public static String sha256Upper(String text) {
        String sha256 = sha256(text);
        return sha256 != null ? sha256.toUpperCase() : null;
    }

    // ==================== 字节数组 SHA-256 ====================

    /**
     * 计算字节数组的 SHA-256 值（64 位小写十六进制）。
     *
     * @param bytes 待加密的字节数组
     * @return SHA-256 值（64 位小写十六进制），如果输入为 null 则返回 null
     */
    public static String sha256(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        byte[] sha256Bytes = digest(bytes);
        return bytesToHex(sha256Bytes);
    }

    /**
     * 计算字节数组的 SHA-256 值（返回字节数组）。
     *
     * @param bytes 待加密的字节数组
     * @return SHA-256 值（字节数组），如果输入为 null 则返回 null
     */
    public static byte[] sha256Bytes(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return digest(bytes);
    }

    // ==================== 加盐 SHA-256 ====================

    /**
     * 计算加盐的 SHA-256 值。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String sha256 = SHA256Utils.sha256WithSalt("password", "randomSalt");
     * }</pre>
     * </p>
     *
     * @param text 待加密的字符串
     * @param salt 盐值
     * @return 加盐后的 SHA-256 值，如果任一参数为 null 或空则返回 null
     */
    public static String sha256WithSalt(String text, String salt) {
        if (text == null || text.isEmpty() || salt == null || salt.isEmpty()) {
            return null;
        }
        return sha256(text + salt);
    }

    // ==================== 验证 ====================

    /**
     * 验证 SHA-256 值是否匹配。
     *
     * @param text       原始字符串
     * @param sha256Value 待验证的 SHA-256 值
     * @return 如果匹配返回 true，否则返回 false
     */
    public static boolean verify(String text, String sha256Value) {
        if (text == null || sha256Value == null) {
            return false;
        }
        String computed = sha256(text);
        return sha256Value.equalsIgnoreCase(computed);
    }

    /**
     * 验证加盐 SHA-256 值是否匹配。
     *
     * @param text       原始字符串
     * @param salt       盐值
     * @param sha256Value 待验证的 SHA-256 值
     * @return 如果匹配返回 true，否则返回 false
     */
    public static boolean verifyWithSalt(String text, String salt, String sha256Value) {
        if (text == null || salt == null || sha256Value == null) {
            return false;
        }
        String computed = sha256WithSalt(text, salt);
        return sha256Value.equalsIgnoreCase(computed);
    }

    // ==================== 多次哈希 ====================

    /**
     * 对字符串进行多次 SHA-256 哈希（增强安全性）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String hash = SHA256Utils.sha256Multiple("password", 1000);
     * }</pre>
     * </p>
     *
     * @param text  待加密的字符串
     * @param times 哈希次数（必须大于 0）
     * @return 多次哈希后的 SHA-256 值
     * @throws IllegalArgumentException 如果 times 小于等于 0
     */
    public static String sha256Multiple(String text, int times) {
        if (times <= 0) {
            throw new IllegalArgumentException("Times must be greater than 0");
        }
        if (text == null || text.isEmpty()) {
            return null;
        }

        String result = text;
        for (int i = 0; i < times; i++) {
            result = sha256(result);
        }
        return result;
    }

    // ==================== 内部方法 ====================

    /**
     * 计算字节数组的 SHA-256 摘要
     *
     * @param bytes 字节数组
     * @return SHA-256 摘要字节数组
     */
    private static byte[] digest(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            return md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 算法是 JDK 标准算法，不应该抛出此异常
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串（小写）
     */
    private static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        char[] result = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            result[i * 2] = HEX_CHARS[v >>> 4];
            result[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(result);
    }
}
