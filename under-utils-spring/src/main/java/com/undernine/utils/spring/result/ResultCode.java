package com.undernine.utils.spring.result;

/**
 * 响应状态码枚举
 * <p>
 * 定义常用的响应状态码，遵循 HTTP 状态码规范
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ResultCode {

    // ==================== 成功状态码 2xx ====================
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 已创建
     */
    CREATED(201, "创建成功"),

    // ==================== 客户端错误 4xx ====================
    /**
     * 通用失败
     */
    FAIL(400, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请登录"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无权限访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),

    /**
     * 参数校验失败
     */
    VALIDATION_ERROR(422, "参数校验失败"),

    /**
     * 请求过于频繁
     */
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),

    // ==================== 服务端错误 5xx ====================
    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    /**
     * 服务暂不可用
     */
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // ==================== 业务错误码 10000+ ====================
    /**
     * 业务异常
     */
    BUSINESS_ERROR(10000, "业务处理失败"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(10001, "数据不存在"),

    /**
     * 数据已存在
     */
    DATA_ALREADY_EXISTS(10002, "数据已存在"),

    /**
     * 数据状态异常
     */
    DATA_STATUS_ERROR(10003, "数据状态异常"),

    /**
     * 操作频繁
     */
    OPERATION_TOO_FREQUENT(10004, "操作过于频繁"),

    ;

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 消息
     */
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取消息
     *
     * @return 消息
     */
    public String getMessage() {
        return message;
    }
}
