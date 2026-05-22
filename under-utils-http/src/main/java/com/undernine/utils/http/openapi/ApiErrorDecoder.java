package com.undernine.utils.http.openapi;

import com.undernine.utils.http.response.HttpResponse;

/**
 * 开放平台错误解码 SPI。
 */
@FunctionalInterface
public interface ApiErrorDecoder {

    /**
     * 解码 HTTP 响应中的成功、错误和重试语义。
     *
     * @param request      开放平台请求
     * @param httpResponse 原始 HTTP 响应
     * @return 解码结果
     */
    ApiErrorDecodeResult decode(OpenApiRequest request, HttpResponse httpResponse);

    /**
     * 默认按 HTTP 2xx 判定成功。
     *
     * @return 默认解码器
     */
    static ApiErrorDecoder httpStatus() {
        return (request, httpResponse) -> {
            if (httpResponse.isSuccess()) {
                return ApiErrorDecodeResult.success();
            }
            return ApiErrorDecodeResult.failure(
                    "HTTP_" + httpResponse.getStatusCode(),
                    "HTTP request failed with status " + httpResponse.getStatusCode(),
                    isRetryableHttpStatus(httpResponse.getStatusCode())
            );
        };
    }

    static boolean isRetryableHttpStatus(int statusCode) {
        return statusCode == 408 || statusCode == 425 || statusCode == 429
                || (statusCode >= 500 && statusCode <= 599);
    }
}
