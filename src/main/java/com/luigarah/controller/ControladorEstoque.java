package com.luigarah.controller;

import com.luigarah.controller.doc.EstoqueControllerDoc;
import com.luigarah.dto.ProdutoTamanhoDTO;
import com.luigarah.dto.RespostaProdutoDTO;
import com.luigarah.service.ServicoEstoque;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class ControladorEstoque implements EstoqueControllerDoc {

    private final ServicoEstoque servico;

    public ControladorEstoque(ServicoEstoque servico) {
        this.servico = servico;
    }

    @GetMapping("/produtos/{id}/estoque")
    @Override
    public ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> listarEstoque(@PathVariable Long id) {
        try {
            var lista = servico.listarEstoqueDoProduto(id);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(lista, "Estoque listado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao listar estoque: " + e.getMessage()));
        }
    }

    @PutMapping("/produtos/{id}/estoque")
    @Override
    public ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> atualizarEstoqueEmMassa(
            @PathVariable Long id, @RequestBody List<ProdutoTamanhoDTO> itens) {
        try {
            var out = servico.atualizarEstoqueEmMassa(id, itens);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(out, "Estoque atualizado com sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao atualizar estoque: " + e.getMessage()));
        }
    }

    // patch unitário COM tamanho (roupas/sapatos)
    @PatchMapping("/produtos/{id}/estoque/{etiqueta}")
    @Override
    public ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> atualizarEstoquePorEtiqueta(
            @PathVariable Long id,
            @PathVariable String etiqueta,
            @RequestParam(defaultValue = "set") String modo,
            @RequestParam int valor) {
        try {
            var out = servico.atualizarEstoqueUnitario(id, etiqueta, modo, valor);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(out, "Estoque atualizado com sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao atualizar estoque: " + e.getMessage()));
        }
    }

    // patch unitário SEM tamanho (bolsas)
    @PatchMapping("/produtos/{id}/estoque")
    @Override
    public ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> atualizarEstoqueSemTamanho(
            @PathVariable Long id,
            @RequestParam(defaultValue = "set") String modo,
            @RequestParam int valor) {
        try {
            var out = servico.atualizarEstoqueUnitario(id, null, modo, valor);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(out, "Estoque atualizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao atualizar estoque: " + e.getMessage()));
        }
    }
}
