/**
 * Spring 工具类包
 * <p>
 * 提供各种 Spring 相关的工具类，包括敏感信息脱敏等功能。
 * </p>
 *
 * <h2>核心工具类</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.util.DesensitizeUtils} - 敏感信息脱敏工具类</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 手机号脱敏
 * String phone = DesensitizeUtils.mobilePhone("13812345678");
 * // 输出：138****5678
 *
 * // 身份证号脱敏
 * String idCard = DesensitizeUtils.idCard("320123199001011234");
 * // 输出：320***********1234
 *
 * // 邮箱脱敏
 * String email = DesensitizeUtils.email("abc@example.com");
 * // 输出：a***@example.com
 *
 * // 自定义规则脱敏
 * String custom = DesensitizeUtils.desensitizeCustom("1234567890", "3,4");
 * // 输出：123***7890
 * }</pre>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring.util;
