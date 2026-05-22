package com.undernine.utils.samples.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("redis")
public class SampleRedisConfiguration {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient sampleRedissonClient(
            @Value("${samples.redis.address:redis://127.0.0.1:6379}") String address,
            @Value("${samples.redis.database:0}") int database,
            @Value("${samples.redis.password:}") String password) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(database)
                .setPassword(password.isBlank() ? null : password);
        return Redisson.create(config);
    }
}
