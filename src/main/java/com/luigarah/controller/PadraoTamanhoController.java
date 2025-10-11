package com.luigarah.controller;

import com.luigarah.controller.doc.PadraoTamanhoControllerDoc;
import com.luigarah.dto.PadraoAtualizacaoDTO;
import com.luigarah.dto.PadraoItemDTO;
import com.luigarah.dto.RespostaProdutoDTO;
import com.luigarah.service.ServicoPadraoTamanho;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class PadraoTamanhoController implements PadraoTamanhoControllerDoc {

    private final ServicoPadraoTamanho servico;

    public PadraoTamanhoController(ServicoPadraoTamanho servico) {
        this.servico = servico;
    }

    // -------- LISTAS --------
    @GetMapping("/padroes/produtos")
    @Override
    public ResponseEntity<RespostaProdutoDTO<List<PadraoItemDTO>>> listarProdutos(@RequestParam(required = false) String padrao) {
        try {
            var lista = servico.listarProdutosPorPadrao(padrao);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(lista, "Produtos listados por padrão."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao listar: " + e.getMessage()));
        }
    }

    @GetMapping("/padroes/tamanhos")
    @Override
    public ResponseEntity<RespostaProdutoDTO<List<PadraoItemDTO>>> listarTamanhos(@RequestParam(required = false) String padrao) {
        try {
            var lista = servico.listarTamanhosPorPadrao(padrao);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(lista, "Tamanhos listados por padrão."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao listar: " + e.getMessage()));
        }
    }

    // -------- PRODUTO: um --------
    @PatchMapping("/produtos/{id}/padrao")
    @Override
    public ResponseEntity<RespostaProdutoDTO<PadraoItemDTO>> setPadraoProduto(@PathVariable Long id, @RequestParam(required = false) String padrao) {
        try {
            var out = servico.definirPadraoEmProduto(id, padrao);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(out, "Padrão do produto atualizado."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao atualizar: " + e.getMessage()));
        }
    }

    @DeleteMapping("/produtos/{id}/padrao")
    @Override
    public ResponseEntity<RespostaProdutoDTO<PadraoItemDTO>> clearPadraoProduto(@PathVariable Long id) {
        try {
            var out = servico.limparPadraoDeProduto(id);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(out, "Padrão do produto limpo (indefinido)."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao limpar: " + e.getMessage()));
        }
    }

    // -------- PRODUTO: lote --------
    @PatchMapping("/padroes/produtos")
    @Override
    public ResponseEntity<RespostaProdutoDTO<Object>> setPadraoProdutos(@RequestBody PadraoAtualizacaoDTO dto) {
        try {
            servico.definirPadraoEmProdutos(dto.getPadrao(), dto.getProdutoIds());
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(null, "Padrão aplicado aos produtos."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao atualizar: " + e.getMessage()));
        }
    }

    // -------- TAMANHO: um --------
    @PatchMapping("/tamanhos/{id}/padrao")
    @Override
    public ResponseEntity<RespostaProdutoDTO<PadraoItemDTO>> setPadraoTamanho(@PathVariable Long id, @RequestParam(required = false) String padrao) {
        try {
            var out = servico.definirPadraoEmTamanho(id, padrao);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(out, "Padrão do tamanho atualizado."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao atualizar: " + e.getMessage()));
        }
    }

    @DeleteMapping("/tamanhos/{id}/padrao")
    @Override
    public ResponseEntity<RespostaProdutoDTO<PadraoItemDTO>> clearPadraoTamanho(@PathVariable Long id) {
        try {
            var out = servico.limparPadraoDeTamanho(id);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(out, "Padrão do tamanho limpo (indefinido)."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao limpar: " + e.getMessage()));
        }
    }

    // -------- TAMANHO: lote --------
    @PatchMapping("/padroes/tamanhos")
    @Override
    public ResponseEntity<RespostaProdutoDTO<Object>> setPadraoTamanhos(@RequestBody PadraoAtualizacaoDTO dto) {
        try {
            servico.definirPadraoEmTamanhos(dto.getPadrao(), dto.getTamanhoIds());
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(null, "Padrão aplicado aos tamanhos."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao atualizar: " + e.getMessage()));
        }
    }

    // -------- composto --------
    @PatchMapping("/padroes/aplicar")
    @Override
    public ResponseEntity<RespostaProdutoDTO<Object>> atualizarComposto(@RequestBody PadraoAtualizacaoDTO dto) {
        try {
            servico.atualizarEmLote(dto);
            return ResponseEntity.ok(RespostaProdutoDTO.sucesso(null, "Padrão aplicado (composto)."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(RespostaProdutoDTO.erro(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao atualizar: " + e.getMessage()));
        }
    }
}
