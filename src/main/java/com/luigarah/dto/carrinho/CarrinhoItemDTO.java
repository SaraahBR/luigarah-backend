package com.luigarah.dto.carrinho;

import com.luigarah.dto.produto.ProdutoDTO;
import com.luigarah.dto.tamanho.TamanhoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Item do carrinho de compras")
public class CarrinhoItemDTO {

    @Schema(description = "ID do item no carrinho", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Dados do produto")
    private ProdutoDTO produto;

    @Schema(description = "Dados do tamanho selecionado (se aplicável)")
    private TamanhoDTO tamanho;

    @Schema(description = "Quantidade do produto", example = "2")
    private Integer quantidade;

    @Schema(description = "Estoque disponível do produto/tamanho", example = "10", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer estoqueDisponivel;

    @Schema(description = "Data de adição ao carrinho", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataAdicao;

    @Schema(description = "Data da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataAtualizacao;
}
