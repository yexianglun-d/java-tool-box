package com.undernine.utils.biz.importtask;

/**
 * 导入任务级异常。
 * <p>
 * 该异常表示任务无法继续执行，模板不会将其转换为行级错误。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImportTaskException extends RuntimeException {

    /**
     * 构造任务级异常。
     *
     * @param message 异常消息
     */
    public ImportTaskException(String message) {
        super(message);
    }

    /**
     * 构造任务级异常。
     *
     * @param message 异常消息
     * @param cause   原始异常
     */
    public ImportTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造任务级异常。
     *
     * @param cause 原始异常
     */
    public ImportTaskException(Throwable cause) {
        super(cause);
    }
}
