package com.luigarah.service.autenticacao;

import com.luigarah.config.JwtTokenProvider;
import com.luigarah.dto.autenticacao.AlterarSenhaRequestDTO;
import com.luigarah.dto.autenticacao.AuthResponseDTO;
import com.luigarah.dto.autenticacao.LoginRequestDTO;
import com.luigarah.dto.autenticacao.RegistroRequestDTO;
import com.luigarah.dto.usuario.UsuarioDTO;
import com.luigarah.exception.RecursoNaoEncontradoException;
import com.luigarah.exception.RegraDeNegocioException;
import com.luigarah.mapper.usuario.UsuarioMapper;
import com.luigarah.model.autenticacao.AuthProvider;
import com.luigarah.model.autenticacao.Role;
import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.repository.autenticacao.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getSenha()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        usuario.setUltimoAcesso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario);

        return AuthResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .usuario(usuarioDTO)
                .build();
    }

    @Transactional
    public AuthResponseDTO registrar(RegistroRequestDTO registroRequest) {
        if (usuarioRepository.existsByEmail(registroRequest.getEmail())) {
            throw new RegraDeNegocioException("Email já está em uso");
        }

        Usuario usuario = Usuario.builder()
                .nome(registroRequest.getNome())
                .sobrenome(registroRequest.getSobrenome())
                .email(registroRequest.getEmail())
                .senha(passwordEncoder.encode(registroRequest.getSenha()))
                .telefone(registroRequest.getTelefone())
                .dataNascimento(registroRequest.getDataNascimento())
                .genero(registroRequest.getGenero())
                .role(Role.USER)
                .ativo(true)
                .emailVerificado(false)
                .provider(AuthProvider.LOCAL)
                .build();

        usuario = usuarioRepository.save(usuario);

        String token = tokenProvider.generateTokenFromUsername(usuario.getEmail());

        UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario);

        return AuthResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .usuario(usuarioDTO)
                .build();
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RegraDeNegocioException("Usuário não autenticado");
        }

        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getPerfil() {
        Usuario usuario = getUsuarioAutenticado();
        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO atualizarPerfil(RegistroRequestDTO updateRequest) {
        Usuario usuario = getUsuarioAutenticado();

        if (!usuario.getEmail().equals(updateRequest.getEmail())) {
            if (usuarioRepository.existsByEmail(updateRequest.getEmail())) {
                throw new RegraDeNegocioException("Email já está em uso");
            }
            usuario.setEmail(updateRequest.getEmail());
            usuario.setEmailVerificado(false);
        }

        usuario.setNome(updateRequest.getNome());
        usuario.setSobrenome(updateRequest.getSobrenome());
        usuario.setTelefone(updateRequest.getTelefone());
        usuario.setDataNascimento(updateRequest.getDataNascimento());
        usuario.setGenero(updateRequest.getGenero());

        if (updateRequest.getSenha() != null && !updateRequest.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(updateRequest.getSenha()));
        }

        usuario = usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public void alterarSenha(AlterarSenhaRequestDTO request) {
        Usuario usuario = getUsuarioAutenticado();

        // Validar senha atual
        if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
            throw new RegraDeNegocioException("Senha atual incorreta");
        }

        // Validar confirmação de senha
        if (!request.getNovaSenha().equals(request.getConfirmarNovaSenha())) {
            throw new RegraDeNegocioException("Nova senha e confirmação não coincidem");
        }

        // Atualizar senha
        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);

        log.info("Senha alterada com sucesso para o usuário: {}", usuario.getEmail());
    }
}
