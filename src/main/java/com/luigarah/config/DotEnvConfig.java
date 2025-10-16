package com.luigarah.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Carrega variáveis do .env e extrai a wallet ANTES da inicialização do Spring (profile=local).
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
            log.warn("⚠️ Arquivo .env não encontrado. Defina ORACLE_WALLET_ZIP_B64/_FILE ou ORACLE_WALLET_ZIP_FILE.");
            return;
        }

        try {
            Map<String, Object> vars = loadEnvFile(envFile);
            if (!vars.isEmpty()) {
                env.getPropertySources().addFirst(new MapPropertySource("dotenv", vars));
                log.info("✅ Arquivo .env carregado com {} variáveis", vars.size());
                extractWalletFromEnv(vars);
            }
        } catch (IOException e) {
            log.error("❌ Erro ao carregar .env: {}", e.getMessage());
        }
    }

    /**
     * Suporta:
     *  - ORACLE_WALLET_ZIP_B64: ZIP em base64 (linha única)
     *  - ORACLE_WALLET_ZIP_B64_FILE: caminho de arquivo contendo base64
     *  - ORACLE_WALLET_ZIP_FILE: caminho de arquivo .zip (binário)
     *  - (fallback legado) ORACLE_WALLET_* individuais em base64
     */
    private void extractWalletFromEnv(Map<String, Object> envVars) {
        String b64Inline = str(envVars.get("ORACLE_WALLET_ZIP_B64"));
        String b64File   = str(envVars.get("ORACLE_WALLET_ZIP_B64_FILE"));
        String zipFile   = str(envVars.get("ORACLE_WALLET_ZIP_FILE"));

        try {
            Path walletDir = Files.createTempDirectory("oracle_wallet_");
            log.info("📁 Criando wallet Oracle em: {}", walletDir.toAbsolutePath());

            if (!isBlank(zipFile)) {
                // Caminho para ZIP binário
                unzipWalletFromBytes(Files.readAllBytes(Paths.get(zipFile)), walletDir);
                log.info("✅ Wallet ZIP extraída de ORACLE_WALLET_ZIP_FILE");
            } else if (!isBlank(b64Inline) || !isBlank(b64File)) {
                // Base64 (inline ou arquivo .b64)
                String base64 = !isBlank(b64Inline) ? b64Inline
                        : Files.readString(Paths.get(b64File), StandardCharsets.UTF_8);
                byte[] bytes = decodeBase64Relaxed(base64);
                unzipWalletFromBytes(bytes, walletDir);
                log.info("✅ Wallet ZIP extraída (Base64)");
            } else {
                // Fallback legado
                String cwallet = str(envVars.get("ORACLE_WALLET_CWALLET"));
                String tns     = str(envVars.get("ORACLE_WALLET_TNSNAMES"));
                if (isBlank(cwallet) && isBlank(tns)) {
                    log.warn("⚠️ Nenhuma wallet encontrada (.env não tem ORACLE_WALLET_ZIP_* nem ORACLE_WALLET_*)");
                    return;
                }
                createWalletFile(walletDir, "cwallet.sso", cwallet);
                createWalletFile(walletDir, "ewallet.p12", str(envVars.get("ORACLE_WALLET_EWALLET")));
                createWalletFile(walletDir, "tnsnames.ora", tns);
                createWalletFile(walletDir, "sqlnet.ora", str(envVars.get("ORACLE_WALLET_SQLNET")));
                createWalletFile(walletDir, "keystore.jks", str(envVars.get("ORACLE_WALLET_KEYSTORE")));
                createWalletFile(walletDir, "truststore.jks", str(envVars.get("ORACLE_WALLET_TRUSTSTORE")));
                log.info("✅ Wallet (arquivos Base64) criada (modo legado)");
            }

            // Define TNS_ADMIN antes de qualquer conexão
            String tnsAdmin = walletDir.toAbsolutePath().toString();
            System.setProperty("oracle.net.tns_admin", tnsAdmin);
            log.info("✅ Wallet Oracle configurada! TNS_ADMIN={}", tnsAdmin);

        } catch (IOException e) {
            log.error("❌ Erro ao extrair wallet: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao configurar wallet Oracle", e);
        }
    }

    /** Decoder tolerante p/ Base64 (remove BOM/controle, normaliza, limpa não-base64, corrige padding). */
    private byte[] decodeBase64Relaxed(String input) throws IOException {
        if (isBlank(input)) throw new IOException("Base64 vazio");

        String cleaned = input
                .replace("\uFEFF", "")                              // BOM
                .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");         // remove controles (exceto \r \n \t)

        cleaned = cleaned.replace('-', '+').replace('_', '/');      // URL-safe -> padrão
        cleaned = cleaned.replaceAll("[^A-Za-z0-9+/=]", "");        // só deixa alfabeto Base64

        int mod = cleaned.length() % 4;
        if (mod == 1) {
            throw new IOException("Base64 inválido (tamanho % 4 == 1) — possivelmente truncado.");
        }
        if (mod > 0) cleaned = cleaned + "====".substring(mod);

        try {
            byte[] out = Base64.getDecoder().decode(cleaned);
            validateZipMagic(out);
            return out;
        } catch (IllegalArgumentException e1) {
            try {
                byte[] out = Base64.getUrlDecoder().decode(cleaned);
                validateZipMagic(out);
                return out;
            } catch (IllegalArgumentException e2) {
                throw new IOException("Base64 inválido após normalização/padding.", e2);
            }
        }
    }

    /** Confere assinatura ZIP (PK\003\004) para evitar lixo silencioso. */
    private void validateZipMagic(byte[] bytes) throws IOException {
        if (bytes.length < 4 || bytes[0] != 0x50 || bytes[1] != 0x4B) {
            throw new IOException("Conteúdo não parece ZIP (assinatura PK ausente).");
        }
    }

    private void unzipWalletFromBytes(byte[] bytes, Path destDir) throws IOException {
        validateZipMagic(bytes);
        try (InputStream in = new ByteArrayInputStream(bytes);
             ZipInputStream zis = new ZipInputStream(in)) {
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                Path out = destDir.resolve(e.getName()).normalize();
                if (!out.startsWith(destDir)) {
                    throw new IOException("Entrada ZIP fora do diretório destino: " + e.getName());
                }
                if (e.isDirectory()) {
                    Files.createDirectories(out);
                } else {
                    Files.createDirectories(out.getParent());
                    try (FileOutputStream fos = new FileOutputStream(out.toFile())) {
                        zis.transferTo(fos);
                    }
                    log.debug("🗂️ Extraído: {}", out.getFileName());
                }
                zis.closeEntry();
            }
        }
    }

    /** Modo legado: cria arquivos individuais a partir de Base64. */
    private void createWalletFile(Path dir, String fileName, String base64) throws IOException {
        if (isBlank(base64)) {
            log.debug("⏭️ Arquivo {} não fornecido (Base64 vazio)", fileName);
            return;
        }
        byte[] bytes = decodeBase64Relaxed(base64);
        Path out = dir.resolve(fileName);
        Files.createDirectories(out.getParent());
        try (FileOutputStream fos = new FileOutputStream(out.toFile())) {
            fos.write(bytes);
        }
        log.info("✅ Arquivo criado: {} ({} bytes)", fileName, bytes.length);
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
                    if (key.startsWith("ORACLE_WALLET") || key.startsWith("ORACLE_PASSWORD")) {
                        log.debug("📝 Carregada variável: {} ({} chars)", key, val.length());
                    }
                }
            }
        }
        return m;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    private static String str(Object o) { return o == null ? null : o.toString(); }
}
