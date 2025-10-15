package com.luigarah.controller.doc;

import com.luigarah.dto.produto.ProdutoDTO;
import com.luigarah.dto.produto.ProdutoIdentidadeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Documentação central dos endpoints de PRODUTO-IDENTIDADE (Swagger/OpenAPI).
 *
 * Estes endpoints gerenciam a relação entre produtos e identidades (gênero/idade):
 *  • Atribuir identidade a um produto
 *  • Remover identidade de um produto
 *  • Filtrar produtos por identidade
 *  • Listar produtos com/sem identidade
 *
 * Convenções de resposta:
 *  • Sucesso: HTTP 200 com ProdutoDTO ou List<ProdutoDTO>
 *  • Erro:    HTTP 400/404/500 com mensagem de erro
 *
 * Tratamento de erros:
 *  • 400 – parâmetros inválidos
 *  • 404 – produto ou identidade não encontrada
 *  • 500 – erro interno inesperado
 */
@Tag(
        name = "Produtos",
        description = "Endpoints para gerenciamento de produtos e suas identidades (Masculino, Feminino, Unissex, Infantil)."
)
public interface ProdutoIdentidadeControllerDoc {

    // ============================================================
    // ATRIBUIR IDENTIDADE
    // ============================================================

    @Operation(
            summary = "Atribuir identidade a um produto",
            description = """
                Define ou altera a identidade de um produto específico.
                Se o produto já possuir uma identidade, ela será substituída.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Identidade atribuída com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProdutoDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 200",
                                    value = """
                                    {
                                      "id": 1,
                                      "titulo": "Gucci",
                                      "subtitulo": "Bolsa Tiracolo",
                                      "autor": "Alessandro Michele",
                                      "descricao": "Bolsa Dionysus GG Supreme mini",
                                      "preco": 9399.00,
                                      "dimensao": "Mini",
                                      "imagem": "https://example.com/gucci.jpg",
                                      "categoria": "bolsas",
                                      "identidade": {
                                        "id": 2,
                                        "codigo": "mulher",
                                        "nome": "Feminino",
                                        "ordem": 2,
                                        "ativo": "S"
                                      },
                                      "dataCriacao": "2025-10-11T10:00:00",
                                      "dataAtualizacao": "2025-10-11T19:00:00"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto ou identidade não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Produto não encontrado",
                                            value = """
                                            {
                                              "mensagem": "Produto não encontrado com ID: 999"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Identidade não encontrada",
                                            value = """
                                            {
                                              "mensagem": "Identidade não encontrada com ID: 999"
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno.",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<ProdutoDTO> atribuirIdentidade(
            @Parameter(description = "ID do produto", example = "1", required = true)
            Long produtoId,
            @RequestBody(
                    description = "ID da identidade a atribuir",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProdutoIdentidadeDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de requisição",
                                    value = """
                                    {
                                      "identidadeId": 2
                                    }
                                    """
                            )
                    )
            )
            ProdutoIdentidadeDTO dto
    );

    // ============================================================
    // REMOVER IDENTIDADE
    // ============================================================

    @Operation(
            summary = "Remover identidade de um produto",
            description = """
                Remove a identidade atribuída a um produto, deixando-o sem classificação de gênero/idade.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Identidade removida com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProdutoDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 200",
                                    value = """
                                    {
                                      "id": 1,
                                      "titulo": "Gucci",
                                      "subtitulo": "Bolsa Tiracolo",
                                      "autor": "Alessandro Michele",
                                      "descricao": "Bolsa Dionysus GG Supreme mini",
                                      "preco": 9399.00,
                                      "dimensao": "Mini",
                                      "imagem": "https://example.com/gucci.jpg",
                                      "categoria": "bolsas",
                                      "identidade": null,
                                      "dataCriacao": "2025-10-11T10:00:00",
                                      "dataAtualizacao": "2025-10-11T19:30:00"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 404",
                                    value = """
                                    {
                                      "mensagem": "Produto não encontrado com ID: 999"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno.",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<ProdutoDTO> removerIdentidade(
            @Parameter(description = "ID do produto", example = "1", required = true)
            Long produtoId
    );

    // ============================================================
    // LISTAR PRODUTOS COM IDENTIDADE
    // ============================================================

    @Operation(
            summary = "Listar produtos com identidade",
            description = """
                Retorna todos os produtos que possuem alguma identidade atribuída.
                Útil para verificar quais produtos já foram classificados.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProdutoDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 200",
                                    value = """
                                    [
                                      {
                                        "id": 1,
                                        "titulo": "Gucci",
                                        "categoria": "bolsas",
                                        "identidade": {
                                          "id": 2,
                                          "codigo": "mulher",
                                          "nome": "Feminino"
                                        }
                                      },
                                      {
                                        "id": 14,
                                        "titulo": "Maria Lucia Hohan",
                                        "categoria": "roupas",
                                        "identidade": {
                                          "id": 2,
                                          "codigo": "mulher",
                                          "nome": "Feminino"
                                        }
                                      }
                                    ]
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno.",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<List<ProdutoDTO>> listarProdutosComIdentidade();

    // ============================================================
    // LISTAR PRODUTOS SEM IDENTIDADE
    // ============================================================

    @Operation(
            summary = "Listar produtos sem identidade",
            description = """
                Retorna todos os produtos que NÃO possuem identidade atribuída.
                Útil para identificar produtos que precisam ser classificados.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProdutoDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 200",
                                    value = """
                                    [
                                      {
                                        "id": 3,
                                        "titulo": "Zadig&Voltaire",
                                        "categoria": "bolsas",
                                        "identidade": null
                                      },
                                      {
                                        "id": 4,
                                        "titulo": "DeMellier",
                                        "categoria": "bolsas",
                                        "identidade": null
                                      }
                                    ]
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno.",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<List<ProdutoDTO>> listarProdutosSemIdentidade();

    // ============================================================
    // BUSCAR PRODUTOS POR IDENTIDADE (ID)
    // ============================================================

    @Operation(
            summary = "Buscar produtos por ID de identidade",
            description = """
                Retorna todos os produtos que possuem a identidade especificada pelo ID.
                Exemplo: buscar todos os produtos femininos (identidade ID = 2).
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProdutoDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 200",
                                    value = """
                                    [
                                      {
                                        "id": 1,
                                        "titulo": "Gucci",
                                        "categoria": "bolsas",
                                        "identidade": {
                                          "id": 2,
                                          "codigo": "mulher",
                                          "nome": "Feminino"
                                        }
                                      },
                                      {
                                        "id": 2,
                                        "titulo": "Saint Laurent",
                                        "categoria": "bolsas",
                                        "identidade": {
                                          "id": 2,
                                          "codigo": "mulher",
                                          "nome": "Feminino"
                                        }
                                      }
                                    ]
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Identidade não encontrada.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 404",
                                    value = """
                                    {
                                      "mensagem": "Identidade não encontrada com ID: 999"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno.",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<List<ProdutoDTO>> buscarPorIdentidadeId(
            @Parameter(description = "ID da identidade", example = "2", required = true)
            Long identidadeId
    );

    // ============================================================
    // BUSCAR PRODUTOS POR IDENTIDADE (CÓDIGO)
    // ============================================================

    @Operation(
            summary = "Buscar produtos por código de identidade",
            description = """
                Retorna todos os produtos que possuem a identidade especificada pelo código.
                Códigos válidos: <code>homem</code>, <code>mulher</code>, <code>unissex</code>, <code>infantil</code>.
                <br><br>
                Este endpoint é ideal para filtros no frontend, permitindo URLs amigáveis como:
                <code>/api/produtos/identidade/codigo/mulher</code>
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada com sucesso.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProdutoDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo 200 - Produtos femininos",
                                    value = """
                                    [
                                      {
                                        "id": 1,
                                        "titulo": "Gucci",
                                        "subtitulo": "Bolsa Tiracolo",
                                        "categoria": "bolsas",
                                        "preco": 9399.00,
                                        "identidade": {
                                          "id": 2,
                                          "codigo": "mulher",
                                          "nome": "Feminino"
                                        }
                                      },
                                      {
                                        "id": 14,
                                        "titulo": "Maria Lucia Hohan",
                                        "subtitulo": "Vestido",
                                        "categoria": "roupas",
                                        "preco": 20753.00,
                                        "identidade": {
                                          "id": 2,
                                          "codigo": "mulher",
                                          "nome": "Feminino"
                                        }
                                      }
                                    ]
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Código de identidade não encontrado.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo 404",
                                    value = """
                                    {
                                      "mensagem": "Identidade não encontrada com código: xyz"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno.",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<List<ProdutoDTO>> buscarPorIdentidadeCodigo(
            @Parameter(
                    description = "Código da identidade",
                    example = "mulher",
                    required = true,
                    schema = @Schema(allowableValues = {"homem", "mulher", "unissex", "infantil"})
            )
            String codigo
    );
}
