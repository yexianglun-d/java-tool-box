/**
 * 字符串工具包
 * <p>
 * 提供常用的字符串处理工具类，包括字符串判空、格式化、转换等功能。
 * </p>
 *
 * <h2>主要工具类</h2>
 * <ul>
 *   <li>{@link com.undernine.utils.core.string.StringUtils} - 字符串工具类</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 判断字符串是否为空
 * boolean isEmpty = StringUtils.isEmpty("  ");  // true
 *
 * // 判断字符串是否不为空
 * boolean isNotEmpty = StringUtils.isNotEmpty("hello");  // true
 *
 * // 判断字符串是否为空白
 * boolean isBlank = StringUtils.isBlank("  ");  // true
 *
 * // 判断字符串是否不为空白
 * boolean isNotBlank = StringUtils.isNotBlank("hello");  // true
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>所有方法都是空安全的，null 值不会抛出异常</li>
 *   <li>所有方法都是静态的、无状态的、线程安全的</li>
 *   <li>仅依赖 JDK，不依赖任何第三方框架</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core.string;
