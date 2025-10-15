package com.luigarah.controller.doc;

import com.luigarah.dto.listadesejos.ListaDesejoItemDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Documentação da API de Lista de Desejos.
 */
@Tag(name = "Lista de Desejos", description = "Endpoints para gerenciar a lista de desejos/favoritos do usuário")
@SecurityRequirement(name = "bearerAuth")
public interface ListaDesejoControllerDoc {

    @Operation(
            summary = "Listar itens da lista de desejos",
            description = "Retorna todos os produtos favoritos do usuário autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de favoritos retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ListaDesejoItemDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            )
    })
    ResponseEntity<List<ListaDesejoItemDTO>> listarItens();

    @Operation(
            summary = "Adicionar produto aos favoritos",
            description = "Adiciona um produto à lista de desejos do usuário autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Produto adicionado aos favoritos com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ListaDesejoItemDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Produto já está na lista de desejos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content
            )
    })
    ResponseEntity<ListaDesejoItemDTO> adicionarItem(
            @Parameter(description = "ID do produto a ser adicionado aos favoritos", required = true)
            @PathVariable Long produtoId
    );

    @Operation(
            summary = "Remover item da lista de desejos",
            description = "Remove um item específico da lista de desejos pelo ID do item"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Item removido com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item não encontrado na lista de desejos",
                    content = @Content
            )
    })
    ResponseEntity<Void> removerItem(
            @Parameter(description = "ID do item na lista de desejos", required = true)
            @PathVariable Long itemId
    );

    @Operation(
            summary = "Remover produto dos favoritos",
            description = "Remove um produto da lista de desejos pelo ID do produto"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Produto removido dos favoritos com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado na lista de desejos",
                    content = @Content
            )
    })
    ResponseEntity<Void> removerPorProdutoId(
            @Parameter(description = "ID do produto a ser removido dos favoritos", required = true)
            @PathVariable Long produtoId
    );

    @Operation(
            summary = "Verificar se produto está nos favoritos",
            description = "Verifica se um produto específico está na lista de desejos do usuário autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Verificação realizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            )
    })
    ResponseEntity<Boolean> verificarSeEstaNosFavoritos(
            @Parameter(description = "ID do produto a ser verificado", required = true)
            @PathVariable Long produtoId
    );

    @Operation(
            summary = "Limpar lista de desejos",
            description = "Remove todos os itens da lista de desejos do usuário autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Lista de desejos limpa com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            )
    })
    ResponseEntity<Void> limparLista();

    @Operation(
            summary = "Contar itens na lista de desejos",
            description = "Retorna o número total de produtos favoritos do usuário autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contagem retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuário não autenticado",
                    content = @Content
            )
    })
    ResponseEntity<Long> contarItens();
}

