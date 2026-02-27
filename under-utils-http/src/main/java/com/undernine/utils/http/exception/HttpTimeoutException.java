package com.undernine.utils.http.exception;

/**
 * HTTP 超时异常
 * <p>
 * 当 HTTP 请求超时时抛出此异常。
 * </p>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
public class HttpTimeoutException extends HttpException {

    /**
     * 构造方法
     *
     * @param message 异常信息
     */
    public HttpTimeoutException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 异常信息
     * @param cause   原因
     */
    public HttpTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
