package com.luigarah.controller.doc;

import com.luigarah.dto.carrinho.CarrinhoItemDTO;
import com.luigarah.dto.carrinho.CarrinhoItemRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Documentação dos endpoints do carrinho de compras (Swagger/OpenAPI).
 */
@Tag(
        name = "Carrinho de Compras",
        description = """
                Gerenciamento do carrinho de compras do usuário autenticado.
                • Requer autenticação (token JWT no header Authorization).
                • Cada usuário tem seu próprio carrinho isolado.
                """
)
public interface CarrinhoControllerDoc {

    @Operation(
            summary = "Listar itens do carrinho",
            description = "Retorna todos os itens do carrinho do usuário autenticado, ordenados por data de adição (mais recentes primeiro)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Itens listados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarrinhoItemDTO.class),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "id": 1,
                                        "produto": {
                                          "id": 14,
                                          "titulo": "Maria Lucia Hohan",
                                          "subtitulo": "Vestido",
                                          "preco": 20753
                                        },
                                        "tamanho": {
                                          "id": 5,
                                          "etiqueta": "M",
                                          "nome": "Medium"
                                        },
                                        "quantidade": 2
                                      }
                                    ]
                                    """))),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<List<CarrinhoItemDTO>> listarItens();

    @Operation(
            summary = "Adicionar item ao carrinho",
            description = """
                    Adiciona um produto ao carrinho com a quantidade e tamanho especificados.
                    • Se o item já existir (mesmo produto e tamanho), incrementa a quantidade.
                    • Para produtos sem tamanho (bolsas), não envie o campo tamanhoId.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarrinhoItemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produto ou tamanho não encontrado"),
            @ApiResponse(responseCode = "400", description = "Quantidade excede o máximo (99)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<CarrinhoItemDTO> adicionarItem(
            @RequestBody(description = "Dados do item a adicionar", required = true)
            CarrinhoItemRequestDTO request
    );

    @Operation(
            summary = "Atualizar quantidade de um item",
            description = "Atualiza a quantidade de um item específico do carrinho."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantidade atualizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarrinhoItemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "403", description = "Item não pertence ao usuário"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida (deve estar entre 1 e 99)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<CarrinhoItemDTO> atualizarQuantidade(Long itemId, Integer quantidade);

    @Operation(
            summary = "Remover item do carrinho",
            description = "Remove um item específico do carrinho."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "403", description = "Item não pertence ao usuário"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Void> removerItem(Long itemId);

    @Operation(
            summary = "Limpar carrinho",
            description = "Remove todos os itens do carrinho do usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrinho limpo com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Void> limparCarrinho();

    @Operation(
            summary = "Contar itens do carrinho",
            description = "Retorna a quantidade total de itens diferentes no carrinho."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "5"))),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<Long> contarItens();
}
