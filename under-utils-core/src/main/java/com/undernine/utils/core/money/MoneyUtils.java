package com.undernine.utils.core.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 金额计算工具类
 * <p>
 * 提供金额相关的计算、转换、格式化等方法，避免浮点数精度问题。
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>所有金额计算使用 BigDecimal，避免精度丢失</li>
 *   <li>支持元和分之间的转换</li>
 *   <li>提供常用的加减乘除运算</li>
 *   <li>支持金额格式化显示</li>
 *   <li>线程安全</li>
 * </ul>
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>元转分时会四舍五入到整数</li>
 *   <li>所有计算默认保留 2 位小数</li>
 *   <li>除法运算默认使用四舍五入模式</li>
 * </ul>
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MoneyUtils {

    /**
     * 元转分的倍数
     */
    private static final BigDecimal YUAN_TO_FEN_RATE = new BigDecimal("100");

    /**
     * 默认小数位数
     */
    private static final int DEFAULT_SCALE = 2;

    /**
     * 默认舍入模式（四舍五入）
     */
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 零值
     */
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    /**
     * 私有构造方法，防止实例化
     *
     * @throws UnsupportedOperationException 如果尝试实例化此类
     */
    private MoneyUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== 单位转换 ====================

    /**
     * 元转分（四舍五入到整数）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * Long fen = MoneyUtils.yuan2Fen(new BigDecimal("10.50"));
     * // 结果：1050
     * }</pre>
     * </p>
     *
     * @param yuan 金额（元），可以为 null
     * @return 金额（分），如果输入为 null 则返回 null
     */
    public static Long yuan2Fen(BigDecimal yuan) {
        if (yuan == null) {
            return null;
        }
        return yuan.multiply(YUAN_TO_FEN_RATE).setScale(0, DEFAULT_ROUNDING_MODE).longValue();
    }

    /**
     * 分转元。
     * <p>
     * 使用示例：
     * <pre>{@code
     * BigDecimal yuan = MoneyUtils.fen2Yuan(1050L);
     * // 结果：10.50
     * }</pre>
     * </p>
     *
     * @param fen 金额（分），可以为 null
     * @return 金额（元），如果输入为 null 则返回 null
     */
    public static BigDecimal fen2Yuan(Long fen) {
        if (fen == null) {
            return null;
        }
        return new BigDecimal(fen).divide(YUAN_TO_FEN_RATE, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    // ==================== 四则运算 ====================

    /**
     * 金额相加。
     * <p>
     * 使用示例：
     * <pre>{@code
     * BigDecimal result = MoneyUtils.add(new BigDecimal("10.50"), new BigDecimal("20.30"));
     * // 结果：30.80
     * }</pre>
     * </p>
     *
     * @param value1 金额1
     * @param value2 金额2
     * @return 相加后的金额，如果任一参数为 null 则返回另一个值，都为 null 则返回 0
     */
    public static BigDecimal add(BigDecimal value1, BigDecimal value2) {
        if (value1 == null && value2 == null) {
            return ZERO;
        }
        if (value1 == null) {
            return value2.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        }
        if (value2 == null) {
            return value1.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        }
        return value1.add(value2).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 多个金额相加。
     * <p>
     * 使用示例：
     * <pre>{@code
     * BigDecimal result = MoneyUtils.add(
     *     new BigDecimal("10.50"),
     *     new BigDecimal("20.30"),
     *     new BigDecimal("5.00")
     * );
     * // 结果：35.80
     * }</pre>
     * </p>
     *
     * @param values 金额数组
     * @return 相加后的金额，如果数组为空或全为 null 则返回 0
     */
    public static BigDecimal add(BigDecimal... values) {
        if (values == null || values.length == 0) {
            return ZERO;
        }
        BigDecimal result = ZERO;
        for (BigDecimal value : values) {
            if (value != null) {
                result = result.add(value);
            }
        }
        return result.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 金额相减。
     * <p>
     * 使用示例：
     * <pre>{@code
     * BigDecimal result = MoneyUtils.subtract(new BigDecimal("30.50"), new BigDecimal("10.30"));
     * // 结果：20.20
     * }</pre>
     * </p>
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 相减后的金额，如果 value1 为 null 则返回 -value2，如果 value2 为 null 则返回 value1
     * @throws NullPointerException 如果两个参数都为 null
     */
    public static BigDecimal subtract(BigDecimal value1, BigDecimal value2) {
        if (value1 == null && value2 == null) {
            throw new NullPointerException("Both values cannot be null");
        }
        if (value1 == null) {
            return value2.negate().setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        }
        if (value2 == null) {
            return value1.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        }
        return value1.subtract(value2).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 金额相乘。
     * <p>
     * 使用示例：
     * <pre>{@code
     * BigDecimal result = MoneyUtils.multiply(new BigDecimal("10.50"), new BigDecimal("2"));
     * // 结果：21.00
     * }</pre>
     * </p>
     *
     * @param value1 金额
     * @param value2 乘数
     * @return 相乘后的金额
     * @throws NullPointerException 如果任一参数为 null
     */
    public static BigDecimal multiply(BigDecimal value1, BigDecimal value2) {
        if (value1 == null || value2 == null) {
            throw new NullPointerException("Values cannot be null");
        }
        return value1.multiply(value2).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 金额相除（使用默认的四舍五入模式）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * BigDecimal result = MoneyUtils.divide(new BigDecimal("10.00"), new BigDecimal("3"));
     * // 结果：3.33
     * }</pre>
     * </p>
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return 相除后的金额
     * @throws NullPointerException 如果任一参数为 null
     * @throws ArithmeticException  如果除数为 0
     */
    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return divide(dividend, divisor, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 金额相除（指定小数位数和舍入模式）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * BigDecimal result = MoneyUtils.divide(
     *     new BigDecimal("10.00"),
     *     new BigDecimal("3"),
     *     4,
     *     RoundingMode.HALF_UP
     * );
     * // 结果：3.3333
     * }</pre>
     * </p>
     *
     * @param dividend     被除数
     * @param divisor      除数
     * @param scale        小数位数
     * @param roundingMode 舍入模式
     * @return 相除后的金额
     * @throws NullPointerException 如果任一参数为 null
     * @throws ArithmeticException  如果除数为 0
     */
    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale, RoundingMode roundingMode) {
        if (dividend == null || divisor == null) {
            throw new NullPointerException("Values cannot be null");
        }
        if (roundingMode == null) {
            roundingMode = DEFAULT_ROUNDING_MODE;
        }
        return dividend.divide(divisor, scale, roundingMode);
    }

    // ==================== 比较运算 ====================

    /**
     * 比较两个金额的大小。
     * <p>
     * 使用示例：
     * <pre>{@code
     * int result = MoneyUtils.compare(new BigDecimal("10.50"), new BigDecimal("20.30"));
     * // 结果：-1（第一个小于第二个）
     * }</pre>
     * </p>
     *
     * @param value1 金额1
     * @param value2 金额2
     * @return -1（value1 < value2）、0（value1 = value2）、1（value1 > value2）
     * @throws NullPointerException 如果任一参数为 null
     */
    public static int compare(BigDecimal value1, BigDecimal value2) {
        if (value1 == null || value2 == null) {
            throw new NullPointerException("Values cannot be null");
        }
        return value1.compareTo(value2);
    }

    /**
     * 判断两个金额是否相等。
     * <p>
     * 使用示例：
     * <pre>{@code
     * boolean equal = MoneyUtils.equals(new BigDecimal("10.50"), new BigDecimal("10.50"));
     * // 结果：true
     * }</pre>
     * </p>
     *
     * @param value1 金额1
     * @param value2 金额2
     * @return 如果相等返回 true，否则返回 false
     */
    public static boolean equals(BigDecimal value1, BigDecimal value2) {
        if (value1 == null && value2 == null) {
            return true;
        }
        if (value1 == null || value2 == null) {
            return false;
        }
        return value1.compareTo(value2) == 0;
    }

    /**
     * 判断金额是否大于另一个金额。
     *
     * @param value1 金额1
     * @param value2 金额2
     * @return 如果 value1 > value2 返回 true，否则返回 false
     * @throws NullPointerException 如果任一参数为 null
     */
    public static boolean greaterThan(BigDecimal value1, BigDecimal value2) {
        return compare(value1, value2) > 0;
    }

    /**
     * 判断金额是否大于等于另一个金额。
     *
     * @param value1 金额1
     * @param value2 金额2
     * @return 如果 value1 >= value2 返回 true，否则返回 false
     * @throws NullPointerException 如果任一参数为 null
     */
    public static boolean greaterThanOrEqual(BigDecimal value1, BigDecimal value2) {
        return compare(value1, value2) >= 0;
    }

    /**
     * 判断金额是否小于另一个金额。
     *
     * @param value1 金额1
     * @param value2 金额2
     * @return 如果 value1 < value2 返回 true，否则返回 false
     * @throws NullPointerException 如果任一参数为 null
     */
    public static boolean lessThan(BigDecimal value1, BigDecimal value2) {
        return compare(value1, value2) < 0;
    }

    /**
     * 判断金额是否小于等于另一个金额。
     *
     * @param value1 金额1
     * @param value2 金额2
     * @return 如果 value1 <= value2 返回 true，否则返回 false
     * @throws NullPointerException 如果任一参数为 null
     */
    public static boolean lessThanOrEqual(BigDecimal value1, BigDecimal value2) {
        return compare(value1, value2) <= 0;
    }

    /**
     * 判断金额是否为零。
     *
     * @param value 金额
     * @return 如果为零返回 true，否则返回 false
     */
    public static boolean isZero(BigDecimal value) {
        return value != null && value.compareTo(ZERO) == 0;
    }

    /**
     * 判断金额是否为正数。
     *
     * @param value 金额
     * @return 如果大于零返回 true，否则返回 false
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(ZERO) > 0;
    }

    /**
     * 判断金额是否为负数。
     *
     * @param value 金额
     * @return 如果小于零返回 true，否则返回 false
     */
    public static boolean isNegative(BigDecimal value) {
        return value != null && value.compareTo(ZERO) < 0;
    }

    // ==================== 格式化 ====================

    /**
     * 格式化金额为标准显示格式（保留 2 位小数，千分位分隔）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String formatted = MoneyUtils.format(new BigDecimal("12345.67"));
     * // 结果：12,345.67
     * }</pre>
     * </p>
     *
     * @param value 金额
     * @return 格式化后的字符串，如果输入为 null 则返回 "0.00"
     */
    public static String format(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(value);
    }

    /**
     * 格式化金额为指定格式。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String formatted = MoneyUtils.format(new BigDecimal("12345.67"), "#,##0.00元");
     * // 结果：12,345.67元
     * }</pre>
     * </p>
     *
     * @param value   金额
     * @param pattern 格式模式
     * @return 格式化后的字符串
     * @throws IllegalArgumentException 如果 pattern 为空
     */
    public static String format(BigDecimal value, String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be empty");
        }
        if (value == null) {
            value = ZERO;
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(value);
    }

    /**
     * 格式化金额为货币显示格式（带货币符号）。
     * <p>
     * 使用示例：
     * <pre>{@code
     * String formatted = MoneyUtils.formatWithSymbol(new BigDecimal("12345.67"));
     * // 结果：¥12,345.67
     * }</pre>
     * </p>
     *
     * @param value 金额
     * @return 格式化后的字符串
     */
    public static String formatWithSymbol(BigDecimal value) {
        return "¥" + format(value);
    }

    /**
     * 格式化金额为货币显示格式（自定义货币符号）。
     *
     * @param value  金额
     * @param symbol 货币符号
     * @return 格式化后的字符串
     */
    public static String formatWithSymbol(BigDecimal value, String symbol) {
        if (symbol == null) {
            symbol = "¥";
        }
        return symbol + format(value);
    }

    // ==================== 其他工具方法 ====================

    /**
     * 获取两个金额中的较大值。
     *
     * @param value1 金额1
     * @param value2 金额2
     * @return 较大的金额
     * @throws NullPointerException 如果任一参数为 null
     */
    public static BigDecimal max(BigDecimal value1, BigDecimal value2) {
        if (value1 == null || value2 == null) {
            throw new NullPointerException("Values cannot be null");
        }
        return value1.compareTo(value2) >= 0 ? value1 : value2;
    }

    /**
     * 获取两个金额中的较小值。
     *
     * @param value1 金额1
     * @param value2 金额2
     * @return 较小的金额
     * @throws NullPointerException 如果任一参数为 null
     */
    public static BigDecimal min(BigDecimal value1, BigDecimal value2) {
        if (value1 == null || value2 == null) {
            throw new NullPointerException("Values cannot be null");
        }
        return value1.compareTo(value2) <= 0 ? value1 : value2;
    }

    /**
     * 获取金额的绝对值。
     *
     * @param value 金额
     * @return 绝对值
     * @throws NullPointerException 如果参数为 null
     */
    public static BigDecimal abs(BigDecimal value) {
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }
        return value.abs().setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 获取金额的相反数。
     *
     * @param value 金额
     * @return 相反数
     * @throws NullPointerException 如果参数为 null
     */
    public static BigDecimal negate(BigDecimal value) {
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }
        return value.negate().setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 设置金额的小数位数（使用默认的四舍五入模式）。
     *
     * @param value 金额
     * @param scale 小数位数
     * @return 设置小数位数后的金额
     * @throws NullPointerException 如果参数为 null
     */
    public static BigDecimal setScale(BigDecimal value, int scale) {
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }
        return value.setScale(scale, DEFAULT_ROUNDING_MODE);
    }
}
