package com.jing.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Admin
 * @title: RedisConfig
 * @projectName demo
 * @description: TODO
 * @date 2020/2/28 14:22
 */
@Configuration
public class RedissonConfig {
    /**
     * 读取配置文件中的redis的ip地址
     */
    @Value("${spring.redis.host:127.0.0.1}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    @Bean
    public RedissonClient getRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        return Redisson.create();
    }

}
