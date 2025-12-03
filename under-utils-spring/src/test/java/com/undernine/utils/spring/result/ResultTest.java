package com.undernine.utils.spring.result;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Result 单元测试
 *
 * @author Under-Utils Team
 */
class ResultTest {

    @Test
    void testSuccessWithoutData() {
        Result<Void> result = Result.success();
        
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("操作成功");
        assertThat(result.getData()).isNull();
        assertThat(result.getTimestamp()).isNotNull();
        System.out.println( result);
    }

    @Test
    void testSuccessWithData() {
        String data = "test data";
        Result<String> result = Result.success(data);
        
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("操作成功");
        assertThat(result.getData()).isEqualTo(data);
        assertThat(result.getTimestamp()).isNotNull();
        System.out.println( result);
    }

    @Test
    void testSuccessWithDataAndCustomMessage() {
        String data = "test data";
        String message = "创建成功";
        Result<String> result = Result.success(data, message);
        
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getData()).isEqualTo(data);
        System.out.println( result);
    }

    @Test
    void testFailWithMessage() {
        String message = "操作失败";
        Result<Void> result = Result.fail(message);
        
        assertThat(result.getCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getData()).isNull();
        System.out.println( result);
    }

    @Test
    void testFailWithCodeAndMessage() {
        Integer code = 10001;
        String message = "数据不存在";
        Result<Void> result = Result.fail(code, message);
        
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getData()).isNull();
        System.out.println( result);
    }

    @Test
    void testFailWithResultCode() {
        Result<Void> result = Result.fail(ResultCode.PARAM_ERROR);
        
        assertThat(result.getCode()).isEqualTo(ResultCode.PARAM_ERROR.getCode());
        assertThat(result.getMessage()).isEqualTo(ResultCode.PARAM_ERROR.getMessage());
        assertThat(result.getData()).isNull();
    }

    @Test
    void testIsSuccess() {
        Result<Void> successResult = Result.success();
        assertThat(successResult.isSuccess()).isTrue();
        
        Result<Void> failResult = Result.fail("操作失败");
        assertThat(failResult.isSuccess()).isFalse();
    }

    @Test
    void testIsFail() {
        Result<Void> successResult = Result.success();
        assertThat(successResult.isFail()).isFalse();
        
        Result<Void> failResult = Result.fail("操作失败");
        assertThat(failResult.isFail()).isTrue();
    }

    @Test
    void testAllFieldsPresent() {
        Result<String> result = Result.success("data");
        
        assertThat(result.getCode()).isNotNull();
        assertThat(result.getMessage()).isNotNull();
        assertThat(result.getData()).isNotNull();
        assertThat(result.getTimestamp()).isNotNull();
    }

    @Test
    void testTimestampIsCurrentTime() {
        long before = System.currentTimeMillis();
        Result<Void> result = Result.success();
        long after = System.currentTimeMillis();
        
        assertThat(result.getTimestamp()).isBetween(before, after);
        System.out.println( result);
    }
}
