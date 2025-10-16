package com.luigarah.dto.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Resposta do upload de imagem
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta do upload de imagem")
public class ImageUploadResponse {

    @Schema(description = "URL pública da imagem enviada", example = "https://r2.cloudflarestorage.com/luigarah-prod/produtos/1234567890-produto.jpg")
    private String url;

    @Schema(description = "Nome original do arquivo", example = "produto.jpg")
    private String originalFilename;

    @Schema(description = "Tipo MIME do arquivo", example = "image/jpeg")
    private String contentType;

    @Schema(description = "Tamanho do arquivo em bytes", example = "102400")
    private Long size;

    @Schema(description = "Chave/caminho do arquivo no storage", example = "produtos/1234567890-produto.jpg")
    private String key;

    @Schema(description = "Mensagem de sucesso", example = "Imagem enviada com sucesso")
    private String message;
}

