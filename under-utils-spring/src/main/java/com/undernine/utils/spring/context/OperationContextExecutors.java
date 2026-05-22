package com.undernine.utils.spring.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 支持 {@link OperationContext} 传播的执行器包装工具。
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class OperationContextExecutors {

    private OperationContextExecutors() {
    }

    /**
     * 包装普通 Executor，使每次 execute 时捕获当前线程上下文。
     *
     * @param executor 原始执行器
     * @return 支持上下文传播的执行器
     */
    public static Executor wrap(Executor executor) {
        Objects.requireNonNull(executor, "executor must not be null");
        if (executor instanceof ContextAwareExecutor || executor instanceof ContextAwareExecutorService) {
            return executor;
        }
        if (executor instanceof ExecutorService executorService) {
            return wrap(executorService);
        }
        return new ContextAwareExecutor(executor);
    }

    /**
     * 包装 ExecutorService，使提交的任务自动传播当前线程上下文。
     *
     * @param executorService 原始线程池
     * @return 支持上下文传播的线程池
     */
    public static ExecutorService wrap(ExecutorService executorService) {
        Objects.requireNonNull(executorService, "executorService must not be null");
        if (executorService instanceof ContextAwareExecutorService) {
            return executorService;
        }
        return new ContextAwareExecutorService(executorService);
    }

    private static Runnable wrapRunnable(Runnable task) {
        Objects.requireNonNull(task, "task must not be null");
        return OperationContextSnapshot.capture().wrap(task);
    }

    private static <T> Callable<T> wrapCallable(Callable<T> task) {
        Objects.requireNonNull(task, "task must not be null");
        return OperationContextSnapshot.capture().wrap(task);
    }

    private static <T> Collection<Callable<T>> wrapCallables(Collection<? extends Callable<T>> tasks) {
        Objects.requireNonNull(tasks, "tasks must not be null");
        OperationContextSnapshot snapshot = OperationContextSnapshot.capture();
        List<Callable<T>> wrappedTasks = new ArrayList<>(tasks.size());
        for (Callable<T> task : tasks) {
            wrappedTasks.add(snapshot.wrap(Objects.requireNonNull(task, "task must not be null")));
        }
        return wrappedTasks;
    }

    private static final class ContextAwareExecutor implements Executor {

        private final Executor delegate;

        private ContextAwareExecutor(Executor delegate) {
            this.delegate = delegate;
        }

        @Override
        public void execute(Runnable command) {
            delegate.execute(wrapRunnable(command));
        }
    }

    private static final class ContextAwareExecutorService extends AbstractExecutorService {

        private final ExecutorService delegate;

        private ContextAwareExecutorService(ExecutorService delegate) {
            this.delegate = delegate;
        }

        @Override
        public void shutdown() {
            delegate.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            return delegate.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return delegate.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return delegate.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return delegate.awaitTermination(timeout, unit);
        }

        @Override
        public void execute(Runnable command) {
            delegate.execute(wrapRunnable(command));
        }

        @Override
        public Future<?> submit(Runnable task) {
            return delegate.submit(wrapRunnable(task));
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return delegate.submit(wrapRunnable(task), result);
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return delegate.submit(wrapCallable(task));
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return delegate.invokeAll(wrapCallables(tasks));
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
                throws InterruptedException {
            return delegate.invokeAll(wrapCallables(tasks), timeout, unit);
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException,
                java.util.concurrent.ExecutionException {
            return delegate.invokeAny(wrapCallables(tasks));
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
                throws InterruptedException, java.util.concurrent.ExecutionException,
                java.util.concurrent.TimeoutException {
            return delegate.invokeAny(wrapCallables(tasks), timeout, unit);
        }
    }
}
