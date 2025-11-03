package com.luigarah.dto.carrinho;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO para atualizar tamanho e quantidade de um item do carrinho.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para atualizar item do carrinho")
public class AtualizarCarrinhoItemDTO {

    @Schema(description = "ID do novo tamanho (obrigatório para roupas/sapatos, null para bolsas)", example = "123")
    private Long tamanhoId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima é 1")
    @Max(value = 99, message = "Quantidade máxima é 99")
    @Schema(description = "Nova quantidade do produto", example = "2", required = true)
    private Integer quantidade;
}

