package com.undernine.utils.spring.exception;

import com.undernine.utils.spring.result.Result;
import com.undernine.utils.spring.result.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link GlobalExceptionHandler} 单元测试
 *
 * @author Under-Utils Team
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleBizException() {
        BizException exception = new BizException(10001, "数据不存在");
        Result<?> result = handler.handleBizException(exception);

        assertThat(result.getCode()).isEqualTo(10001);
        assertThat(result.getMessage()).isEqualTo("数据不存在");
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void testHandleValidException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);

        FieldError fieldError = new FieldError("user", "username", "用户名不能为空");
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        Result<?> result = handler.handleValidException(exception);

        assertThat(result.getCode()).isEqualTo(ResultCode.VALIDATION_ERROR.getCode());
        assertThat(result.getMessage()).contains("用户名不能为空");
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void testHandleBindException() {
        BindException exception = new BindException(new Object(), "user");
        exception.addError(new FieldError("user", "email", "邮箱格式不正确"));

        Result<?> result = handler.handleBindException(exception);

        assertThat(result.getCode()).isEqualTo(ResultCode.PARAM_ERROR.getCode());
        assertThat(result.getMessage()).contains("邮箱格式不正确");
        assertThat(result.isSuccess()).isFalse();
    }

    // ConstraintViolationException 在当前 GlobalExceptionHandler 中没有处理，移除此测试

    @Test
    void testHandleMethodNotSupportedException() {
        HttpRequestMethodNotSupportedException exception =
                new HttpRequestMethodNotSupportedException("POST");

        Result<?> result = handler.handleMethodNotSupportedException(exception);

        assertThat(result.getCode()).isEqualTo(ResultCode.METHOD_NOT_ALLOWED.getCode());
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void testHandleNotFoundException() {
        NoHandlerFoundException exception =
                new NoHandlerFoundException("GET", "/api/test", null);

        Result<?> result = handler.handleNotFoundException(exception);

        assertThat(result.getCode()).isEqualTo(ResultCode.NOT_FOUND.getCode());
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("参数非法");
        Result<?> result = handler.handleIllegalArgumentException(exception);

        assertThat(result.getCode()).isEqualTo(ResultCode.PARAM_ERROR.getCode());
        assertThat(result.getMessage()).isEqualTo("参数非法");
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void testHandleNullPointerException() {
        NullPointerException exception = new NullPointerException("空指针");
        Result<?> result = handler.handleNullPointerException(exception);

        assertThat(result.getCode()).isEqualTo(ResultCode.INTERNAL_SERVER_ERROR.getCode());
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void testHandleException() {
        Exception exception = new Exception("未知异常");
        Result<?> result = handler.handleException(exception);

        assertThat(result.getCode()).isEqualTo(ResultCode.INTERNAL_SERVER_ERROR.getCode());
        assertThat(result.isSuccess()).isFalse();
    }
}
