package com.undernine.utils.spring.key;

import com.undernine.utils.spring.context.OperationContext;
import com.undernine.utils.spring.context.OperationContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultOperationKeyResolverTest {

    private final DefaultOperationKeyResolver resolver = new DefaultOperationKeyResolver();

    @AfterEach
    void tearDown() {
        OperationContextHolder.clear();
    }

    @Test
    void defaultKeyPrefersOperationContextUserTenantAndUri() {
        ProceedingJoinPoint point = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(point.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("submit");
        when(point.getArgs()).thenReturn(new Object[]{"order-1"});
        OperationContext context = OperationContext.builder()
                .traceId("trace-context")
                .tenantId("tenant-context")
                .userId("user-context")
                .requestUri("/ctx/orders")
                .build();

        String key;
        try (OperationContextHolder.Scope ignored = OperationContextHolder.scope(context)) {
            key = resolver.resolve(point, "orders", "");
        }

        assertThat(key).startsWith("orders:tenant-context:user-context:/ctx/orders:submit:");
    }

    @Test
    void expressionCanUseOperationContextVariables() throws Exception {
        ProceedingJoinPoint point = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        Target target = new Target();
        Method method = Target.class.getDeclaredMethod("submit", String.class);
        when(point.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(signature.getName()).thenReturn("submit");
        when(point.getTarget()).thenReturn(target);
        when(point.getArgs()).thenReturn(new Object[]{"order-1"});
        OperationContext context = OperationContext.builder()
                .traceId("trace-context")
                .tenantId("tenant-context")
                .userId("user-context")
                .requestMethod("POST")
                .requestUri("/ctx/orders")
                .clientIp("10.0.0.1")
                .operationName("submit-order")
                .build();

        String key;
        try (OperationContextHolder.Scope ignored = OperationContextHolder.scope(context)) {
            key = resolver.resolve(point, "orders",
                    "#traceId + ':' + #tenantId + ':' + #userId + ':' + #requestMethod + ':' + #clientIp + ':' + #operationName");
        }

        assertThat(key).isEqualTo("orders:tenant-context:user-context:/ctx/orders:submit:"
                + "trace-context:tenant-context:user-context:POST:10.0.0.1:submit-order");
    }

    private static class Target {
        @SuppressWarnings("unused")
        public String submit(String orderId) {
            return orderId;
        }
    }
}
