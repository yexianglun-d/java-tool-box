package com.undernine.utils.spring.aspect;

import com.undernine.utils.spring.annotation.RateLimit;
import com.undernine.utils.spring.exception.BizException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * RateLimitAspect 测试类
 *
 * @author deng
 */
@ExtendWith(MockitoExtension.class)
class RateLimitAspectTest {

    @InjectMocks
    private RateLimitAspect aspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private RateLimit rateLimit;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Signature signature;

    @BeforeEach
    void setUp() {
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        when(attributes.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(attributes);
        
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(joinPoint.getSignature()).thenReturn(signature);
    }

    @Test
    void testWithinLimit() throws Throwable {
        when(rateLimit.limit()).thenReturn(10);
        when(rateLimit.period()).thenReturn(60);
        when(rateLimit.message()).thenReturn("访问过于频繁");
        when(joinPoint.proceed()).thenReturn("success");

        Object result = aspect.around(joinPoint, rateLimit);

        assertThat(result).isEqualTo("success");
        verify(joinPoint).proceed();
    }

    @Test
    void testExceedLimit() throws Throwable {
        when(rateLimit.limit()).thenReturn(2);
        when(rateLimit.period()).thenReturn(60);
        when(rateLimit.message()).thenReturn("访问过于频繁");
        when(joinPoint.proceed()).thenReturn("success");

        // 前两次请求成功
        aspect.around(joinPoint, rateLimit);
        aspect.around(joinPoint, rateLimit);

        // 第三次请求应该被限流
        assertThatThrownBy(() -> aspect.around(joinPoint, rateLimit))
            .isInstanceOf(BizException.class)
            .hasMessage("访问过于频繁");
    }
}
