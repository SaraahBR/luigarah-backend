package com.luigarah.mapper.listadesejos;

import com.luigarah.dto.listadesejos.ListaDesejoItemDTO;
import com.luigarah.model.listadesejos.ListaDesejoItem;
import com.luigarah.mapper.produto.ProdutoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListaDesejoItemMapper {

    private final ProdutoMapper produtoMapper;

    public ListaDesejoItemDTO toDTO(ListaDesejoItem item) {
        if (item == null) {
            return null;
        }

        return ListaDesejoItemDTO.builder()
                .id(item.getId())
                .produto(produtoMapper.toDTO(item.getProduto()))
                .dataAdicao(item.getDataAdicao())
                .build();
    }
}
