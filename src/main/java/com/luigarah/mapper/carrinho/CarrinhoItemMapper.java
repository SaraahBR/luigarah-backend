package com.luigarah.mapper.carrinho;

import com.luigarah.dto.carrinho.CarrinhoItemDTO;
import com.luigarah.model.carrinho.CarrinhoItem;
import com.luigarah.mapper.produto.ProdutoMapper;
import com.luigarah.mapper.tamanho.TamanhoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarrinhoItemMapper {

    private final ProdutoMapper produtoMapper;
    private final TamanhoMapper tamanhoMapper;

    public CarrinhoItemDTO toDTO(CarrinhoItem item) {
        if (item == null) {
            return null;
        }

        return CarrinhoItemDTO.builder()
                .id(item.getId())
                .produto(produtoMapper.toDTO(item.getProduto()))
                .tamanho(item.getTamanho() != null ? tamanhoMapper.toDTO(item.getTamanho()) : null)
                .quantidade(item.getQuantidade())
                .dataAdicao(item.getDataAdicao())
                .dataAtualizacao(item.getDataAtualizacao())
                .build();
    }
}
