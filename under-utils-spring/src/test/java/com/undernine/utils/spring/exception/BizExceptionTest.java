package com.undernine.utils.spring.exception;

import com.undernine.utils.spring.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BizException 单元测试
 *
 * @author Under-Utils Team
 */
@Slf4j
class BizExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "业务异常";
        BizException exception = new BizException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCode()).isEqualTo(ResultCode.BUSINESS_ERROR.getCode());
        log.error("e: ", exception);
    }

    @Test
    void testConstructorWithCodeAndMessage() {
        Integer code = 10001;
        String message = "数据不存在";
        BizException exception = new BizException(code, message);
        
        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void testConstructorWithResultCode() {
        BizException exception = new BizException(ResultCode.DATA_NOT_FOUND);
        
        assertThat(exception.getCode()).isEqualTo(ResultCode.DATA_NOT_FOUND.getCode());
        assertThat(exception.getMessage()).isEqualTo(ResultCode.DATA_NOT_FOUND.getMessage());
    }

    @Test
    void testConstructorWithResultCodeAndCustomMessage() {
        String customMessage = "用户数据不存在";
        BizException exception = new BizException(ResultCode.DATA_NOT_FOUND, customMessage);
        
        assertThat(exception.getCode()).isEqualTo(ResultCode.DATA_NOT_FOUND.getCode());
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    void testConstructorWithCause() {
        String message = "业务异常";
        Throwable cause = new RuntimeException("根本原因");
        BizException exception = new BizException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCode()).isEqualTo(ResultCode.BUSINESS_ERROR.getCode());
    }

    @Test
    void testConstructorWithCodeMessageAndCause() {
        Integer code = 10002;
        String message = "数据已存在";
        Throwable cause = new IllegalStateException("状态错误");
        BizException exception = new BizException(code, message, cause);
        
        assertThat(exception.getCode()).isEqualTo(code);
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void testExceptionCanBeThrown() {
        try {
            throw new BizException("测试异常");
        } catch (BizException e) {
            assertThat(e).isInstanceOf(RuntimeException.class);
            assertThat(e.getMessage()).isEqualTo("测试异常");
        }
    }

    @Test
    void testCustomMessageWithResultCode() {
        String customMessage = "用户名不能为空";
        BizException exception = new BizException(ResultCode.PARAM_ERROR, customMessage);
        
        assertThat(exception.getCode()).isEqualTo(ResultCode.PARAM_ERROR.getCode());
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    void testDefaultBusinessErrorCode() {
        BizException exception = new BizException("默认业务错误");
        
        assertThat(exception.getCode()).isEqualTo(10000);
    }
}
