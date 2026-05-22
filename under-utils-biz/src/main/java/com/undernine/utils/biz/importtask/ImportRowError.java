package com.undernine.utils.biz.importtask;

import java.util.Objects;

/**
 * 导入行级错误。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ImportRowError {

    private final int rowNumber;
    private final String field;
    private final String errorCode;
    private final String message;
    private final String rawRowSummary;

    /**
     * 构造行级错误。
     *
     * @param rowNumber     1-based 行号，未知时可传 0
     * @param field         字段名，可为空
     * @param errorCode     错误码
     * @param message       错误消息
     * @param rawRowSummary 原始行摘要，可为空
     */
    public ImportRowError(int rowNumber, String field, String errorCode, String message, String rawRowSummary) {
        if (rowNumber < 0) {
            throw new IllegalArgumentException("rowNumber must be greater than or equal to 0");
        }
        this.rowNumber = rowNumber;
        this.field = field;
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
        this.message = Objects.requireNonNull(message, "message must not be null");
        this.rawRowSummary = rawRowSummary;
    }

    /**
     * 创建包含行号的错误。
     *
     * @param rowNumber 1-based 行号
     * @param field     字段名，可为空
     * @param errorCode 错误码
     * @param message   错误消息
     * @return 行级错误
     */
    public static ImportRowError of(int rowNumber, String field, String errorCode, String message) {
        return new ImportRowError(rowNumber, field, errorCode, message, null);
    }

    /**
     * 创建不含行号的错误，模板会在执行时补齐当前行号。
     *
     * @param field     字段名，可为空
     * @param errorCode 错误码
     * @param message   错误消息
     * @return 行级错误
     */
    public static ImportRowError of(String field, String errorCode, String message) {
        return new ImportRowError(0, field, errorCode, message, null);
    }

    /**
     * 1-based 行号。
     *
     * @return 行号，未知时为 0
     */
    public int getRowNumber() {
        return rowNumber;
    }

    /**
     * 字段名。
     *
     * @return 字段名
     */
    public String getField() {
        return field;
    }

    /**
     * 错误码。
     *
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 错误消息。
     *
     * @return 错误消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 原始行摘要。
     *
     * @return 原始行摘要
     */
    public String getRawRowSummary() {
        return rawRowSummary;
    }

    ImportRowError withRowMetadata(int rowNumber, String rawRowSummary) {
        int resolvedRowNumber = this.rowNumber > 0 ? this.rowNumber : rowNumber;
        String resolvedRawRowSummary = this.rawRowSummary != null ? this.rawRowSummary : rawRowSummary;
        return new ImportRowError(resolvedRowNumber, field, errorCode, message, resolvedRawRowSummary);
    }
}
