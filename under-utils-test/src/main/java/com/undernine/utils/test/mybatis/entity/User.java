package com.undernine.utils.test.mybatis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.undernine.utils.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体（测试用）
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 状态（0:禁用, 1:启用）
     */
    private Integer status;
}
