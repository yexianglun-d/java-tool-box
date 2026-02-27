package com.undernine.utils.spring.aspect;

import com.undernine.utils.spring.annotation.PreventRepeat;
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
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * PreventRepeatAspect 测试类
 *
 * @author deng
 */
@ExtendWith(MockitoExtension.class)
class PreventRepeatAspectTest {

    @InjectMocks
    private PreventRepeatAspect aspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private PreventRepeat preventRepeat;

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
        when(signature.getName()).thenReturn("testMethod");
    }

    @Test
    void testFirstRequestSuccess() throws Throwable {
        when(preventRepeat.timeout()).thenReturn(3L);
        when(preventRepeat.timeUnit()).thenReturn(TimeUnit.SECONDS);
        when(preventRepeat.message()).thenReturn("请勿重复提交");
        when(joinPoint.proceed()).thenReturn("success");

        Object result = aspect.around(joinPoint, preventRepeat);

        assertThat(result).isEqualTo("success");
        verify(joinPoint).proceed();
    }

    @Test
    void testRepeatRequestThrowsException() throws Throwable {
        when(preventRepeat.timeout()).thenReturn(3L);
        when(preventRepeat.timeUnit()).thenReturn(TimeUnit.SECONDS);
        when(preventRepeat.message()).thenReturn("请勿重复提交");
        when(joinPoint.proceed()).thenReturn("success");

        // 第一次请求成功
        aspect.around(joinPoint, preventRepeat);

        // 第二次请求应该被拒绝
        assertThatThrownBy(() -> aspect.around(joinPoint, preventRepeat))
            .isInstanceOf(BizException.class)
            .hasMessage("请勿重复提交");
    }

    @Test
    void testRequestAfterTimeoutSuccess() throws Throwable {
        when(preventRepeat.timeout()).thenReturn(1L);
        when(preventRepeat.timeUnit()).thenReturn(TimeUnit.MILLISECONDS);
        when(preventRepeat.message()).thenReturn("请勿重复提交");
        when(joinPoint.proceed()).thenReturn("success");

        // 第一次请求
        aspect.around(joinPoint, preventRepeat);

        // 等待超时
        Thread.sleep(10);

        // 超时后再次请求应该成功
        Object result = aspect.around(joinPoint, preventRepeat);
        assertThat(result).isEqualTo("success");
    }
}
