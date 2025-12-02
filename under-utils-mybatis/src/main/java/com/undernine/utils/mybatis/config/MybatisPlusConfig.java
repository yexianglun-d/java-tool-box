package com.undernine.utils.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

/**
 * MyBatis-Plus 配置类
 * <p>
 * 配置分页插件、乐观锁插件、防止全表更新删除插件等
 * </p>
 * <p>
 * 使用说明：
 * 在 Spring Boot 项目中，创建配置类继承此类，或直接使用 mybatisPlusInterceptor() 方法注册 Bean
 * </p>
 * <pre>{@code
 * @Configuration
 * public class MyBatisConfig {
 *     @Bean
 *     public MybatisPlusInterceptor mybatisPlusInterceptor() {
 *         return MybatisPlusConfig.mybatisPlusInterceptor(DbType.MYSQL);
 *     }
 * }
 * }</pre>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MybatisPlusConfig {

    private MybatisPlusConfig() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 创建 MyBatis-Plus 拦截器（使用默认 MySQL 数据库）
     *
     * @return MybatisPlusInterceptor
     */
    public static MybatisPlusInterceptor mybatisPlusInterceptor() {
        return mybatisPlusInterceptor(DbType.MYSQL);
    }

    /**
     * 创建 MyBatis-Plus 拦截器（指定数据库类型）
     * <p>
     * 包含的插件：
     * 1. 分页插件（PaginationInnerInterceptor）
     * 2. 乐观锁插件（OptimisticLockerInnerInterceptor）
     * 3. 防止全表更新删除插件（BlockAttackInnerInterceptor）
     * </p>
     *
     * @param dbType 数据库类型
     * @return MybatisPlusInterceptor
     */
    public static MybatisPlusInterceptor mybatisPlusInterceptor(DbType dbType) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(dbType);
        // 设置请求的页面大于最大页后操作，true 调回到首页，false 继续请求（默认 false）
        paginationInterceptor.setOverflow(false);
        // 设置单页分页条数限制，默认 500 条，-1 不受限制
        paginationInterceptor.setMaxLimit(1000L);
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 乐观锁插件（需要在实体字段上添加 @Version 注解）
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 防止全表更新删除插件（针对 update 和 delete 语句）
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }

    /**
     * 创建分页插件（独立使用）
     *
     * @param dbType 数据库类型
     * @return PaginationInnerInterceptor
     */
    public static PaginationInnerInterceptor paginationInterceptor(DbType dbType) {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(dbType);
        paginationInterceptor.setOverflow(false);
        paginationInterceptor.setMaxLimit(1000L);
        return paginationInterceptor;
    }
}
