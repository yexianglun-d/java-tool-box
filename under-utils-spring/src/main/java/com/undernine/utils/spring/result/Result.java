package com.undernine.utils.spring.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装
 * <p>
 * 用于 RESTful API 的统一返回格式，包含状态码、消息和数据
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 成功返回（无数据）
 * return Result.success();
 *
 * // 成功返回（有数据）
 * return Result.success(user);
 *
 * // 成功返回（有数据 + 自定义消息）
 * return Result.success(user, "创建成功");
 *
 * // 失败返回
 * return Result.fail("用户不存在");
 *
 * // 自定义状态码
 * return Result.fail(40001, "参数校验失败");
 * }</pre>
 *
 * @param <T> 返回数据类型
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 私有构造函数
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 私有构造函数
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     */
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 成功响应 ====================

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功响应（有数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应（有数据和自定义消息）
     *
     * @param data    响应数据
     * @param message 自定义消息
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    // ==================== 失败响应 ====================

    /**
     * 失败响应（默认消息）
     *
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.FAIL.getCode(), ResultCode.FAIL.getMessage(), null);
    }

    /**
     * 失败响应（自定义消息）
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultCode.FAIL.getCode(), message, null);
    }

    /**
     * 失败响应（自定义状态码和消息）
     *
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败响应（使用 ResultCode 枚举）
     *
     * @param resultCode 结果码枚举
     * @param <T>        数据类型
     * @return Result
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    // ==================== 判断方法 ====================

    /**
     * 判断是否成功
     *
     * @return true-成功 false-失败
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 判断是否失败
     *
     * @return true-失败 false-成功
     */
    public boolean isFail() {
        return !isSuccess();
    }
}
