package com.undernine.utils.spring.context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SpringContextHolder 单元测试
 *
 * @author Under-Utils Team
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringContextHolderTest.TestConfig.class)
class SpringContextHolderTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testGetApplicationContext() {
        ApplicationContext context = SpringContextHolder.getApplicationContext();
        
        assertThat(context).isNotNull();
        assertThat(context).isEqualTo(applicationContext);
    }

    @Test
    void testGetBean() {
        TestService service = SpringContextHolder.getBean(TestService.class);
        
        assertThat(service).isNotNull();
        assertThat(service.getMessage()).isEqualTo("Test Service");
    }

    @Test
    void testGetBeanByName() {
        Object service = SpringContextHolder.getBean("testService");
        
        assertThat(service).isNotNull();
        assertThat(service).isInstanceOf(TestService.class);
        assertThat(((TestService) service).getMessage()).isEqualTo("Test Service");
    }

    @Test
    void testGetBeanByNameAndClass() {
        TestService service = SpringContextHolder.getBean("testService", TestService.class);
        
        assertThat(service).isNotNull();
        assertThat(service.getMessage()).isEqualTo("Test Service");
    }

    @Test
    void testContainsBean() {
        boolean exists = SpringContextHolder.containsBean("testService");
        assertThat(exists).isTrue();
        
        boolean notExists = SpringContextHolder.containsBean("nonExistentBean");
        assertThat(notExists).isFalse();
    }

    @Test
    void testIsSingleton() {
        boolean isSingleton = SpringContextHolder.isSingleton("testService");
        assertThat(isSingleton).isTrue();
    }

    @Test
    void testIsPrototype() {
        boolean isPrototype = SpringContextHolder.isPrototype("testService");
        assertThat(isPrototype).isFalse();
    }

    @Test
    void testGetType() {
        Class<?> type = SpringContextHolder.getType("testService");
        assertThat(type).isEqualTo(TestService.class);
    }

    @Test
    void testGetAliases() {
        String[] aliases = SpringContextHolder.getAliases("testService");
        assertThat(aliases).isNotNull();
    }

    @Test
    void testGetBeanNamesForType() {
        String[] beanNames = SpringContextHolder.getBeanNamesForType(TestService.class);
        
        assertThat(beanNames).isNotNull();
        assertThat(beanNames).contains("testService");
    }

    @Test
    void testGetBeanDefinitionNames() {
        String[] beanNames = SpringContextHolder.getBeanDefinitionNames();
        
        assertThat(beanNames).isNotNull();
        assertThat(beanNames.length).isGreaterThan(0);
    }

    @Test
    void testGetBeansOfType() {
        java.util.Map<String, TestService> beans = SpringContextHolder.getBeansOfType(TestService.class);
        
        assertThat(beans).isNotNull();
        assertThat(beans).containsKey("testService");
        assertThat(beans.get("testService")).isNotNull();
    }

    @Test
    void testPublishEvent() {
        // 发布一个简单的事件
        String event = "Test Event";
        
        // 验证方法能正常执行，不抛异常
        SpringContextHolder.publishEvent(event);
    }

    @Test
    void testGetProperty() {
        // 测试获取配置属性（即使不存在也不会报错，返回null）
        String property = SpringContextHolder.getProperty("test.property");
        // 属性可能不存在，所以我们只验证方法能正常执行
        assertThat(property).isNull();
    }

    @Test
    void testGetPropertyWithDefault() {
        String defaultValue = "default";
        String property = SpringContextHolder.getProperty("test.property", defaultValue);
        assertThat(property).isEqualTo(defaultValue);
    }

    @Test
    void testGetApplicationName() {
        // 测试获取应用名称（可能返回null）
        String appName = SpringContextHolder.getApplicationName();
        // 在测试环境中可能没有设置应用名称
        // 只验证方法能正常执行，不抛异常
    }

    @Test
    void testGetActiveProfiles() {
        String[] profiles = SpringContextHolder.getActiveProfiles();
        
        assertThat(profiles).isNotNull();
    }

    /**
     * 测试配置类
     */
    @Configuration
    static class TestConfig {
        
        @Bean
        public SpringContextHolder springContextHolder() {
            return new SpringContextHolder();
        }
        
        @Bean
        public TestService testService() {
            return new TestService();
        }
    }

    /**
     * 测试服务类
     */
    static class TestService {
        public String getMessage() {
            return "Test Service";
        }
    }
}
