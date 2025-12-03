package com.undernine.utils.spring.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.undernine.utils.spring.annotation.Sensitive;
import com.undernine.utils.spring.enums.SensitiveType;
import com.undernine.utils.spring.util.DesensitizeUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * 敏感信息JSON序列化器
 * <p>
 * 在JSON序列化时自动对标注了 @Sensitive 注解的字段进行脱敏处理
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class SensitiveJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveType type;
    private String customRule;

    public SensitiveJsonSerializer() {
    }

    public SensitiveJsonSerializer(SensitiveType type, String customRule) {
        this.type = type;
        this.customRule = customRule;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        String desensitized;
        if (type == SensitiveType.CUSTOM && customRule != null && !customRule.isEmpty()) {
            desensitized = DesensitizeUtils.desensitizeCustom(value, customRule);
        } else {
            desensitized = DesensitizeUtils.desensitize(value, type);
        }
        
        gen.writeString(desensitized);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) 
            throws JsonMappingException {
        if (property != null) {
            Sensitive sensitive = property.getAnnotation(Sensitive.class);
            if (sensitive == null) {
                sensitive = property.getContextAnnotation(Sensitive.class);
            }
            if (sensitive != null) {
                return new SensitiveJsonSerializer(sensitive.type(), sensitive.customRule());
            }
        }
        return prov.findNullValueSerializer(null);
    }
}
