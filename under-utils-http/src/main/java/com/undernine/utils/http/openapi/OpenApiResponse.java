package com.undernine.utils.http.openapi;

import lombok.Builder;
import lombok.Data;

/**
 * 开放平台响应模型。
 *
 * @param <T> 业务数据类型
 */
@Data
@Builder(toBuilder = true)
public class OpenApiResponse<T> {

    /**
     * 治理层判定是否成功。
     */
    private boolean success;

    /**
     * HTTP 状态码。网络异常等未获得响应时为 0。
     */
    private int statusCode;

    /**
     * 原始响应体。
     */
    private String rawBody;

    /**
     * 成功响应解析后的数据。
     */
    private T data;

    /**
     * 业务或 HTTP 错误码。
     */
    private String errorCode;

    /**
     * 错误信息。
     */
    private String errorMessage;

    /**
     * 当前错误是否允许重试。
     */
    private boolean retryable;
}
