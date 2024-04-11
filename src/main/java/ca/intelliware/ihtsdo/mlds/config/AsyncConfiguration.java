package ca.intelliware.ihtsdo.mlds.config;

import ca.intelliware.ihtsdo.mlds.async.ExceptionHandlingAsyncTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration extends AsyncConfigurerSupport implements EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public Executor getAsyncExecutor() {
        log.debug("Creating Async Task Executor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        Binder binder = Binder.get(environment);
        executor.setCorePoolSize(binder.bind("async.core-pool-size", Integer.class).orElse(2));
        executor.setMaxPoolSize(binder.bind("async.max-pool-size", Integer.class).orElse(50));
        executor.setQueueCapacity(binder.bind("async.queue-capacity", Integer.class).orElse(10000));
        executor.setThreadNamePrefix("mlds-Executor-");
        return new ExceptionHandlingAsyncTaskExecutor(executor);
    }

}
