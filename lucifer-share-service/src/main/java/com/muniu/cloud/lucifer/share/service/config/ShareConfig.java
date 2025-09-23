package com.muniu.cloud.lucifer.share.service.config;

import com.muniu.cloud.lucifer.commons.utils.http.LuciferHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@Configuration
public class ShareConfig {

    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        int coreCount = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreCount * 2);
        executor.setMaxPoolSize(coreCount * 4);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("MyExecutor-");
        executor.initialize();
        return executor;
    }


    @Bean(name = "functionExecutor")
    public ExecutorService functionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE * 4);
        executor.setMaxPoolSize(CORE_POOL_SIZE * 8);
        executor.setQueueCapacity(100000);
        executor.setThreadNamePrefix("FunctionExecutor-");
        executor.initialize();
        return executor.getThreadPoolExecutor();
    }


    /**
     * 配置RedisTemplate
     * 设置序列化器，使用StringRedisSerializer作为key的序列化器
     * 使用GenericJackson2JsonRedisSerializer作为value的序列化器
     *
     * @param redisConnectionFactory Redis连接工厂
     * @return RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // 设置key的序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        // 设置value的序列化器
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }


    @Bean(name = "proxyHttpClient")
    public LuciferHttpClient proxyHttpClient() {
        return new LuciferHttpClient();
    }

    @Bean(name = "httpClient")
    public LuciferHttpClient httpClient() {
        return new LuciferHttpClient();
    }

}
