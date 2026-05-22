package com.undernine.utils.biz.importtask;

/**
 * 导入任务内置错误码。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ImportErrorCodes {

    /**
     * 行解析失败。
     */
    public static final String PARSE_ERROR = "IMPORT_PARSE_ERROR";

    /**
     * 行校验失败。
     */
    public static final String VALIDATION_ERROR = "IMPORT_VALIDATION_ERROR";

    /**
     * 行处理失败。
     */
    public static final String PROCESS_ERROR = "IMPORT_PROCESS_ERROR";

    private ImportErrorCodes() {
        throw new UnsupportedOperationException("Utility class");
    }
}
