package com.luigarah.mapper.usuario;

import com.luigarah.dto.usuario.UsuarioAdminDTO;
import com.luigarah.dto.usuario.UsuarioDTO;
import com.luigarah.model.autenticacao.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

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
                .fotoUrl(usuario.getFotoUrl())
                .role(usuario.getRole())
                .ativo(usuario.getAtivo())
                .emailVerificado(usuario.getEmailVerificado())
                .provider(usuario.getProvider())
                .dataCriacao(usuario.getDataCriacao())
                .dataAtualizacao(usuario.getDataAtualizacao())
                .ultimoAcesso(usuario.getUltimoAcesso())
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
                .role(usuario.getRole() != null ? usuario.getRole().name() : null)
                .ativo(usuario.getAtivo())
                .emailVerificado(usuario.getEmailVerificado())
                .provider(usuario.getProvider() != null ? usuario.getProvider().name() : null)
                .build();
    }
}
