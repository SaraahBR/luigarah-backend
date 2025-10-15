package com.luigarah.service.usuario;

import com.luigarah.dto.usuario.EnderecoDTO;
import com.luigarah.exception.RecursoNaoEncontradoException;
import com.luigarah.exception.RegraDeNegocioException;
import com.luigarah.mapper.usuario.EnderecoMapper;
import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.model.usuario.Endereco;
import com.luigarah.repository.autenticacao.UsuarioRepository;
import com.luigarah.repository.autenticacao.EnderecoRepository; // Corrigido o pacote
import com.luigarah.service.autenticacao.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;
    private final EnderecoMapper enderecoMapper;

    @Transactional(readOnly = true)
    public List<EnderecoDTO> listarEnderecos() {
        Usuario usuario = authService.getUsuarioAutenticado();
        List<Endereco> enderecos = enderecoRepository.findByUsuarioId(usuario.getId());
        return enderecoMapper.toDTOList(enderecos);
    }

    @Transactional(readOnly = true)
    public EnderecoDTO buscarPorId(Long id) {
        Usuario usuario = authService.getUsuarioAutenticado();
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço não encontrado"));

        // Verifica se o endereço pertence ao usuário autenticado
        if (!endereco.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraDeNegocioException("Você não tem permissão para acessar este endereço");
        }

        return enderecoMapper.toDTO(endereco);
    }

    @Transactional
    public EnderecoDTO adicionar(EnderecoDTO enderecoDTO) {
        Usuario usuario = authService.getUsuarioAutenticado();

        // Se este for marcado como principal, desmarca os outros
        if (Boolean.TRUE.equals(enderecoDTO.getPrincipal())) {
            enderecoRepository.desmarcarTodosPrincipais(usuario.getId());
        }

        Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
        endereco.setUsuario(usuario);

        endereco = enderecoRepository.save(endereco);
        log.info("Endereço adicionado para usuário: {}", usuario.getEmail());

        return enderecoMapper.toDTO(endereco);
    }

    @Transactional
    public EnderecoDTO atualizar(Long id, EnderecoDTO enderecoDTO) {
        Usuario usuario = authService.getUsuarioAutenticado();
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço não encontrado"));

        // Verifica se o endereço pertence ao usuário autenticado
        if (!endereco.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraDeNegocioException("Você não tem permissão para atualizar este endereço");
        }

        // Se este for marcado como principal, desmarca os outros
        if (Boolean.TRUE.equals(enderecoDTO.getPrincipal())) {
            enderecoRepository.desmarcarTodosPrincipais(usuario.getId());
        }

        endereco.setPais(enderecoDTO.getPais());
        endereco.setEstado(enderecoDTO.getEstado());
        endereco.setCidade(enderecoDTO.getCidade());
        endereco.setCep(enderecoDTO.getCep());
        endereco.setBairro(enderecoDTO.getBairro());
        endereco.setRua(enderecoDTO.getRua());
        endereco.setNumero(enderecoDTO.getNumero());
        endereco.setComplemento(enderecoDTO.getComplemento());
        endereco.setPrincipal(enderecoDTO.getPrincipal());

        endereco = enderecoRepository.save(endereco);
        log.info("Endereço atualizado: {}", id);

        return enderecoMapper.toDTO(endereco);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = authService.getUsuarioAutenticado();
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço não encontrado"));

        // Verifica se o endereço pertence ao usuário autenticado
        if (!endereco.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraDeNegocioException("Você não tem permissão para deletar este endereço");
        }

        enderecoRepository.delete(endereco);
        log.info("Endereço deletado: {}", id);
    }

    @Transactional
    public EnderecoDTO marcarComoPrincipal(Long id) {
        Usuario usuario = authService.getUsuarioAutenticado();
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço não encontrado"));

        // Verifica se o endereço pertence ao usuário autenticado
        if (!endereco.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraDeNegocioException("Você não tem permissão para modificar este endereço");
        }

        // Desmarca todos os outros como principal
        enderecoRepository.desmarcarTodosPrincipais(usuario.getId());

        // Marca este como principal
        endereco.setPrincipal(true);
        endereco = enderecoRepository.save(endereco);

        log.info("Endereço {} marcado como principal para usuário: {}", id, usuario.getEmail());

        return enderecoMapper.toDTO(endereco);
    }
}
