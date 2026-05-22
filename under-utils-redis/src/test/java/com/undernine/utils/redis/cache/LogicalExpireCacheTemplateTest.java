package com.undernine.utils.redis.cache;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LogicalExpireCacheTemplateTest {

    @Test
    void getOrLoadReturnsCachedValueWhenLogicalTtlNotExpired() {
        CacheHarness harness = CacheHarness.create();
        TrackingExecutor refreshExecutor = new TrackingExecutor();
        LogicalExpireCacheTemplate template = new LogicalExpireCacheTemplate(harness.redissonClient());
        LogicalExpireCacheOptions options = options(refreshExecutor)
            .logicalTtl(Duration.ofSeconds(30))
            .build();

        String first = template.getOrLoad("hot:user:1", String.class, options, key -> "cached");
        String second = template.getOrLoad("hot:user:1", String.class, options, key -> {
            throw new AssertionError("loader should not be called on unexpired cache hit");
        });

        assertThat(first).isEqualTo("cached");
        assertThat(second).isEqualTo("cached");
        assertThat(refreshExecutor.executeCount()).hasValue(0);
    }

    @Test
    void getOrLoadReturnsExpiredValueAndRefreshesInBackground() throws Exception {
        CacheHarness harness = CacheHarness.create();
        ExecutorService refreshExecutor = Executors.newSingleThreadExecutor();
        LogicalExpireCacheTemplate template = new LogicalExpireCacheTemplate(harness.redissonClient());
        LogicalExpireCacheOptions options = options(refreshExecutor)
            .logicalTtl(Duration.ofMillis(10))
            .physicalTtl(Duration.ofSeconds(30))
            .build();
        CountDownLatch refreshStarted = new CountDownLatch(1);
        CountDownLatch allowRefreshFinish = new CountDownLatch(1);

        String oldValue = template.getOrLoad("hot:user:2", String.class, options, key -> "old");
        waitUntilLogicallyExpired();
        String returned = template.getOrLoad("hot:user:2", String.class, options, key -> {
            refreshStarted.countDown();
            assertThat(allowRefreshFinish.await(2, TimeUnit.SECONDS)).isTrue();
            return "fresh";
        });

        assertThat(oldValue).isEqualTo("old");
        assertThat(returned).isEqualTo("old");
        assertThat(refreshStarted.await(2, TimeUnit.SECONDS)).isTrue();

        allowRefreshFinish.countDown();
        refreshExecutor.shutdown();
        assertThat(refreshExecutor.awaitTermination(2, TimeUnit.SECONDS)).isTrue();

        String fresh = template.getOrLoad("hot:user:2", String.class, options, key -> {
            throw new AssertionError("fresh cache should be available after background refresh");
        });
        assertThat(fresh).isEqualTo("fresh");
    }

    @Test
    void getOrLoadSynchronouslyLoadsWhenCacheMissed() {
        CacheHarness harness = CacheHarness.create();
        TrackingExecutor refreshExecutor = new TrackingExecutor();
        LogicalExpireCacheTemplate template = new LogicalExpireCacheTemplate(harness.redissonClient());
        LogicalExpireCacheOptions options = options(refreshExecutor)
            .logicalTtl(Duration.ofSeconds(20))
            .physicalTtl(Duration.ofSeconds(60))
            .build();
        AtomicInteger loadCount = new AtomicInteger();

        String value = template.getOrLoad("hot:user:3", String.class, options, key -> {
            loadCount.incrementAndGet();
            return "fresh";
        });

        assertThat(value).isEqualTo("fresh");
        assertThat(loadCount).hasValue(1);
        assertThat(refreshExecutor.executeCount()).hasValue(0);
        assertThat(harness.currentCacheValue()).startsWith("__UNDER_UTILS_LOGICAL_CACHE_VALUE_V1__:");
        assertThat(harness.lastWrittenTtl()).isEqualTo(Duration.ofSeconds(60));
    }

    @Test
    void refreshFailureDoesNotAffectExpiredValueReturn() throws Exception {
        CacheHarness harness = CacheHarness.create();
        ExecutorService refreshExecutor = Executors.newSingleThreadExecutor();
        LogicalExpireCacheTemplate template = new LogicalExpireCacheTemplate(harness.redissonClient());
        AtomicReference<Throwable> handledFailure = new AtomicReference<>();
        CountDownLatch failureHandled = new CountDownLatch(1);
        LogicalExpireCacheOptions options = options(refreshExecutor)
            .logicalTtl(Duration.ofMillis(10))
            .refreshFailureHandler((key, cacheKey, error) -> {
                handledFailure.set(error);
                failureHandled.countDown();
            })
            .build();
        IllegalStateException failure = new IllegalStateException("source failed");

        String oldValue = template.getOrLoad("hot:user:4", String.class, options, key -> "old");
        waitUntilLogicallyExpired();
        String returned = template.getOrLoad("hot:user:4", String.class, options, key -> {
            throw failure;
        });

        assertThat(oldValue).isEqualTo("old");
        assertThat(returned).isEqualTo("old");
        assertThat(failureHandled.await(2, TimeUnit.SECONDS)).isTrue();
        assertThat(handledFailure.get()).isSameAs(failure);

        refreshExecutor.shutdown();
        assertThat(refreshExecutor.awaitTermination(2, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    void concurrentExpiredHitsOnlyTriggerOneRefreshLoad() throws Exception {
        CacheHarness harness = CacheHarness.create();
        ExecutorService refreshExecutor = Executors.newFixedThreadPool(8);
        LogicalExpireCacheTemplate template = new LogicalExpireCacheTemplate(harness.redissonClient());
        LogicalExpireCacheOptions options = options(refreshExecutor)
            .logicalTtl(Duration.ofMillis(10))
            .lockWaitTime(Duration.ofSeconds(1))
            .build();
        AtomicInteger refreshLoadCount = new AtomicInteger();
        CountDownLatch refreshStarted = new CountDownLatch(1);
        int threads = 8;
        ExecutorService callers = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<String>> futures = new ArrayList<>();

        String oldValue = template.getOrLoad("hot:user:5", String.class, options, key -> "old");
        waitUntilLogicallyExpired();
        for (int i = 0; i < threads; i++) {
            futures.add(callers.submit(() -> {
                start.await();
                return template.getOrLoad("hot:user:5", String.class, options, key -> {
                    refreshLoadCount.incrementAndGet();
                    refreshStarted.countDown();
                    Thread.sleep(80L);
                    return "fresh";
                });
            }));
        }

        start.countDown();
        for (Future<String> future : futures) {
            assertThat(future.get(3, TimeUnit.SECONDS)).isEqualTo("old");
        }
        callers.shutdownNow();

        assertThat(oldValue).isEqualTo("old");
        assertThat(refreshStarted.await(2, TimeUnit.SECONDS)).isTrue();
        refreshExecutor.shutdown();
        assertThat(refreshExecutor.awaitTermination(3, TimeUnit.SECONDS)).isTrue();
        assertThat(refreshLoadCount).hasValue(1);
    }

    private static LogicalExpireCacheOptions.Builder options(Executor refreshExecutor) {
        return LogicalExpireCacheOptions.builder()
            .keyPrefix("logical-cache:")
            .rebuildLockKeyPrefix("logical-cache:rebuild-lock:")
            .physicalTtl(Duration.ofSeconds(30))
            .lockWaitTime(Duration.ofSeconds(1))
            .lockLeaseTime(Duration.ofSeconds(10))
            .refreshExecutor(refreshExecutor);
    }

    private static void waitUntilLogicallyExpired() throws InterruptedException {
        Thread.sleep(30L);
    }

    private static final class TrackingExecutor implements Executor {

        private final AtomicInteger executeCount = new AtomicInteger();

        @Override
        public void execute(Runnable command) {
            executeCount.incrementAndGet();
            command.run();
        }

        AtomicInteger executeCount() {
            return executeCount;
        }
    }

    private record CacheHarness(
        RedissonClient redissonClient,
        RBucket<String> bucket,
        AtomicReference<String> cacheValue,
        AtomicReference<Duration> lastTtl
    ) {

        static CacheHarness create() {
            RedissonClient redissonClient = mock(RedissonClient.class);
            @SuppressWarnings("unchecked")
            RBucket<String> bucket = mock(RBucket.class);
            RLock lock = mock(RLock.class);
            AtomicReference<String> cacheValue = new AtomicReference<>();
            AtomicReference<Duration> lastTtl = new AtomicReference<>();

            when(redissonClient.<String>getBucket(anyString())).thenReturn(bucket);
            when(redissonClient.getLock(anyString())).thenReturn(lock);
            when(bucket.get()).thenAnswer(invocation -> cacheValue.get());
            doAnswer(invocation -> {
                cacheValue.set(invocation.getArgument(0));
                lastTtl.set(invocation.getArgument(1));
                return null;
            }).when(bucket).set(anyString(), any(Duration.class));

            configureLock(lock);
            return new CacheHarness(redissonClient, bucket, cacheValue, lastTtl);
        }

        String currentCacheValue() {
            return cacheValue.get();
        }

        Duration lastWrittenTtl() {
            return lastTtl.get();
        }

        private static void configureLock(RLock lock) {
            ReentrantLock delegate = new ReentrantLock();
            try {
                when(lock.tryLock(anyLong(), anyLong(), eq(TimeUnit.MILLISECONDS))).thenAnswer(invocation -> {
                    long waitMillis = invocation.getArgument(0);
                    return delegate.tryLock(waitMillis, TimeUnit.MILLISECONDS);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            when(lock.isHeldByCurrentThread()).thenAnswer(invocation -> delegate.isHeldByCurrentThread());
            doAnswer(invocation -> {
                delegate.unlock();
                return null;
            }).when(lock).unlock();
        }
    }
}
