package com.luigarah.service.usuario;

import com.luigarah.dto.usuario.UsuarioAdminDTO;
import com.luigarah.dto.usuario.UsuarioAdminUpdateDTO;
import com.luigarah.exception.RecursoNaoEncontradoException;
import com.luigarah.exception.RegraDeNegocioException;
import com.luigarah.mapper.usuario.UsuarioMapper;
import com.luigarah.model.autenticacao.Role;
import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.repository.autenticacao.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de administração de usuários
 * Permite ADMIN gerenciar usuários conforme LGPD
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    /**
     * Lista todos os usuários (sem dados sensíveis - LGPD)
     */
    @Transactional(readOnly = true)
    public List<UsuarioAdminDTO> listarTodos() {
        log.info("Listando todos os usuários");
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toAdminDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista usuários com paginação
     */
    @Transactional(readOnly = true)
    public Page<UsuarioAdminDTO> listarComPaginacao(Pageable pageable) {
        log.info("Listando usuários com paginação: {}", pageable);
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toAdminDTO);
    }

    /**
     * Busca usuário por ID (sem dados sensíveis - LGPD)
     */
    @Transactional(readOnly = true)
    public UsuarioAdminDTO buscarPorId(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
        return usuarioMapper.toAdminDTO(usuario);
    }

    /**
     * Busca usuários por nome (parcial)
     */
    @Transactional(readOnly = true)
    public List<UsuarioAdminDTO> buscarPorNome(String nome) {
        log.info("Buscando usuários por nome: {}", nome);
        return usuarioRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(usuarioMapper::toAdminDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca usuários por email (parcial)
     */
    @Transactional(readOnly = true)
    public List<UsuarioAdminDTO> buscarPorEmail(String email) {
        log.info("Buscando usuários por email: {}", email);
        return usuarioRepository.findByEmailContainingIgnoreCase(email).stream()
                .map(usuarioMapper::toAdminDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca usuários por role
     */
    @Transactional(readOnly = true)
    public List<UsuarioAdminDTO> buscarPorRole(Role role) {
        log.info("Buscando usuários por role: {}", role);
        return usuarioRepository.findByRole(role).stream()
                .map(usuarioMapper::toAdminDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca usuários ativos ou inativos
     */
    @Transactional(readOnly = true)
    public List<UsuarioAdminDTO> buscarPorStatus(Boolean ativo) {
        log.info("Buscando usuários por status ativo: {}", ativo);
        return usuarioRepository.findByAtivo(ativo).stream()
                .map(usuarioMapper::toAdminDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza dados do usuário (apenas campos permitidos)
     * ADMIN pode alterar: nome, sobrenome, email, telefone, role
     * ADMIN NÃO pode alterar: senha, documentos, dados sensíveis
     */
    @Transactional
    public UsuarioAdminDTO atualizar(Long id, UsuarioAdminUpdateDTO updateDTO) {
        log.info("Atualizando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));

        // Validar se está alterando email
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(updateDTO.getEmail())) {
                throw new RegraDeNegocioException("Email já está em uso: " + updateDTO.getEmail());
            }
            usuario.setEmail(updateDTO.getEmail());
            usuario.setEmailVerificado(false); // Requer nova verificação
        }

        // Atualizar campos permitidos
        if (updateDTO.getNome() != null) {
            usuario.setNome(updateDTO.getNome());
        }
        if (updateDTO.getSobrenome() != null) {
            usuario.setSobrenome(updateDTO.getSobrenome());
        }
        if (updateDTO.getTelefone() != null) {
            usuario.setTelefone(updateDTO.getTelefone());
        }

        // Atualizar role se fornecido
        if (updateDTO.getRole() != null) {
            try {
                Role novaRole = Role.valueOf(updateDTO.getRole().toUpperCase());
                usuario.setRole(novaRole);
                log.info("Role alterada para {} no usuário ID: {}", novaRole, id);
            } catch (IllegalArgumentException e) {
                throw new RegraDeNegocioException("Role inválida: " + updateDTO.getRole());
            }
        }

        usuario = usuarioRepository.save(usuario);
        log.info("Usuário atualizado com sucesso: ID {}", id);

        return usuarioMapper.toAdminDTO(usuario);
    }

    /**
     * Desativa usuário (soft delete)
     * Mantém os dados no banco mas marca como inativo
     */
    @Transactional
    public void desativar(Long id) {
        log.info("Desativando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));

        if (!usuario.getAtivo()) {
            throw new RegraDeNegocioException("Usuário já está desativado");
        }

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuário desativado com sucesso: ID {}", id);
    }

    /**
     * Ativa usuário previamente desativado
     */
    @Transactional
    public void ativar(Long id) {
        log.info("Ativando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));

        if (usuario.getAtivo()) {
            throw new RegraDeNegocioException("Usuário já está ativo");
        }

        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        log.info("Usuário ativado com sucesso: ID {}", id);
    }

    /**
     * Conta total de usuários
     */
    @Transactional(readOnly = true)
    public long contarTodos() {
        return usuarioRepository.count();
    }

    /**
     * Conta usuários ativos
     */
    @Transactional(readOnly = true)
    public long contarAtivos() {
        return usuarioRepository.countByAtivo(true);
    }

    /**
     * Conta usuários por role
     */
    @Transactional(readOnly = true)
    public long contarPorRole(Role role) {
        return usuarioRepository.countByRole(role);
    }
}

