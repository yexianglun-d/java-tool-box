package com.undernine.utils.ai;

/**
 * AI 调用错误分类。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.2
 */
public enum AiErrorType {

    /**
     * 鉴权失败或无权限。
     */
    AUTHENTICATION,

    /**
     * 触发模型服务限流。
     */
    RATE_LIMIT,

    /**
     * 请求超时。
     */
    TIMEOUT,

    /**
     * 客户端请求错误。
     */
    CLIENT_ERROR,

    /**
     * 模型服务端错误。
     */
    SERVER_ERROR,

    /**
     * 网络连接失败。
     */
    NETWORK,

    /**
     * 响应解析失败。
     */
    RESPONSE_PARSE,

    /**
     * 未归类错误。
     */
    UNKNOWN
}
