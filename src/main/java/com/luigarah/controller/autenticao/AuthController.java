package com.luigarah.controller.autenticacao;

import com.luigarah.controller.doc.AuthControllerDoc;
import com.luigarah.dto.autenticacao.AuthResponseDTO;
import com.luigarah.dto.autenticacao.LoginRequestDTO;
import com.luigarah.dto.autenticacao.RegistroRequestDTO;
import com.luigarah.dto.autenticacao.AlterarSenhaRequestDTO;
import com.luigarah.dto.usuario.UsuarioDTO;
import com.luigarah.service.autenticacao.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticação JWT.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDoc {

    private final AuthService authService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/registrar")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO registroRequest) {
        AuthResponseDTO response = authService.registrar(registroRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> getPerfil() {
        UsuarioDTO usuario = authService.getPerfil();
        return ResponseEntity.ok(usuario);
    }

    @Override
    @PutMapping("/perfil")
    public ResponseEntity<UsuarioDTO> atualizarPerfil(@Valid @RequestBody RegistroRequestDTO updateRequest) {
        UsuarioDTO usuario = authService.atualizarPerfil(updateRequest);
        return ResponseEntity.ok(usuario);
    }

    @Override
    @PutMapping("/alterar-senha")
    public ResponseEntity<String> alterarSenha(@Valid @RequestBody AlterarSenhaRequestDTO request) {
        authService.alterarSenha(request);
        return ResponseEntity.ok("Senha alterada com sucesso");
    }
}
