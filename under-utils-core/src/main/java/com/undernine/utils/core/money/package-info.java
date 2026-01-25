/**
 * 金额计算工具包
 * <p>
 * 提供精确的金额计算工具类，基于 {@link java.math.BigDecimal} 实现，避免浮点数精度问题。
 * </p>
 *
 * <h2>主要工具类</h2>
 * <ul>
 *   <li>{@link com.undernine.utils.core.money.MoneyUtils} - 金额计算工具类</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>金额计算</h3>
 * <pre>{@code
 * // 加法
 * BigDecimal sum = MoneyUtils.add("100.50", "200.30");  // 300.80
 *
 * // 减法
 * BigDecimal diff = MoneyUtils.subtract("300.80", "100.50");  // 200.30
 *
 * // 乘法
 * BigDecimal product = MoneyUtils.multiply("100.50", "2");  // 201.00
 *
 * // 除法（保留 2 位小数，四舍五入）
 * BigDecimal quotient = MoneyUtils.divide("100", "3", 2);  // 33.33
 * }</pre>
 *
 * <h3>金额格式化</h3>
 * <pre>{@code
 * // 格式化为字符串（保留 2 位小数）
 * String formatted = MoneyUtils.format(new BigDecimal("100.5"));  // "100.50"
 *
 * // 格式化为带货币符号的字符串
 * String withSymbol = MoneyUtils.formatWithSymbol(new BigDecimal("100.50"), "¥");  // "¥100.50"
 * }</pre>
 *
 * <h3>金额比较</h3>
 * <pre>{@code
 * BigDecimal amount1 = new BigDecimal("100.50");
 * BigDecimal amount2 = new BigDecimal("200.30");
 *
 * boolean isEqual = MoneyUtils.equals(amount1, amount2);  // false
 * boolean isGreater = MoneyUtils.greaterThan(amount2, amount1);  // true
 * boolean isLess = MoneyUtils.lessThan(amount1, amount2);  // true
 * }</pre>
 *
 * <h3>金额转换</h3>
 * <pre>{@code
 * // 元转分
 * long cents = MoneyUtils.yuanToCent(new BigDecimal("100.50"));  // 10050
 *
 * // 分转元
 * BigDecimal yuan = MoneyUtils.centToYuan(10050);  // 100.50
 * }</pre>
 *
 * <h2>为什么使用 BigDecimal</h2>
 * <p>
 * 浮点数（float、double）在计算时存在精度问题：
 * </p>
 * <pre>{@code
 * // 错误示例：使用 double
 * double result = 0.1 + 0.2;  // 0.30000000000000004（精度丢失）
 *
 * // 正确示例：使用 BigDecimal
 * BigDecimal result = MoneyUtils.add("0.1", "0.2");  // 0.3（精确）
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>金额计算必须使用 {@link java.math.BigDecimal}，禁止使用 float 或 double</li>
 *   <li>创建 BigDecimal 时使用字符串构造方法，避免精度丢失</li>
 *   <li>除法运算必须指定精度和舍入模式，避免无限小数</li>
 *   <li>默认舍入模式为 {@link java.math.RoundingMode#HALF_UP}（四舍五入）</li>
 *   <li>所有方法都是空安全的，null 值不会抛出异常</li>
 *   <li>所有方法都是静态的、无状态的、线程安全的</li>
 *   <li>仅依赖 JDK，不依赖任何第三方框架</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core.money;
