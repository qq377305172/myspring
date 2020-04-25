package com.jing.config;

import com.example.demo.util.RedisUtil;
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
public class RedisConfig {
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
    public RedisUtil getRedisUtil() {
        final String DISABLED_STR = "disabled";
        if (DISABLED_STR.equals(host)) {
            return null;
        }
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.initPool(host, port, database);
        return redisUtil;
    }

}
