package com.undernine.utils.biz.importtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 行级校验异常。
 * <p>
 * handler 可在 parse、validate 或 process 阶段抛出该异常，模板会将其转换为当前行的行级错误。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class RowValidationException extends RuntimeException {

    private final List<ImportRowError> rowErrors;

    /**
     * 构造单个错误的行级校验异常。
     *
     * @param rowError 行级错误
     */
    public RowValidationException(ImportRowError rowError) {
        super(rowError == null ? null : rowError.getMessage());
        this.rowErrors = rowError == null
                ? Collections.emptyList()
                : Collections.singletonList(rowError);
    }

    /**
     * 构造多个错误的行级校验异常。
     *
     * @param rowErrors 行级错误列表
     */
    public RowValidationException(List<ImportRowError> rowErrors) {
        super(firstMessage(rowErrors));
        this.rowErrors = immutableErrors(rowErrors);
    }

    /**
     * 构造行级校验异常。
     *
     * @param message 异常消息
     */
    public RowValidationException(String message) {
        super(message);
        this.rowErrors = Collections.emptyList();
    }

    /**
     * 构造行级校验异常。
     *
     * @param message 异常消息
     * @param cause   原始异常
     */
    public RowValidationException(String message, Throwable cause) {
        super(message, cause);
        this.rowErrors = Collections.emptyList();
    }

    /**
     * 行级错误列表。
     *
     * @return 不可变错误列表
     */
    public List<ImportRowError> getRowErrors() {
        return rowErrors;
    }

    private static List<ImportRowError> immutableErrors(List<ImportRowError> rowErrors) {
        if (rowErrors == null || rowErrors.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(rowErrors));
    }

    private static String firstMessage(List<ImportRowError> rowErrors) {
        if (rowErrors == null || rowErrors.isEmpty() || rowErrors.get(0) == null) {
            return null;
        }
        return rowErrors.get(0).getMessage();
    }
}
