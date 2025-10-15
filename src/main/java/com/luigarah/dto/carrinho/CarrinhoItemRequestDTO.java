package com.luigarah.dto.carrinho;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para adicionar item ao carrinho")
public class CarrinhoItemRequestDTO {

    @NotNull(message = "ID do produto é obrigatório")
    @Schema(description = "ID do produto", example = "14", required = true)
    private Long produtoId;

    @Schema(description = "ID do tamanho (opcional para produtos sem tamanho como bolsas)", example = "5")
    private Long tamanhoId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima é 1")
    @Max(value = 99, message = "Quantidade máxima é 99")
    @Schema(description = "Quantidade do produto", example = "2", required = true, minimum = "1", maximum = "99")
    private Integer quantidade;
}
