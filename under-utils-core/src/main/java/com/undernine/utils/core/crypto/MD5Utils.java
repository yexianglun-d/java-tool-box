package com.undernine.utils.core.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 摘要工具类。
 * <p>
 * 该类仅保留为兼容维护 API。MD5 不是加密算法，也不适合密码、签名或安全校验场景；
 * 新代码应优先使用 JDK {@link java.security.MessageDigest} 明确表达摘要用途，或使用专用密码哈希方案。
 * </p>
 * <p>
 * 注意：MD5 算法已不安全，仅适合历史兼容或非安全校验场景。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 * @deprecated 历史摘要工具保留为兼容 API，不作为 Under-Utils 后续工程模式主线能力演进。
 */
@Deprecated(since = "1.0.0")
public final class MD5Utils {

    /**
     * MD5 算法名称
     */
    private static final String ALGORITHM = "MD5";

    /**
     * 十六进制字符
     */
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * 私有构造方法，防止实例化
     *
     * @throws UnsupportedOperationException 如果尝试实例化此类
     */
    private MD5Utils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== 字符串 MD5 ====================

    /**
     * 计算字符串的 MD5 值（32 位小写十六进制）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String md5 = MD5Utils.md5("hello world");
     * // 结果：5eb63bbbe01eeed093cb22bb8f5acdc3
     * }</pre>
     * </p>
     *
     * @param text 待加密的字符串
     * @return MD5 值（32 位小写十六进制），如果输入为 null 则返回 null
     */
    public static String md5(String text) {
        if (text == null) {
            return null;
        }
        return md5(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 计算字符串的 MD5 值（32 位大写十六进制）。
     *
     * @param text 待加密的字符串
     * @return MD5 值（32 位大写十六进制），如果输入为 null 则返回 null
     */
    public static String md5Upper(String text) {
        String md5 = md5(text);
        return md5 != null ? md5.toUpperCase() : null;
    }

    /**
     * 计算字符串的 MD5 值（16 位小写十六进制，取中间 16 位）。
     *
     * @param text 待加密的字符串
     * @return MD5 值（16 位小写十六进制），如果输入为 null 或空则返回 null
     */
    public static String md5Short(String text) {
        String md5 = md5(text);
        return md5 != null ? md5.substring(8, 24) : null;
    }

    /**
     * 计算字符串的 MD5 值（16 位大写十六进制，取中间 16 位）。
     *
     * @param text 待加密的字符串
     * @return MD5 值（16 位大写十六进制），如果输入为 null 或空则返回 null
     */
    public static String md5ShortUpper(String text) {
        String md5Short = md5Short(text);
        return md5Short != null ? md5Short.toUpperCase() : null;
    }

    // ==================== 字节数组 MD5 ====================

    /**
     * 计算字节数组的 MD5 值（32 位小写十六进制）。
     *
     * @param bytes 待加密的字节数组
     * @return MD5 值（32 位小写十六进制），如果输入为 null 则返回 null
     */
    public static String md5(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        byte[] md5Bytes = digest(bytes);
        return bytesToHex(md5Bytes);
    }

    /**
     * 计算字节数组的 MD5 值（返回字节数组）。
     *
     * @param bytes 待加密的字节数组
     * @return MD5 值（字节数组），如果输入为 null 则返回 null
     */
    public static byte[] md5Bytes(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return digest(bytes);
    }

    // ==================== 加盐 MD5 ====================

    /**
     * 计算加盐的 MD5 值。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String md5 = MD5Utils.md5WithSalt("password", "randomSalt");
     * }</pre>
     * </p>
     *
     * @param text 待加密的字符串
     * @param salt 盐值
     * @return 加盐后的 MD5 值，如果任一参数为 null 或空则返回 null
     */
    public static String md5WithSalt(String text, String salt) {
        if (text == null || text.isEmpty() || salt == null || salt.isEmpty()) {
            return null;
        }
        return md5(text + salt);
    }

    // ==================== 文件 MD5（字节数组模拟）====================

    /**
     * 验证 MD5 值是否匹配。
     *
     * @param text     原始字符串
     * @param md5Value 待验证的 MD5 值
     * @return 如果匹配返回 true，否则返回 false
     */
    public static boolean verify(String text, String md5Value) {
        if (text == null || md5Value == null) {
            return false;
        }
        String computed = md5(text);
        return md5Value.equalsIgnoreCase(computed);
    }

    /**
     * 验证加盐 MD5 值是否匹配。
     *
     * @param text     原始字符串
     * @param salt     盐值
     * @param md5Value 待验证的 MD5 值
     * @return 如果匹配返回 true，否则返回 false
     */
    public static boolean verifyWithSalt(String text, String salt, String md5Value) {
        if (text == null || salt == null || md5Value == null) {
            return false;
        }
        String computed = md5WithSalt(text, salt);
        return md5Value.equalsIgnoreCase(computed);
    }

    // ==================== 内部方法 ====================

    /**
     * 计算字节数组的 MD5 摘要
     *
     * @param bytes 字节数组
     * @return MD5 摘要字节数组
     */
    private static byte[] digest(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            return md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            // MD5 算法是 JDK 标准算法，不应该抛出此异常
            throw new RuntimeException("MD5 algorithm not found", e);
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
