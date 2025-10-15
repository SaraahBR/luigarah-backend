package com.luigarah.exception;

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

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarProdutoNaoEncontrado(ProductNotFoundException ex) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarRegraDeNegocio(RegraDeNegocioException ex) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarCredenciaisInvalidas(BadCredentialsException ex) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro("Email ou senha incorretos");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarUsuarioNaoEncontrado(UsernameNotFoundException ex) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro("Email ou senha incorretos");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarErroAutenticacao(AuthenticationException ex) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro("Erro na autenticação: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaProdutoDTO<Map<String, String>>> tratarValidacao(MethodArgumentNotValidException ex) {
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
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarArgumentoIlegal(IllegalArgumentException ex) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaProdutoDTO<Object>> tratarExcecaoGeral(Exception ex) {
        RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro("Erro interno do servidor: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }
}