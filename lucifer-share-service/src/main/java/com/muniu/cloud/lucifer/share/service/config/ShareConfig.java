package com.muniu.cloud.lucifer.share.service.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.core.http.LuciferAutoProxyHttpClient;
import com.muniu.cloud.lucifer.commons.core.http.LuciferHttpClient;
import com.muniu.cloud.lucifer.commons.core.http.LuciferProxy;
import com.muniu.cloud.lucifer.commons.utils.constants.DateConstant;
import com.muniu.cloud.lucifer.commons.utils.exception.HttpClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.Proxy;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@Slf4j
@Configuration
@EnableScheduling
public class ShareConfig {

    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();


    @Value("${siyetian.proxy.auth-key}")
    private String authKey;

    @Value("${siyetian.proxy.password}")
    private String password;

    @Value("${siyetian.proxy.url}")
    private String proxyUrl;

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


    @Bean(name = "httpClient")
    public LuciferHttpClient httpClient() {
        return new LuciferHttpClient();
    }

    @Bean(name = "autoProxyHttpClient")
    public LuciferHttpClient autoProxyHttpClient() {
        return new LuciferAutoProxyHttpClient(client -> {
            String json = httpClient().get(proxyUrl);
            JSONObject object = JSON.parseObject(json);
            if (object.getInteger("code") != 1 || object.getJSONArray("data") == null || object.getJSONArray("data").isEmpty()) {
                log.error("load proxy config error . jsonString = {}", json);
                return new LuciferProxy(Proxy.Type.HTTP, "127.0.0.1", 80);
            }
            JSONArray data = object.getJSONArray("data");
            if (data.isEmpty()) {
                log.error("load proxy config error . jsonString = {}", json);
                return new LuciferProxy(Proxy.Type.HTTP, "127.0.0.1", 80);
            }
            JSONObject obj = data.getJSONObject(0);
            LocalDateTime localDateTime = LocalDateTime.parse(obj.getString("end_time"), DateConstant.DATE_FORMATTER_YYYY_MM_DD_HH_MM_SS);
            long timestamp = localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
            log.info("load proxy config success . ip = {} , port = {} , timestamp = {} , t={}", obj.getString("ip"), obj.getInteger("port"), timestamp - 10000, timestamp);
            return new LuciferProxy(Proxy.Type.HTTP, obj.getString("ip"), obj.getInteger("port"), authKey, password, timestamp - 10000);
        });
    }

}
