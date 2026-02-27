package com.undernine.utils.spring.aspect;

import com.undernine.utils.spring.annotation.OperationLog;
import com.undernine.utils.spring.enums.OperationType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
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
 * OperationLogAspect 测试类
 *
 * @author deng
 */
@ExtendWith(MockitoExtension.class)
class OperationLogAspectTest {

    @InjectMocks
    private OperationLogAspect aspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature signature;

    @Mock
    private OperationLog operationLog;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        when(attributes.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(attributes);
        
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    void testAroundSuccess() throws Throwable {
        when(operationLog.module()).thenReturn("测试模块");
        when(operationLog.type()).thenReturn(OperationType.CREATE);
        when(operationLog.content()).thenReturn("测试操作");
        when(operationLog.recordParams()).thenReturn(false);
        when(operationLog.recordResult()).thenReturn(false);
        when(joinPoint.proceed()).thenReturn("success");

        Object result = aspect.around(joinPoint, operationLog);

        assertThat(result).isEqualTo("success");
        verify(joinPoint).proceed();
    }

    @Test
    void testAroundWithException() throws Throwable {
        when(operationLog.module()).thenReturn("测试模块");
        when(operationLog.type()).thenReturn(OperationType.CREATE);
        when(operationLog.content()).thenReturn("测试操作");
        when(operationLog.recordParams()).thenReturn(false);
        when(operationLog.recordResult()).thenReturn(false);
        when(joinPoint.proceed()).thenThrow(new RuntimeException("测试异常"));

        assertThatThrownBy(() -> aspect.around(joinPoint, operationLog))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("测试异常");
    }

    @Test
    void testAroundWithParams() throws Throwable {
        when(operationLog.module()).thenReturn("测试模块");
        when(operationLog.type()).thenReturn(OperationType.CREATE);
        when(operationLog.content()).thenReturn("测试操作");
        when(operationLog.recordParams()).thenReturn(true);
        when(operationLog.recordResult()).thenReturn(false);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"param1", "param2"});
        when(joinPoint.proceed()).thenReturn("success");

        Object result = aspect.around(joinPoint, operationLog);

        assertThat(result).isEqualTo("success");
        verify(joinPoint).getArgs();
    }

    @Test
    void testAroundWithResult() throws Throwable {
        when(operationLog.module()).thenReturn("测试模块");
        when(operationLog.type()).thenReturn(OperationType.QUERY);
        when(operationLog.content()).thenReturn("查询操作");
        when(operationLog.recordParams()).thenReturn(false);
        when(operationLog.recordResult()).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("result data");

        Object result = aspect.around(joinPoint, operationLog);

        assertThat(result).isEqualTo("result data");
    }
}
