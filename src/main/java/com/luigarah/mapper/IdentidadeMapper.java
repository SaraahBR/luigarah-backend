package com.luigarah.mapper;

import com.luigarah.dto.IdentidadeCreateDTO;
import com.luigarah.dto.IdentidadeDTO;
import com.luigarah.dto.IdentidadeUpdateDTO;
import com.luigarah.model.Identidade;
import org.springframework.stereotype.Component;

@Component
public class IdentidadeMapper {

    public IdentidadeDTO toDTO(Identidade identidade) {
        if (identidade == null) {
            return null;
        }
        return IdentidadeDTO.builder()
                .id(identidade.getId())
                .codigo(identidade.getCodigo())
                .nome(identidade.getNome())
                .ordem(identidade.getOrdem())
                .ativo(identidade.getAtivo())
                .dataCriacao(identidade.getDataCriacao())
                .dataAtualizacao(identidade.getDataAtualizacao())
                .build();
    }

    public Identidade toEntity(IdentidadeCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return Identidade.builder()
                .codigo(dto.getCodigo())
                .nome(dto.getNome())
                .ordem(dto.getOrdem())
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : "S")
                .build();
    }

    public void updateEntityFromDTO(IdentidadeUpdateDTO dto, Identidade identidade) {
        if (dto == null || identidade == null) {
            return;
        }
        if (dto.getCodigo() != null) {
            identidade.setCodigo(dto.getCodigo());
        }
        if (dto.getNome() != null) {
            identidade.setNome(dto.getNome());
        }
        if (dto.getOrdem() != null) {
            identidade.setOrdem(dto.getOrdem());
        }
        if (dto.getAtivo() != null) {
            identidade.setAtivo(dto.getAtivo());
        }
    }
}

