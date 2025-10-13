package com.luigarah.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para criação de nova Identidade.
 *
 * <p>Utilizado no endpoint POST /api/identidades para cadastrar
 * uma nova identidade no sistema.</p>
 *
 * <p><b>Validações:</b></p>
 * <ul>
 *   <li>Código é obrigatório e deve ser único</li>
 *   <li>Nome é obrigatório</li>
 *   <li>Ordem é opcional (padrão: último)</li>
 *   <li>Ativo é opcional (padrão: 'S')</li>
 * </ul>
 *
 * <p><b>Exemplo de uso:</b></p>
 * <pre>
 * {
 *   "codigo": "plus-size",
 *   "nome": "Plus Size",
 *   "ordem": 5,
 *   "ativo": "S"
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para criação de uma nova identidade de produto")
public class IdentidadeCreateDTO {

    @NotBlank(message = "Código é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Schema(
            description = "Código único identificador da identidade (será usado em URLs e filtros)",
            example = "plus-size",
            required = true,
            maxLength = 50
    )
    private String codigo;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(
            description = "Nome descritivo da identidade (será exibido para o usuário)",
            example = "Plus Size",
            required = true,
            maxLength = 100
    )
    private String nome;

    @Schema(
            description = "Ordem de exibição/classificação (quanto menor, mais prioritário)",
            example = "5"
    )
    private Integer ordem;

    @Pattern(regexp = "S|N", message = "Ativo deve ser 'S' ou 'N'")
    @Schema(
            description = "Status de ativação da identidade ('S' = ativo, 'N' = inativo)",
            example = "S",
            allowableValues = {"S", "N"},
            defaultValue = "S"
    )
    private String ativo;
}
