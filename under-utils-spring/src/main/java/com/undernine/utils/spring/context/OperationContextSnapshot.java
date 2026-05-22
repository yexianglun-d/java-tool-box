package com.undernine.utils.spring.context;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * {@link OperationContext} 快照。
 * <p>
 * 在任务提交线程捕获当前上下文，在任务执行线程通过 {@link OperationContextHolder#scope(OperationContext)}
 * 临时切入上下文，任务结束后恢复执行线程原有上下文，避免线程池复用造成上下文泄漏。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class OperationContextSnapshot {

    private static final OperationContextSnapshot EMPTY = new OperationContextSnapshot(null);

    private final OperationContext context;

    private OperationContextSnapshot(OperationContext context) {
        this.context = context;
    }

    /**
     * 捕获当前线程上下文。
     *
     * @return 当前线程上下文快照
     */
    public static OperationContextSnapshot capture() {
        return of(OperationContextHolder.getContext());
    }

    /**
     * 基于指定上下文创建快照。
     *
     * @param context 上下文，可为 null
     * @return 上下文快照
     */
    public static OperationContextSnapshot of(OperationContext context) {
        return context == null ? EMPTY : new OperationContextSnapshot(context);
    }

    /**
     * 创建空上下文快照。
     *
     * @return 空上下文快照
     */
    public static OperationContextSnapshot empty() {
        return EMPTY;
    }

    /**
     * 获取快照内的上下文。
     *
     * @return 上下文，不存在时返回 null
     */
    public OperationContext getContext() {
        return context;
    }

    /**
     * 判断快照是否包含上下文。
     *
     * @return 包含上下文时返回 true
     */
    public boolean hasContext() {
        return context != null;
    }

    /**
     * 打开快照上下文作用域。
     *
     * @return 可关闭作用域
     */
    public OperationContextHolder.Scope openScope() {
        return OperationContextHolder.scope(context);
    }

    /**
     * 包装 Runnable 任务。
     *
     * @param task 原始任务
     * @return 带上下文传播能力的任务
     */
    public Runnable wrap(Runnable task) {
        Objects.requireNonNull(task, "task must not be null");
        return () -> {
            try (OperationContextHolder.Scope ignored = openScope()) {
                task.run();
            }
        };
    }

    /**
     * 包装 Callable 任务。
     *
     * @param task 原始任务
     * @param <T>  返回值类型
     * @return 带上下文传播能力的任务
     */
    public <T> Callable<T> wrap(Callable<T> task) {
        Objects.requireNonNull(task, "task must not be null");
        return () -> {
            try (OperationContextHolder.Scope ignored = openScope()) {
                return task.call();
            }
        };
    }

    /**
     * 包装 Supplier 任务。
     *
     * @param supplier 原始任务
     * @param <T>      返回值类型
     * @return 带上下文传播能力的任务
     */
    public <T> Supplier<T> wrapSupplier(Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "supplier must not be null");
        return () -> {
            try (OperationContextHolder.Scope ignored = openScope()) {
                return supplier.get();
            }
        };
    }
}
