package com.luigarah.exception;

import com.luigarah.dto.error.ErrorResponseDTO;
import com.luigarah.dto.produto.RespostaProdutoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * ✅ CORREÇÃO: Removido getCorsHeaders() - CORS é gerenciado pelo ConfiguracaoCors.java
 * Não precisa adicionar headers manualmente, o CorsFilter já faz isso automaticamente
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarProdutoNaoEncontrado(
            ProductNotFoundException ex, WebRequest request) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex, WebRequest request) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarRegraDeNegocio(
            RegraDeNegocioException ex, WebRequest request) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    // =====================================================
    // HANDLERS ESPECÍFICOS PARA AUTENTICAÇÃO
    // =====================================================

    @ExceptionHandler(ContaNaoVerificadaException.class)
    public ResponseEntity<ErrorResponseDTO> tratarContaNaoVerificada(
            ContaNaoVerificadaException ex, WebRequest request) {
        ErrorResponseDTO erro = ErrorResponseDTO.of(
                403,
                "Verificação pendente",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    @ExceptionHandler(ContaOAuthExistenteException.class)
    public ResponseEntity<ErrorResponseDTO> tratarContaOAuthExistente(
            ContaOAuthExistenteException ex, WebRequest request) {
        ErrorResponseDTO erro = ErrorResponseDTO.of(
                400,
                "Conta OAuth existente",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErrorResponseDTO> tratarCredenciaisInvalidasCustom(
            CredenciaisInvalidasException ex, WebRequest request) {
        ErrorResponseDTO erro = ErrorResponseDTO.of(
                401,
                "Credenciais inválidas",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> tratarCredenciaisInvalidas(
            BadCredentialsException ex, WebRequest request) {
        ErrorResponseDTO erro = ErrorResponseDTO.of(
                401,
                "Credenciais inválidas",
                "E-mail ou senha incorretos"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarUsuarioNaoEncontrado(
            UsernameNotFoundException ex, WebRequest request) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro("Email ou senha incorretos");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarErroAutenticacao(
            AuthenticationException ex, WebRequest request) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro("Erro na autenticação: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaProdutoDTO<Map<String, String>>> tratarValidacao(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((erro) -> {
            String nomeCampo = ((FieldError) erro).getField();
            String mensagemErro = erro.getDefaultMessage();
            erros.put(nomeCampo, mensagemErro);
        });

        RespostaProdutoDTO<Map<String, String>> resposta = new RespostaProdutoDTO<>(
                erros, false, "Dados de entrada inválidos"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarArgumentoIlegal(
            IllegalArgumentException ex, WebRequest request) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarExcecaoGeral(
            Exception ex, WebRequest request) {

        // Log detalhado do erro para debug
        ex.printStackTrace();

        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro(
                "Erro interno do servidor: " + ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }
}