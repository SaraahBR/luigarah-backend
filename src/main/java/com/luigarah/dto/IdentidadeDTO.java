package com.luigarah.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de resposta para Identidade.
 *
 * <p>Representa uma identidade de produto (classificação por gênero/idade).
 * Utilizado em respostas de leitura dos endpoints de identidade.</p>
 *
 * <p><b>Identidades disponíveis:</b></p>
 * <ul>
 *   <li><b>homem</b> - Produtos masculinos</li>
 *   <li><b>mulher</b> - Produtos femininos</li>
 *   <li><b>unissex</b> - Produtos unissex</li>
 *   <li><b>infantil</b> - Produtos infantis</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de uma identidade de produto (classificação por gênero/idade)")
public class IdentidadeDTO {

    @Schema(
            description = "ID único da identidade",
            example = "2",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Código único da identidade",
            example = "mulher",
            maxLength = 50
    )
    private String codigo;

    @Schema(
            description = "Nome descritivo da identidade",
            example = "Feminino",
            maxLength = 100
    )
    private String nome;

    @Schema(
            description = "Ordem de exibição/classificação",
            example = "2"
    )
    private Integer ordem;

    @Schema(
            description = "Status de ativação da identidade",
            example = "S",
            allowableValues = {"S", "N"}
    )
    private String ativo;

    @Schema(
            description = "Data e hora de criação do registro",
            example = "2025-10-11T17:13:18.433",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime dataCriacao;

    @Schema(
            description = "Data e hora da última atualização",
            example = "2025-10-11T19:00:00.000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime dataAtualizacao;
}
