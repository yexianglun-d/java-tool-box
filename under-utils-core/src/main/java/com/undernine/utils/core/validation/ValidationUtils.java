package com.undernine.utils.core.validation;

import java.util.regex.Pattern;

/**
 * 参数校验工具类。
 * <p>
 * 该类仅保留为兼容维护 API。新代码应优先使用 Bean Validation、业务显式规则或专门校验组件，
 * 避免把会随地区、政策和业务场景变化的规则固化为通用工具方法。
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>所有方法都是空安全的，null 或空字符串返回 false</li>
 *   <li>使用预编译的正则表达式，提高性能</li>
 *   <li>支持中国大陆常用格式校验</li>
 * </ul>
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 * @deprecated 历史基础工具保留为兼容 API，不作为 Under-Utils 后续工程模式主线能力演进。
 */
@Deprecated(since = "1.0.0")
public final class ValidationUtils {

    /**
     * 手机号正则表达式（中国大陆 11 位手机号）
     * 支持：13x, 14x, 15x, 16x, 17x, 18x, 19x 开头
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 邮箱正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /**
     * 身份证号正则表达式（18 位）
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
            "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$"
    );

    /**
     * URL 正则表达式
     */
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * IPv4 地址正则表达式
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$"
    );

    /**
     * 中文字符正则表达式
     */
    private static final Pattern CHINESE_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5]+$");

    /**
     * 整数正则表达式（包括正负数）
     */
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^-?\\d+$");

    /**
     * 小数正则表达式（包括正负数）
     */
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("^-?\\d+\\.\\d+$");

    /**
     * 数字正则表达式（整数或小数）
     */
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    /**
     * 身份证号权重因子
     */
    private static final int[] ID_CARD_WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 身份证号校验码
     */
    private static final char[] ID_CARD_CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /**
     * 私有构造方法，防止实例化
     *
     * @throws UnsupportedOperationException 如果尝试实例化此类
     */
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== 手机号校验 ====================

    /**
     * 校验是否为有效的中国大陆手机号（11 位）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isPhone("13812345678"); // true
     * boolean invalid = ValidationUtils.isPhone("12345678901"); // false
     * }</pre>
     * </p>
     *
     * @param phone 待校验的手机号
     * @return 如果是有效的手机号返回 true，否则返回 false
     */
    public static boolean isPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    // ==================== 邮箱校验 ====================

    /**
     * 校验是否为有效的邮箱地址。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isEmail("user@example.com"); // true
     * boolean invalid = ValidationUtils.isEmail("invalid-email"); // false
     * }</pre>
     * </p>
     *
     * @param email 待校验的邮箱地址
     * @return 如果是有效的邮箱返回 true，否则返回 false
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    // ==================== 身份证号校验 ====================

    /**
     * 校验是否为有效的身份证号（18 位，含校验位验证）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isIdCard("110101199003074796"); // true（示例）
     * boolean invalid = ValidationUtils.isIdCard("123456789012345678"); // false
     * }</pre>
     * </p>
     *
     * @param idCard 待校验的身份证号
     * @return 如果是有效的身份证号返回 true，否则返回 false
     */
    public static boolean isIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return false;
        }

        // 格式校验
        if (!ID_CARD_PATTERN.matcher(idCard).matches()) {
            return false;
        }

        // 校验位验证
        return validateIdCardCheckCode(idCard);
    }

    /**
     * 验证身份证号的校验位
     *
     * @param idCard 身份证号
     * @return 校验位是否正确
     */
    private static boolean validateIdCardCheckCode(String idCard) {
        try {
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                int digit = idCard.charAt(i) - '0';
                sum += digit * ID_CARD_WEIGHTS[i];
            }
            int mod = sum % 11;
            char expectedCheckCode = ID_CARD_CHECK_CODES[mod];
            char actualCheckCode = Character.toUpperCase(idCard.charAt(17));

            return expectedCheckCode == actualCheckCode;
        } catch (Exception e) {
            return false;
        }
    }

    // ==================== URL 校验 ====================

    /**
     * 校验是否为有效的 URL 地址。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isUrl("https://www.example.com"); // true
     * boolean invalid = ValidationUtils.isUrl("not a url"); // false
     * }</pre>
     * </p>
     *
     * @param url 待校验的 URL
     * @return 如果是有效的 URL 返回 true，否则返回 false
     */
    public static boolean isUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }

    // ==================== IP 地址校验 ====================

    /**
     * 校验是否为有效的 IPv4 地址。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isIpv4("192.168.1.1"); // true
     * boolean invalid = ValidationUtils.isIpv4("256.1.1.1"); // false
     * }</pre>
     * </p>
     *
     * @param ip 待校验的 IP 地址
     * @return 如果是有效的 IPv4 地址返回 true，否则返回 false
     */
    public static boolean isIpv4(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip).matches();
    }

    // ==================== 中文校验 ====================

    /**
     * 校验是否全部为中文字符。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isChinese("中文"); // true
     * boolean invalid = ValidationUtils.isChinese("中文abc"); // false
     * }</pre>
     * </p>
     *
     * @param str 待校验的字符串
     * @return 如果全部为中文字符返回 true，否则返回 false
     */
    public static boolean isChinese(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        return CHINESE_PATTERN.matcher(str).matches();
    }

    /**
     * 校验字符串中是否包含中文字符。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean contains = ValidationUtils.containsChinese("abc中文def"); // true
     * boolean notContains = ValidationUtils.containsChinese("abcdef"); // false
     * }</pre>
     * </p>
     *
     * @param str 待校验的字符串
     * @return 如果包含中文字符返回 true，否则返回 false
     */
    public static boolean containsChinese(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (c >= 0x4e00 && c <= 0x9fa5) {
                return true;
            }
        }
        return false;
    }

    // ==================== 数字校验 ====================

    /**
     * 校验是否为整数（包括正负整数）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isInteger("123"); // true
     * boolean valid2 = ValidationUtils.isInteger("-456"); // true
     * boolean invalid = ValidationUtils.isInteger("123.45"); // false
     * }</pre>
     * </p>
     *
     * @param str 待校验的字符串
     * @return 如果是整数返回 true，否则返回 false
     */
    public static boolean isInteger(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        return INTEGER_PATTERN.matcher(str).matches();
    }

    /**
     * 校验是否为小数。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isDecimal("123.45"); // true
     * boolean valid2 = ValidationUtils.isDecimal("-67.89"); // true
     * boolean invalid = ValidationUtils.isDecimal("123"); // false
     * }</pre>
     * </p>
     *
     * @param str 待校验的字符串
     * @return 如果是小数返回 true，否则返回 false
     */
    public static boolean isDecimal(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        return DECIMAL_PATTERN.matcher(str).matches();
    }

    /**
     * 校验是否为数字（整数或小数）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid1 = ValidationUtils.isNumber("123"); // true
     * boolean valid2 = ValidationUtils.isNumber("123.45"); // true
     * boolean valid3 = ValidationUtils.isNumber("-67.89"); // true
     * boolean invalid = ValidationUtils.isNumber("abc"); // false
     * }</pre>
     * </p>
     *
     * @param str 待校验的字符串
     * @return 如果是数字返回 true，否则返回 false
     */
    public static boolean isNumber(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        return NUMBER_PATTERN.matcher(str).matches();
    }

    // ==================== 范围校验 ====================

    /**
     * 校验数值是否在指定范围内（包含边界）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isInRange(50, 1, 100); // true
     * boolean invalid = ValidationUtils.isInRange(150, 1, 100); // false
     * }</pre>
     * </p>
     *
     * @param value 待校验的数值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 如果在范围内返回 true，否则返回 false
     */
    public static boolean isInRange(Number value, Number min, Number max) {
        if (value == null || min == null || max == null) {
            return false;
        }
        double val = value.doubleValue();
        double minVal = min.doubleValue();
        double maxVal = max.doubleValue();
        return val >= minVal && val <= maxVal;
    }

    /**
     * 校验字符串长度是否在指定范围内（包含边界）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isLengthInRange("hello", 1, 10); // true
     * boolean invalid = ValidationUtils.isLengthInRange("hello", 1, 3); // false
     * }</pre>
     * </p>
     *
     * @param str       待校验的字符串
     * @param minLength 最小长度（包含）
     * @param maxLength 最大长度（包含）
     * @return 如果长度在范围内返回 true，否则返回 false
     */
    public static boolean isLengthInRange(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    // ==================== 特殊校验 ====================

    /**
     * 校验字符串是否只包含字母。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isAlpha("abcABC"); // true
     * boolean invalid = ValidationUtils.isAlpha("abc123"); // false
     * }</pre>
     * </p>
     *
     * @param str 待校验的字符串
     * @return 如果只包含字母返回 true，否则返回 false
     */
    public static boolean isAlpha(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验字符串是否只包含字母和数字。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.isAlphanumeric("abc123"); // true
     * boolean invalid = ValidationUtils.isAlphanumeric("abc-123"); // false
     * }</pre>
     * </p>
     *
     * @param str 待校验的字符串
     * @return 如果只包含字母和数字返回 true，否则返回 false
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验字符串是否符合指定的正则表达式。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean valid = ValidationUtils.matches("abc123", "^[a-z0-9]+$"); // true
     * }</pre>
     * </p>
     *
     * @param str   待校验的字符串
     * @param regex 正则表达式
     * @return 如果符合正则表达式返回 true，否则返回 false
     */
    public static boolean matches(String str, String regex) {
        if (str == null || regex == null) {
            return false;
        }
        return str.matches(regex);
    }
}
