package com.remember5.es.es7.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步配置
 * 配置ES同步的线程池
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * ES同步任务线程池
     * 专门用于处理ES数据同步的异步任务
     */
    @Bean("esSyncTaskExecutor")
    public Executor esSyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：处理ES同步的基础线程数
        executor.setCorePoolSize(5);

        // 最大线程数：高峰期可以创建的线程数
        executor.setMaxPoolSize(20);

        // 队列容量：等待执行的任务队列大小
        executor.setQueueCapacity(100);

        // 线程名前缀：便于日志追踪
        executor.setThreadNamePrefix("es-sync-");

        // 线程空闲时间：超过核心线程数的线程空闲多久后回收
        executor.setKeepAliveSeconds(60);

        // 拒绝策略：队列满且线程池满时的处理策略
        // CallerRunsPolicy：由调用线程执行任务，这样可以降低提交速度
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 等待时间
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }

    /**
     * 通用异步任务线程池
     * 用于其他异步任务
     */
    @Bean("commonTaskExecutor")
    public Executor commonTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("common-");
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
