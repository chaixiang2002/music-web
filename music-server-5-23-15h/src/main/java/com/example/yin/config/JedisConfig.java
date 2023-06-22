package com.example.yin.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author benjamin_5
 * @Description
 * @date 2022/12/22
 */
@Configuration
public class JedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(JedisConfig.class);

    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private Integer maxIdle;
    @Value("${spring.redis.jedis.pool.max-active}")
    private Integer maxActive;
    @Value("${spring.redis.jedis.pool.min-idle}")
    private Integer minIdle;
    @Value("${spring.redis.timeout}")
    private Integer timeout;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大空闲连接数
        jedisPoolConfig.setMaxIdle(maxIdle);
        // 最大连接数
        jedisPoolConfig.setMaxTotal(maxActive);
        // 最小空闲连接数
        jedisPoolConfig.setMinIdle(minIdle);
        // 连接空闲多久后释放,当空闲时间大于该值且空闲连接大于最大空闲连接数时直接释放连接线程
        jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(10000);
        // 连接最小空闲时间
        jedisPoolConfig.setMinEvictableIdleTimeMillis(1800000);
        // 获取连接时的最大等待毫秒数,小于零:阻塞不确定的时间,默认-1
        jedisPoolConfig.setMaxWaitMillis(1500);
        // 在获取连接的时候检查有效性, 默认false
        jedisPoolConfig.setTestOnBorrow(true);
        // 在空闲时检查有效性, 默认false
        jedisPoolConfig.setTestWhileIdle(true);
        // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        jedisPoolConfig.setBlockWhenExhausted(false);
//        if (StringUtils.isBlank(password)) {
//            password = null;
//        }
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, null);
        logger.info("redis连接成功-{}:{}", host, port);
        return jedisPool;
    }

}
