package com.luigarah.dto.listadesejos;

import com.luigarah.dto.produto.ProdutoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Item da lista de desejos")
public class ListaDesejoItemDTO {

    @Schema(description = "ID do item na lista", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Dados do produto")
    private ProdutoDTO produto;

    @Schema(description = "Data de adição à lista", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataAdicao;
}
