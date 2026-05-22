/**
 * Redis 缓存治理组件。
 * <p>
 * 该包提供 cache-aside、逻辑过期、空值缓存、TTL 抖动和重建锁等缓存访问模式封装，
 * 面向热点数据读取、缓存穿透防护和缓存击穿防护等高重复工程场景。
 * </p>
 * <p>
 * 包内模板依赖 Redisson 执行 Redis 读写与分布式锁；业务侧只需要提供 key、值类型和加载函数。
 * {@link com.undernine.utils.redis.cache.CacheOptions#ttl()} 表示正常业务值 TTL，
 * {@link com.undernine.utils.redis.cache.CacheOptions#nullTtl()} 表示空值占位 TTL。
 * 逻辑过期缓存中，{@link com.undernine.utils.redis.cache.LogicalExpireCacheOptions#logicalTtl()}
 * 表示逻辑新鲜期，{@link com.undernine.utils.redis.cache.LogicalExpireCacheOptions#physicalTtl()}
 * 表示 Redis key 的物理存活时间。
 * </p>
 */
package com.undernine.utils.redis.cache;
