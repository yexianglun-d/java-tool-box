package com.undernine.utils.test.redis;

import com.undernine.utils.redis.cache.CacheAsideTemplate;
import com.undernine.utils.redis.cache.CacheOptions;
import com.undernine.utils.redis.cache.LogicalExpireCacheOptions;
import com.undernine.utils.redis.cache.LogicalExpireCacheTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Testcontainers
class RedisCacheTemplateIntegrationTest {

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    private static RedissonClient redissonClient;

    @BeforeAll
    static void createRedissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + REDIS.getHost() + ":" + REDIS.getMappedPort(6379));
        redissonClient = Redisson.create(config);
    }

    @AfterAll
    static void shutdownRedissonClient() {
        if (redissonClient != null) {
            redissonClient.shutdown();
        }
    }

    @BeforeEach
    void cleanRedis() {
        redissonClient.getKeys().flushdb();
    }

    @Test
    void cacheAsideUsesRedisValueOnSecondRead() {
        CacheAsideTemplate template = new CacheAsideTemplate(redissonClient);
        CacheOptions options = CacheOptions.builder()
                .keyPrefix("it:cache:")
                .ttl(Duration.ofSeconds(30))
                .rebuildLockEnabled(true)
                .lockWaitTime(Duration.ofSeconds(1))
                .lockLeaseTime(Duration.ofSeconds(5))
                .build();
        AtomicInteger loadCount = new AtomicInteger();

        String first = template.getOrLoad("user:1", String.class, options, key -> {
            loadCount.incrementAndGet();
            return "Alice";
        });
        String second = template.getOrLoad("user:1", String.class, options, key -> {
            loadCount.incrementAndGet();
            return "Bob";
        });

        assertThat(first).isEqualTo("Alice");
        assertThat(second).isEqualTo("Alice");
        assertThat(loadCount).hasValue(1);
        assertThat(redissonClient.getBucket("it:cache:user:1").get()).isNotNull();
    }

    @Test
    void cacheAsideCachesNullPlaceholder() {
        CacheAsideTemplate template = new CacheAsideTemplate(redissonClient);
        CacheOptions options = CacheOptions.builder()
                .keyPrefix("it:null-cache:")
                .ttl(Duration.ofSeconds(30))
                .nullTtl(Duration.ofSeconds(5))
                .cacheNull(true)
                .rebuildLockEnabled(false)
                .build();
        AtomicInteger loadCount = new AtomicInteger();

        String first = template.getOrLoad("missing-user", String.class, options, key -> {
            loadCount.incrementAndGet();
            return null;
        });
        String second = template.getOrLoad("missing-user", String.class, options, key -> {
            loadCount.incrementAndGet();
            return "loaded-after-null";
        });

        assertThat(first).isNull();
        assertThat(second).isNull();
        assertThat(loadCount).hasValue(1);
        assertThat(redissonClient.getBucket("it:null-cache:missing-user").get()).isNotNull();
    }

    @Test
    void logicalExpireCacheReturnsStaleValueAndRefreshesInBackground() throws Exception {
        ExecutorService refreshExecutor = Executors.newSingleThreadExecutor();
        try {
            LogicalExpireCacheTemplate template = new LogicalExpireCacheTemplate(redissonClient);
            LogicalExpireCacheOptions options = LogicalExpireCacheOptions.builder()
                    .keyPrefix("it:logical-cache:")
                    .rebuildLockKeyPrefix("it:logical-cache:lock:")
                    .logicalTtl(Duration.ofMillis(300))
                    .physicalTtl(Duration.ofSeconds(10))
                    .lockWaitTime(Duration.ofSeconds(1))
                    .lockLeaseTime(Duration.ofSeconds(5))
                    .refreshExecutor(refreshExecutor)
                    .build();
            CountDownLatch refreshStarted = new CountDownLatch(1);

            String first = template.getOrLoad("product:1", String.class, options, key -> "old");
            Thread.sleep(350L);
            String stale = template.getOrLoad("product:1", String.class, options, key -> {
                refreshStarted.countDown();
                return "fresh";
            });

            assertThat(first).isEqualTo("old");
            assertThat(stale).isEqualTo("old");
            assertThat(refreshStarted.await(2, TimeUnit.SECONDS)).isTrue();
            assertThat(waitUntilFresh(template, options)).isEqualTo("fresh");
        } finally {
            refreshExecutor.shutdownNow();
        }
    }

    private static String waitUntilFresh(LogicalExpireCacheTemplate template, LogicalExpireCacheOptions options)
            throws Exception {
        for (int i = 0; i < 30; i++) {
            String value = template.getOrLoad("product:1", String.class, options, key -> "unexpected");
            if ("fresh".equals(value)) {
                return value;
            }
            Thread.sleep(20L);
        }
        fail("logical cache value was not refreshed in time");
        return null;
    }
}
