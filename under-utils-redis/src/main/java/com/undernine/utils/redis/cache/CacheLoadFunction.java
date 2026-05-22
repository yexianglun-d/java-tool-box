package com.undernine.utils.redis.cache;

/**
 * 缓存未命中时加载源数据的函数式接口。
 *
 * @param <T> 返回值类型
 * @param <E> 加载过程可能抛出的异常类型
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface CacheLoadFunction<T, E extends Throwable> {

    /**
     * 从源数据加载数据。
     *
     * @param key 未加前缀的业务 key
     * @return 加载结果，可以为 null
     * @throws E 加载异常，模板会原样透传
     */
    T load(String key) throws E;
}
