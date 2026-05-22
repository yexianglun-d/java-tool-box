package com.undernine.utils.spring.context;

/**
 * 当前线程操作上下文持有器。
 * <p>
 * 使用 {@link #scope(OperationContext)} 创建 try-with-resources 作用域，作用域关闭时会恢复
 * 进入作用域前的上下文，避免线程复用时发生上下文泄漏。
 * </p>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class OperationContextHolder {

    private static final ThreadLocal<OperationContext> HOLDER = new ThreadLocal<>();

    private OperationContextHolder() {
    }

    /**
     * 获取当前线程上下文。
     *
     * @return 当前上下文，不存在时返回 null
     */
    public static OperationContext getContext() {
        return HOLDER.get();
    }

    /**
     * 获取当前线程上下文。
     *
     * @return 当前上下文，不存在时返回 null
     */
    public static OperationContext current() {
        return getContext();
    }

    /**
     * 设置当前线程上下文。
     *
     * @param context 当前上下文，可为 null
     */
    public static void setContext(OperationContext context) {
        if (context == null) {
            clear();
            return;
        }
        HOLDER.set(context);
    }

    /**
     * 清理当前线程上下文。
     */
    public static void clear() {
        HOLDER.remove();
    }

    /**
     * 创建上下文作用域。
     * <p>
     * 作用域关闭时会恢复之前的上下文；如果之前没有上下文，则清理 ThreadLocal。
     * </p>
     *
     * @param context 作用域内使用的上下文，可为 null
     * @return 可关闭作用域
     */
    public static Scope scope(OperationContext context) {
        OperationContext previous = HOLDER.get();
        setContext(context);
        return new Scope(previous);
    }

    /**
     * 当前上下文作用域。
     */
    public static final class Scope implements AutoCloseable {

        private final OperationContext previous;
        private boolean closed;

        private Scope(OperationContext previous) {
            this.previous = previous;
        }

        @Override
        public void close() {
            if (closed) {
                return;
            }
            closed = true;
            setContext(previous);
        }
    }
}
