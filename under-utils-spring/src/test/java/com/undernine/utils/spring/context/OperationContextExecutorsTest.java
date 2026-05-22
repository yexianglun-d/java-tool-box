package com.undernine.utils.spring.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class OperationContextExecutorsTest {

    private ExecutorService rawExecutor;
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        rawExecutor = Executors.newSingleThreadExecutor();
        executor = OperationContextExecutors.wrap(rawExecutor);
    }

    @AfterEach
    void tearDown() {
        OperationContextHolder.clear();
        rawExecutor.shutdownNow();
    }

    @Test
    void executePropagatesRunnableContextAndCleansWorkerThread() throws Exception {
        OperationContext context = context("runnable");
        AtomicReference<OperationContext> seenContext = new AtomicReference<>();
        CountDownLatch completed = new CountDownLatch(1);

        try (OperationContextHolder.Scope ignored = OperationContextHolder.scope(context)) {
            executor.execute(() -> {
                seenContext.set(OperationContextHolder.getContext());
                completed.countDown();
            });
        }

        assertThat(completed.await(2, TimeUnit.SECONDS)).isTrue();
        assertThat(seenContext.get()).isSameAs(context);
        assertWorkerContextIsNull();
    }

    @Test
    void submitPropagatesCallableContext() throws Exception {
        OperationContext context = context("callable");
        Future<String> future;

        try (OperationContextHolder.Scope ignored = OperationContextHolder.scope(context)) {
            future = executor.submit(() -> OperationContextHolder.getContext().getTraceId());
        }

        assertThat(future.get()).isEqualTo("callable");
        assertWorkerContextIsNull();
    }

    @Test
    void clearsWorkerThreadWhenTaskMutatesContext() throws Exception {
        OperationContext context = context("request");
        OperationContext leaked = context("leaked");

        try (OperationContextHolder.Scope ignored = OperationContextHolder.scope(context)) {
            executor.submit(() -> {
                assertThat(OperationContextHolder.getContext()).isSameAs(context);
                OperationContextHolder.setContext(leaked);
            }).get();
        }

        assertWorkerContextIsNull();
    }

    @Test
    void restoresWorkerContextAfterNestedContextExecution() throws Exception {
        OperationContext workerPrevious = context("worker-previous");
        OperationContext captured = context("captured");

        rawExecutor.submit(() -> OperationContextHolder.setContext(workerPrevious)).get();
        try {
            Future<OperationContext> future;
            try (OperationContextHolder.Scope ignored = OperationContextHolder.scope(captured)) {
                future = executor.submit(OperationContextHolder::getContext);
            }

            assertThat(future.get()).isSameAs(captured);
            assertThat(rawExecutor.submit(OperationContextHolder::getContext).get())
                    .isSameAs(workerPrevious);
        } finally {
            rawExecutor.submit(OperationContextHolder::clear).get();
        }
    }

    @Test
    void emptySubmissionContextDoesNotPolluteWorkerThread() throws Exception {
        OperationContextHolder.clear();

        assertThat(executor.submit(OperationContextHolder::getContext).get()).isNull();
        assertWorkerContextIsNull();
    }

    @Test
    void emptySubmissionContextDoesNotExposeExistingWorkerContext() throws Exception {
        OperationContext workerPrevious = context("worker-previous");

        rawExecutor.submit(() -> OperationContextHolder.setContext(workerPrevious)).get();
        try {
            OperationContextHolder.clear();

            assertThat(executor.submit(OperationContextHolder::getContext).get()).isNull();
            assertThat(rawExecutor.submit(OperationContextHolder::getContext).get())
                    .isSameAs(workerPrevious);
        } finally {
            rawExecutor.submit(OperationContextHolder::clear).get();
        }
    }

    private void assertWorkerContextIsNull() throws Exception {
        assertThat(rawExecutor.submit(OperationContextHolder::getContext).get()).isNull();
    }

    private OperationContext context(String traceId) {
        return OperationContext.builder().traceId(traceId).build();
    }
}
