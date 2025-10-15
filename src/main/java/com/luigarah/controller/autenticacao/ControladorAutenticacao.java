package com.luigarah.controller.autenticacao;

import com.luigarah.dto.autenticacao.AlterarSenhaRequestDTO;
import com.luigarah.dto.autenticacao.AuthResponseDTO;
import com.luigarah.dto.autenticacao.LoginRequestDTO;
import com.luigarah.dto.autenticacao.OAuthSyncRequest;
import com.luigarah.dto.autenticacao.RegistroRequestDTO;
import com.luigarah.dto.usuario.UsuarioDTO;
import com.luigarah.service.autenticacao.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação e gerenciamento de usuários")
public class ControladorAutenticacao {

    private final AuthService authService;

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
     */
    @PutMapping("/perfil")
    @Operation(summary = "Atualizar perfil", description = "Atualiza dados do usuário autenticado")
    public ResponseEntity<UsuarioDTO> atualizarPerfil(@Valid @RequestBody RegistroRequestDTO updateRequest) {
        UsuarioDTO usuario = authService.atualizarPerfil(updateRequest);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Alterar senha do usuário autenticado
     * PUT /api/auth/alterar-senha
     */
    @PutMapping("/alterar-senha")
    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário autenticado")
    public ResponseEntity<Void> alterarSenha(@Valid @RequestBody AlterarSenhaRequestDTO request) {
        authService.alterarSenha(request);
        return ResponseEntity.ok().build();
    }
}

