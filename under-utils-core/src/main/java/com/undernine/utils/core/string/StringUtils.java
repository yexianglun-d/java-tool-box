package com.undernine.utils.core.string;

/**
 * 字符串工具类
 * <p>
 * 提供常用的字符串处理方法，如判空、trim、格式化等。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class StringUtils {

    /**
     * 私有构造方法，防止实例化
     *
     * @throws UnsupportedOperationException 如果尝试实例化此类
     */
    private StringUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 判断字符串是否为空（null 或去除首尾空格后长度为 0）。
     *
     * @param str 待判断字符串
     * @return 当字符串为 null 或空白时返回 true，否则返回 false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否非空（不为 null 且去除首尾空格后长度大于 0）。
     *
     * @param str 待判断字符串
     * @return 当字符串不为 null 且非空白时返回 true，否则返回 false
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白（null、空字符串或只包含空白字符）。
     *
     * @param str 待判断字符串
     * @return 当字符串为 null、空字符串或只包含空白字符时返回 true，否则返回 false
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否非空白。
     *
     * @param str 待判断字符串
     * @return 当字符串不为 null 且包含非空白字符时返回 true，否则返回 false
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 安全地对字符串进行 trim 操作，如果字符串为 null 则返回 null。
     *
     * @param str 待处理字符串
     * @return trim 后的字符串，如果输入为 null 则返回 null
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 安全地对字符串进行 trim 操作，如果字符串为 null 或 trim 后为空则返回 null。
     *
     * @param str 待处理字符串
     * @return trim 后的字符串，如果输入为 null 或 trim 后为空则返回 null
     */
    public static String trimToNull(String str) {
        String trimmed = trim(str);
        return isEmpty(trimmed) ? null : trimmed;
    }

    /**
     * 安全地对字符串进行 trim 操作，如果字符串为 null 或 trim 后为空则返回空字符串。
     *
     * @param str 待处理字符串
     * @return trim 后的字符串，如果输入为 null 或 trim 后为空则返回空字符串
     */
    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * 返回默认值（如果字符串为空）。
     *
     * @param str          待检查字符串
     * @param defaultValue 默认值
     * @return 如果字符串为空则返回默认值，否则返回原字符串
     */
    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * 返回默认值（如果字符串为空白）。
     *
     * @param str          待检查字符串
     * @param defaultValue 默认值
     * @return 如果字符串为空白则返回默认值，否则返回原字符串
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }
}
