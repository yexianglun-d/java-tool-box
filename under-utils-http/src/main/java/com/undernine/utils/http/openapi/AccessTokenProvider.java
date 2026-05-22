package com.undernine.utils.http.openapi;

/**
 * 开放平台 access token 提供者 SPI。
 */
@FunctionalInterface
public interface AccessTokenProvider {

    /**
     * 获取当前请求的 access token。
     *
     * @param request 开放平台请求
     * @return access token，空值表示不附加 Authorization
     */
    String getAccessToken(OpenApiRequest request);

    /**
     * 默认不提供 token。
     *
     * @return no-op token 提供者
     */
    static AccessTokenProvider noop() {
        return request -> null;
    }
}
