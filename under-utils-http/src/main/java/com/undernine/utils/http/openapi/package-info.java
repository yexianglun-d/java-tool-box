/**
 * OpenAPI 客户端治理组件。
 * <p>
 * 该包封装面向第三方开放平台调用的重复工程模式，包括 access token 注入、请求签名、
 * traceId 与幂等键透传、统一错误解码和基于错误语义的重试。
 * </p>
 * <p>
 * 包边界限定在客户端治理编排，不绑定具体开放平台协议；平台差异应通过
 * {@link com.undernine.utils.http.openapi.AccessTokenProvider}、
 * {@link com.undernine.utils.http.openapi.RequestSigner} 和
 * {@link com.undernine.utils.http.openapi.ApiErrorDecoder} 接入。
 * </p>
 */
package com.undernine.utils.http.openapi;
