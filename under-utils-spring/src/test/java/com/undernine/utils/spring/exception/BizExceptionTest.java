package com.undernine.utils.spring.exception;

import com.undernine.utils.spring.result.ResultCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link BizException} 单元测试
 *
 * @author Under-Utils Team
 */
class BizExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "业务异常";
        BizException exception = new BizException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCode()).isEqualTo(ResultCode.BUSINESS_ERROR.getCode());
    }

    @Test
    void testConstructorWithCodeAndMessage() {
        int code = 10001;
        String message = "数据不存在";
        BizException exception = new BizException(code, message);

        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void testConstructorWithResultCode() {
        BizException exception = new BizException(ResultCode.UNAUTHORIZED);

        assertThat(exception.getCode()).isEqualTo(ResultCode.UNAUTHORIZED.getCode());
        assertThat(exception.getMessage()).isEqualTo(ResultCode.UNAUTHORIZED.getMessage());
    }

    @Test
    void testConstructorWithResultCodeAndCustomMessage() {
        String customMessage = "自定义消息";
        BizException exception = new BizException(ResultCode.FORBIDDEN, customMessage);

        assertThat(exception.getCode()).isEqualTo(ResultCode.FORBIDDEN.getCode());
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "业务异常";
        Throwable cause = new RuntimeException("原因");
        BizException exception = new BizException(message, cause);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCode()).isEqualTo(ResultCode.BUSINESS_ERROR.getCode());
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void testConstructorWithCodeMessageAndCause() {
        int code = 10002;
        String message = "数据已存在";
        Throwable cause = new RuntimeException("原因");
        BizException exception = new BizException(code, message, cause);

        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void testThrowBizException() {
        assertThatThrownBy(() -> {
            throw new BizException("测试异常");
        })
                .isInstanceOf(BizException.class)
                .hasMessage("测试异常");
    }

    @Test
    void testBizExceptionIsRuntimeException() {
        BizException exception = new BizException("测试");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
