package com.undernine.utils.http.openapi;

import com.undernine.utils.http.enums.HttpMethod;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.HashMap;
import java.util.Map;

/**
 * 开放平台请求模型。
 */
@Data
@Builder(toBuilder = true)
public class OpenApiRequest {

    /**
     * 请求地址。
     */
    private String url;

    /**
     * HTTP 方法，默认 GET。
     */
    @Builder.Default
    private HttpMethod method = HttpMethod.GET;

    /**
     * 请求头。
     */
    @Singular("header")
    private Map<String, String> headers;

    /**
     * 查询参数。
     */
    @Singular("query")
    private Map<String, String> query;

    /**
     * 请求体。字符串按 JSON 字符串透传，其他对象由底层 HTTP 模块序列化为 JSON。
     */
    private Object body;

    /**
     * 幂等键，非空时会写入 Idempotency-Key 请求头。
     */
    private String idempotencyKey;

    /**
     * 操作名称，用于日志、签名或错误定位。
     */
    private String operationName;

    /**
     * 调用链 ID，非空时按配置的 trace header 名称透传。
     */
    private String traceId;

    /**
     * 添加或覆盖请求头，供 token、签名等治理组件使用。
     *
     * @param name  请求头名称
     * @param value 请求头值
     * @return 当前请求
     */
    public OpenApiRequest header(String name, String value) {
        mutableHeaders().put(name, value);
        return this;
    }

    /**
     * 添加或覆盖查询参数，供签名等治理组件使用。
     *
     * @param name  参数名称
     * @param value 参数值
     * @return 当前请求
     */
    public OpenApiRequest query(String name, String value) {
        mutableQuery().put(name, value);
        return this;
    }

    Map<String, String> mutableHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        } else if (!(headers instanceof HashMap)) {
            headers = new HashMap<>(headers);
        }
        return headers;
    }

    Map<String, String> mutableQuery() {
        if (query == null) {
            query = new HashMap<>();
        } else if (!(query instanceof HashMap)) {
            query = new HashMap<>(query);
        }
        return query;
    }
}
