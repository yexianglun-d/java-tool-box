/**
 * MyBatis-Plus 元数据处理器
 * <p>
 * 提供自动填充功能，在插入和更新时自动填充创建时间、修改时间、操作人等字段。
 * </p>
 *
 * <h3>核心类：</h3>
 * <ul>
 *     <li>{@link com.undernine.utils.mybatis.handler.DefaultMetaObjectHandler} - 默认元数据自动填充处理器</li>
 *     <li>{@link com.undernine.utils.mybatis.handler.AuditorProvider} - 当前审计用户提供者</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 注册为 Spring Bean
 * @Configuration
 * public class MyBatisConfig {
 *     @Bean
 *     public MetaObjectHandler metaObjectHandler() {
 *         return new DefaultMetaObjectHandler(UserContext::getCurrentUserId);
 *     }
 * }
 *
 * // 2. 实体类字段添加注解
 * @TableField(fill = FieldFill.INSERT)
 * private LocalDateTime createTime;
 *
 * @TableField(fill = FieldFill.INSERT_UPDATE)
 * private LocalDateTime updateTime;
 * }</pre>
 *
 * <h3>自动填充规则：</h3>
 * <ul>
 *     <li>插入时填充：createTime、updateTime、createBy、updateBy、deleted</li>
 *     <li>更新时填充：updateTime、updateBy</li>
 *     <li>如果字段已有值，不会覆盖</li>
 * </ul>
 *
 * <h3>注意事项：</h3>
 * <ul>
 *     <li>需要重写 {@code getUserId()} 方法来提供当前用户 ID</li>
 *     <li>如果不需要填充操作人，可以让 {@code getUserId()} 返回 null</li>
 *     <li>确保实体类字段使用了正确的 {@code @TableField} 注解</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.mybatis.handler;
