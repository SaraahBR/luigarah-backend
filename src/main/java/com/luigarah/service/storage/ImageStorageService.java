package com.luigarah.service.storage;

import java.io.InputStream;

/**
 * Interface para serviços de armazenamento de imagens.
 * Permite ter diferentes implementações (local, S3/R2, etc.)
 */
public interface ImageStorageService {

    /**
     * Salva uma imagem no storage e retorna a URL pública.
     *
     * @param key Nome/chave do arquivo (ex: "produtos/produto-123.jpg")
     * @param contentType Tipo MIME (ex: "image/jpeg")
     * @param contentLength Tamanho do arquivo em bytes
     * @param in InputStream com os dados da imagem
     * @return URL pública da imagem salva
     */
    String save(String key, String contentType, long contentLength, InputStream in);

    /**
     * Valida se o tipo de arquivo é uma imagem permitida.
     *
     * @param contentType Tipo MIME do arquivo
     * @return true se for um tipo de imagem válido
     */
    default boolean isValidImageType(String contentType) {
        if (contentType == null) return false;
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/webp") ||
               contentType.equals("image/gif");
    }

    /**
     * Gera uma chave única para o arquivo baseada em timestamp e nome original.
     *
     * @param folder Pasta onde salvar (ex: "produtos", "usuarios")
     * @param originalFilename Nome original do arquivo
     * @return Chave única para o arquivo
     */
    default String generateKey(String folder, String originalFilename) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sanitized = sanitizeFilename(originalFilename);
        return folder + "/" + timestamp + "-" + sanitized;
    }

    /**
     * Remove caracteres especiais do nome do arquivo.
     */
    default String sanitizeFilename(String filename) {
        if (filename == null) return "file";
        // Remove path, mantém apenas o nome
        String name = filename.replaceAll(".*[/\\\\]", "");
        // Remove caracteres especiais, mantém apenas letras, números, ponto e hífen
        return name.replaceAll("[^a-zA-Z0-9._-]", "_").toLowerCase();
    }
}

