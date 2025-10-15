package com.luigarah.mapper.tamanho;

import com.luigarah.dto.tamanho.TamanhoDTO;
import com.luigarah.model.tamanho.Tamanho;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre Tamanho e TamanhoDTO.
 */
@Component
public class TamanhoMapper {

    public TamanhoDTO toDTO(Tamanho tamanho) {
        if (tamanho == null) {
            return null;
        }

        TamanhoDTO dto = new TamanhoDTO();
        dto.setEtiqueta(tamanho.getEtiqueta());
        dto.setCategoria(tamanho.getCategoria());
        // qtdEstoque será preenchido em outro contexto quando necessário
        dto.setQtdEstoque(null);

        return dto;
    }

    public TamanhoDTO toDTO(Tamanho tamanho, Integer qtdEstoque) {
        if (tamanho == null) {
            return null;
        }

        TamanhoDTO dto = new TamanhoDTO();
        dto.setEtiqueta(tamanho.getEtiqueta());
        dto.setCategoria(tamanho.getCategoria());
        dto.setQtdEstoque(qtdEstoque);

        return dto;
    }
}
