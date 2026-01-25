package com.undernine.utils.spring.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法执行时间统计注解
 * <p>
 * 用于统计方法执行时间，支持慢方法监控。
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Service
 * public class UserService {
 *
 *     // 记录方法执行时间
 *     @TimeLog
 *     public User getById(Long id) {
 *         return userMapper.selectById(id);
 *     }
 *
 *     // 自定义描述和慢方法阈值
 *     @TimeLog(value = "批量查询用户", slowThreshold = 500)
 *     public List<User> listUsers(UserQuery query) {
 *         return userMapper.selectList(query);
 *     }
 * }
 * }</pre>
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeLog {

    /**
     * 操作描述
     *
     * @return 操作描述
     */
    String value() default "";

    /**
     * 慢方法阈值（单位：毫秒）
     * <p>
     * 当方法执行时间超过此阈值时，会以 WARN 级别记录日志。
     * </p>
     *
     * @return 慢方法阈值，默认 1000ms
     */
    long slowThreshold() default 1000;
}
