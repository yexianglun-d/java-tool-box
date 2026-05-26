package com.undernine.utils.ai;

/**
 * AI 模型调用异常。
 * <p>
 * 异常信息不会包含 API key、Authorization header 或完整请求体。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public class AiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final AiErrorType errorType;
    private final int statusCode;
    private final String errorCode;
    private final boolean retryable;

    /**
     * 构造 AI 调用异常。
     *
     * @param errorType 错误分类
     * @param message 异常信息
     * @param statusCode HTTP 状态码，没有响应时为 0
     * @param errorCode 上游错误码
     * @param retryable 是否适合由调用方重试
     */
    public AiException(AiErrorType errorType, String message, int statusCode, String errorCode, boolean retryable) {
        super(message);
        this.errorType = errorType;
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.retryable = retryable;
    }

    /**
     * 构造 AI 调用异常。
     *
     * @param errorType 错误分类
     * @param message 异常信息
     * @param statusCode HTTP 状态码，没有响应时为 0
     * @param errorCode 上游错误码
     * @param retryable 是否适合由调用方重试
     * @param cause 原始异常
     */
    public AiException(AiErrorType errorType, String message, int statusCode, String errorCode, boolean retryable,
                       Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.retryable = retryable;
    }

    /**
     * 错误分类。
     *
     * @return 错误分类
     */
    public AiErrorType getErrorType() {
        return errorType;
    }

    /**
     * HTTP 状态码，没有响应时为 0。
     *
     * @return HTTP 状态码
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * 上游错误码。
     *
     * @return 上游错误码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 是否适合由调用方重试。
     *
     * @return true 表示可重试
     */
    public boolean isRetryable() {
        return retryable;
    }
}
