package com.jing.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cj
 * @date 2020/4/17 15:54
 */
@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host:127.0.0.1}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    @Bean
    public Redisson redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress(host + ":" + port).setDatabase(database);
        return (Redisson) Redisson.create(config);
    }
}
