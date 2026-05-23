package com.undernine.utils.biz.importtask;

import java.time.Instant;
import java.util.Objects;

/**
 * 导入任务进度快照。
 * <p>
 * 该对象既可用于同步导入的进度回调，也可用于异步导入任务的状态查询。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.1
 * @since 1.0.1
 */
public final class ImportProgress {

    private final String taskId;
    private final ImportTaskStatus status;
    private final int totalCount;
    private final int successCount;
    private final int failureCount;
    private final int skippedCount;
    private final int errorCount;
    private final Instant startedAt;
    private final Instant finishedAt;
    private final String failureMessage;

    /**
     * 构造导入进度快照。
     *
     * @param taskId         任务 ID，同步导入可为空
     * @param status         任务状态
     * @param totalCount     已读取行数
     * @param successCount   成功行数
     * @param failureCount   失败行数
     * @param skippedCount   跳过行数
     * @param errorCount     已收集行级错误数
     * @param startedAt      开始时间
     * @param finishedAt     结束时间，未结束时为空
     * @param failureMessage 任务级失败信息
     */
    public ImportProgress(String taskId, ImportTaskStatus status, int totalCount, int successCount,
                          int failureCount, int skippedCount, int errorCount, Instant startedAt,
                          Instant finishedAt, String failureMessage) {
        if (totalCount < 0 || successCount < 0 || failureCount < 0 || skippedCount < 0 || errorCount < 0) {
            throw new IllegalArgumentException("import progress counters must be greater than or equal to 0");
        }
        this.taskId = taskId;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.skippedCount = skippedCount;
        this.errorCount = errorCount;
        this.startedAt = Objects.requireNonNull(startedAt, "startedAt must not be null");
        this.finishedAt = finishedAt;
        this.failureMessage = failureMessage;
    }

    /**
     * 创建初始进度。
     *
     * @param taskId    任务 ID
     * @param startedAt 开始时间
     * @return 初始进度
     */
    public static ImportProgress pending(String taskId, Instant startedAt) {
        return new ImportProgress(taskId, ImportTaskStatus.PENDING, 0, 0, 0, 0, 0, startedAt, null, null);
    }

    /**
     * 从执行结果创建完成态进度。
     *
     * @param taskId     任务 ID
     * @param startedAt  开始时间
     * @param finishedAt 结束时间
     * @param result     导入结果
     * @return 完成态进度
     */
    public static ImportProgress completed(String taskId, Instant startedAt, Instant finishedAt, ImportResult result) {
        Objects.requireNonNull(result, "result must not be null");
        return new ImportProgress(
                taskId,
                ImportTaskStatus.COMPLETED,
                result.getTotalCount(),
                result.getSuccessCount(),
                result.getFailureCount(),
                result.getSkippedCount(),
                result.getRowErrors().size(),
                startedAt,
                finishedAt,
                null
        );
    }

    /**
     * 创建任务级失败进度。
     *
     * @param taskId         任务 ID
     * @param startedAt      开始时间
     * @param finishedAt     结束时间
     * @param previous       最近一次进度，可为空
     * @param failureMessage 失败信息
     * @return 失败态进度
     */
    public static ImportProgress failed(String taskId, Instant startedAt, Instant finishedAt,
                                        ImportProgress previous, String failureMessage) {
        if (previous == null) {
            return new ImportProgress(taskId, ImportTaskStatus.FAILED, 0, 0, 0, 0, 0,
                    startedAt, finishedAt, failureMessage);
        }
        return new ImportProgress(taskId, ImportTaskStatus.FAILED, previous.totalCount, previous.successCount,
                previous.failureCount, previous.skippedCount, previous.errorCount, startedAt, finishedAt,
                failureMessage);
    }

    ImportProgress withTask(String taskId, ImportTaskStatus status, Instant startedAt, Instant finishedAt,
                            String failureMessage) {
        return new ImportProgress(taskId, status, totalCount, successCount, failureCount, skippedCount, errorCount,
                startedAt, finishedAt, failureMessage);
    }

    static ImportProgress running(String taskId, int totalCount, int successCount, int failureCount,
                                  int skippedCount, int errorCount, Instant startedAt) {
        return new ImportProgress(taskId, ImportTaskStatus.RUNNING, totalCount, successCount, failureCount,
                skippedCount, errorCount, startedAt, null, null);
    }

    public String getTaskId() {
        return taskId;
    }

    public ImportTaskStatus getStatus() {
        return status;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public String getFailureMessage() {
        return failureMessage;
    }
}
