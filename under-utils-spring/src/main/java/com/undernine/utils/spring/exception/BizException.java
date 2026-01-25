package com.undernine.utils.spring.exception;

import com.undernine.utils.spring.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 用于业务逻辑中抛出的异常，会被全局异常处理器捕获并转换为统一的返回格式。
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 抛出默认业务异常
 * throw new BizException("用户不存在");
 *
 * // 抛出带状态码的业务异常
 * throw new BizException(10001, "用户不存在");
 *
 * // 使用状态码枚举
 * throw new BizException(ResultCode.DATA_NOT_FOUND);
 * }</pre>
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 构造方法（使用默认业务错误码）
     *
     * @param message 错误消息
     */
    public BizException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造方法（自定义状态码和消息）
     *
     * @param code    状态码
     * @param message 错误消息
     */
    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法（使用状态码枚举）
     *
     * @param resultCode 状态码枚举
     */
    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 构造方法（使用状态码枚举，覆盖消息）
     *
     * @param resultCode 状态码枚举
     * @param message    错误消息
     */
    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    /**
     * 构造方法（带原因异常）
     *
     * @param message 错误消息
     * @param cause   原因异常
     */
    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造方法（带原因异常和状态码）
     *
     * @param code    状态码
     * @param message 错误消息
     * @param cause   原因异常
     */
    public BizException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
