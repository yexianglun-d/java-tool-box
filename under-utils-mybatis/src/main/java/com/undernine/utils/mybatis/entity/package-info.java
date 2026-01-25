/**
 * MyBatis 实体类相关
 * <p>
 * 提供统一的基础实体类，包含常用的公共字段。
 * </p>
 *
 * <h3>核心类：</h3>
 * <ul>
 *     <li>{@link com.undernine.utils.mybatis.entity.BaseEntity} - 基础实体类，包含 ID、时间戳、操作人、逻辑删除等字段</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * @Data
 * @EqualsAndHashCode(callSuper = true)
 * @TableName("sys_user")
 * public class SysUser extends BaseEntity {
 *     private String username;
 *     private String email;
 *     private Integer status;
 * }
 *
 * // 继承后自动拥有以下字段：
 * // - id: 主键（雪花算法）
 * // - createTime: 创建时间（自动填充）
 * // - updateTime: 修改时间（自动填充）
 * // - createBy: 创建人 ID（自动填充）
 * // - updateBy: 修改人 ID（自动填充）
 * // - deleted: 逻辑删除标记（0-未删除，1-已删除）
 * }</pre>
 *
 * <h3>注意事项：</h3>
 * <ul>
 *     <li>数据库表必须包含对应的字段（使用下划线命名：create_time、update_time 等）</li>
 *     <li>deleted 字段必须为 INT 类型</li>
 *     <li>需要配合 {@link com.undernine.utils.mybatis.handler.DefaultMetaObjectHandler} 使用才能自动填充</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.mybatis.entity;
