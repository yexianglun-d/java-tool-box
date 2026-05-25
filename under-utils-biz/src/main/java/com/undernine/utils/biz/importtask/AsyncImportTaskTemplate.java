package com.undernine.utils.biz.importtask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 异步导入任务模板。
 * <p>
 * 模板负责提交后台导入、保存进度快照、保存完成结果或任务级失败信息。任务状态默认保存在当前 JVM 内，
 * 如果需要跨实例查询，应在应用层把进度监听事件同步到数据库、Redis 或消息系统。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.1
 * @since 1.0.1
 */
public final class AsyncImportTaskTemplate {

    private static final Logger log = LoggerFactory.getLogger(AsyncImportTaskTemplate.class);
    private static final Duration DEFAULT_TASK_RETENTION = Duration.ofHours(24);
    private static final long CLEANUP_INTERVAL_MILLIS = Duration.ofMinutes(1).toMillis();

    private final ImportOptions options;
    private final Executor executor;
    private final long taskRetentionMillis;
    private final Map<String, TaskState> tasks = new ConcurrentHashMap<>();
    private final AtomicLong nextCleanupAt = new AtomicLong();

    /**
     * 使用默认导入选项创建异步模板。
     *
     * @param executor 后台执行器
     */
    public AsyncImportTaskTemplate(Executor executor) {
        this(ImportOptions.defaults(), executor);
    }

    /**
     * 使用指定导入选项创建异步模板。
     *
     * @param options  导入选项
     * @param executor 后台执行器
     */
    public AsyncImportTaskTemplate(ImportOptions options, Executor executor) {
        this(options, executor, DEFAULT_TASK_RETENTION);
    }

    /**
     * 使用指定导入选项、执行器和任务状态保留时间创建异步模板。
     *
     * @param options       导入选项
     * @param executor      后台执行器
     * @param taskRetention 完成或失败任务在当前 JVM 内的保留时间
     */
    public AsyncImportTaskTemplate(ImportOptions options, Executor executor, Duration taskRetention) {
        this.options = options == null ? ImportOptions.defaults() : options;
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
        this.taskRetentionMillis = normalizeTaskRetention(taskRetention).toMillis();
    }

    /**
     * 提交 Iterable 行数据导入任务，并自动生成任务 ID。
     *
     * @param rows    原始行集合
     * @param handler 行处理器
     * @param <R>     原始行类型
     * @param <T>     解析后的业务行类型
     * @return 任务 ID
     */
    public <R, T> String submit(Iterable<R> rows, ImportRowHandler<R, T> handler) {
        return submit(newTaskId(), rows, handler);
    }

    /**
     * 提交 Iterable 行数据导入任务。
     *
     * @param taskId  任务 ID
     * @param rows    原始行集合
     * @param handler 行处理器
     * @param <R>     原始行类型
     * @param <T>     解析后的业务行类型
     * @return 任务 ID
     */
    public <R, T> String submit(String taskId, Iterable<R> rows, ImportRowHandler<R, T> handler) {
        Objects.requireNonNull(rows, "rows must not be null");
        Objects.requireNonNull(handler, "handler must not be null");
        return submitTask(taskId, () -> ImportTaskTemplate.create(optionsWithTaskProgress(taskId))
                .execute(rows, handler));
    }

    /**
     * 提交行读取器导入任务，并自动生成任务 ID。读取器会在后台任务结束时关闭。
     *
     * @param rowReader 行读取器
     * @param handler   行处理器
     * @param <R>       原始行类型
     * @param <T>       解析后的业务行类型
     * @return 任务 ID
     */
    public <R, T> String submit(ImportRowReader<R> rowReader, ImportRowHandler<R, T> handler) {
        return submit(newTaskId(), rowReader, handler);
    }

    /**
     * 提交行读取器导入任务。读取器会在后台任务结束时关闭。
     *
     * @param taskId    任务 ID
     * @param rowReader 行读取器
     * @param handler   行处理器
     * @param <R>       原始行类型
     * @param <T>       解析后的业务行类型
     * @return 任务 ID
     */
    public <R, T> String submit(String taskId, ImportRowReader<R> rowReader, ImportRowHandler<R, T> handler) {
        Objects.requireNonNull(rowReader, "rowReader must not be null");
        Objects.requireNonNull(handler, "handler must not be null");
        return submitTask(taskId, () -> ImportTaskTemplate.create(optionsWithTaskProgress(taskId))
                .execute(rowReader, handler));
    }

    /**
     * 查询任务进度。
     *
     * @param taskId 任务 ID
     * @return 进度快照
     */
    public Optional<ImportProgress> findProgress(String taskId) {
        TaskState state = findTaskState(taskId);
        return state == null ? Optional.empty() : Optional.of(state.progress.get());
    }

    /**
     * 查询已完成任务的导入结果。
     *
     * @param taskId 任务 ID
     * @return 导入结果；任务未完成、失败或不存在时为空
     */
    public Optional<ImportResult> findResult(String taskId) {
        TaskState state = findTaskState(taskId);
        return state == null ? Optional.empty() : Optional.ofNullable(state.result);
    }

    /**
     * 查询任务级失败。
     *
     * @param taskId 任务 ID
     * @return 失败异常；任务未失败或不存在时为空
     */
    public Optional<Throwable> findFailure(String taskId) {
        TaskState state = findTaskState(taskId);
        return state == null ? Optional.empty() : Optional.ofNullable(state.failure);
    }

    /**
     * 移除任务快照。
     *
     * @param taskId 任务 ID
     */
    public void remove(String taskId) {
        tasks.remove(taskId);
    }

    private String submitTask(String taskId, ImportExecution execution) {
        String normalizedTaskId = normalizeTaskId(taskId);
        Instant startedAt = Instant.now();
        cleanupExpiredTask(normalizedTaskId, System.currentTimeMillis());
        cleanupExpiredTasks(System.currentTimeMillis(), false);
        TaskState state = new TaskState(ImportProgress.pending(normalizedTaskId, startedAt));
        if (tasks.putIfAbsent(normalizedTaskId, state) != null) {
            throw new IllegalArgumentException("import task already exists: " + normalizedTaskId);
        }

        try {
            executor.execute(() -> runTask(normalizedTaskId, startedAt, state, execution));
        } catch (RuntimeException ex) {
            Instant finishedAt = Instant.now();
            state.failure = ex;
            state.progress.set(ImportProgress.failed(normalizedTaskId, startedAt, finishedAt,
                    state.progress.get(), failureMessage(ex)));
            state.markFinished(finishedAt);
            throw ex;
        }
        return normalizedTaskId;
    }

    private void runTask(String taskId, Instant startedAt, TaskState state, ImportExecution execution) {
        state.progress.set(ImportProgress.running(taskId, 0, 0, 0, 0, 0, startedAt));
        try {
            ImportResult result = execution.execute();
            Instant finishedAt = Instant.now();
            state.result = result;
            state.progress.set(ImportProgress.completed(taskId, startedAt, finishedAt, result));
            state.markFinished(finishedAt);
        } catch (Throwable error) {
            Instant finishedAt = Instant.now();
            state.failure = error;
            state.progress.set(ImportProgress.failed(taskId, startedAt, finishedAt, state.progress.get(),
                    failureMessage(error)));
            state.markFinished(finishedAt);
            log.warn("Async import task failed: {}", taskId, error);
        }
    }

    private ImportOptions optionsWithTaskProgress(String taskId) {
        ImportProgressListener userListener = options.getProgressListener();
        return options.toBuilder()
                .progressListener(progress -> {
                    ImportProgress taskProgress = progress.withTask(taskId, progress.getStatus(),
                            progress.getStartedAt(), progress.getFinishedAt(), progress.getFailureMessage());
                    TaskState state = tasks.get(taskId);
                    if (state != null) {
                        state.progress.set(taskProgress);
                    }
                    userListener.onProgress(taskProgress);
                })
                .build();
    }

    private String normalizeTaskId(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("taskId must not be blank");
        }
        return taskId.trim();
    }

    private String newTaskId() {
        return UUID.randomUUID().toString();
    }

    private String failureMessage(Throwable error) {
        if (error.getMessage() != null && !error.getMessage().isBlank()) {
            return error.getMessage();
        }
        return error.getClass().getSimpleName();
    }

    private Duration normalizeTaskRetention(Duration taskRetention) {
        Duration retention = taskRetention == null ? DEFAULT_TASK_RETENTION : taskRetention;
        if (retention.isZero() || retention.isNegative()) {
            throw new IllegalArgumentException("taskRetention must be positive");
        }
        return retention;
    }

    private TaskState findTaskState(String taskId) {
        TaskState state = tasks.get(taskId);
        long now = System.currentTimeMillis();
        if (state != null && state.isExpired(now, taskRetentionMillis)) {
            tasks.remove(taskId, state);
            return null;
        }
        cleanupExpiredTasks(now, false);
        return state;
    }

    private void cleanupExpiredTask(String taskId, long now) {
        TaskState state = tasks.get(taskId);
        if (state != null && state.isExpired(now, taskRetentionMillis)) {
            tasks.remove(taskId, state);
        }
    }

    private void cleanupExpiredTasks(long now, boolean force) {
        if (!force) {
            long next = nextCleanupAt.get();
            if (now < next || !nextCleanupAt.compareAndSet(next, now + CLEANUP_INTERVAL_MILLIS)) {
                return;
            }
        }
        tasks.entrySet().removeIf(entry -> entry.getValue().isExpired(now, taskRetentionMillis));
    }

    private static final class TaskState {

        private final AtomicReference<ImportProgress> progress;
        private volatile ImportResult result;
        private volatile Throwable failure;
        private volatile long finishedAtMillis = -1L;

        private TaskState(ImportProgress progress) {
            this.progress = new AtomicReference<>(progress);
        }

        private void markFinished(Instant finishedAt) {
            this.finishedAtMillis = finishedAt.toEpochMilli();
        }

        private boolean isExpired(long now, long taskRetentionMillis) {
            return finishedAtMillis >= 0L && now - finishedAtMillis >= taskRetentionMillis;
        }
    }

    @FunctionalInterface
    private interface ImportExecution {

        ImportResult execute();
    }
}
