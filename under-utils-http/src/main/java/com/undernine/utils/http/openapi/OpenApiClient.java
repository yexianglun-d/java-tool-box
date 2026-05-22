package com.undernine.utils.http.openapi;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 开放平台调用客户端。
 */
public interface OpenApiClient {

    /**
     * 执行请求并返回原始字符串数据。
     *
     * @param request 开放平台请求
     * @return 开放平台响应
     */
    OpenApiResponse<String> execute(OpenApiRequest request);

    /**
     * 执行请求并将成功响应解析为指定类型。
     *
     * @param request      开放平台请求
     * @param responseType 响应数据类型
     * @param <T>          响应数据泛型
     * @return 开放平台响应
     */
    <T> OpenApiResponse<T> execute(OpenApiRequest request, Class<T> responseType);

    /**
     * 执行请求并将成功响应解析为指定泛型类型。
     *
     * @param request       开放平台请求
     * @param typeReference 响应数据类型引用
     * @param <T>           响应数据泛型
     * @return 开放平台响应
     */
    <T> OpenApiResponse<T> execute(OpenApiRequest request, TypeReference<T> typeReference);
}
