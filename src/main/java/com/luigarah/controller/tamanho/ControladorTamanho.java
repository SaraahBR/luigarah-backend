package com.luigarah.controller.tamanho;

import com.luigarah.controller.doc.TamanhoControllerDoc;
import com.luigarah.dto.produto.RespostaProdutoDTO;
import com.luigarah.service.tamanho.ServicoTamanho;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tamanhos")
public class ControladorTamanho implements TamanhoControllerDoc {

    private final ServicoTamanho servico;

    public ControladorTamanho(ServicoTamanho servico) {
        this.servico = servico;
    }

    // catálogo (com padrao opcional)
    @GetMapping
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

    // vários produtos de uma vez (listagens)
    @GetMapping("/produtos")
    @Operation(
            summary = "Tamanhos de vários produtos",
            description = "Devolve { produtoId: [etiquetas] } numa única consulta, para ?ids=1,2,3 "
                    + "ou para todos os produtos de ?categoria=roupas. Com comEstoque=true, só tamanhos "
                    + "com estoque. Na busca por ids, produtos sem tamanho (bolsas) vêm com lista vazia."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tamanhos por produto."),
            @ApiResponse(responseCode = "400", description = "Sem ids nem categoria, categoria inválida ou mais de 500 produtos.")
    })
    public ResponseEntity<RespostaProdutoDTO<Map<Long, List<String>>>> listarTamanhosDosProdutos(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) String categoria,
            @RequestParam(defaultValue = "false") boolean comEstoque
    ) {
        try {
            Map<Long, List<String>> tamanhos;
            if (ids != null && !ids.isEmpty()) {
                tamanhos = servico.listarTamanhosDosProdutos(ids, comEstoque);
            } else if (categoria != null) {
                tamanhos = servico.listarTamanhosDaCategoria(categoria.toLowerCase(), comEstoque);
            } else {
                throw new IllegalArgumentException("Informe ids ou categoria");
            }
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(tamanhos, "Tamanhos listados com sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro(e.getMessage()));
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
