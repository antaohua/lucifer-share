package com.muniu.cloud.lucifer.share.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

}
