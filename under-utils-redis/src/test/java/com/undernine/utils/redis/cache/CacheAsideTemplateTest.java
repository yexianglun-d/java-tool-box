package com.undernine.utils.redis.cache;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CacheAsideTemplateTest {

    @Test
    void getOrLoadReturnsCachedValueWithoutLoading() {
        CacheHarness harness = CacheHarness.create();
        CacheAsideTemplate template = new CacheAsideTemplate(harness.redissonClient());
        CacheOptions options = optionsWithoutLock().build();

        String first = template.getOrLoad("user:1", String.class, options, key -> "loaded");
        String second = template.getOrLoad("user:1", String.class, options, key -> {
            throw new AssertionError("loader should not be called on cache hit");
        });

        assertThat(first).isEqualTo("loaded");
        assertThat(second).isEqualTo("loaded");
    }

    @Test
    void getOrLoadLoadsAndCachesWhenMissed() {
        CacheHarness harness = CacheHarness.create();
        CacheAsideTemplate template = new CacheAsideTemplate(harness.redissonClient());
        CacheOptions options = optionsWithoutLock()
            .ttl(Duration.ofSeconds(20))
            .build();

        AtomicInteger loadCount = new AtomicInteger();
        String value = template.getOrLoad("user:2", String.class, options, key -> {
            loadCount.incrementAndGet();
            return "fresh";
        });

        assertThat(value).isEqualTo("fresh");
        assertThat(loadCount).hasValue(1);
        assertThat(harness.currentCacheValue()).isNotBlank();
        assertThat(harness.lastWrittenTtl()).isEqualTo(Duration.ofSeconds(20));
    }

    @Test
    void getOrLoadCachesNullValueWithNullTtlAndJitter() {
        CacheHarness harness = CacheHarness.create();
        CacheAsideTemplate template = new CacheAsideTemplate(harness.redissonClient());
        CacheOptions options = optionsWithoutLock()
            .cacheNull(true)
            .nullTtl(Duration.ofSeconds(3))
            .jitter(Duration.ofSeconds(2))
            .build();

        AtomicInteger loadCount = new AtomicInteger();
        String first = template.getOrLoad("missing:user", String.class, options, key -> {
            loadCount.incrementAndGet();
            return null;
        });
        String second = template.getOrLoad("missing:user", String.class, options, key -> {
            loadCount.incrementAndGet();
            return "should-not-load";
        });

        assertThat(first).isNull();
        assertThat(second).isNull();
        assertThat(loadCount).hasValue(1);
        assertThat(harness.lastWrittenTtl()).isBetween(Duration.ofSeconds(3), Duration.ofSeconds(5));
    }

    @Test
    void getOrLoadAppliesTtlJitterForNormalValue() {
        CacheHarness harness = CacheHarness.create();
        CacheAsideTemplate template = new CacheAsideTemplate(harness.redissonClient());
        CacheOptions options = optionsWithoutLock()
            .ttl(Duration.ofSeconds(10))
            .jitter(Duration.ofSeconds(5))
            .build();

        String value = template.getOrLoad("user:3", String.class, options, key -> "fresh");

        assertThat(value).isEqualTo("fresh");
        assertThat(harness.lastWrittenTtl()).isBetween(Duration.ofSeconds(10), Duration.ofSeconds(15));
    }

    @Test
    void getOrLoadPropagatesLoadingException() {
        CacheHarness harness = CacheHarness.create();
        CacheAsideTemplate template = new CacheAsideTemplate(harness.redissonClient());
        CacheOptions options = optionsWithoutLock().build();
        IllegalStateException failure = new IllegalStateException("source failed");

        assertThatThrownBy(() -> template.getOrLoad("user:4", String.class, options, key -> {
            throw failure;
        })).isSameAs(failure);

        verify(harness.bucket(), never()).set(anyString(), any(Duration.class));
    }

    @Test
    void getOrLoadUsesRebuildLockToPreventStampede() throws Exception {
        CacheHarness harness = CacheHarness.create();
        CacheAsideTemplate template = new CacheAsideTemplate(harness.redissonClient());
        CacheOptions options = CacheOptions.builder()
            .keyPrefix("cache:")
            .ttl(Duration.ofSeconds(30))
            .lockWaitTime(Duration.ofSeconds(2))
            .lockLeaseTime(Duration.ofSeconds(10))
            .build();

        AtomicInteger loadCount = new AtomicInteger();
        int threads = 8;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            futures.add(executor.submit(() -> {
                start.await();
                return template.getOrLoad("hot:user", String.class, options, key -> {
                    loadCount.incrementAndGet();
                    Thread.sleep(80L);
                    return "rebuilt";
                });
            }));
        }

        start.countDown();
        for (Future<String> future : futures) {
            assertThat(future.get(3, TimeUnit.SECONDS)).isEqualTo("rebuilt");
        }
        executor.shutdownNow();

        assertThat(loadCount).hasValue(1);
    }

    @Test
    void getOrLoadPublishesObserverEvents() {
        CacheHarness harness = CacheHarness.create();
        RecordingObserver observer = new RecordingObserver();
        CacheAsideTemplate template = new CacheAsideTemplate(
                harness.redissonClient(),
                new JacksonCacheValueCodec(),
                optionsWithoutLock().build(),
                observer
        );

        String first = template.getOrLoad("user:observer", String.class, key -> "fresh");
        String second = template.getOrLoad("user:observer", String.class, key -> "should-not-load");

        assertThat(first).isEqualTo("fresh");
        assertThat(second).isEqualTo("fresh");
        assertThat(observer.events()).contains("miss:user:observer:false",
                "loadSuccess:user:observer:false",
                "write:user:observer:false",
                "hit:user:observer:false");
    }

    @Test
    void getMetricsTracksCacheAsideEventsWithoutCustomObserver() {
        CacheHarness harness = CacheHarness.create();
        CacheAsideTemplate template = new CacheAsideTemplate(harness.redissonClient());
        CacheOptions options = optionsWithoutLock().build();

        String first = template.getOrLoad("user:metrics", String.class, options, key -> "fresh");
        String second = template.getOrLoad("user:metrics", String.class, options, key -> "should-not-load");

        CacheMetrics metrics = template.getMetrics();
        assertThat(first).isEqualTo("fresh");
        assertThat(second).isEqualTo("fresh");
        assertThat(metrics.getHitCount()).isEqualTo(1L);
        assertThat(metrics.getMissCount()).isEqualTo(1L);
        assertThat(metrics.getHitRate()).isEqualTo(0.5D);
        assertThat(metrics.getLoadSuccessCount()).isEqualTo(1L);
        assertThat(metrics.getLoadFailureCount()).isZero();
        assertThat(metrics.getWriteCount()).isEqualTo(1L);

        template.resetMetrics();
        assertThat(template.getMetrics().getLookupCount()).isZero();
    }

    @Test
    void fluentCacheQueryAppliesInlineOptions() {
        CacheHarness harness = CacheHarness.create();
        CacheAsideTemplate template = new CacheAsideTemplate(harness.redissonClient());

        String value = template.cache("user:fluent", String.class)
                .keyPrefix("chain:")
                .ttl(Duration.ofSeconds(9))
                .cacheNull(false)
                .rebuildLockEnabled(false)
                .getOrLoad(key -> "fresh");

        assertThat(value).isEqualTo("fresh");
        assertThat(harness.lastWrittenTtl()).isEqualTo(Duration.ofSeconds(9));
        verify(harness.redissonClient()).getBucket("chain:user:fluent");
    }

    private static CacheOptions.Builder optionsWithoutLock() {
        return CacheOptions.builder()
            .keyPrefix("cache:")
            .rebuildLockEnabled(false);
    }

    private static final class RecordingObserver implements CacheOperationObserver {

        private final List<String> events = new ArrayList<>();

        @Override
        public void onHit(CacheOperationEvent event) {
            events.add("hit:" + event.getKey() + ":" + event.isNullValue());
        }

        @Override
        public void onMiss(CacheOperationEvent event) {
            events.add("miss:" + event.getKey() + ":" + event.isNullValue());
        }

        @Override
        public void onLoadSuccess(CacheOperationEvent event) {
            events.add("loadSuccess:" + event.getKey() + ":" + event.isNullValue());
        }

        @Override
        public void onWrite(CacheOperationEvent event) {
            events.add("write:" + event.getKey() + ":" + event.isNullValue());
        }

        List<String> events() {
            return events;
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
