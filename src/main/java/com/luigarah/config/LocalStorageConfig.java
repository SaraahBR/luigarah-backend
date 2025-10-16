package com.luigarah.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Configuração para servir arquivos estáticos em ambiente local.
 * Permite acessar imagens enviadas via URL (ex: http://localhost:8080/uploads/produtos/imagem.jpg)
 */
@Configuration
@Profile("local")
public class LocalStorageConfig implements WebMvcConfigurer {

    @Value("${storage.local.basePath:uploads}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Converte o caminho relativo para absoluto
        String absolutePath = Paths.get(basePath).toAbsolutePath().toUri().toString();

        // Mapeia /uploads/** para o diretório local
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absolutePath);
    }
}

