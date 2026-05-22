package com.undernine.utils.core.id;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * UUID 生成工具类。
 * <p>
 * 该类仅保留为兼容维护 API。新代码中标准 UUID 建议直接使用 {@link java.util.UUID}；
 * 数据库主键或业务流水号场景优先使用边界更清晰的 {@link IdGenerator} 或业务专用 ID 方案。
 * </p>
 * <p>
 * UUID（Universally Unique Identifier）是一个 128 位的标识符，
 * 标准格式为 8-4-4-4-12 的 36 个字符（包含 4 个连字符）。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 * @deprecated 历史基础工具保留为兼容 API，不作为 Under-Utils 后续工程模式主线能力演进。
 */
@Deprecated(since = "1.0.0")
public final class UUIDUtils {

    /**
     * 私有构造方法，防止实例化
     *
     * @throws UnsupportedOperationException 如果尝试实例化此类
     */
    private UUIDUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== 随机 UUID ====================

    /**
     * 生成标准的随机 UUID（包含连字符）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String uuid = UUIDUtils.randomUUID();
     * // 结果：550e8400-e29b-41d4-a716-446655440000
     * }</pre>
     * </p>
     *
     * @return 标准 UUID 字符串
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成随机 UUID（不包含连字符）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String uuid = UUIDUtils.randomUUIDNoDash();
     * // 结果：550e8400e29b41d4a716446655440000
     * }</pre>
     * </p>
     *
     * @return UUID 字符串（不含连字符）
     */
    public static String randomUUIDNoDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成随机 UUID（大写，不包含连字符）。
     *
     * @return UUID 字符串（大写，不含连字符）
     */
    public static String randomUUIDUpperNoDash() {
        return randomUUIDNoDash().toUpperCase();
    }

    /**
     * 生成短 UUID（22 位，Base62 编码）。
     * <p>
     * 使用 Base62 编码（0-9a-zA-Z）将 UUID 压缩为 22 位字符串，
     * 更短且 URL 友好，适合作为短链接 ID、文件名等。
     * </p>
     *
     * @return 短 UUID 字符串（22 位）
     */
    public static String shortUUID() {
        UUID uuid = UUID.randomUUID();
        return toBase62(uuid);
    }

    // ==================== 基于名称的 UUID ====================

    /**
     * 基于名称生成 UUID（MD5 算法，版本 3）。
     * <p>
     * 相同的名称总是生成相同的 UUID，适用于需要确定性 ID 的场景。
     * </p>
     * <p>
     * 使用示例：
     * <pre>{@code
     * String uuid1 = UUIDUtils.nameUUIDFromString("test");
     * String uuid2 = UUIDUtils.nameUUIDFromString("test");
     * // uuid1 等于 uuid2
     * }</pre>
     * </p>
     *
     * @param name 名称字符串
     * @return UUID 字符串
     * @throws IllegalArgumentException 如果 name 为 null
     */
    public static String nameUUIDFromString(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return UUID.nameUUIDFromBytes(name.getBytes()).toString();
    }

    /**
     * 基于名称生成 UUID（不包含连字符）。
     *
     * @param name 名称字符串
     * @return UUID 字符串（不含连字符）
     * @throws IllegalArgumentException 如果 name 为 null
     */
    public static String nameUUIDFromStringNoDash(String name) {
        return nameUUIDFromString(name).replace("-", "");
    }

    // ==================== 有序 UUID（时间戳 UUID）====================

    /**
     * 生成基于时间戳的有序 UUID（适合数据库索引）。
     * <p>
     * 将时间戳放在 UUID 的前面部分，使得生成的 UUID 具有时间顺序性，
     * 有利于数据库 B-Tree 索引性能。
     * </p>
     *
     * @return 有序 UUID 字符串
     */
    public static String timeBasedUUID() {
        long timestamp = System.currentTimeMillis();
        UUID uuid = UUID.randomUUID();

        // 将时间戳放在最前面（16位十六进制）
        String timestampHex = String.format("%016x", timestamp);
        String uuidNoDash = uuid.toString().replace("-", "");

        // 组合：时间戳(16位) + UUID后半部分(16位)
        return timestampHex + uuidNoDash.substring(16);
    }

    /**
     * 生成基于时间戳的有序 UUID（带连字符，标准格式）。
     *
     * @return 有序 UUID 字符串（带连字符）
     */
    public static String timeBasedUUIDWithDash() {
        String timeUuid = timeBasedUUID();
        // 格式化为 8-4-4-4-12
        return String.format("%s-%s-%s-%s-%s",
                timeUuid.substring(0, 8),
                timeUuid.substring(8, 12),
                timeUuid.substring(12, 16),
                timeUuid.substring(16, 20),
                timeUuid.substring(20, 32));
    }

    // ==================== UUID 验证 ====================

    /**
     * 验证字符串是否为有效的 UUID 格式。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = UUIDUtils.isValidUUID("550e8400-e29b-41d4-a716-446655440000");
     * // 结果：true
     * }</pre>
     * </p>
     *
     * @param uuid UUID 字符串
     * @return 如果是有效的 UUID 格式返回 true，否则返回 false
     */
    public static boolean isValidUUID(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return false;
        }
        // 标准 UUID 格式必须是 36 个字符（8-4-4-4-12）
        if (uuid.length() != 36) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ==================== UUID 转换 ====================

    /**
     * 将 UUID 字符串转换为字节数组。
     *
     * @param uuid UUID 字符串
     * @return 字节数组（16 字节）
     * @throws IllegalArgumentException 如果 UUID 格式无效
     */
    public static byte[] toBytes(String uuid) {
        if (!isValidUUID(uuid)) {
            throw new IllegalArgumentException("Invalid UUID format: " + uuid);
        }
        UUID id = UUID.fromString(uuid);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());
        return buffer.array();
    }

    /**
     * 从字节数组创建 UUID 字符串。
     *
     * @param bytes 字节数组（必须是 16 字节）
     * @return UUID 字符串
     * @throws IllegalArgumentException 如果字节数组长度不是 16
     */
    public static String fromBytes(byte[] bytes) {
        if (bytes == null || bytes.length != 16) {
            throw new IllegalArgumentException("Byte array must be 16 bytes");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long mostSigBits = buffer.getLong();
        long leastSigBits = buffer.getLong();
        return new UUID(mostSigBits, leastSigBits).toString();
    }

    // ==================== 内部方法 ====================

    /**
     * Base62 字符集
     */
    private static final char[] BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * 将 UUID 转换为 Base62 编码（22 位）
     *
     * @param uuid UUID 对象
     * @return Base62 编码字符串
     */
    private static String toBase62(UUID uuid) {
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();

        // 将两个 long 合并为 byte 数组
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(mostSigBits);
        buffer.putLong(leastSigBits);
        byte[] bytes = buffer.array();

        // 转换为 Base62
        StringBuilder result = new StringBuilder();
        java.math.BigInteger num = new java.math.BigInteger(1, bytes);
        java.math.BigInteger base = java.math.BigInteger.valueOf(62);

        while (num.compareTo(java.math.BigInteger.ZERO) > 0) {
            int remainder = num.mod(base).intValue();
            result.insert(0, BASE62_CHARS[remainder]);
            num = num.divide(base);
        }

        // 补齐到 22 位
        while (result.length() < 22) {
            result.insert(0, '0');
        }

        return result.toString();
    }
}
