package com.luigarah.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO padrão para respostas de erro da API.
 * Usado para manter consistência nas mensagens de erro retornadas ao frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de erro padrão da API")
public class ErrorResponseDTO {

    @Schema(description = "Código de status HTTP", example = "400")
    private int status;

    @Schema(description = "Tipo do erro (código interno)", example = "E-mail já cadastrado")
    private String error;

    @Schema(description = "Mensagem amigável para exibir ao usuário", example = "Este e-mail já foi verificado. Faça login para acessar sua conta.")
    private String message;

    @Schema(description = "Timestamp do erro", example = "2025-12-03T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Factory method para criar resposta de erro rapidamente
     */
    public static ErrorResponseDTO of(int status, String error, String message) {
        return ErrorResponseDTO.builder()
                .status(status)
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

