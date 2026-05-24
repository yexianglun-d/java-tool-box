package com.undernine.utils.starter.autoconfigure;

import com.undernine.utils.spring.aspect.PreventRepeatAspect;
import com.undernine.utils.spring.aspect.RateLimitAspect;
import com.undernine.utils.spring.context.CurrentTenantProvider;
import com.undernine.utils.spring.context.CurrentUserProvider;
import com.undernine.utils.spring.context.OperationContextFilter;
import com.undernine.utils.spring.context.OperationContextTaskDecorator;
import com.undernine.utils.spring.context.TraceIdProvider;
import com.undernine.utils.spring.key.OperationKeyResolver;
import com.undernine.utils.spring.ratelimit.LocalRateLimitStore;
import com.undernine.utils.spring.ratelimit.RateLimitStore;
import com.undernine.utils.spring.repeat.LocalRepeatSubmitStore;
import com.undernine.utils.spring.repeat.RepeatSubmitStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.task.TaskDecorator;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UnderUtilsSpringAutoConfiguration 测试。
 *
 * @author Under-Utils Team
 */
class UnderUtilsSpringAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(UnderUtilsSpringAutoConfiguration.class));

    @Test
    void shouldAutoConfigureDefaultLocalComponents() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CurrentUserProvider.class);
            assertThat(context).hasSingleBean(CurrentTenantProvider.class);
            assertThat(context).hasSingleBean(TraceIdProvider.class);
            assertThat(context).hasSingleBean(OperationKeyResolver.class);
            assertThat(context).hasSingleBean(OperationContextFilter.class);
            assertThat(context).hasSingleBean(OperationContextTaskDecorator.class);
            assertThat(context).hasBean("underUtilsOperationContextFilterRegistration");
            assertThat(context).hasSingleBean(RateLimitStore.class);
            assertThat(context).hasSingleBean(RepeatSubmitStore.class);
            assertThat(context).hasSingleBean(RateLimitAspect.class);
            assertThat(context).hasSingleBean(PreventRepeatAspect.class);
            assertThat(context.getBean(RateLimitStore.class)).isInstanceOf(LocalRateLimitStore.class);
            assertThat(context.getBean(RepeatSubmitStore.class)).isInstanceOf(LocalRepeatSubmitStore.class);
        });
    }

    @Test
    void shouldBackOffWhenUserProviderExists() {
        CurrentUserProvider customProvider = () -> "custom-user";

        contextRunner
                .withBean(CurrentUserProvider.class, () -> customProvider)
                .run(context -> assertThat(context.getBean(CurrentUserProvider.class)).isSameAs(customProvider));
    }

    @Test
    void shouldAllowOperationContextFilterToBeDisabled() {
        contextRunner
                .withPropertyValues("under.utils.web.operation-context.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(OperationContextFilter.class);
                    assertThat(context).doesNotHaveBean("underUtilsOperationContextFilterRegistration");
                });
    }

    @Test
    void shouldNotCreateTaskDecoratorWhenOperationContextTaskDecoratorDisabled() {
        contextRunner
                .withPropertyValues("under.utils.web.operation-context.task-decorator-enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(TaskDecorator.class);
                    assertThat(context).doesNotHaveBean(OperationContextTaskDecorator.class);
                });
    }

    @Test
    void shouldBackOffOperationContextTaskDecoratorWhenUserTaskDecoratorExists() {
        TaskDecorator customDecorator = runnable -> runnable;

        contextRunner
                .withBean(TaskDecorator.class, () -> customDecorator)
                .run(context -> {
                    assertThat(context).hasSingleBean(TaskDecorator.class);
                    assertThat(context.getBean(TaskDecorator.class)).isSameAs(customDecorator);
                    assertThat(context).doesNotHaveBean(OperationContextTaskDecorator.class);
                });
    }

    @Test
    void shouldBackOffRateLimitStoreWhenUserStoreExists() {
        RateLimitStore customStore = (key, limit, window) -> true;

        contextRunner
                .withBean(RateLimitStore.class, () -> customStore)
                .run(context -> {
                    assertThat(context).hasSingleBean(RateLimitStore.class);
                    assertThat(context.getBean(RateLimitStore.class)).isSameAs(customStore);
                    assertThat(context).hasSingleBean(RateLimitAspect.class);
                });
    }

    @Test
    void shouldBackOffRepeatSubmitStoreWhenUserStoreExists() {
        RepeatSubmitStore customStore = new RepeatSubmitStore() {
            @Override
            public boolean acquire(String key, Duration ttl) {
                return true;
            }

            @Override
            public void release(String key) {
            }
        };

        contextRunner
                .withBean(RepeatSubmitStore.class, () -> customStore)
                .run(context -> {
                    assertThat(context).hasSingleBean(RepeatSubmitStore.class);
                    assertThat(context.getBean(RepeatSubmitStore.class)).isSameAs(customStore);
                    assertThat(context).hasSingleBean(PreventRepeatAspect.class);
                });
    }
}
