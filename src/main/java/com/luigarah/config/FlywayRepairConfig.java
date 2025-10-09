package com.luigarah.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod") // só vale no profile prod
public class FlywayRepairConfig {

    @Value("${app.flyway.repair:false}")
    private boolean doRepair;

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return (Flyway flyway) -> {
            if (doRepair) {
                // remove entradas half-applied e corrige checksums
                flyway.repair();
            }
            flyway.migrate();
        };
    }
}
