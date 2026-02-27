package com.undernine.utils.spring.result;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Result 测试类
 *
 * @author deng
 */
class ResultTest {

    @Test
    void testSuccessWithoutData() {
        Result<Void> result = Result.success();
        
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("操作成功");
        assertThat(result.getData()).isNull();
        assertThat(result.getTimestamp()).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFail()).isFalse();
    }

    @Test
    void testSuccessWithData() {
        String data = "test data";
        Result<String> result = Result.success(data);
        
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("操作成功");
        assertThat(result.getData()).isEqualTo(data);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testSuccessWithDataAndMessage() {
        String data = "test data";
        String message = "创建成功";
        Result<String> result = Result.success(data, message);
        
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getData()).isEqualTo(data);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testFailWithoutMessage() {
        Result<Void> result = Result.fail();
        
        assertThat(result.getCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("操作失败");
        assertThat(result.getData()).isNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isFail()).isTrue();
    }

    @Test
    void testFailWithMessage() {
        String message = "用户不存在";
        Result<Void> result = Result.fail(message);
        
        assertThat(result.getCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getData()).isNull();
        assertThat(result.isFail()).isTrue();
    }

    @Test
    void testFailWithCodeAndMessage() {
        Integer code = 40001;
        String message = "参数校验失败";
        Result<Void> result = Result.fail(code, message);
        
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getData()).isNull();
        assertThat(result.isFail()).isTrue();
    }

    @Test
    void testFailWithResultCode() {
        Result<Void> result = Result.fail(ResultCode.UNAUTHORIZED);
        
        assertThat(result.getCode()).isEqualTo(401);
        assertThat(result.getMessage()).isEqualTo("未授权，请登录");
        assertThat(result.isFail()).isTrue();
    }

    @Test
    void testIsSuccess() {
        assertThat(Result.success().isSuccess()).isTrue();
        assertThat(Result.fail().isSuccess()).isFalse();
    }

    @Test
    void testIsFail() {
        assertThat(Result.success().isFail()).isFalse();
        assertThat(Result.fail().isFail()).isTrue();
    }

    @Test
    void testTimestampNotNull() {
        Result<Void> result = Result.success();
        assertThat(result.getTimestamp()).isNotNull();
        assertThat(result.getTimestamp()).isLessThanOrEqualTo(System.currentTimeMillis());
    }
}
