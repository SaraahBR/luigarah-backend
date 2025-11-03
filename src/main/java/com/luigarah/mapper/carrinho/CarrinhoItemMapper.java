package com.luigarah.mapper.carrinho;

import com.luigarah.dto.carrinho.CarrinhoItemDTO;
import com.luigarah.model.carrinho.CarrinhoItem;
import com.luigarah.mapper.produto.ProdutoMapper;
import com.luigarah.mapper.tamanho.TamanhoMapper;
import com.luigarah.repository.produto.RepositorioProduto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarrinhoItemMapper {

    private final ProdutoMapper produtoMapper;
    private final TamanhoMapper tamanhoMapper;
    private final RepositorioProduto repositorioProduto;

    public CarrinhoItemDTO toDTO(CarrinhoItem item) {
        if (item == null) {
            return null;
        }

        Integer estoqueDisponivel = calcularEstoqueDisponivel(item);

        return CarrinhoItemDTO.builder()
                .id(item.getId())
                .produto(produtoMapper.toDTO(item.getProduto()))
                .tamanho(item.getTamanho() != null ? tamanhoMapper.toDTO(item.getTamanho()) : null)
                .quantidade(item.getQuantidade())
                .estoqueDisponivel(estoqueDisponivel)
                .dataAdicao(item.getDataAdicao())
                .dataAtualizacao(item.getDataAtualizacao())
                .build();
    }

    /**
     * Calcula o estoque disponível baseado na categoria do produto.
     * - BOLSAS: usa produtos_estoque
     * - ROUPAS/SAPATOS: usa produtos_tamanhos (por tamanho)
     */
    private Integer calcularEstoqueDisponivel(CarrinhoItem item) {
        String categoria = item.getProduto().getCategoria();
        Long produtoId = item.getProduto().getId();

        if ("bolsas".equalsIgnoreCase(categoria)) {
            // Bolsas não têm tamanho, consulta estoque geral
            Integer estoque = repositorioProduto.obterEstoqueProduto(produtoId);
            return estoque != null ? estoque : 0;
        } else {
            // Roupas/Sapatos têm tamanho obrigatório
            if (item.getTamanho() == null || item.getTamanho().getEtiqueta() == null) {
                return 0;
            }

            // Busca estoque específico do tamanho
            var estoques = repositorioProduto.listarEstoquePorProduto(produtoId);
            for (Object[] row : estoques) {
                String etiqueta = (String) row[0];
                Number qtd = (Number) row[1];

                if (etiqueta != null && etiqueta.equalsIgnoreCase(item.getTamanho().getEtiqueta())) {
                    return qtd != null ? qtd.intValue() : 0;
                }
            }
            return 0;
        }
    }
}

