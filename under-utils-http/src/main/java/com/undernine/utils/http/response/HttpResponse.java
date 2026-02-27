package com.undernine.utils.http.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.undernine.utils.core.json.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTP 响应封装类
 * <p>
 * 封装 HTTP 响应的状态码、响应头、响应体等信息。
 * </p>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse {

    /**
     * HTTP 状态码
     */
    private int statusCode;

    /**
     * 响应头
     */
    private Map<String, String> headers;

    /**
     * 响应体（字符串形式）
     */
    private String body;

    /**
     * 响应体（字节数组形式）
     */
    private byte[] bodyBytes;

    /**
     * 请求是否成功（状态码 2xx）
     *
     * @return 如果状态码在 200-299 之间返回 true，否则返回 false
     */
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * 请求是否失败（状态码非 2xx）
     *
     * @return 如果状态码不在 200-299 之间返回 true，否则返回 false
     */
    public boolean isFail() {
        return !isSuccess();
    }

    /**
     * 获取响应体字符串
     *
     * @return 响应体字符串
     */
    public String asString() {
        if (body != null) {
            return body;
        }
        if (bodyBytes != null) {
            return new String(bodyBytes, StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * 将响应体解析为指定类型的对象
     *
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 解析后的对象
     */
    public <T> T asObject(Class<T> clazz) {
        String json = asString();
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return JsonUtils.fromJson(json, clazz);
    }

    /**
     * 将响应体解析为指定类型的对象（支持泛型）
     *
     * @param typeReference 类型引用
     * @param <T>           泛型类型
     * @return 解析后的对象
     */
    public <T> T asObject(TypeReference<T> typeReference) {
        String json = asString();
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        return JsonUtils.fromJson(json, typeReference);
    }

    /**
     * 获取响应体字节数组
     *
     * @return 响应体字节数组
     */
    public byte[] asBytes() {
        if (bodyBytes != null) {
            return bodyBytes;
        }
        if (body != null) {
            return body.getBytes(StandardCharsets.UTF_8);
        }
        return null;
    }

    /**
     * 将响应体保存到文件
     *
     * @param file 目标文件
     * @throws IOException 如果保存失败
     */
    public void saveToFile(File file) throws IOException {
        byte[] bytes = asBytes();
        if (bytes == null) {
            throw new IOException("Response body is empty");
        }

        // 确保父目录存在
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        }
    }

    /**
     * 获取指定名称的响应头
     *
     * @param name 响应头名称
     * @return 响应头值，如果不存在返回 null
     */
    public String getHeader(String name) {
        if (headers == null) {
            return null;
        }
        return headers.get(name);
    }

    /**
     * 判断是否包含指定名称的响应头
     *
     * @param name 响应头名称
     * @return 如果包含返回 true，否则返回 false
     */
    public boolean hasHeader(String name) {
        return headers != null && headers.containsKey(name);
    }
}
