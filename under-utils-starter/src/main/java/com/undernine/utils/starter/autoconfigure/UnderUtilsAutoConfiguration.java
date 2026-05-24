package com.undernine.utils.starter.autoconfigure;

/**
 * 兼容旧 starter 坐标的自动配置入口占位类。
 * <p>
 * 从 1.0.2 起，实际自动配置由 {@code under-utils-spring-starter} 和
 * {@code under-utils-redis-starter} 提供。保留本类型是为了避免使用方直接引用旧类名时编译失败。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.2
 * @since 1.0.0
 * @deprecated 请按需引入 {@code under-utils-spring-starter} 或 {@code under-utils-redis-starter}。
 */
@Deprecated(since = "1.0.2")
public final class UnderUtilsAutoConfiguration {

    private UnderUtilsAutoConfiguration() {
    }
}
