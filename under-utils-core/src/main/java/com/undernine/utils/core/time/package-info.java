/**
 * 日期时间工具包
 * <p>
 * 提供基于 Java 8+ 时间 API 的日期时间处理工具类，包括格式化、解析、计算等功能。
 * </p>
 *
 * <h2>主要工具类</h2>
 * <ul>
 *   <li>{@link com.undernine.utils.core.time.LocalDateTimeUtils} - LocalDateTime 工具类</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 获取当前时间
 * LocalDateTime now = LocalDateTimeUtils.now();
 *
 * // 格式化日期时间
 * String formatted = LocalDateTimeUtils.format(now, "yyyy-MM-dd HH:mm:ss");
 *
 * // 解析日期时间字符串
 * LocalDateTime parsed = LocalDateTimeUtils.parse("2024-01-01 12:00:00", "yyyy-MM-dd HH:mm:ss");
 *
 * // 日期时间计算
 * LocalDateTime tomorrow = LocalDateTimeUtils.plusDays(now, 1);
 * LocalDateTime yesterday = LocalDateTimeUtils.minusDays(now, 1);
 *
 * // 判断日期时间关系
 * boolean isBefore = LocalDateTimeUtils.isBefore(yesterday, now);  // true
 * boolean isAfter = LocalDateTimeUtils.isAfter(tomorrow, now);  // true
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>使用 Java 8+ 的 {@link java.time.LocalDateTime} API</li>
 *   <li>所有方法都是空安全的，null 值不会抛出异常</li>
 *   <li>所有方法都是静态的、无状态的、线程安全的</li>
 *   <li>仅依赖 JDK，不依赖任何第三方框架</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core.time;
