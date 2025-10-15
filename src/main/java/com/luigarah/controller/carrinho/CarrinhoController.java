package com.luigarah.controller.carrinho;

import com.luigarah.controller.doc.CarrinhoControllerDoc;
import com.luigarah.dto.carrinho.CarrinhoItemDTO;
import com.luigarah.dto.carrinho.CarrinhoItemRequestDTO;
import com.luigarah.service.carrinho.CarrinhoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller do carrinho de compras.
 */
@RestController
@RequestMapping("/api/carrinho")
@RequiredArgsConstructor
public class CarrinhoController implements CarrinhoControllerDoc {

    private final CarrinhoService carrinhoService;

    @Override
    @GetMapping
    public ResponseEntity<List<CarrinhoItemDTO>> listarItens() {
        List<CarrinhoItemDTO> itens = carrinhoService.listarItens();
        return ResponseEntity.ok(itens);
    }

    @Override
    @PostMapping
    public ResponseEntity<CarrinhoItemDTO> adicionarItem(@Valid @RequestBody CarrinhoItemRequestDTO request) {
        CarrinhoItemDTO item = carrinhoService.adicionarItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @Override
    @PutMapping("/{itemId}")
    public ResponseEntity<CarrinhoItemDTO> atualizarQuantidade(
            @PathVariable Long itemId,
            @RequestParam Integer quantidade) {
        CarrinhoItemDTO item = carrinhoService.atualizarQuantidade(itemId, quantidade);
        return ResponseEntity.ok(item);
    }

    @Override
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removerItem(@PathVariable Long itemId) {
        carrinhoService.removerItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> limparCarrinho() {
        carrinhoService.limparCarrinho();
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/count")
    public ResponseEntity<Long> contarItens() {
        Long count = carrinhoService.contarItens();
        return ResponseEntity.ok(count);
    }
}
