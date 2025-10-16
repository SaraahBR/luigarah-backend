package com.luigarah.mapper.usuario;

import com.luigarah.dto.usuario.UsuarioAdminDTO;
import com.luigarah.dto.usuario.UsuarioDTO;
import com.luigarah.model.autenticacao.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final EnderecoMapper enderecoMapper;

    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .sobrenome(usuario.getSobrenome())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .dataNascimento(usuario.getDataNascimento())
                .genero(usuario.getGenero())
                .fotoPerfil(usuario.getFotoPerfil())
                .role(usuario.getRole())
                .ativo(usuario.getAtivo())
                .emailVerificado(usuario.getEmailVerificado())
                .provider(usuario.getProvider())
                .dataCriacao(usuario.getDataCriacao())
                .dataAtualizacao(usuario.getDataAtualizacao())
                .ultimoAcesso(usuario.getUltimoAcesso())
                .enderecos(enderecoMapper.toDTOList(usuario.getEnderecos()))
                .build();
    }

    /**
     * Converte Usuario para UsuarioAdminDTO
     * Remove dados sensíveis conforme LGPD
     */
    public UsuarioAdminDTO toAdminDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioAdminDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .sobrenome(usuario.getSobrenome())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .dataNascimento(usuario.getDataNascimento())
                .genero(usuario.getGenero())
                .fotoPerfil(usuario.getFotoPerfil())
                .role(usuario.getRole() != null ? usuario.getRole().name() : null)
                .ativo(usuario.getAtivo())
                .emailVerificado(usuario.getEmailVerificado())
                .provider(usuario.getProvider() != null ? usuario.getProvider().name() : null)
                .enderecos(enderecoMapper.toDTOList(usuario.getEnderecos()))
                .build();
    }
}
