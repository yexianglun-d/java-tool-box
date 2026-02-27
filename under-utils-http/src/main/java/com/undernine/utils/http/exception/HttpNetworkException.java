package com.undernine.utils.http.exception;

/**
 * HTTP 网络异常
 * <p>
 * 当网络连接失败时抛出此异常。
 * </p>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
public class HttpNetworkException extends HttpException {

    /**
     * 构造方法
     *
     * @param message 异常信息
     */
    public HttpNetworkException(String message) {
        super(message);
    }

    /**
     * 构造方法
     *
     * @param message 异常信息
     * @param cause   原因
     */
    public HttpNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
