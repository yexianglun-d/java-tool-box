package com.undernine.utils.samples.config;

import com.undernine.utils.biz.importtask.AsyncImportTaskTemplate;
import com.undernine.utils.mybatis.handler.AuditorProvider;
import com.undernine.utils.spring.context.CurrentTenantProvider;
import com.undernine.utils.spring.context.CurrentUserProvider;
import com.undernine.utils.spring.context.OperationContext;
import com.undernine.utils.spring.context.OperationContextCustomizer;
import com.undernine.utils.spring.context.OperationContextHolder;
import com.undernine.utils.spring.context.TraceIdProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration(proxyBeanMethods = false)
public class SampleInfrastructureConfiguration {

    @Bean
    public CurrentUserProvider sampleCurrentUserProvider(HttpServletRequest request) {
        return () -> headerOrDefault(request, "X-User-Id", "sample-user");
    }

    @Bean
    public CurrentTenantProvider sampleCurrentTenantProvider(HttpServletRequest request) {
        return () -> headerOrDefault(request, "X-Tenant-Id", "sample-tenant");
    }

    @Bean
    public TraceIdProvider sampleTraceIdProvider(HttpServletRequest request) {
        return () -> headerOrDefault(request, "X-Trace-Id", "sample-" + UUID.randomUUID());
    }

    @Bean
    public OperationContextCustomizer sampleOperationContextCustomizer() {
        return (builder, request) -> builder.attribute("sample", true);
    }

    @Bean
    public AuditorProvider sampleAuditorProvider() {
        return () -> {
            OperationContext context = OperationContextHolder.getContext();
            if (context == null || context.getUserId() == null) {
                return null;
            }
            String digits = context.getUserId().replaceAll("\\D", "");
            return digits.isEmpty() ? 0L : Long.parseLong(digits);
        };
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService sampleExecutorService() {
        return Executors.newFixedThreadPool(2);
    }

    @Bean
    public AsyncImportTaskTemplate sampleAsyncImportTaskTemplate(ExecutorService sampleExecutorService) {
        return new AsyncImportTaskTemplate(sampleExecutorService);
    }

    private static String headerOrDefault(HttpServletRequest request, String headerName, String defaultValue) {
        String value = request.getHeader(headerName);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
