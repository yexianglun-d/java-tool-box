package com.undernine.utils.biz.importtask;

/**
 * 当前导入行上下文。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ImportRowContext {

    private final int rowNumber;
    private final int rowIndex;

    /**
     * 构造行上下文。
     *
     * @param rowNumber 1-based 行号
     */
    public ImportRowContext(int rowNumber) {
        if (rowNumber < 1) {
            throw new IllegalArgumentException("rowNumber must be greater than or equal to 1");
        }
        this.rowNumber = rowNumber;
        this.rowIndex = rowNumber - 1;
    }

    /**
     * 1-based 行号。
     *
     * @return 行号
     */
    public int getRowNumber() {
        return rowNumber;
    }

    /**
     * 0-based 行索引。
     *
     * @return 行索引
     */
    public int getRowIndex() {
        return rowIndex;
    }
}
