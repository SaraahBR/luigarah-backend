package com.luigarah.mapper.usuario;

import com.luigarah.dto.usuario.EnderecoDTO;
import com.luigarah.model.usuario.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper {

    public EnderecoDTO toDTO(Endereco endereco) {
        if (endereco == null) {
            return null;
        }

        return EnderecoDTO.builder()
                .id(endereco.getId())
                .pais(endereco.getPais())
                .estado(endereco.getEstado())
                .cidade(endereco.getCidade())
                .cep(endereco.getCep())
                .bairro(endereco.getBairro())
                .rua(endereco.getRua())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .principal(endereco.getPrincipal())
                .dataCriacao(endereco.getDataCriacao())
                .dataAtualizacao(endereco.getDataAtualizacao())
                .build();
    }

    public Endereco toEntity(EnderecoDTO dto) {
        if (dto == null) {
            return null;
        }

        return Endereco.builder()
                .id(dto.getId())
                .pais(dto.getPais())
                .estado(dto.getEstado())
                .cidade(dto.getCidade())
                .cep(dto.getCep())
                .bairro(dto.getBairro())
                .rua(dto.getRua())
                .numero(dto.getNumero())
                .complemento(dto.getComplemento())
                .principal(dto.getPrincipal())
                .build();
    }
}
