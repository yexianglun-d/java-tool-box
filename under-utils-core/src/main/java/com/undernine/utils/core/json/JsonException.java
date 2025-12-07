package com.undernine.utils.core.json;

/**
 * JSON 处理异常
 * <p>
 * 当 JSON 序列化或反序列化失败时抛出此异常。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class JsonException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     *
     * @param message 异常信息
     */
    public JsonException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 异常信息
     * @param cause   原始异常
     */
    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
