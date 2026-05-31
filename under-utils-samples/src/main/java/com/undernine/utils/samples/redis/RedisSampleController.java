package com.undernine.utils.samples.redis;

import com.undernine.utils.redis.cache.CacheAsideTemplate;
import com.undernine.utils.redis.cache.LogicalExpireCacheTemplate;
import com.undernine.utils.redis.lock.DistributedLockTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/samples/redis")
public class RedisSampleController {

    private final ObjectProvider<DistributedLockTemplate> lockTemplate;
    private final ObjectProvider<CacheAsideTemplate> cacheAsideTemplate;
    private final ObjectProvider<LogicalExpireCacheTemplate> logicalCacheTemplate;

    public RedisSampleController(ObjectProvider<DistributedLockTemplate> lockTemplate,
                                 ObjectProvider<CacheAsideTemplate> cacheAsideTemplate,
                                 ObjectProvider<LogicalExpireCacheTemplate> logicalCacheTemplate) {
        this.lockTemplate = lockTemplate;
        this.cacheAsideTemplate = cacheAsideTemplate;
        this.logicalCacheTemplate = logicalCacheTemplate;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "distributedLock", lockTemplate.getIfAvailable() != null,
                "cacheAside", cacheAsideTemplate.getIfAvailable() != null,
                "logicalCache", logicalCacheTemplate.getIfAvailable() != null
        );
    }

    @GetMapping("/lock")
    public Map<String, Object> lock() {
        DistributedLockTemplate template = lockTemplate.getIfAvailable();
        if (template == null) {
            return missingRedis("DistributedLockTemplate");
        }
        String value = template.execute("sample:lock", 1, 10, TimeUnit.SECONDS, () -> "locked-" + Instant.now());
        return Map.of("available", true, "value", value);
    }

    @GetMapping("/cache-aside")
    public Map<String, Object> cacheAside() {
        CacheAsideTemplate template = cacheAsideTemplate.getIfAvailable();
        if (template == null) {
            return missingRedis("CacheAsideTemplate");
        }
        ProductView view = template.get("sample:product:1001", ProductView.class,
                key -> new ProductView("1001", "Sample product", Instant.now().toString()));
        return Map.of("available", true, "value", view);
    }

    @GetMapping("/cache-aside/fluent")
    public Map<String, Object> cacheAsideFluent() {
        CacheAsideTemplate template = cacheAsideTemplate.getIfAvailable();
        if (template == null) {
            return missingRedis("CacheAsideTemplate");
        }
        ProductView view = template.cache("sample:product:fluent:1001", ProductView.class)
                .ttl(Duration.ofMinutes(2))
                .nullValueCacheEnabled(true)
                .rebuildLockEnabled(true)
                .getOrLoad(key -> new ProductView("1001", "Sample product fluent", Instant.now().toString()));
        return Map.of("available", true, "value", view, "metrics", template.getMetrics());
    }

    @GetMapping("/logical-cache")
    public Map<String, Object> logicalCache() {
        LogicalExpireCacheTemplate template = logicalCacheTemplate.getIfAvailable();
        if (template == null) {
            return missingRedis("LogicalExpireCacheTemplate");
        }
        ProductView view = template.get("sample:hot-product:1001", ProductView.class,
                key -> new ProductView("1001", "Hot product", Instant.now().toString()));
        return Map.of("available", true, "value", view);
    }

    @GetMapping("/cache-metrics")
    public Map<String, Object> cacheMetrics() {
        Map<String, Object> result = new LinkedHashMap<>();
        CacheAsideTemplate cacheAside = cacheAsideTemplate.getIfAvailable();
        LogicalExpireCacheTemplate logicalCache = logicalCacheTemplate.getIfAvailable();
        result.put("cacheAsideAvailable", cacheAside != null);
        result.put("logicalCacheAvailable", logicalCache != null);
        if (cacheAside != null) {
            result.put("cacheAside", cacheAside.getMetrics());
        }
        if (logicalCache != null) {
            result.put("logicalCache", logicalCache.getMetrics());
        }
        return result;
    }

    private static Map<String, Object> missingRedis(String beanName) {
        return Map.of(
                "available", false,
                "bean", beanName,
                "reason", "No RedissonClient bean. Configure Redis and enable the related under.utils.redis feature."
        );
    }

    public record ProductView(String id, String name, String loadedAt) {
    }
}
