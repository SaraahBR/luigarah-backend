package com.luigarah.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Carrega variáveis do .env ANTES da inicialização do Spring (somente profile=local).
 *
 * Registrado em META-INF/spring.factories. As variáveis entram como a fonte de
 * propriedades de maior prioridade, então placeholders como ${DB_URL} em
 * application.properties são resolvidos com os valores do .env.
 *
 * Variáveis esperadas no .env (ver README):
 *   DB_URL, DB_USERNAME, DB_PASSWORD  -> conexão com o Supabase (PostgreSQL)
 *   JWT_SECRET, BREVO_*, etc.         -> demais configurações, se necessário
 *
 * Em produção (Render) o .env não é usado: as variáveis vêm do ambiente.
 */
@Slf4j
public class DotEnvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();

        boolean isLocal = false;
        for (String p : env.getActiveProfiles()) {
            if ("local".equals(p)) { isLocal = true; break; }
        }
        if (!isLocal) {
            log.debug("⏭️ Profile não é 'local', pulando carregamento do .env");
            return;
        }

        Path envFile = Paths.get(".env");
        if (!Files.exists(envFile)) {
            log.warn("⚠️ Arquivo .env não encontrado. Defina DB_URL, DB_USERNAME e DB_PASSWORD.");
            return;
        }

        try {
            Map<String, Object> vars = loadEnvFile(envFile);
            if (!vars.isEmpty()) {
                env.getPropertySources().addFirst(new MapPropertySource("dotenv", vars));
                log.info("✅ Arquivo .env carregado com {} variáveis", vars.size());
            }
        } catch (IOException e) {
            log.error("❌ Erro ao carregar .env: {}", e.getMessage());
        }
    }

    private Map<String, Object> loadEnvFile(Path envFile) throws IOException {
        Map<String, Object> m = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(envFile.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String key = line.substring(0, idx).trim();
                    String val = line.substring(idx + 1).trim();
                    if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                        val = val.substring(1, val.length() - 1);
                    }
                    m.put(key, val);
                }
            }
        }
        return m;
    }
}
