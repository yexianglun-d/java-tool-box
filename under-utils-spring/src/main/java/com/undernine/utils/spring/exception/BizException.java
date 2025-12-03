package com.undernine.utils.spring.exception;

import com.undernine.utils.spring.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 用于业务逻辑中抛出的可预期异常，会被全局异常处理器捕获并转换为统一的响应格式
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 使用默认消息
 * throw new BizException("用户不存在");
 *
 * // 使用自定义状态码和消息
 * throw new BizException(40001, "参数校验失败");
 *
 * // 使用 ResultCode 枚举
 * throw new BizException(ResultCode.DATA_NOT_FOUND);
 * }</pre>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 构造函数（默认错误码）
     *
     * @param message 错误消息
     */
    public BizException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造函数（自定义错误码）
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数（使用 ResultCode 枚举）
     *
     * @param resultCode 结果码枚举
     */
    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 构造函数（使用 ResultCode 枚举，自定义消息）
     *
     * @param resultCode 结果码枚举
     * @param message    自定义消息
     */
    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    /**
     * 构造函数（包含原始异常）
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.BUSINESS_ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造函数（自定义错误码，包含原始异常）
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   原始异常
     */
    public BizException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}
