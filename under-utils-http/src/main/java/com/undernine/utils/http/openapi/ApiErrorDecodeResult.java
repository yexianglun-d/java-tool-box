package com.undernine.utils.http.openapi;

import lombok.Builder;
import lombok.Data;

/**
 * 开放平台错误解码结果。
 * <p>
 * 该对象承载治理层对一次 HTTP 响应的业务语义判断：是否成功、错误码、错误消息以及是否值得重试。
 * </p>
 */
@Data
@Builder
public class ApiErrorDecodeResult {

    /**
     * 当前响应是否被判定为成功。
     */
    private boolean success;

    /**
     * 业务或 HTTP 错误码。
     */
    private String errorCode;

    /**
     * 错误消息。
     */
    private String errorMessage;

    /**
     * 当前错误是否允许客户端重试。
     */
    private boolean retryable;

    /**
     * 创建成功解码结果。
     *
     * @return 成功解码结果
     */
    public static ApiErrorDecodeResult success() {
        return ApiErrorDecodeResult.builder()
                .success(true)
                .build();
    }

    /**
     * 创建失败解码结果。
     *
     * @param errorCode    错误码
     * @param errorMessage 错误消息
     * @param retryable    是否允许重试
     * @return 失败解码结果
     */
    public static ApiErrorDecodeResult failure(String errorCode, String errorMessage, boolean retryable) {
        return ApiErrorDecodeResult.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .retryable(retryable)
                .build();
    }
}
