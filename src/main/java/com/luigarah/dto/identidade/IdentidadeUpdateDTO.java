package com.luigarah.dto.identidade;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para atualização de Identidade existente.
 *
 * <p>Utilizado no endpoint PUT /api/identidades/{id} para atualizar parcialmente
 * uma identidade existente.</p>
 *
 * <p><b>Características:</b></p>
 * <ul>
 *   <li>Todos os campos são opcionais</li>
 *   <li>Apenas os campos enviados serão atualizados</li>
 *   <li>Se alterar o código, ele deve permanecer único</li>
 * </ul>
 *
 * <p><b>Exemplo de uso (atualizar apenas o nome):</b></p>
 * <pre>
 * {
 *   "nome": "Feminino Premium"
 * }
 * </pre>
 *
 * <p><b>Exemplo de uso (atualizar múltiplos campos):</b></p>
 * <pre>
 * {
 *   "nome": "Feminino Premium",
 *   "ordem": 1,
 *   "ativo": "S"
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para atualização parcial de uma identidade existente")
public class IdentidadeUpdateDTO {

    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Schema(
            description = "Novo código da identidade (opcional, deve ser único se alterado)",
            example = "mulher",
            maxLength = 50
    )
    private String codigo;

    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(
            description = "Novo nome descritivo da identidade (opcional)",
            example = "Feminino Premium",
            maxLength = 100
    )
    private String nome;

    @Schema(
            description = "Nova ordem de exibição (opcional)",
            example = "1"
    )
    private Integer ordem;

    @Pattern(regexp = "S|N", message = "Ativo deve ser 'S' ou 'N'")
    @Schema(
            description = "Novo status de ativação (opcional)",
            example = "S",
            allowableValues = {"S", "N"}
    )
    private String ativo;
}
