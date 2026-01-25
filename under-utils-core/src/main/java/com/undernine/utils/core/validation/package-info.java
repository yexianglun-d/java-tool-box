/**
 * 参数校验工具包
 * <p>
 * 提供常用的参数校验工具类，如手机号、邮箱、身份证号、URL 等格式校验。
 * </p>
 *
 * <h2>主要工具类</h2>
 * <ul>
 *   <li>{@link com.undernine.utils.core.validation.ValidationUtils} - 参数校验工具类</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 手机号校验
 * boolean isPhone = ValidationUtils.isPhone("13812345678");  // true
 *
 * // 邮箱校验
 * boolean isEmail = ValidationUtils.isEmail("user@example.com");  // true
 *
 * // 身份证号校验
 * boolean isIdCard = ValidationUtils.isIdCard("110101199003074792");  // true
 *
 * // URL 校验
 * boolean isUrl = ValidationUtils.isUrl("https://www.example.com");  // true
 *
 * // 数字校验
 * boolean isNumber = ValidationUtils.isNumber("123.45");  // true
 * }</pre>
 *
 * <h2>注意事项</h2>
 * <ul>
 *   <li>所有方法都是空安全的，null 或空字符串返回 false</li>
 *   <li>所有方法都是静态的、无状态的、线程安全的</li>
 *   <li>仅依赖 JDK，不依赖任何第三方框架</li>
 *   <li>身份证号校验包含校验位验证</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.core.validation;
