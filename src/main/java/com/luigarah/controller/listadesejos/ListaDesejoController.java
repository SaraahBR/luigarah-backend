package com.luigarah.controller.listadesejos;

import com.luigarah.controller.doc.ListaDesejoControllerDoc;
import com.luigarah.dto.listadesejos.ListaDesejoItemDTO;
import com.luigarah.service.listadesejos.ListaDesejoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller da lista de desejos.
 */
@RestController
@RequestMapping("/api/lista-desejos")
@RequiredArgsConstructor
public class ListaDesejoController implements ListaDesejoControllerDoc {

    private final ListaDesejoService listaDesejoService;

    @GetMapping
    public ResponseEntity<List<ListaDesejoItemDTO>> listarItens() {
        List<ListaDesejoItemDTO> itens = listaDesejoService.listarItens();
        return ResponseEntity.ok(itens);
    }

    @PostMapping("/{produtoId}")
    public ResponseEntity<ListaDesejoItemDTO> adicionarItem(@PathVariable Long produtoId) {
        ListaDesejoItemDTO item = listaDesejoService.adicionarItem(produtoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removerItem(@PathVariable Long itemId) {
        listaDesejoService.removerItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/produto/{produtoId}")
    public ResponseEntity<Void> removerPorProdutoId(@PathVariable Long produtoId) {
        listaDesejoService.removerPorProdutoId(produtoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verificar/{produtoId}")
    public ResponseEntity<Boolean> verificarSeEstaNosFavoritos(@PathVariable Long produtoId) {
        Boolean estaNosFavoritos = listaDesejoService.verificarSeEstaNosFavoritos(produtoId);
        return ResponseEntity.ok(estaNosFavoritos);
    }

    @DeleteMapping
    public ResponseEntity<Void> limparLista() {
        listaDesejoService.limparLista();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contarItens() {
        Long count = listaDesejoService.contarItens();
        return ResponseEntity.ok(count);
    }
}
