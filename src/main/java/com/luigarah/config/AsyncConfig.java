package com.luigarah.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Execução em segundo plano das traduções automáticas.
 * Uma única thread: as traduções são poucas e assim não disputam as
 * conexões do pool com as requisições do site.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "traducaoExecutor")
    public Executor traducaoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("traducao-");
        executor.initialize();
        return executor;
    }
}
