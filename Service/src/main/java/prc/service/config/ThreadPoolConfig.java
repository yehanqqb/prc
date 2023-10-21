package prc.service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {

    /**
     * mq start
     */
    @Bean("mqExecutor")
    public ThreadPoolTaskExecutor mqExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(300);
        taskExecutor.setMaxPoolSize(500);
        taskExecutor.setQueueCapacity(100000);
        taskExecutor.setKeepAliveSeconds(600000);
        taskExecutor.setThreadNamePrefix("mqExecutor--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(600);
        return taskExecutor;
    }

    /**
     * system start
     */
    @Bean("systemExecutor")
    public ThreadPoolTaskExecutor systemExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(300);
        taskExecutor.setMaxPoolSize(500);
        taskExecutor.setQueueCapacity(100000);
        taskExecutor.setKeepAliveSeconds(600000);
        taskExecutor.setThreadNamePrefix("systemExecutor--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(600);
        return taskExecutor;
    }

    /**
     * system start
     */
    @Bean("apiExecutor")
    public ThreadPoolTaskExecutor apiExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(300);
        taskExecutor.setMaxPoolSize(500);
        taskExecutor.setQueueCapacity(100000);
        taskExecutor.setKeepAliveSeconds(600000);
        taskExecutor.setThreadNamePrefix("apiExecutor--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(600);
        return taskExecutor;
    }
}
