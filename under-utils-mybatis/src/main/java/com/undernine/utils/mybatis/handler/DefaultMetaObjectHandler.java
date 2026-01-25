package com.undernine.utils.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 元数据自动填充处理器
 * <p>
 * 自动填充创建时间、修改时间、创建人、修改人等字段
 * </p>
 * <p>
 * 使用说明：
 * 1. 在 Spring Boot 项目中，将此类注册为 Bean 即可生效
 * 2. 如需自定义用户 ID 获取逻辑，可继承此类并重写 getUserId() 方法
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class DefaultMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATE_TIME = "createTime";
    private static final String UPDATE_TIME = "updateTime";
    private static final String CREATE_BY = "createBy";
    private static final String UPDATE_BY = "updateBy";

    /**
     * 插入时自动填充
     *
     * @param metaObject 元数据对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");

        // 填充创建时间
        this.fillStrategy(metaObject, CREATE_TIME, LocalDateTime.now());

        // 填充修改时间
        this.fillStrategy(metaObject, UPDATE_TIME, LocalDateTime.now());

        // 填充创建人（需要从上下文获取当前用户 ID）
        Long userId = getUserId();
        if (userId != null) {
            this.fillStrategy(metaObject, CREATE_BY, userId);
            this.fillStrategy(metaObject, UPDATE_BY, userId);
        }

        // 填充逻辑删除标记（默认未删除）
        this.fillStrategy(metaObject, "deleted", 0);
    }

    /**
     * 更新时自动填充
     *
     * @param metaObject 元数据对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        // 填充修改时间（强制更新）
        this.setFieldValByName(UPDATE_TIME, LocalDateTime.now(), metaObject);

        // 填充修改人（强制更新）
        Long userId = getUserId();
        if (userId != null) {
            this.setFieldValByName(UPDATE_BY, userId, metaObject);
        }
    }

    /**
     * 获取当前用户 ID
     * <p>
     * 默认实现返回 null，子类可以重写此方法，从 ThreadLocal、Spring Security 等获取当前用户 ID
     * </p>
     *
     * @return 当前用户 ID，如果无法获取则返回 null
     */
    protected Long getUserId() {
        // 默认返回 null，子类可以重写此方法
        // 例如：return UserContextHolder.getUserId();
        return null;
    }
}
