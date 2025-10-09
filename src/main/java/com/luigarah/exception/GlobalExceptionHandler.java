package com.luigarah.exception;

import com.luigarah.dto.RespostaProdutoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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