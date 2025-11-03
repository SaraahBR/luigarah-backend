package com.luigarah.controller.carrinho;

import com.luigarah.controller.doc.CarrinhoControllerDoc;
import com.luigarah.dto.carrinho.AtualizarCarrinhoItemDTO;
import com.luigarah.dto.carrinho.CarrinhoItemDTO;
import com.luigarah.dto.carrinho.CarrinhoItemRequestDTO;
import com.luigarah.service.carrinho.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Carrinho", description = "Gerenciamento do carrinho de compras")
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

    /**
     * Atualiza tamanho e quantidade de um item do carrinho.
     *
     * @param itemId ID do item no carrinho
     * @param dto Dados de atualização (tamanhoId e quantidade)
     * @return Item atualizado com informações de estoque
     */
    @PutMapping("/{itemId}/atualizar")
    @Operation(
        summary = "Atualizar tamanho e quantidade do item",
        description = "Atualiza tamanho e quantidade de um item do carrinho com validação de estoque. " +
                      "Para BOLSAS: tamanhoId deve ser null. " +
                      "Para ROUPAS/SAPATOS: tamanhoId é obrigatório."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Estoque insuficiente ou dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Item não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão para alterar este item")
    })
    public ResponseEntity<CarrinhoItemDTO> atualizarItem(
            @Parameter(description = "ID do item no carrinho", required = true)
            @PathVariable Long itemId,
            @Valid @RequestBody AtualizarCarrinhoItemDTO dto) {
        CarrinhoItemDTO item = carrinhoService.atualizarItem(itemId, dto);
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
