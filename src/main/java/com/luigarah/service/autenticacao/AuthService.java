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
import com.luigarah.exception.CodigoVerificacaoInvalidoException;
import com.luigarah.exception.ContaNaoVerificadaException;
import com.luigarah.exception.ContaOAuthExistenteException;
import com.luigarah.exception.CredenciaisInvalidasException;
import com.luigarah.exception.RecursoNaoEncontradoException;
import com.luigarah.exception.RegraDeNegocioException;
import com.luigarah.exception.SenhaInvalidaException;
import com.luigarah.mapper.usuario.EnderecoMapper;
import com.luigarah.mapper.usuario.UsuarioMapper;
import com.luigarah.model.autenticacao.AuthProvider;
import com.luigarah.model.autenticacao.OAuthProvider;
import com.luigarah.model.autenticacao.Role;
import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.model.autenticacao.VerificationToken;
import com.luigarah.model.usuario.Endereco;
import com.luigarah.repository.autenticacao.EnderecoRepository;
import com.luigarah.repository.autenticacao.UsuarioRepository;
import com.luigarah.repository.autenticacao.OAuthProviderRepository;
import com.luigarah.repository.autenticacao.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    // Map para controlar locks por email (evita race condition)
    private final ConcurrentHashMap<String, Object> emailLocks = new ConcurrentHashMap<>();

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        // Verifica se o usuário existe antes de tentar autenticar
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CredenciaisInvalidasException("E-mail ou senha incorretos"));

        // Valida se a conta foi verificada (apenas para contas LOCAL)
        if (usuario.getProvider() == AuthProvider.LOCAL && !usuario.getEmailVerificado()) {
            throw new ContaNaoVerificadaException(
                    "Sua conta ainda não foi verificada. Clique em 'Reenviar código de verificação'."
            );
        }

        try {
            // Tenta autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getSenha()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = tokenProvider.generateToken(authentication);

            usuario.setUltimoAcesso(LocalDateTime.now());
            usuarioRepository.save(usuario);

            UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario);

            return AuthResponseDTO.builder()
                    .token(token)
                    .tipo("Bearer")
                    .usuario(usuarioDTO)
                    .build();

        } catch (BadCredentialsException e) {
            throw new CredenciaisInvalidasException("E-mail ou senha incorretos");
        }
    }

    @Transactional
    public AuthResponseDTO registrar(RegistroRequestDTO registroRequest) {
        String email = registroRequest.getEmail();

        // Valida os requisitos da senha
        validarSenha(registroRequest.getSenha());

        // Verifica se o email já existe
        if (usuarioRepository.existsByEmail(email)) {
            Usuario usuarioExistente = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

            // Cenário 1: Conta OAuth existente
            if (usuarioExistente.getProvider() != AuthProvider.LOCAL) {
                String providerNome = usuarioExistente.getProvider().name();
                throw new ContaOAuthExistenteException(
                        "Esta conta foi criada com " + providerNome + ". Use o botão correspondente para fazer login.",
                        providerNome
                );
            }

            // Cenário 2: Email já cadastrado e verificado
            if (usuarioExistente.getEmailVerificado()) {
                throw new RegraDeNegocioException("Este e-mail já foi verificado. Faça login para acessar sua conta.");
            }

            // Cenário 3: Email já cadastrado mas NÃO verificado
            throw new ContaNaoVerificadaException(
                    "Esta conta já está cadastrada mas não foi verificada. Clique em 'Reenviar código de verificação'."
            );
        }

        // Cria nova conta LOCAL
        Usuario usuario = Usuario.builder()
                .nome(registroRequest.getNome())
                .sobrenome(registroRequest.getSobrenome())
                .email(email)
                .senha(passwordEncoder.encode(registroRequest.getSenha()))
                .telefone(registroRequest.getTelefone())
                .dataNascimento(registroRequest.getDataNascimento())
                .genero(registroRequest.getGenero())
                .fotoPerfil(registroRequest.getFotoPerfil())
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
        if (updateRequest.getFotoPerfil() != null && !updateRequest.getFotoPerfil().isBlank()) {
            usuario.setFotoPerfil(updateRequest.getFotoPerfil());
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
            String fotoParaSalvar = request.getFotoPerfil();
            log.info("📸 Foto recebida do frontend: {}", fotoParaSalvar);

            if (fotoParaSalvar != null && !fotoParaSalvar.isEmpty()) {
                usuario.setFotoPerfil(fotoParaSalvar);
                log.info("✅ Foto de perfil atualizada para: {}", fotoParaSalvar);
            } else {
                log.warn("⚠️ Nenhuma foto fornecida no request");
            }

            // Atualiza provider se estava como LOCAL
            if (usuario.getProvider() == AuthProvider.LOCAL) {
                AuthProvider authProvider = mapStringToAuthProvider(request.getProvider());
                if (authProvider != null) {
                    usuario.setProvider(authProvider);
                }
            }

            usuario = usuarioRepository.save(usuario);

        } else {
            // E-mail não existe: cria nova conta
            isNewUser = true;
            log.info("Criando novo usuário OAuth: {}", request.getEmail());

            String fotoParaSalvar = request.getFotoPerfil();
            log.info("📸 Foto recebida do frontend (novo usuário): {}", fotoParaSalvar);

            AuthProvider authProvider = mapStringToAuthProvider(request.getProvider());

            usuario = Usuario.builder()
                    .nome(request.getNome())
                    .sobrenome(request.getSobrenome())
                    .email(request.getEmail())
                    .fotoPerfil(fotoParaSalvar)
                    .role(Role.USER)
                    .ativo(true)
                    .emailVerificado(true) // OAuth já verifica o email
                    .provider(authProvider != null ? authProvider : AuthProvider.LOCAL)
                    .providerId(request.getOauthId())
                    .senha(passwordEncoder.encode(java.util.UUID.randomUUID().toString())) // Senha aleatória
                    .build();

            usuario = usuarioRepository.save(usuario);
            log.info("✅ Novo usuário criado com ID: {} e foto: {}", usuario.getId(), usuario.getFotoPerfil());
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

        // 6. Envia email de boas-vindas para novos usuários OAuth
        if (isNewUser) {
            try {
                emailService.enviarBoasVindas(usuario.getEmail(), usuario.getNome(), true);
            } catch (Exception e) {
                log.error("Erro ao enviar email de boas-vindas: {}", e.getMessage());
                // Não interrompe o fluxo se o email falhar
            }
        }

        // 7. Retorna response
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
        if (request.getFotoPerfil() != null && !request.getFotoPerfil().isBlank()) {
            usuario.setFotoPerfil(request.getFotoPerfil());
        }
        if (request.getReceberNovidades() != null) {
            usuario.setReceberNovidades(request.getReceberNovidades());
        }
        if (request.getAlertasReposicao() != null) {
            usuario.setAlertasReposicao(request.getAlertasReposicao());
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

        // 1. Remove os endereços antigos pela própria coleção do usuário
        //    (orphanRemoval apaga no banco e mantém a entidade em memória coerente)
        usuario.getEnderecos().clear();
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

        // 4. Salva todos os endereços (cascade do Usuario)
        usuario.getEnderecos().addAll(novosEnderecos);
        usuarioRepository.saveAndFlush(usuario);
        log.info("✅ {} endereços salvos com sucesso!", novosEnderecos.size());
    }

    /**
     * ✅ NOVO: Atualiza apenas a foto de perfil
     */
    @Transactional
    public UsuarioDTO atualizarFotoPerfil(String fotoPerfil, String email) {
        log.info("📸 Atualizando foto de perfil do usuário: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        usuario.setFotoPerfil(fotoPerfil);
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

        usuario.setFotoPerfil(null);
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

    // ============================================================
    // MÉTODOS DE VERIFICAÇÃO DE EMAIL E RESET DE SENHA
    // ============================================================

    /**
     * Envia código de verificação para confirmação de conta
     */
    @Transactional
    public void enviarCodigoVerificacao(String email) {
        log.info("📧 Enviando código de verificação para: {}", email);

        // Cenário 1: Email não encontrado
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Esta conta não existe. Por favor, crie uma conta primeiro."
                ));

        // Cenário 2: Conta OAuth
        if (usuario.getProvider() != AuthProvider.LOCAL) {
            String providerNome = usuario.getProvider().name();
            throw new ContaOAuthExistenteException(
                    "Esta conta foi criada com " + providerNome + ". Use o botão correspondente para fazer login.",
                    providerNome
            );
        }

        // Cenário 3: Conta já verificada
        if (usuario.getEmailVerificado()) {
            throw new RegraDeNegocioException(
                    "Este e-mail já foi verificado. Faça login para acessar sua conta."
            );
        }

        // Cenário 4: Sucesso - envia código
        // Remove tokens antigos de verificação deste email
        verificationTokenRepository.deleteByEmailAndTipo(email, VerificationToken.TipoToken.VERIFICACAO_EMAIL);

        // Gera novo código
        String codigo = gerarCodigoAleatorio();

        // Cria token de verificação (válido por 12 horas)
        VerificationToken token = criarVerificationToken(
                email,
                codigo,
                VerificationToken.TipoToken.VERIFICACAO_EMAIL,
                12
        );

        verificationTokenRepository.save(token);

        // Envia email com o código
        emailService.enviarCodigoVerificacao(email, codigo, usuario.getNome());

        log.info("✅ Código de verificação enviado com sucesso!");
    }

    /**
     * Verifica código de confirmação de conta
     */
    @Transactional
    public AuthResponseDTO verificarCodigo(String email, String codigo) {
        log.info("🔍 Verificando código para: {}", email);

        // Busca o usuário
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        // Busca o token
        VerificationToken token = verificationTokenRepository
                .findLatestByEmailAndTipo(email, VerificationToken.TipoToken.VERIFICACAO_EMAIL)
                .orElseThrow(() -> new CodigoVerificacaoInvalidoException(
                        "Código não encontrado. Solicite um novo código.",
                        "Código não encontrado"
                ));

        // Valida o token
        if (token.isExpirado()) {
            throw new CodigoVerificacaoInvalidoException(
                    "Código expirado. Solicite um novo código.",
                    "Código expirado"
            );
        }

        if (token.getUsado()) {
            throw new CodigoVerificacaoInvalidoException(
                    "Código já foi utilizado. Solicite um novo código.",
                    "Código já utilizado"
            );
        }

        if (!token.getCodigo().equals(codigo)) {
            throw new CodigoVerificacaoInvalidoException(
                    "Código inválido.",
                    "Código inválido"
            );
        }

        // Marca o token como usado
        token.setUsado(true);
        token.setUsadoEm(LocalDateTime.now());
        verificationTokenRepository.save(token);

        // Marca o email como verificado
        usuario.setEmailVerificado(true);
        usuarioRepository.save(usuario);

        log.info("✅ Conta verificada com sucesso!");

        // Envia email de boas-vindas
        emailService.enviarBoasVindas(email, usuario.getNome(), false);

        // Gera token JWT
        String jwtToken = generateTokenForUser(usuario);
        UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario);

        return AuthResponseDTO.builder()
                .token(jwtToken)
                .tipo("Bearer")
                .usuario(usuarioDTO)
                .build();
    }

    /**
     * Solicita código para redefinição de senha
     */
    @Transactional
    public void solicitarResetSenha(String email) {
        log.info("📧 Solicitação de reset de senha para: {}", email);

        // Verifica se o email existe
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Email não cadastrado"));

        // Verifica se é conta local (não OAuth)
        if (usuario.getProvider() != AuthProvider.LOCAL) {
            throw new RegraDeNegocioException("Esta conta foi criada com " + usuario.getProvider() +
                    ". Use o mesmo método para fazer login.");
        }

        // Remove tokens antigos de reset de senha deste email
        verificationTokenRepository.deleteByEmailAndTipo(email, VerificationToken.TipoToken.RESET_SENHA);

        // Gera novo código
        String codigo = gerarCodigoAleatorio();

        // Cria token de reset (válido por 12 horas)
        VerificationToken token = criarVerificationToken(
                email,
                codigo,
                VerificationToken.TipoToken.RESET_SENHA,
                12
        );

        verificationTokenRepository.save(token);

        // Envia email com o código
        emailService.enviarCodigoResetSenha(email, codigo, usuario.getNome());

        log.info("✅ Código de reset de senha enviado com sucesso!");
    }

    /**
     * Redefine senha usando código de verificação
     */
    @Transactional
    public void redefinirSenhaComCodigo(String email, String codigo, String novaSenha, String confirmarNovaSenha) {
        log.info("🔑 Redefinindo senha para: {}", email);

        // Valida se as senhas coincidem
        if (!novaSenha.equals(confirmarNovaSenha)) {
            throw new RegraDeNegocioException("As senhas não coincidem");
        }

        // Valida os requisitos da senha
        validarSenha(novaSenha);

        // Busca o usuário
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        // Busca o token
        VerificationToken token = verificationTokenRepository
                .findLatestByEmailAndTipo(email, VerificationToken.TipoToken.RESET_SENHA)
                .orElseThrow(() -> new CodigoVerificacaoInvalidoException(
                        "Código não encontrado. Solicite um novo código.",
                        "Código não encontrado"
                ));

        // Valida o token
        if (token.isExpirado()) {
            throw new CodigoVerificacaoInvalidoException(
                    "Código expirado. Solicite um novo código.",
                    "Código expirado"
            );
        }

        if (token.getUsado()) {
            throw new CodigoVerificacaoInvalidoException(
                    "Código já foi utilizado. Solicite um novo código.",
                    "Código já utilizado"
            );
        }

        if (!token.getCodigo().equals(codigo)) {
            throw new CodigoVerificacaoInvalidoException(
                    "Código inválido.",
                    "Código inválido"
            );
        }

        // Marca o token como usado
        token.setUsado(true);
        token.setUsadoEm(LocalDateTime.now());
        verificationTokenRepository.save(token);

        // Atualiza a senha
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        log.info("✅ Senha redefinida com sucesso!");
    }

    /**
     * Valida os requisitos da senha
     * Mínimo 6 caracteres, Máximo 40 caracteres, 1 maiúscula, 1 minúscula, 1 número e 1 caractere especial
     */
    private void validarSenha(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new SenhaInvalidaException("A senha é obrigatória");
        }

        if (senha.length() < 6) {
            throw new SenhaInvalidaException("A senha deve ter no mínimo 6 caracteres");
        }

        if (senha.length() > 40) {
            throw new SenhaInvalidaException("A senha deve ter no máximo 40 caracteres");
        }

        if (!senha.matches(".*[A-Z].*")) {
            throw new SenhaInvalidaException("A senha deve conter pelo menos uma letra maiúscula");
        }

        if (!senha.matches(".*[a-z].*")) {
            throw new SenhaInvalidaException("A senha deve conter pelo menos uma letra minúscula");
        }

        if (!senha.matches(".*\\d.*")) {
            throw new SenhaInvalidaException("A senha deve conter pelo menos um número");
        }

        if (!senha.matches(".*[!@#$%^&*(),.?\":{}|<>.\\-_+=\\[\\]';/~`].*")) {
            throw new SenhaInvalidaException("A senha deve conter pelo menos um caractere especial (!@#$%^&*(),.?\":{}|<>.-_+=[]';/~`)");
        }
    }

    /**
     * Gera código aleatório de 6 dígitos
     */
    private String gerarCodigoAleatorio() {
        SecureRandom random = new SecureRandom();
        int codigo = 100000 + random.nextInt(900000); // Gera número entre 100000 e 999999
        return String.valueOf(codigo);
    }

    /**
     * Cria um VerificationToken
     */
    private VerificationToken criarVerificationToken(String email, String codigo,
                                                     VerificationToken.TipoToken tipo, int horasValidade) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime expiracao = agora.plusHours(horasValidade);

        return VerificationToken.builder()
                .codigo(codigo)
                .token(UUID.randomUUID().toString())
                .email(email)
                .tipo(tipo)
                .criadoEm(agora)
                .expiraEm(expiracao)
                .usado(false)
                .build();
    }

    /**
     * Atualiza o método registrar para NÃO enviar email de boas-vindas imediatamente
     * (enviará apenas após verificação)
     */
    @Transactional
    public AuthResponseDTO registrarComVerificacao(RegistroRequestDTO registroRequest) {
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
                .fotoPerfil(registroRequest.getFotoPerfil())
                .role(Role.USER)
                .ativo(true)
                .emailVerificado(false) // Precisa verificar
                .provider(AuthProvider.LOCAL)
                .build();

        usuario = usuarioRepository.save(usuario);

        // Envia código de verificação
        enviarCodigoVerificacao(usuario.getEmail());

        // Gera token com as authorities corretas do usuário
        String token = generateTokenForUser(usuario);

        UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario);

        return AuthResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .usuario(usuarioDTO)
                .build();
    }
}
