package com.luigarah.controller.produto;

import com.luigarah.controller.doc.ProdutoIdentidadeControllerDoc;
import com.luigarah.dto.produto.ProdutoDTO;
import com.luigarah.dto.produto.ProdutoIdentidadeDTO;
import com.luigarah.mapper.produto.ProdutoMapper;
import com.luigarah.model.produto.Produto;
import com.luigarah.service.produto.ServicoProdutoIdentidade;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Gerenciamento de produtos e suas identidades")
public class ControladorProdutoIdentidade implements ProdutoIdentidadeControllerDoc {

    private final ServicoProdutoIdentidade servicoProdutoIdentidade;
    private final ProdutoMapper produtoMapper;

    @Override
    @PutMapping("/{produtoId}/identidade")
    public ResponseEntity<ProdutoDTO> atribuirIdentidade(
            @PathVariable Long produtoId,
            @RequestBody ProdutoIdentidadeDTO dto) {
        Produto produto = servicoProdutoIdentidade.atribuirIdentidade(produtoId, dto.getIdentidadeId());
        return ResponseEntity.ok(produtoMapper.toDTO(produto));
    }

    @Override
    @DeleteMapping("/{produtoId}/identidade")
    public ResponseEntity<ProdutoDTO> removerIdentidade(@PathVariable Long produtoId) {
        Produto produto = servicoProdutoIdentidade.removerIdentidade(produtoId);
        return ResponseEntity.ok(produtoMapper.toDTO(produto));
    }

    @Override
    @GetMapping("/com-identidade")
    public ResponseEntity<List<ProdutoDTO>> listarProdutosComIdentidade() {
        List<Produto> produtos = servicoProdutoIdentidade.buscarProdutosComIdentidade();
        List<ProdutoDTO> produtoDTOs = produtos.stream()
                .map(produtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtoDTOs);
    }

    @Override
    @GetMapping("/sem-identidade")
    public ResponseEntity<List<ProdutoDTO>> listarProdutosSemIdentidade() {
        List<Produto> produtos = servicoProdutoIdentidade.buscarProdutosSemIdentidade();
        List<ProdutoDTO> produtoDTOs = produtos.stream()
                .map(produtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtoDTOs);
    }

    @Override
    @GetMapping("/identidade/{identidadeId}")
    public ResponseEntity<List<ProdutoDTO>> buscarPorIdentidadeId(@PathVariable Long identidadeId) {
        List<Produto> produtos = servicoProdutoIdentidade.buscarProdutosPorIdentidadeId(identidadeId);
        List<ProdutoDTO> produtoDTOs = produtos.stream()
                .map(produtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtoDTOs);
    }

    @Override
    @GetMapping("/identidade/codigo/{codigo}")
    public ResponseEntity<List<ProdutoDTO>> buscarPorIdentidadeCodigo(@PathVariable String codigo) {
        List<Produto> produtos = servicoProdutoIdentidade.buscarProdutosPorIdentidadeCodigo(codigo);
        List<ProdutoDTO> produtoDTOs = produtos.stream()
                .map(produtoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtoDTOs);
    }
}
