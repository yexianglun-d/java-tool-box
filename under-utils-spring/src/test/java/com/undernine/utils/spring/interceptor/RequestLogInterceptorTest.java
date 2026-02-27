package com.undernine.utils.spring.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * RequestLogInterceptor 测试类
 *
 * @author deng
 */
@ExtendWith(MockitoExtension.class)
class RequestLogInterceptorTest {

    @InjectMocks
    private RequestLogInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    void testPreHandle() {
        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verify(request).setAttribute(eq("REQUEST_START_TIME"), anyLong());
    }

    @Test
    void testAfterCompletion() {
        when(response.getStatus()).thenReturn(200);
        request.setAttribute("REQUEST_START_TIME", System.currentTimeMillis());

        interceptor.afterCompletion(request, response, new Object(), null);

        verify(response).getStatus();
    }

    @Test
    void testAfterCompletionWithException() {
        when(response.getStatus()).thenReturn(500);
        request.setAttribute("REQUEST_START_TIME", System.currentTimeMillis());
        Exception exception = new RuntimeException("测试异常");

        interceptor.afterCompletion(request, response, new Object(), exception);

        verify(response).getStatus();
    }

    @Test
    void testGetClientIpFromXForwardedFor() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1, 10.0.0.1");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
    }
}
