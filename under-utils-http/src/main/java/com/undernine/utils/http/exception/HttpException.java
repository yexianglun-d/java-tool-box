package com.undernine.utils.http.exception;

/**
 * HTTP 异常基类
 * <p>
 * 所有 HTTP 相关异常的父类。
 * </p>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
public class HttpException extends RuntimeException {

    /**
     * 构造方法
     *
     * @param message 异常信息
     */
    public HttpException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 异常信息
     * @param cause   原因
     */
    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造方法
     *
     * @param cause 原因
     */
    public HttpException(Throwable cause) {
        super(cause);
    }
}
