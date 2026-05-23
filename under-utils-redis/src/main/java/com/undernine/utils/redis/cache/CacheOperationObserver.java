package com.undernine.utils.redis.cache;

/**
 * Redis 缓存模板观测 SPI。
 * <p>
 * 应用可以在这里接入 Micrometer、OpenTelemetry 或内部日志。模板会捕获 observer 的运行时异常，
 * 避免观测失败影响缓存主流程。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.1
 * @since 1.0.1
 */
public interface CacheOperationObserver {

    /**
     * 缓存命中。
     *
     * @param event 缓存事件
     */
    default void onHit(CacheOperationEvent event) {
    }

    /**
     * 缓存未命中。
     *
     * @param event 缓存事件
     */
    default void onMiss(CacheOperationEvent event) {
    }

    /**
     * 源数据加载成功。
     *
     * @param event 缓存事件
     */
    default void onLoadSuccess(CacheOperationEvent event) {
    }

    /**
     * 源数据加载失败。
     *
     * @param event 缓存事件
     */
    default void onLoadFailure(CacheOperationEvent event) {
    }

    /**
     * 缓存写入。
     *
     * @param event 缓存事件
     */
    default void onWrite(CacheOperationEvent event) {
    }

    /**
     * 重建锁获取成功。
     *
     * @param event 缓存事件
     */
    default void onLockAcquired(CacheOperationEvent event) {
    }

    /**
     * 重建锁获取失败。
     *
     * @param event 缓存事件
     */
    default void onLockRejected(CacheOperationEvent event) {
    }

    /**
     * 逻辑过期缓存提交后台刷新。
     *
     * @param event 缓存事件
     */
    default void onRefreshSubmitted(CacheOperationEvent event) {
    }

    /**
     * 逻辑过期缓存后台刷新成功。
     *
     * @param event 缓存事件
     */
    default void onRefreshSuccess(CacheOperationEvent event) {
    }

    /**
     * 逻辑过期缓存后台刷新失败。
     *
     * @param event 缓存事件
     */
    default void onRefreshFailure(CacheOperationEvent event) {
    }

    /**
     * 返回空 observer。
     *
     * @return no-op observer
     */
    static CacheOperationObserver noop() {
        return NoopCacheOperationObserver.INSTANCE;
    }

    final class NoopCacheOperationObserver implements CacheOperationObserver {

        private static final NoopCacheOperationObserver INSTANCE = new NoopCacheOperationObserver();

        private NoopCacheOperationObserver() {
        }
    }
}
