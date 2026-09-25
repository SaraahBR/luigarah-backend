package com.luigarah.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("prod") // só vale no profile prod
public class FlywayRepairConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return (Flyway flyway) -> {
            try {
                // Tenta validar primeiro
                flyway.validate();
                // Se validou, faz migrate normalmente
                flyway.migrate();
            } catch (Exception e) {
                // Se falhou a validação, faz repair automático
                log.warn("⚠️ Flyway validation failed. Running repair...");
                flyway.repair();
                log.info("✅ Flyway repair completed. Running migration...");
                flyway.migrate();
            }
        };
    }
}
