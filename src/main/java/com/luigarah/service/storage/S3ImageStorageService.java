package com.luigarah.service.storage;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de storage usando AWS S3 / Cloudflare R2.
 * Ativa apenas em produção (profile != local).
 */
@Service
@Profile("!local")
public class S3ImageStorageService implements ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(S3ImageStorageService.class);

    @Value("${storage.bucket}")
    private String bucket;

    @Value("${storage.publicBaseUrl:}")
    private String publicBaseUrl;

    @Value("${aws.region:auto}")
    private String region;

    @Value("${aws.s3.endpoint:}")
    private String endpoint;

    @Value("${aws.credentials.accessKey:}")
    private String accessKey;

    @Value("${aws.credentials.secretKey:}")
    private String secretKey;

    private S3Client s3;

    @PostConstruct
    void init() {
        log.info("🔧 Inicializando S3ImageStorageService...");
        log.debug("   storage.bucket={}", bucket);
        log.debug("   storage.publicBaseUrl={}", publicBaseUrl);
        log.debug("   aws.region={}", region);
        log.debug("   aws.s3.endpoint={}", endpoint);
        log.debug("   aws.credentials.accessKey={}", accessKey != null && !accessKey.isEmpty() ? "***configurado***" : "VAZIO");
        log.debug("   aws.credentials.secretKey={}", secretKey != null && !secretKey.isEmpty() ? "***configurado***" : "VAZIO");

        // Validações mínimas para falhar rápido se ambiente estiver incorreto
        if (isBlank(bucket)) {
            log.error("❌ storage.bucket não configurado!");
            throw new IllegalStateException("storage.bucket não configurado. Configure a variável STORAGE_BUCKET");
        }
        if (isBlank(endpoint)) {
            log.error("❌ aws.s3.endpoint não configurado!");
            throw new IllegalStateException("aws.s3.endpoint não configurado. Configure a variável AWS_S3_ENDPOINT");
        }
        if (isBlank(accessKey)) {
            log.error("❌ aws.credentials.accessKey não configurado!");
            throw new IllegalStateException("AWS_ACCESS_KEY_ID não configurado");
        }
        if (isBlank(secretKey)) {
            log.error("❌ aws.credentials.secretKey não configurado!");
            throw new IllegalStateException("AWS_SECRET_ACCESS_KEY não configurado");
        }

        try {
            var s3cfg = S3Configuration.builder()
                    .pathStyleAccessEnabled(true) // Obrigatório para Cloudflare R2
                    .build();

            this.s3 = S3Client.builder()
                    .region(Region.of(region)) // Rótulo simbólico; R2 ignora região real
                    .serviceConfiguration(s3cfg)
                    .endpointOverride(URI.create(endpoint))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)
                    ))
                    .build();

            log.info("✅ S3ImageStorageService inicializado com sucesso!");
            log.info("   endpoint={}", endpoint);
            log.info("   bucket={}", bucket);
            log.info("   publicBaseUrl={}", isBlank(publicBaseUrl) ? "(não configurado, usando fallback)" : publicBaseUrl);
        } catch (Exception e) {
            log.error("❌ Erro ao inicializar S3Client: {}", e.getMessage(), e);
            throw new IllegalStateException("Falha ao inicializar S3ImageStorageService: " + e.getMessage(), e);
        }
    }

    @Override
    public String save(String key, String contentType, long contentLength, InputStream in) {
        if (isBlank(key)) {
            throw new IllegalArgumentException("key não pode ser vazio");
        }
        if (contentLength < 0) {
            throw new IllegalArgumentException("contentLength inválido");
        }

        var put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType == null ? "application/octet-stream" : contentType)
                // Melhora caching para CDN/navegador (ajuste conforme sua política)
                .cacheControl("public, max-age=31536000, immutable")
                .build();

        try {
            s3.putObject(put, RequestBody.fromInputStream(in, contentLength));
            var url = buildPublicUrl(key);
            log.info("✅ Upload OK para key='{}' -> {}", key, url);
            return url;
        } catch (Exception e) {
            log.error("❌ Falha no upload para key='{}' no bucket '{}': {}", key, bucket, e.getMessage(), e);
            throw new RuntimeException("Falha no upload para o storage: " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------------------------------------------------
    // Helpers
    // --------------------------------------------------------------------------------------------

    private String buildPublicUrl(String key) {
        // Preferir domínio/base pública quando configurada (ex.: CDN ou endpoint do bucket)
        if (!isBlank(publicBaseUrl)) {
            var base = publicBaseUrl.endsWith("/") ? publicBaseUrl : publicBaseUrl + "/";
            return base + encodePath(key);
        }
        // Fallback: endpoint path-style do R2 => https://<ACCOUNT_ID>.r2.../<bucket>/<key>
        var base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        return base + "/" + bucket + "/" + encodePath(key);
    }

    private static String encodePath(String key) {
        // Faz URL-encoding por segmento para manter '/' e escapar caracteres especiais/espaços
        return Arrays.stream(key.split("/"))
                .map(part -> URLEncoder.encode(part, StandardCharsets.UTF_8))
                .collect(Collectors.joining("/"));
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
