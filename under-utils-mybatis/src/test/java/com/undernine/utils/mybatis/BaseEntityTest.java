package com.undernine.utils.mybatis;

import com.baomidou.mybatisplus.annotation.TableName;
import com.undernine.utils.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BaseEntity 测试
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@DisplayName("BaseEntity 测试")
class BaseEntityTest {

    /**
     * 测试用实体类
     */
    @Data
    @EqualsAndHashCode(callSuper = true)
    @TableName("test_user")
    static class TestUser extends BaseEntity {
        private String username;
        private String email;
    }

    @Test
    @DisplayName("测试 BaseEntity 字段初始化")
    void testBaseEntityFields() {
        TestUser user = new TestUser();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // 验证业务字段
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");

        // 验证基础字段初始值为 null
        assertThat(user.getId()).isNull();
        assertThat(user.getCreateTime()).isNull();
        assertThat(user.getUpdateTime()).isNull();
        assertThat(user.getCreateBy()).isNull();
        assertThat(user.getUpdateBy()).isNull();
        assertThat(user.getDeleted()).isNull();
    }

    @Test
    @DisplayName("测试设置 BaseEntity 字段")
    void testSetBaseEntityFields() {
        TestUser user = new TestUser();
        LocalDateTime now = LocalDateTime.now();

        user.setId(123456789L);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setCreateBy(1L);
        user.setUpdateBy(1L);
        user.setDeleted(0);

        assertThat(user.getId()).isEqualTo(123456789L);
        assertThat(user.getCreateTime()).isEqualTo(now);
        assertThat(user.getUpdateTime()).isEqualTo(now);
        assertThat(user.getCreateBy()).isEqualTo(1L);
        assertThat(user.getUpdateBy()).isEqualTo(1L);
        assertThat(user.getDeleted()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试 BaseEntity 序列化")
    void testSerializable() {
        TestUser user = new TestUser();
        user.setUsername("testuser");

        // 验证实现了 Serializable 接口
        assertThat(user).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("测试逻辑删除标记")
    void testLogicDelete() {
        TestUser user = new TestUser();

        // 未删除
        user.setDeleted(0);
        assertThat(user.getDeleted()).isEqualTo(0);

        // 已删除
        user.setDeleted(1);
        assertThat(user.getDeleted()).isEqualTo(1);
    }

    @Test
    @DisplayName("测试实体类继承关系")
    void testInheritance() {
        TestUser user = new TestUser();

        // 验证继承关系
        assertThat(user).isInstanceOf(BaseEntity.class);
        assertThat(user).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("测试 Lombok 生成的方法")
    void testLombokMethods() {
        TestUser user1 = new TestUser();
        user1.setId(1L);
        user1.setUsername("user1");

        TestUser user2 = new TestUser();
        user2.setId(1L);
        user2.setUsername("user1");

        // 测试 equals
        assertThat(user1).isEqualTo(user2);

        // 测试 hashCode
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());

        // 测试 toString
        assertThat(user1.toString()).contains("user1");
    }
}
