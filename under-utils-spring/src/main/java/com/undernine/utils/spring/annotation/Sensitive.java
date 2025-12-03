package com.undernine.utils.spring.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.undernine.utils.spring.enums.SensitiveType;
import com.undernine.utils.spring.serializer.SensitiveJsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感信息脱敏注解
 * <p>
 * 使用此注解标记的字段，在JSON序列化时会自动脱敏处理
 * </p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * public class User {
 *     @Sensitive(type = SensitiveType.MOBILE_PHONE)
 *     private String phone;  // 输出：138****5678
 *     
 *     @Sensitive(type = SensitiveType.ID_CARD)
 *     private String idCard; // 输出：320***********1234
 *     
 *     @Sensitive(type = SensitiveType.EMAIL)
 *     private String email;  // 输出：a***@example.com
 * }
 * }</pre>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 * @see SensitiveType
 * @see SensitiveJsonSerializer
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveJsonSerializer.class)
public @interface Sensitive {
    
    /**
     * 脱敏类型
     *
     * @return 脱敏类型枚举
     */
    SensitiveType type();
    
    /**
     * 自定义脱敏规则（当type为CUSTOM时有效）
     * <p>
     * 格式：保留前n位,保留后m位
     * 示例："3,4" 表示保留前3位和后4位，中间用*替换
     * </p>
     *
     * @return 自定义规则字符串
     */
    String customRule() default "";
}
