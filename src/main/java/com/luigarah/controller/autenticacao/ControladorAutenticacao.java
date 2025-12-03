package com.luigarah.controller.autenticacao;

import com.luigarah.dto.autenticacao.AlterarSenhaRequestDTO;
import com.luigarah.dto.autenticacao.AuthResponseDTO;
import com.luigarah.dto.autenticacao.LoginRequestDTO;
import com.luigarah.dto.autenticacao.OAuthSyncRequest;
import com.luigarah.dto.autenticacao.RegistroRequestDTO;
import com.luigarah.dto.usuario.EnderecoDTO;
import com.luigarah.dto.usuario.UsuarioDTO;
import com.luigarah.service.autenticacao.AuthService;
import com.luigarah.service.storage.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticação", description = "Endpoints para autenticação e gerenciamento de usuários")
public class ControladorAutenticacao {

    private final AuthService authService;
    private final ImageStorageService imageStorageService;

    /**
     * Login com email e senha
     * POST /api/auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "Login de usuário", description = "Autentica usuário com email e senha e retorna token JWT")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Registro de novo usuário
     * POST /api/auth/registrar
     */
    @PostMapping("/registrar")
    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário e retorna token JWT")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO registroRequest) {
        AuthResponseDTO response = authService.registrar(registroRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Sincroniza conta OAuth com o backend
     * POST /api/auth/oauth/sync
     */
    @PostMapping("/oauth/sync")
    @Operation(
        summary = "Sincronizar conta OAuth",
        description = "Cria ou vincula uma conta OAuth (Google, Facebook, GitHub) ao sistema e retorna token JWT"
    )
    public ResponseEntity<AuthResponseDTO> syncOAuth(@Valid @RequestBody OAuthSyncRequest request) {
        AuthResponseDTO response = authService.syncOAuth(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obter perfil do usuário autenticado
     * GET /api/auth/perfil
     */
    @GetMapping("/perfil")
    @Operation(summary = "Obter perfil do usuário", description = "Retorna dados do usuário autenticado")
    public ResponseEntity<UsuarioDTO> getPerfil() {
        UsuarioDTO usuario = authService.getPerfil();
        return ResponseEntity.ok(usuario);
    }

    /**
     * Atualizar perfil do usuário autenticado
     * PUT /api/auth/perfil
     * ✅ Email vem do JWT (não do body)
     * ✅ Senha NÃO pode ser alterada aqui (usar /alterar-senha)
     */
    @PutMapping("/perfil")
    @Operation(summary = "Atualizar perfil", description = "Atualiza dados do usuário autenticado (exceto email e senha)")
    public ResponseEntity<?> atualizarPerfil(
            @Valid @RequestBody AtualizarPerfilRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            System.out.println("📝 Atualizando perfil do usuário: " + userDetails.getUsername());
            System.out.println("📥 Dados recebidos: nome=" + request.getNome() +
                             ", sobrenome=" + request.getSobrenome() +
                             ", telefone=" + request.getTelefone());

            UsuarioDTO usuario = authService.atualizarPerfilComJWT(request, userDetails.getUsername());

            System.out.println("✅ Perfil atualizado com sucesso!");

            return ResponseEntity.ok(
                Map.of(
                    "sucesso", true,
                    "mensagem", "Perfil atualizado com sucesso",
                    "dados", usuario
                )
            );

        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar perfil: " + e.getMessage());
            log.error("Erro ao atualizar perfil", e);

            return ResponseEntity.status(500).body(
                Map.of(
                    "sucesso", false,
                    "mensagem", "Erro ao atualizar perfil: " + e.getMessage()
                )
            );
        }
    }

    /**
     * Atualizar apenas a foto de perfil (por URL)
     * PUT /api/auth/perfil/foto
     */
    @PutMapping("/perfil/foto")
    @Operation(
        summary = "Atualizar foto de perfil por URL",
        description = "Atualiza a URL da foto de perfil do usuário autenticado",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "URL da foto de perfil",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                    {
                      "fotoUrl": "https://exemplo.com/foto.jpg"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Foto atualizada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                    {
                      "sucesso": true,
                      "mensagem": "Foto atualizada com sucesso",
                      "fotoUrl": "https://exemplo.com/foto.jpg"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "URL da foto é obrigatória"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<?> atualizarFotoPerfil(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String fotoUrl = request.get("fotoUrl");

            if (fotoUrl == null || fotoUrl.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("sucesso", false, "mensagem", "URL da foto é obrigatória")
                );
            }

            System.out.println("📸 Atualizando foto de perfil do usuário: " + userDetails.getUsername());

            UsuarioDTO usuario = authService.atualizarFotoPerfil(fotoUrl, userDetails.getUsername());

            return ResponseEntity.ok(
                Map.of(
                    "sucesso", true,
                    "mensagem", "Foto atualizada com sucesso",
                    "fotoUrl", usuario.getFotoPerfil()
                )
            );
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar foto: " + e.getMessage());
            log.error("Erro ao atualizar foto de perfil", e);
            return ResponseEntity.status(500).body(
                Map.of("sucesso", false, "mensagem", "Erro ao atualizar foto: " + e.getMessage())
            );
        }
    }

    /**
     * Remover foto de perfil
     * DELETE /api/auth/perfil/foto
     */
    @DeleteMapping("/perfil/foto")
    @Operation(summary = "Remover foto de perfil", description = "Remove a foto de perfil do usuário autenticado")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Foto removida com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                    {
                      "sucesso": true,
                      "mensagem": "Foto removida com sucesso"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<?> removerFotoPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("🗑️ Removendo foto de perfil do usuário: " + userDetails.getUsername());

            authService.removerFotoPerfil(userDetails.getUsername());

            return ResponseEntity.ok(
                Map.of("sucesso", true, "mensagem", "Foto removida com sucesso")
            );
        } catch (Exception e) {
            System.err.println("❌ Erro ao remover foto: " + e.getMessage());
            log.error("Erro ao remover foto de perfil", e);
            return ResponseEntity.status(500).body(
                Map.of("sucesso", false, "mensagem", "Erro ao remover foto")
            );
        }
    }

    /**
     * Upload de foto de perfil (arquivo)
     * POST /api/auth/perfil/foto/upload
     */
    @PostMapping(value = "/perfil/foto/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload de foto de perfil",
        description = """
            Faz upload de arquivo de imagem para foto de perfil do usuário autenticado.
            
            **Formatos aceitos:** JPG, JPEG, PNG, WEBP, GIF
            **Tamanho máximo:** 5MB
            
            **Exemplo de uso:**
            ```javascript
            const formData = new FormData();
            formData.append('file', fotoFile);
            
            const response = await fetch('/api/auth/perfil/foto/upload', {
              method: 'POST',
              headers: { 'Authorization': `Bearer ${token}` },
              body: formData
            });
            ```
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Foto enviada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                    {
                      "sucesso": true,
                      "mensagem": "Foto enviada com sucesso",
                      "fotoPerfil": "https://pub-xxxxx.r2.dev/usuarios/admin-luigarah-com.jpg"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Arquivo inválido ou muito grande"),
        @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<?> uploadFotoPerfil(
            @Parameter(description = "Arquivo de imagem da foto de perfil", required = true)
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            // Validações
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("sucesso", false, "mensagem", "Arquivo vazio")
                );
            }

            String contentType = file.getContentType();
            if (!imageStorageService.isValidImageType(contentType)) {
                return ResponseEntity.badRequest().body(
                    Map.of("sucesso", false, "mensagem", "Tipo de arquivo inválido. Use: JPG, PNG, WEBP ou GIF")
                );
            }

            // Validar tamanho (máximo 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(
                    Map.of("sucesso", false, "mensagem", "Arquivo muito grande (máx 5MB)")
                );
            }

            log.info("📤 Upload de foto de perfil - Usuário: {}", userDetails.getUsername());
            log.info("📦 Arquivo: {} ({} bytes)", file.getOriginalFilename(), file.getSize());

            // ✅ UPLOAD REAL PARA CLOUDFLARE R2
            String key = imageStorageService.generateKey("usuarios",
                userDetails.getUsername().replace("@", "-").replace(".", "-") + ".jpg");

            String fotoUrl = imageStorageService.save(
                key,
                contentType,
                file.getSize(),
                file.getInputStream()
            );

            log.info("✅ Foto salva no storage: {}", fotoUrl);

            // Atualiza no banco de dados
            UsuarioDTO usuario = authService.atualizarFotoPerfil(fotoUrl, userDetails.getUsername());

            return ResponseEntity.ok(
                Map.of(
                    "sucesso", true,
                    "mensagem", "Foto enviada com sucesso",
                    "fotoPerfil", usuario.getFotoPerfil()
                )
            );

        } catch (Exception e) {
            log.error("❌ Erro ao fazer upload: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(
                Map.of("sucesso", false, "mensagem", "Erro ao fazer upload: " + e.getMessage())
            );
        }
    }

    /**
     * Alterar senha do usuário autenticado
     * PUT /api/auth/alterar-senha
     */
    @PutMapping("/alterar-senha")
    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário autenticado")
    public ResponseEntity<?> alterarSenha(@Valid @RequestBody AlterarSenhaRequestDTO request) {
        try {
            authService.alterarSenha(request);
            return ResponseEntity.ok(
                Map.of("sucesso", true, "mensagem", "Senha alterada com sucesso")
            );
        } catch (Exception e) {
            return ResponseEntity.status(400).body(
                Map.of("sucesso", false, "mensagem", e.getMessage())
            );
        }
    }

    // ============================================================
    // ENDPOINTS DE VERIFICAÇÃO DE EMAIL E RESET DE SENHA
    // ============================================================

    /**
     * Envia código de verificação para confirmação de conta
     * POST /api/auth/enviar-codigo-verificacao
     */
    @PostMapping("/enviar-codigo-verificacao")
    @Operation(
        summary = "Enviar código de verificação",
        description = "Envia um código de 6 dígitos para o email do usuário para confirmação de conta. O código é válido por 12 horas."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Código enviado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                    {
                      "status": 200,
                      "message": "Código de verificação enviado com sucesso"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Email não encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                    {
                      "status": 404,
                      "error": "E-mail não encontrado",
                      "message": "Esta conta não existe. Por favor, crie uma conta primeiro.",
                      "timestamp": "2025-12-03T10:30:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Conta já verificada ou conta OAuth",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                    {
                      "status": 400,
                      "error": "Conta já verificada",
                      "message": "Este e-mail já foi verificado. Faça login para acessar sua conta.",
                      "timestamp": "2025-12-03T10:30:00"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<?> enviarCodigoVerificacao(
            @Valid @RequestBody com.luigarah.dto.autenticacao.EnviarCodigoVerificacaoRequest request) {
        // Deixa as exceções serem tratadas pelo GlobalExceptionHandler para formato padronizado
        authService.enviarCodigoVerificacao(request.getEmail());
        return ResponseEntity.ok(
            Map.of(
                "status", 200,
                "message", "Código de verificação enviado com sucesso"
            )
        );
    }

    /**
     * Verifica código de confirmação de conta
     * POST /api/auth/verificar-codigo
     */
    @PostMapping("/verificar-codigo")
    @Operation(
        summary = "Verificar código de confirmação",
        description = "Verifica o código de 6 dígitos enviado por email e confirma a conta do usuário. Retorna token JWT após confirmação."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Conta verificada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDTO.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Código inválido, expirado ou já utilizado")
    })
    public ResponseEntity<?> verificarCodigo(
            @Valid @RequestBody com.luigarah.dto.autenticacao.VerificarCodigoRequest request) {
        // Deixa as exceções serem tratadas pelo GlobalExceptionHandler
        AuthResponseDTO response = authService.verificarCodigo(request.getEmail(), request.getCodigo());
        return ResponseEntity.ok(response);
    }

    /**
     * Solicita código para redefinição de senha
     * POST /api/auth/solicitar-reset-senha
     */
    @PostMapping("/solicitar-reset-senha")
    @Operation(
        summary = "Solicitar código de redefinição de senha",
        description = "Envia um código de 6 dígitos para o email do usuário para redefinição de senha. O código é válido por 12 horas."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Código enviado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                    {
                      "sucesso": true,
                      "mensagem": "Código de redefinição de senha enviado com sucesso! Verifique seu email."
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Email não cadastrado ou conta OAuth"),
        @ApiResponse(responseCode = "500", description = "Erro ao enviar email")
    })
    public ResponseEntity<?> solicitarResetSenha(
            @Valid @RequestBody com.luigarah.dto.autenticacao.SolicitarResetSenhaRequest request) {
        // Deixa as exceções serem tratadas pelo GlobalExceptionHandler
        authService.solicitarResetSenha(request.getEmail());
        return ResponseEntity.ok(
            Map.of(
                "status", 200,
                "message", "Código de redefinição de senha enviado com sucesso"
            )
        );
    }

    /**
     * Redefine senha usando código de verificação
     * POST /api/auth/redefinir-senha
     */
    @PostMapping("/redefinir-senha")
    @Operation(
        summary = "Redefinir senha com código",
        description = "Redefine a senha do usuário usando o código de 6 dígitos recebido por email."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Senha redefinida com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    example = """
                    {
                      "sucesso": true,
                      "mensagem": "Senha redefinida com sucesso! Você já pode fazer login com a nova senha."
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Código inválido, expirado, já utilizado ou senhas não coincidem")
    })
    public ResponseEntity<?> redefinirSenha(
            @Valid @RequestBody com.luigarah.dto.autenticacao.RedefinirSenhaComCodigoRequest request) {
        // Deixa as exceções serem tratadas pelo GlobalExceptionHandler
        authService.redefinirSenhaComCodigo(
            request.getEmail(),
            request.getCodigo(),
            request.getNovaSenha(),
            request.getConfirmarNovaSenha()
        );
        return ResponseEntity.ok(
            Map.of(
                "status", 200,
                "message", "Senha redefinida com sucesso! Você já pode fazer login com a nova senha."
            )
        );
    }
}
