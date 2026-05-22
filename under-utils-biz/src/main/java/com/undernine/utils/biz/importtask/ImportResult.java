package com.undernine.utils.biz.importtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 导入任务执行结果。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ImportResult {

    private final int totalCount;
    private final int successCount;
    private final int failureCount;
    private final int skippedCount;
    private final List<ImportRowError> rowErrors;

    /**
     * 构造导入结果。
     *
     * @param totalCount   总行数
     * @param successCount 成功行数
     * @param failureCount 失败行数
     * @param skippedCount 跳过行数
     * @param rowErrors    行级错误
     */
    public ImportResult(int totalCount, int successCount, int failureCount, int skippedCount,
                        List<ImportRowError> rowErrors) {
        if (totalCount < 0) {
            throw new IllegalArgumentException("totalCount must be greater than or equal to 0");
        }
        if (successCount < 0) {
            throw new IllegalArgumentException("successCount must be greater than or equal to 0");
        }
        if (failureCount < 0) {
            throw new IllegalArgumentException("failureCount must be greater than or equal to 0");
        }
        if (skippedCount < 0) {
            throw new IllegalArgumentException("skippedCount must be greater than or equal to 0");
        }
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.skippedCount = skippedCount;
        this.rowErrors = rowErrors == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(rowErrors));
    }

    /**
     * 总行数。快速失败或达到 maxErrors 后提前停止时，表示已读取并处理的行数。
     *
     * @return 总行数
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 成功行数。
     *
     * @return 成功行数
     */
    public int getSuccessCount() {
        return successCount;
    }

    /**
     * 失败行数。
     *
     * @return 失败行数
     */
    public int getFailureCount() {
        return failureCount;
    }

    /**
     * 跳过行数。
     *
     * @return 跳过行数
     */
    public int getSkippedCount() {
        return skippedCount;
    }

    /**
     * 行级错误列表。
     *
     * @return 不可变错误列表
     */
    public List<ImportRowError> getRowErrors() {
        return rowErrors;
    }

    /**
     * 是否全部成功。
     *
     * @return 无失败行且无行级错误时返回 true
     */
    public boolean isAllSuccess() {
        return failureCount == 0 && rowErrors.isEmpty();
    }
}
