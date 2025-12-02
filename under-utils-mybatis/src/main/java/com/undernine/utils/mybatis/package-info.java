/**
 * MyBatis / MyBatis-Plus 增强模块
 * <p>
 * 提供以下功能：
 * </p>
 * <ul>
 *     <li>基础实体类（BaseEntity）- 包含公共字段：ID、创建时间、修改时间、创建人、修改人、逻辑删除</li>
 *     <li>元数据自动填充（DefaultMetaObjectHandler）- 自动填充创建时间、修改时间等字段</li>
 *     <li>分页配置（MybatisPlusConfig）- 配置分页插件、乐观锁、防止全表更新删除</li>
 *     <li>分页查询封装（PageQuery、PageResult）- 统一的分页参数和返回结果</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 实体类继承 BaseEntity
 * @TableName("sys_user")
 * public class User extends BaseEntity {
 *     private String username;
 *     private String email;
 * }
 *
 * // 2. 配置类中注册 MyBatis-Plus 拦截器和元数据处理器
 * @Configuration
 * public class MyBatisConfig {
 *     @Bean
 *     public MybatisPlusInterceptor mybatisPlusInterceptor() {
 *         return MybatisPlusConfig.mybatisPlusInterceptor(DbType.MYSQL);
 *     }
 *
 *     @Bean
 *     public MetaObjectHandler metaObjectHandler() {
 *         return new DefaultMetaObjectHandler();
 *     }
 * }
 *
 * // 3. 使用分页查询
 * PageQuery pageQuery = PageQuery.of(1L, 10L).orderByDesc("create_time");
 * Page<User> page = pageQuery.buildPage();
 * IPage<User> result = userMapper.selectPage(page, null);
 * PageResult<User> pageResult = PageResult.of(result);
 * }</pre>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.mybatis;
