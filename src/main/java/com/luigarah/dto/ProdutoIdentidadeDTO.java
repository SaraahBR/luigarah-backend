package com.luigarah.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para atribuir identidade a um produto.
 *
 * <p>Utilizado no endpoint PUT /api/produtos/{id}/identidade para associar
 * uma identidade (gênero/idade) a um produto específico.</p>
 *
 * <p><b>Validações:</b></p>
 * <ul>
 *   <li>O ID da identidade é obrigatório</li>
 *   <li>A identidade deve existir no sistema</li>
 *   <li>Se o produto já tiver uma identidade, ela será substituída</li>
 * </ul>
 *
 * <p><b>Exemplo de uso:</b></p>
 * <pre>
 * {
 *   "identidadeId": 2
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para atribuir uma identidade a um produto")
public class ProdutoIdentidadeDTO {

    @NotNull(message = "ID da identidade é obrigatório")
    @Schema(
            description = "ID da identidade a ser atribuída ao produto",
            example = "2",
            required = true,
            implementation = Long.class
    )
    private Long identidadeId;
}
