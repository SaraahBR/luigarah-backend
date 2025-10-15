package com.luigarah.service.autenticacao;

import com.luigarah.config.JwtTokenProvider;
import com.luigarah.dto.autenticacao.AlterarSenhaRequestDTO;
import com.luigarah.dto.autenticacao.AuthResponseDTO;
import com.luigarah.dto.autenticacao.LoginRequestDTO;
import com.luigarah.dto.autenticacao.RegistroRequestDTO;
import com.luigarah.dto.autenticacao.OAuthSyncRequest;
import com.luigarah.dto.usuario.AtualizarPerfilRequest;
import com.luigarah.dto.usuario.EnderecoDTO;
import com.luigarah.dto.usuario.UsuarioDTO;
import com.luigarah.exception.RecursoNaoEncontradoException;
import com.luigarah.exception.RegraDeNegocioException;
import com.luigarah.mapper.usuario.EnderecoMapper;
import com.luigarah.mapper.usuario.UsuarioMapper;
import com.luigarah.model.autenticacao.AuthProvider;
import com.luigarah.model.autenticacao.OAuthProvider;
import com.luigarah.model.autenticacao.Role;
import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.model.usuario.Endereco;
import com.luigarah.repository.autenticacao.EnderecoRepository;
import com.luigarah.repository.autenticacao.UsuarioRepository;
import com.luigarah.repository.autenticacao.OAuthProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;
    private final OAuthProviderRepository oAuthProviderRepository;
    private final EnderecoRepository enderecoRepository;
    private final EnderecoMapper enderecoMapper;

    // Map para controlar locks por email (evita race condition)
    private final ConcurrentHashMap<String, Object> emailLocks = new ConcurrentHashMap<>();

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
                .fotoUrl(registroRequest.getFotoUrl())
                .role(Role.USER)
                .ativo(true)
                .emailVerificado(false)
                .provider(AuthProvider.LOCAL)
                .build();

        usuario = usuarioRepository.save(usuario);

        // Gera token com as authorities corretas do usuário
        String token = generateTokenForUser(usuario);

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

        // Atualiza foto de perfil se fornecida
        if (updateRequest.getFotoUrl() != null && !updateRequest.getFotoUrl().isBlank()) {
            usuario.setFotoUrl(updateRequest.getFotoUrl());
        }

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

    /**
     * Sincroniza conta OAuth com o backend.
     * - Se e-mail existe: vincula OAuth e retorna JWT
     * - Se e-mail não existe: cria conta e retorna JWT
     */
    @Transactional
    public AuthResponseDTO syncOAuth(OAuthSyncRequest request) {
        log.info("Sincronizando conta OAuth - Provider: {}, Email: {}", request.getProvider(), request.getEmail());

        // Obtém ou cria um lock específico para este email
        Object lock = emailLocks.computeIfAbsent(request.getEmail(), k -> new Object());

        try {
            synchronized (lock) {
                return executeSyncOAuth(request);
            }
        } finally {
            // Remove o lock se não houver mais threads aguardando
            emailLocks.remove(request.getEmail());
        }
    }

    /**
     * Execução sincronizada do sync OAuth
     */
    private AuthResponseDTO executeSyncOAuth(OAuthSyncRequest request) {
        // 1. Busca usuário por e-mail
        Usuario usuario;
        boolean isNewUser = false;

        var usuarioOpt = usuarioRepository.findByEmail(request.getEmail());

        if (usuarioOpt.isPresent()) {
            // E-mail já existe: vincula OAuth
            usuario = usuarioOpt.get();
            log.info("Usuário existente encontrado: {}", usuario.getEmail());

            // Atualiza foto se fornecida
            if (request.getFotoPerfil() != null && !request.getFotoPerfil().isEmpty()) {
                usuario.setFotoUrl(request.getFotoPerfil());
            }

            // Atualiza provider se estava como LOCAL
            if (usuario.getProvider() == AuthProvider.LOCAL) {
                AuthProvider authProvider = mapStringToAuthProvider(request.getProvider());
                if (authProvider != null) {
                    usuario.setProvider(authProvider);
                }
            }

        } else {
            // E-mail não existe: cria nova conta
            isNewUser = true;
            log.info("Criando novo usuário OAuth: {}", request.getEmail());

            AuthProvider authProvider = mapStringToAuthProvider(request.getProvider());

            usuario = Usuario.builder()
                    .nome(request.getNome())
                    .sobrenome(request.getSobrenome())
                    .email(request.getEmail())
                    .fotoUrl(request.getFotoPerfil())
                    .role(Role.USER)
                    .ativo(true)
                    .emailVerificado(true) // OAuth já verifica o email
                    .provider(authProvider != null ? authProvider : AuthProvider.LOCAL)
                    .providerId(request.getOauthId())
                    .senha(passwordEncoder.encode(java.util.UUID.randomUUID().toString())) // Senha aleatória
                    .build();

            usuario = usuarioRepository.save(usuario);
        }

        // 2. Vincula ou atualiza OAuth provider
        var providerOpt = oAuthProviderRepository
                .findByUsuarioIdAndProvider(usuario.getId(), request.getProvider());

        OAuthProvider oauthProvider;

        if (providerOpt.isPresent()) {
            // Atualiza provider existente
            oauthProvider = providerOpt.get();
            if (request.getOauthId() != null && !request.getOauthId().isEmpty()) {
                oauthProvider.setProviderId(request.getOauthId());
            }
            log.info("OAuth provider atualizado: {}", request.getProvider());
        } else {
            // Cria novo provider
            oauthProvider = OAuthProvider.builder()
                    .usuario(usuario)
                    .provider(request.getProvider())
                    .providerId(request.getOauthId())
                    .build();
            log.info("Novo OAuth provider criado: {}", request.getProvider());
        }

        oAuthProviderRepository.save(oauthProvider);

        // 3. Atualiza último acesso
        usuario.setUltimoAcesso(LocalDateTime.now());
        usuario = usuarioRepository.save(usuario);

        // 4. Gera token JWT com as authorities corretas do usuário
        String token = generateTokenForUser(usuario);

        // 5. Converte para DTO
        UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario);

        log.info("OAuth sync concluído com sucesso. Novo usuário: {}", isNewUser);

        // 6. Retorna response
        return AuthResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .usuario(usuarioDTO)
                .build();
    }

    /**
     * Gera token JWT para um usuário com suas roles corretas
     */
    private String generateTokenForUser(Usuario usuario) {
        // ✅ CORREÇÃO: Criar um UserDetails a partir do Usuario, não passar String
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getSenha() != null ? usuario.getSenha() : "",
                usuario.getAuthorities()
        );

        // Cria um Authentication object com UserDetails como principal
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                usuario.getAuthorities()
        );

        return tokenProvider.generateToken(auth);
    }

    /**
     * Mapeia string do provider para o enum AuthProvider
     */
    private AuthProvider mapStringToAuthProvider(String provider) {
        if (provider == null) {
            return null;
        }

        return switch (provider.toLowerCase()) {
            case "google" -> AuthProvider.GOOGLE;
            case "facebook" -> AuthProvider.FACEBOOK;
            case "github" -> AuthProvider.GITHUB;
            default -> null;
        };
    }

    /**
     * ✅ NOVO: Atualiza perfil usando email do JWT (mais seguro)
     * Email vem do token, não do body do request
     */
    @Transactional
    public UsuarioDTO atualizarPerfilComJWT(AtualizarPerfilRequest request, String email) {
        log.info("📝 Atualizando perfil do usuário: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        // Atualiza apenas os campos fornecidos (null-safe)
        if (request.getNome() != null && !request.getNome().isBlank()) {
            usuario.setNome(request.getNome());
        }
        if (request.getSobrenome() != null && !request.getSobrenome().isBlank()) {
            usuario.setSobrenome(request.getSobrenome());
        }
        if (request.getTelefone() != null && !request.getTelefone().isBlank()) {
            // Telefone já vem limpo do setter do DTO
            usuario.setTelefone(request.getTelefone());
        }
        if (request.getDataNascimento() != null) {
            usuario.setDataNascimento(request.getDataNascimento());
        }
        if (request.getGenero() != null && !request.getGenero().isBlank()) {
            usuario.setGenero(request.getGenero());
        }
        if (request.getFotoUrl() != null && !request.getFotoUrl().isBlank()) {
            usuario.setFotoUrl(request.getFotoUrl());
        }

        // Salva os dados pessoais primeiro
        usuario = usuarioRepository.save(usuario);

        // ✅ PROCESSA ENDEREÇOS se fornecidos
        if (request.getEnderecos() != null && !request.getEnderecos().isEmpty()) {
            log.info("📍 Processando {} endereços recebidos", request.getEnderecos().size());
            atualizarEnderecosUsuario(usuario, request.getEnderecos());
        }

        log.info("✅ Perfil atualizado com sucesso!");

        return usuarioMapper.toDTO(usuario);
    }

    /**
     * Atualiza os endereços do usuário
     * Remove endereços antigos e adiciona os novos
     */
    private void atualizarEnderecosUsuario(Usuario usuario, List<EnderecoDTO> enderecosDTO) {
        log.info("🏠 Atualizando endereços do usuário ID: {}", usuario.getId());

        // 1. Remove todos os endereços antigos
        enderecoRepository.deleteByUsuarioId(usuario.getId());
        enderecoRepository.flush();
        log.info("🗑️ Endereços antigos removidos");

        // 2. Adiciona os novos endereços
        List<Endereco> novosEnderecos = new ArrayList<>();
        boolean temPrincipal = false;

        for (EnderecoDTO dto : enderecosDTO) {
            Endereco endereco = enderecoMapper.toEntity(dto);
            endereco.setUsuario(usuario);

            // Garante que apenas um endereço seja principal
            if (dto.getPrincipal() != null && dto.getPrincipal() && !temPrincipal) {
                endereco.setPrincipal(true);
                temPrincipal = true;
                log.info("✅ Endereço marcado como principal: {}, {}", endereco.getRua(), endereco.getNumero());
            } else {
                endereco.setPrincipal(false);
            }

            novosEnderecos.add(endereco);
        }

        // 3. Se nenhum foi marcado como principal, marca o primeiro
        if (!temPrincipal && !novosEnderecos.isEmpty()) {
            novosEnderecos.get(0).setPrincipal(true);
            log.info("✅ Primeiro endereço marcado como principal automaticamente");
        }

        // 4. Salva todos os endereços
        enderecoRepository.saveAll(novosEnderecos);
        log.info("✅ {} endereços salvos com sucesso!", novosEnderecos.size());
    }

    /**
     * ✅ NOVO: Atualiza apenas a foto de perfil
     */
    @Transactional
    public UsuarioDTO atualizarFotoPerfil(String fotoUrl, String email) {
        log.info("📸 Atualizando foto de perfil do usuário: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        usuario.setFotoUrl(fotoUrl);
        usuario = usuarioRepository.save(usuario);

        log.info("✅ Foto atualizada com sucesso!");

        return usuarioMapper.toDTO(usuario);
    }

    /**
     * ✅ NOVO: Remove a foto de perfil
     */
    @Transactional
    public void removerFotoPerfil(String email) {
        log.info("🗑️ Removendo foto de perfil do usuário: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        usuario.setFotoUrl(null);
        usuarioRepository.save(usuario);

        log.info("✅ Foto removida com sucesso!");
    }

    /**
     * ✅ NOVO: Adiciona ou atualiza um endereço para o usuário autenticado
     */
    @Transactional
    public EnderecoDTO salvarEndereco(EnderecoDTO enderecoDTO) {
        Usuario usuario = getUsuarioAutenticado();

        Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
        endereco.setUsuario(usuario);

        endereco = enderecoRepository.save(endereco);

        log.info("✅ Endereço salvo com sucesso!");

        return enderecoMapper.toDTO(endereco);
    }

    /**
     * ✅ NOVO: Busca todos os endereços do usuário autenticado
     */
    @Transactional(readOnly = true)
    public List<EnderecoDTO> listarEnderecos() {
        Usuario usuario = getUsuarioAutenticado();

        List<Endereco> enderecos = enderecoRepository.findByUsuarioId(usuario.getId());

        return enderecos.stream()
                .map(enderecoMapper::toDTO)
                .toList();
    }

    /**
     * ✅ NOVO: Remove um endereço pelo ID
     */
    @Transactional
    public void removerEndereco(Long enderecoId) {
        Usuario usuario = getUsuarioAutenticado();

        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço não encontrado"));

        // Verifica se o endereço pertence ao usuário
        if (!endereco.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraDeNegocioException("Acesso negado");
        }

        enderecoRepository.delete(endereco);

        log.info("✅ Endereço removido com sucesso!");
    }
}
