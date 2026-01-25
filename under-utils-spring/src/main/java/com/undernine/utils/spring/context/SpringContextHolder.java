package com.undernine.utils.spring.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文持有者
 * <p>
 * 提供在非 Spring 管理的类中获取 Spring Bean 的能力。
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 获取 Bean
 * UserService userService = SpringContextHolder.getBean(UserService.class);
 *
 * // 获取配置属性
 * String appName = SpringContextHolder.getProperty("spring.application.name");
 *
 * // 获取激活的 Profile
 * String[] profiles = SpringContextHolder.getActiveProfiles();
 * }</pre>
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class SpringContextHolder implements ApplicationContextAware {

    /**
     * Spring 应用上下文
     */
    private static ApplicationContext applicationContext;

    /**
     * 设置 Spring 应用上下文
     *
     * @param applicationContext Spring 应用上下文
     * @throws BeansException Bean 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
        log.info("SpringContextHolder 初始化完成");
    }

    /**
     * 获取 Spring 应用上下文
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        assertApplicationContext();
        return applicationContext;
    }

    /**
     * 根据名称获取 Bean
     *
     * @param name Bean 名称
     * @return Bean 实例
     */
    public static Object getBean(String name) {
        assertApplicationContext();
        return applicationContext.getBean(name);
    }

    /**
     * 根据类型获取 Bean
     *
     * @param clazz Bean 类型
     * @param <T>   Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> clazz) {
        assertApplicationContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称和类型获取 Bean
     *
     * @param name  Bean 名称
     * @param clazz Bean 类型
     * @param <T>   Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        assertApplicationContext();
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 判断是否包含指定名称的 Bean
     *
     * @param name Bean 名称
     * @return 包含返回 true，否则返回 false
     */
    public static boolean containsBean(String name) {
        assertApplicationContext();
        return applicationContext.containsBean(name);
    }

    /**
     * 判断指定名称的 Bean 是否为单例
     *
     * @param name Bean 名称
     * @return 单例返回 true，否则返回 false
     */
    public static boolean isSingleton(String name) {
        assertApplicationContext();
        return applicationContext.isSingleton(name);
    }

    /**
     * 获取指定名称的 Bean 类型
     *
     * @param name Bean 名称
     * @return Bean 类型
     */
    public static Class<?> getType(String name) {
        assertApplicationContext();
        return applicationContext.getType(name);
    }

    /**
     * 获取激活的 Profile
     *
     * @return Profile 数组
     */
    public static String[] getActiveProfiles() {
        assertApplicationContext();
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    /**
     * 获取配置属性
     *
     * @param key 属性键
     * @return 属性值
     */
    public static String getProperty(String key) {
        assertApplicationContext();
        return applicationContext.getEnvironment().getProperty(key);
    }

    /**
     * 获取配置属性（带默认值）
     *
     * @param key          属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static String getProperty(String key, String defaultValue) {
        assertApplicationContext();
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    /**
     * 获取配置属性（指定类型）
     *
     * @param key        属性键
     * @param targetType 目标类型
     * @param <T>        类型
     * @return 属性值
     */
    public static <T> T getProperty(String key, Class<T> targetType) {
        assertApplicationContext();
        return applicationContext.getEnvironment().getProperty(key, targetType);
    }

    /**
     * 获取配置属性（指定类型，带默认值）
     *
     * @param key          属性键
     * @param targetType   目标类型
     * @param defaultValue 默认值
     * @param <T>          类型
     * @return 属性值
     */
    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        assertApplicationContext();
        return applicationContext.getEnvironment().getProperty(key, targetType, defaultValue);
    }

    /**
     * 断言 ApplicationContext 已初始化
     */
    private static void assertApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext 未初始化，请确保 SpringContextHolder 已被 Spring 容器管理");
        }
    }
}
