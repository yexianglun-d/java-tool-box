package com.undernine.utils.http.openapi;

/**
 * 开放平台调用治理异常。
 * <p>
 * 表示请求准备、签名、网络重试、响应解析等客户端治理流程失败；业务错误响应通常应通过
 * {@link OpenApiResponse} 的错误字段返回，而不是抛出该异常。
 * </p>
 */
public class OpenApiException extends RuntimeException {

    /**
     * 构造开放平台调用治理异常。
     *
     * @param message 异常消息
     */
    public OpenApiException(String message) {
        super(message);
    }

    /**
     * 构造开放平台调用治理异常。
     *
     * @param message 异常消息
     * @param cause   原始异常
     */
    public OpenApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
