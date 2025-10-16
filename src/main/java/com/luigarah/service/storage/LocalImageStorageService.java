package com.luigarah.service.storage;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Implementação do serviço de storage para desenvolvimento local.
 * Salva arquivos em disco local ao invés de enviar para R2.
 * Ativa apenas em ambiente local (profile = local).
 */
@Service
@Profile("local")
public class LocalImageStorageService implements ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalImageStorageService.class);

    @Value("${storage.local.basePath:uploads}")
    private String basePath;

    @Value("${storage.local.baseUrl:http://localhost:8080/uploads}")
    private String baseUrl;

    private Path uploadDir;

    @PostConstruct
    void init() {
        try {
            // Cria diretório de uploads se não existir
            uploadDir = Paths.get(basePath).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            log.info("✅ LocalImageStorageService inicializado. Diretório: {}, URL base: {}",
                     uploadDir, baseUrl);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível criar diretório de uploads: " + basePath, e);
        }
    }

    @Override
    public String save(String key, String contentType, long contentLength, InputStream in) {
        try {
            // Cria subdiretórios se necessário (ex: "produtos/")
            Path targetFile = uploadDir.resolve(key);
            Files.createDirectories(targetFile.getParent());

            // Salva o arquivo
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);

            // Constrói URL pública
            String publicUrl = baseUrl.endsWith("/") ? baseUrl + key : baseUrl + "/" + key;

            log.info("✅ Arquivo salvo localmente: {} -> {}", targetFile, publicUrl);
            return publicUrl;

        } catch (IOException e) {
            log.error("❌ Erro ao salvar arquivo local: {}", key, e);
            throw new RuntimeException("Falha ao salvar arquivo local: " + e.getMessage(), e);
        }
    }
}

