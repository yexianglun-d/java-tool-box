package com.undernine.utils.redis.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undernine.utils.core.json.JsonException;
import com.undernine.utils.core.json.JsonUtils;

import java.util.Objects;

/**
 * 基于 Jackson 的缓存值编解码实现。
 * <p>
 * 默认复用 {@link JsonUtils} 的 ObjectMapper 配置，也允许业务侧传入定制 ObjectMapper。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class JacksonCacheValueCodec implements CacheValueCodec {

    private final ObjectMapper objectMapper;

    public JacksonCacheValueCodec() {
        this(JsonUtils.getObjectMapper());
    }

    public JacksonCacheValueCodec(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    @Override
    public String encode(Object value) {
        Objects.requireNonNull(value, "value must not be null");
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new JsonException("Failed to encode cache value: " + value.getClass().getName(), e);
        }
    }

    @Override
    public <T> T decode(String payload, Class<T> valueType) {
        Objects.requireNonNull(payload, "payload must not be null");
        Objects.requireNonNull(valueType, "valueType must not be null");
        try {
            return objectMapper.readValue(payload, valueType);
        } catch (JsonProcessingException e) {
            throw new JsonException("Failed to decode cache value to " + valueType.getName(), e);
        }
    }
}
