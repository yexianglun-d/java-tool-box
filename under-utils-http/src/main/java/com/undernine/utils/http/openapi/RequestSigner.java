package com.undernine.utils.http.openapi;

/**
 * 开放平台请求签名 SPI。
 */
@FunctionalInterface
public interface RequestSigner {

    /**
     * 对请求补充签名请求头或查询参数。
     *
     * @param request 可变请求副本
     */
    void sign(OpenApiRequest request);

    /**
     * 默认不签名。
     *
     * @return no-op 签名器
     */
    static RequestSigner noop() {
        return request -> {
        };
    }
}
