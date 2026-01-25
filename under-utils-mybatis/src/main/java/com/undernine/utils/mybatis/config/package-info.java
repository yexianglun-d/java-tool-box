/**
 * MyBatis-Plus 配置类
 * <p>
 * 提供 MyBatis-Plus 的核心配置，包括分页插件、乐观锁、防止全表更新删除等功能。
 * </p>
 *
 * <h3>核心类：</h3>
 * <ul>
 *     <li>{@link com.undernine.utils.mybatis.config.MybatisPlusConfig} - MyBatis-Plus 拦截器配置</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * @Configuration
 * public class MyBatisConfiguration {
 *
 *     @Bean
 *     public MybatisPlusInterceptor mybatisPlusInterceptor() {
 *         // 使用 MySQL 数据库
 *         return MybatisPlusConfig.mybatisPlusInterceptor(DbType.MYSQL);
 *     }
 *
 *     @Bean
 *     public MetaObjectHandler metaObjectHandler() {
 *         return new DefaultMetaObjectHandler() {
 *             @Override
 *             protected Long getUserId() {
 *                 return UserContext.getCurrentUserId();
 *             }
 *         };
 *     }
 * }
 * }</pre>
 *
 * <h3>功能特性：</h3>
 * <ul>
 *     <li>分页插件 - 支持多种数据库的物理分页</li>
 *     <li>乐观锁插件 - 支持 @Version 注解实现乐观锁</li>
 *     <li>防止全表更新删除插件 - 防止误操作导致的数据灾难</li>
 *     <li>支持多种数据库类型（MySQL、PostgreSQL、Oracle、SQL Server 等）</li>
 * </ul>
 *
 * <h3>支持的数据库：</h3>
 * <ul>
 *     <li>MySQL / MariaDB</li>
 *     <li>PostgreSQL</li>
 *     <li>Oracle</li>
 *     <li>SQL Server</li>
 *     <li>H2 / SQLite（用于测试）</li>
 *     <li>达梦 / 人大金仓等国产数据库</li>
 * </ul>
 *
 * <h3>注意事项：</h3>
 * <ul>
 *     <li>必须注册为 Spring Bean 才能生效</li>
 *     <li>选择正确的数据库类型（DbType）以获得最佳性能</li>
 *     <li>防止全表更新删除插件会拦截无 WHERE 条件的 UPDATE/DELETE 语句</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.mybatis.config;
