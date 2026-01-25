package com.undernine.utils.spring.context;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link SpringContextHolder} 单元测试
 *
 * @author Under-Utils Team
 */
@SpringJUnitConfig(SpringContextHolderTest.TestConfig.class)
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
        assertThat(service.getName()).isEqualTo("TestService");
    }

    @Test
    void testGetBeanByName() {
        TestService service = SpringContextHolder.getBean("testService", TestService.class);
        assertThat(service).isNotNull();
        assertThat(service.getName()).isEqualTo("TestService");
    }

    @Test
    void testGetBeanByNameOnly() {
        Object bean = SpringContextHolder.getBean("testService");
        assertThat(bean).isNotNull();
        assertThat(bean).isInstanceOf(TestService.class);
    }

    @Test
    void testGetBeanNotFound() {
        assertThatThrownBy(() -> SpringContextHolder.getBean(NonExistentService.class))
                .isInstanceOf(org.springframework.beans.factory.NoSuchBeanDefinitionException.class);
    }

    @Test
    void testGetBeanByNameNotFound() {
        assertThatThrownBy(() -> SpringContextHolder.getBean("nonExistent"))
                .isInstanceOf(org.springframework.beans.factory.NoSuchBeanDefinitionException.class);
    }

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

    static class TestService {
        public String getName() {
            return "TestService";
        }
    }

    static class NonExistentService {
    }
}
