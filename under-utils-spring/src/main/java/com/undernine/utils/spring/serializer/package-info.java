/**
 * Jackson 序列化器包
 * <p>
 * 提供自定义的 Jackson 序列化器，用于敏感信息脱敏等功能。
 * </p>
 *
 * <h2>核心序列化器</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.serializer.SensitiveJsonSerializer} - 敏感信息 JSON 序列化器</li>
 * </ul>
 *
 * <h2>功能说明</h2>
 * <p>
 * SensitiveJsonSerializer 配合 @Sensitive 注解使用，在 JSON 序列化时自动对敏感字段进行脱敏处理。
 * 支持多种预定义的脱敏规则和自定义脱敏规则。
 * </p>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * public class User {
 *     private String name;
 *
 *     @Sensitive(type = SensitiveType.MOBILE_PHONE)
 *     private String phone;
 *
 *     @Sensitive(type = SensitiveType.ID_CARD)
 *     private String idCard;
 *
 *     @Sensitive(type = SensitiveType.EMAIL)
 *     private String email;
 * }
 *
 * // 序列化输出
 * {
 *   "name": "张三",
 *   "phone": "138****5678",
 *   "idCard": "320***********1234",
 *   "email": "a***@example.com"
 * }
 * }</pre>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring.serializer;
