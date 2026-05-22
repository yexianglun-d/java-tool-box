package com.undernine.utils.redis.cache;

/**
 * 缓存值编解码接口。
 * <p>
 * 模板只依赖该抽象，不直接绑定 JSON 或其他序列化细节。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CacheValueCodec {

    /**
     * 将对象编码为可写入 Redis 的字符串。
     *
     * @param value 缓存对象，非 null
     * @return 编码后的字符串，不允许为 null
     */
    String encode(Object value);

    /**
     * 将 Redis 中的字符串解码为目标类型。
     *
     * @param payload   Redis 中读取到的载荷
     * @param valueType 目标类型
     * @param <T>       目标泛型
     * @return 解码后的对象
     */
    <T> T decode(String payload, Class<T> valueType);
}
