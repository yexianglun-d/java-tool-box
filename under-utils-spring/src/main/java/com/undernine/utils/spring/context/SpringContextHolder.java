package com.undernine.utils.spring.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具类
 * <p>
 * 提供在非 Spring 管理的类中获取 Bean 的能力
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 获取 Bean
 * UserService userService = SpringContextHolder.getBean(UserService.class);
 *
 * // 获取指定名称的 Bean
 * UserService userService = SpringContextHolder.getBean("userService", UserService.class);
 *
 * // 获取应用名称
 * String appName = SpringContextHolder.getApplicationName();
 *
 * // 获取激活的配置文件
 * String[] profiles = SpringContextHolder.getActiveProfiles();
 * }</pre>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 设置应用上下文
     *
     * @param context Spring 应用上下文
     * @throws BeansException Bean 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextHolder.applicationContext = context;
        log.info("SpringContextHolder 初始化成功");
    }

    /**
     * 获取应用上下文
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        assertContextInjected();
        return applicationContext;
    }

    /**
     * 根据类型获取 Bean
     *
     * @param clazz Bean 类型
     * @param <T>   泛型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> clazz) {
        assertContextInjected();
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称和类型获取 Bean
     *
     * @param name  Bean 名称
     * @param clazz Bean 类型
     * @param <T>   泛型
     * @return Bean 实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        assertContextInjected();
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 根据名称获取 Bean
     *
     * @param name Bean 名称
     * @return Bean 实例
     */
    public static Object getBean(String name) {
        assertContextInjected();
        return applicationContext.getBean(name);
    }

    /**
     * 判断是否包含指定名称的 Bean
     *
     * @param name Bean 名称
     * @return true-存在 false-不存在
     */
    public static boolean containsBean(String name) {
        assertContextInjected();
        return applicationContext.containsBean(name);
    }

    /**
     * 获取应用名称
     *
     * @return 应用名称
     */
    public static String getApplicationName() {
        assertContextInjected();
        return applicationContext.getEnvironment().getProperty("spring.application.name");
    }

    /**
     * 获取激活的配置文件
     *
     * @return 配置文件数组
     */
    public static String[] getActiveProfiles() {
        assertContextInjected();
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    /**
     * 获取指定配置属性
     *
     * @param key 属性键
     * @return 属性值
     */
    public static String getProperty(String key) {
        assertContextInjected();
        return applicationContext.getEnvironment().getProperty(key);
    }

    /**
     * 获取指定配置属性（带默认值）
     *
     * @param key          属性键
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static String getProperty(String key, String defaultValue) {
        assertContextInjected();
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    /**
     * 判断指定 Bean 是否为单例
     *
     * @param name Bean 名称
     * @return true-单例 false-非单例
     */
    public static boolean isSingleton(String name) {
        assertContextInjected();
        return applicationContext.isSingleton(name);
    }

    /**
     * 判断指定 Bean 是否为原型
     *
     * @param name Bean 名称
     * @return true-原型 false-非原型
     */
    public static boolean isPrototype(String name) {
        assertContextInjected();
        return applicationContext.isPrototype(name);
    }

    /**
     * 获取指定 Bean 的类型
     *
     * @param name Bean 名称
     * @return Bean 类型
     */
    public static Class<?> getType(String name) {
        assertContextInjected();
        return applicationContext.getType(name);
    }

    /**
     * 获取指定 Bean 的所有别名
     *
     * @param name Bean 名称
     * @return 别名数组
     */
    public static String[] getAliases(String name) {
        assertContextInjected();
        return applicationContext.getAliases(name);
    }

    /**
     * 根据类型获取所有 Bean 名称
     *
     * @param type Bean 类型
     * @return Bean 名称数组
     */
    public static String[] getBeanNamesForType(Class<?> type) {
        assertContextInjected();
        return applicationContext.getBeanNamesForType(type);
    }

    /**
     * 获取所有 Bean 定义的名称
     *
     * @return Bean 名称数组
     */
    public static String[] getBeanDefinitionNames() {
        assertContextInjected();
        return applicationContext.getBeanDefinitionNames();
    }

    /**
     * 根据类型获取所有 Bean 实例（Map形式）
     *
     * @param type Bean 类型
     * @param <T>  泛型
     * @return Bean 名称和实例的 Map
     */
    public static <T> java.util.Map<String, T> getBeansOfType(Class<T> type) {
        assertContextInjected();
        return applicationContext.getBeansOfType(type);
    }

    /**
     * 发布事件
     *
     * @param event 事件对象
     */
    public static void publishEvent(Object event) {
        assertContextInjected();
        applicationContext.publishEvent(event);
        log.debug("事件已发布: {}", event.getClass().getSimpleName());
    }

    /**
     * 断言上下文已注入
     */
    private static void assertContextInjected() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext 未注入，请确保 SpringContextHolder 已被 Spring 管理");
        }
    }

    /**
     * 清除上下文（用于测试）
     */
    public static void clearContext() {
        applicationContext = null;
        log.info("SpringContextHolder 上下文已清除");
    }
}
