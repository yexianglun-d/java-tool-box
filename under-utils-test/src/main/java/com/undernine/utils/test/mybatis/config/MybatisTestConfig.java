package com.undernine.utils.test.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.undernine.utils.mybatis.config.MybatisPlusConfig;
import com.undernine.utils.mybatis.handler.DefaultMetaObjectHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 测试配置类
 *
 * @author undernine
 * @since 2024-12-03
 */
@Configuration
@MapperScan("com.undernine.utils.test.mybatis.mapper")
public class MybatisTestConfig {

    /**
     * 配置 MyBatis-Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return MybatisPlusConfig.mybatisPlusInterceptor(DbType.H2);
    }

    /**
     * 配置元数据自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new DefaultMetaObjectHandler() {
            @Override
            protected Long getUserId() {
                // 测试环境返回固定用户 ID
                return 1001L;
            }
        };
    }
}
