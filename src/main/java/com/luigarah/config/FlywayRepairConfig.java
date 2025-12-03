package com.luigarah.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
                System.out.println("⚠️ Flyway validation failed. Running repair...");
                flyway.repair();
                System.out.println("✅ Flyway repair completed. Running migration...");
                flyway.migrate();
            }
        };
    }
}
