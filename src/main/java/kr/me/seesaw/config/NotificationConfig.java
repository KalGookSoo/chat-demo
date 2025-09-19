package kr.me.seesaw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class NotificationConfig {

    @Value("${notification.async.core-pool-size:5}")
    private int corePoolSize;

    @Value("${notification.async.max-pool-size:10}")
    private int maxPoolSize;

    @Value("${notification.async.queue-capacity:25}")
    private int queueCapacity;

    /**
     * Configure the executor for asynchronous notification processing
     */
    @Bean(name = "notificationTaskExecutor")
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("notification-");
        executor.initialize();
        return executor;
    }

}