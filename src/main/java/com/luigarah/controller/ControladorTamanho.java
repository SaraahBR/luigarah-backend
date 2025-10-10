package com.luigarah.controller;

import com.luigarah.controller.doc.TamanhoControllerDoc;
import com.luigarah.dto.RespostaProdutoDTO;
import com.luigarah.service.ServicoTamanho;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class ControladorTamanho implements TamanhoControllerDoc {

    private final ServicoTamanho servico;

    public ControladorTamanho(ServicoTamanho servico) {
        this.servico = servico;
    }

    // catálogo (com padrao opcional)
    @GetMapping("/tamanhos")
    @Override
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo retornado."),
            @ApiResponse(responseCode = "400", description = "Categoria/Padrão inválido.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"sucesso\":false,\"mensagem\":\"Categoria inválida. Use: bolsas, roupas ou sapatos\"}")))
    })
    public ResponseEntity<RespostaProdutoDTO<List<String>>> listarCatalogoPorCategoria(
            @RequestParam String categoria,
            @RequestParam(required = false) String padrao
    ) {
        try {
            List<String> lista = servico.listarCatalogoPorCategoria(categoria, padrao);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(lista, "Catálogo de tamanhos encontrado"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao listar catálogo: " + e.getMessage()));
        }
    }

    // por produto
    @GetMapping("/produtos/{id}/tamanhos")
    @Override
    public ResponseEntity<RespostaProdutoDTO<List<String>>> listarTamanhosDoProduto(@PathVariable Long id) {
        return ResponseEntity.ok(RespostaProdutoDTO.sucesso(servico.listarTamanhosDoProduto(id),
                "Tamanhos listados com sucesso"));
    }

    @PutMapping("/produtos/{id}/tamanhos")
    @Override
    public ResponseEntity<RespostaProdutoDTO<List<String>>> substituirTamanhosDoProduto(
            @PathVariable Long id, @RequestBody List<String> etiquetas) {
        if (etiquetas == null || etiquetas.isEmpty()) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro("Envie um array JSON de etiquetas"));
        }
        return ResponseEntity.ok(RespostaProdutoDTO.sucesso(servico.substituirTamanhosDoProduto(id, etiquetas),
                "Tamanhos substituídos com sucesso"));
    }

    @PatchMapping("/produtos/{id}/tamanhos")
    @Override
    public ResponseEntity<RespostaProdutoDTO<List<String>>> adicionarTamanhosAoProduto(
            @PathVariable Long id, @RequestBody List<String> etiquetas) {
        if (etiquetas == null || etiquetas.isEmpty()) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro("Envie um array JSON de etiquetas"));
        }
        return ResponseEntity.ok(RespostaProdutoDTO.sucesso(servico.adicionarTamanhosAoProduto(id, etiquetas),
                "Tamanhos adicionados com sucesso"));
    }

    @DeleteMapping("/produtos/{id}/tamanhos/{etiqueta}")
    @Override
    public ResponseEntity<RespostaProdutoDTO<Object>> removerTamanhoDoProduto(
            @PathVariable Long id, @PathVariable String etiqueta) {
        boolean ok = servico.removerTamanhoDoProduto(id, etiqueta);
        if (!ok) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(RespostaProdutoDTO.erro("Tamanho '%s' não encontrado para o produto %d".formatted(etiqueta, id)));
        }
        return ResponseEntity.ok(RespostaProdutoDTO.sucesso(null, "Tamanho removido com sucesso"));
    }
}
