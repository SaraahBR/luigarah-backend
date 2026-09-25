package com.luigarah.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Tarefas em segundo plano:
 * - traduções automáticas (@Async), numa única thread: são poucas e assim não
 *   disputam as conexões do pool com as requisições do site;
 * - aquecimento do cache do catálogo (@Scheduled, ver AquecimentoCacheCatalogo).
 */
@Configuration
@EnableAsync
@EnableScheduling
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
