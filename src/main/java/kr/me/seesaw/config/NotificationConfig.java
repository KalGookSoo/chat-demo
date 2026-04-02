package kr.me.seesaw.config;

import kr.me.seesaw.component.properties.ApplicationProperties;
import kr.me.seesaw.component.properties.Notification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class NotificationConfig {

    /**
     * Configure the executor for asynchronous notification processing
     */
    @Bean(name = "notificationTaskExecutor")
    public Executor notificationTaskExecutor(ApplicationProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        Notification notification = properties.getNotification();
        executor.setCorePoolSize(notification.corePoolSize());
        executor.setMaxPoolSize(notification.maxPoolSize());
        executor.setQueueCapacity(notification.queueCapacity());
        executor.setThreadNamePrefix("notification-");
        executor.initialize();
        return executor;
    }

}