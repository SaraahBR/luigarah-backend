package com.luigarah.controller.doc;

import com.luigarah.dto.tamanho.ProdutoTamanhoDTO;
import com.luigarah.dto.produto.RespostaProdutoDTO;
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

@Tag(
        name = "Estoque",
        description = """
        Gerenciamento de estoque por produto.  
        • **Roupas/Sapatos** → estoque por tamanho (etiqueta).  
        • **Bolsas** → estoque único por produto (sem etiqueta).
        """
)
public interface EstoqueControllerDoc {

    // =============================================================
    // LISTAR ESTOQUE
    // =============================================================

    @Operation(
            summary = "Listar estoque do produto",
            description = """
            • **Roupas/Sapatos** → retorna uma linha por etiqueta com `qtdEstoque`.  
            • **Bolsas** → retorna lista com um único item, `etiqueta = null` e `qtdEstoque` do produto.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque listado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RespostaProdutoDTO.class),
                            examples = {
                                    @ExampleObject(name = "Roupas (BR - TODOS)",
                                            value = """
                                            {
                                              "dados": [
                                                {"etiqueta":"PP","qtdEstoque":8},
                                                {"etiqueta":"P","qtdEstoque":9},
                                                {"etiqueta":"M","qtdEstoque":12},
                                                {"etiqueta":"G","qtdEstoque":7},
                                                {"etiqueta":"XG","qtdEstoque":6},
                                                {"etiqueta":"G1","qtdEstoque":5},
                                                {"etiqueta":"G2","qtdEstoque":4}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Estoque listado com sucesso"
                                            }
                                            """),
                                    @ExampleObject(name = "Roupas (USA - TODOS)",
                                            value = """
                                            {
                                              "dados": [
                                                {"etiqueta":"XXXS","qtdEstoque":4},
                                                {"etiqueta":"XXS","qtdEstoque":6},
                                                {"etiqueta":"XS","qtdEstoque":9},
                                                {"etiqueta":"S","qtdEstoque":11},
                                                {"etiqueta":"M","qtdEstoque":13},
                                                {"etiqueta":"L","qtdEstoque":10},
                                                {"etiqueta":"XL","qtdEstoque":7},
                                                {"etiqueta":"XXL","qtdEstoque":5},
                                                {"etiqueta":"XXXL","qtdEstoque":3}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Estoque listado com sucesso"
                                            }
                                            """),
                                    @ExampleObject(name = "Sapatos (30–46 - TODOS)",
                                            value = """
                                            {
                                              "dados": [
                                                {"etiqueta":"30","qtdEstoque":2},
                                                {"etiqueta":"31","qtdEstoque":3},
                                                {"etiqueta":"32","qtdEstoque":4},
                                                {"etiqueta":"33","qtdEstoque":5},
                                                {"etiqueta":"34","qtdEstoque":6},
                                                {"etiqueta":"35","qtdEstoque":7},
                                                {"etiqueta":"36","qtdEstoque":8},
                                                {"etiqueta":"37","qtdEstoque":9},
                                                {"etiqueta":"38","qtdEstoque":10},
                                                {"etiqueta":"39","qtdEstoque":9},
                                                {"etiqueta":"40","qtdEstoque":8},
                                                {"etiqueta":"41","qtdEstoque":7},
                                                {"etiqueta":"42","qtdEstoque":6},
                                                {"etiqueta":"43","qtdEstoque":5},
                                                {"etiqueta":"44","qtdEstoque":4},
                                                {"etiqueta":"45","qtdEstoque":3},
                                                {"etiqueta":"46","qtdEstoque":2}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Estoque listado com sucesso"
                                            }
                                            """),
                                    @ExampleObject(name = "Bolsas",
                                            value = """
                                            {
                                              "dados": [
                                                {"etiqueta": null, "qtdEstoque": 5}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Estoque listado com sucesso"
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "sucesso": false,
                              "mensagem": "Erro ao listar estoque: detalhes técnicos"
                            }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> listarEstoque(Long id);

    // =============================================================
    // ATUALIZAR ESTOQUE EM MASSA
    // =============================================================

    @Operation(
            summary = "Atualizar estoque em massa",
            description = """
            • **Roupas/Sapatos** → envie um array de {etiqueta, qtdEstoque} para **definir** a quantidade de cada etiqueta informada (upsert por etiqueta).  
            • **Bolsas** → envie um array com **um item** {qtdEstoque} (etiqueta ignorada) para **definir** o estoque do produto.
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Array vazio",
                                            value = """
                                            { "sucesso": false, "mensagem": "Envie um array JSON de itens" }
                                            """),
                                    @ExampleObject(name = "Etiqueta inválida/incompatível",
                                            value = """
                                            { "sucesso": false, "mensagem": "Etiqueta 'PP' não pertence ao padrão de tamanhos do produto" }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao atualizar estoque: detalhes técnicos" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> atualizarEstoqueEmMassa(
            Long id,
            @RequestBody(
                    required = true,
                    description = "Itens de estoque a atualizar.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Roupas (BR - TODOS)",
                                            value = """
                                            [
                                              {"etiqueta":"PP","qtdEstoque":10},
                                              {"etiqueta":"P","qtdEstoque":12},
                                              {"etiqueta":"M","qtdEstoque":9},
                                              {"etiqueta":"G","qtdEstoque":7},
                                              {"etiqueta":"XG","qtdEstoque":6},
                                              {"etiqueta":"G1","qtdEstoque":8},
                                              {"etiqueta":"G2","qtdEstoque":5}
                                            ]
                                            """),
                                    @ExampleObject(name = "Roupas (USA - TODOS)",
                                            value = """
                                            [
                                              {"etiqueta":"XXXS","qtdEstoque":4},
                                              {"etiqueta":"XXS","qtdEstoque":6},
                                              {"etiqueta":"XS","qtdEstoque":8},
                                              {"etiqueta":"S","qtdEstoque":10},
                                              {"etiqueta":"M","qtdEstoque":12},
                                              {"etiqueta":"L","qtdEstoque":9},
                                              {"etiqueta":"XL","qtdEstoque":7},
                                              {"etiqueta":"XXL","qtdEstoque":5},
                                              {"etiqueta":"XXXL","qtdEstoque":3}
                                            ]
                                            """),
                                    @ExampleObject(name = "Sapatos (30–46 - TODOS)",
                                            value = """
                                            [
                                              {"etiqueta":"30","qtdEstoque":2},
                                              {"etiqueta":"31","qtdEstoque":3},
                                              {"etiqueta":"32","qtdEstoque":4},
                                              {"etiqueta":"33","qtdEstoque":5},
                                              {"etiqueta":"34","qtdEstoque":6},
                                              {"etiqueta":"35","qtdEstoque":7},
                                              {"etiqueta":"36","qtdEstoque":8},
                                              {"etiqueta":"37","qtdEstoque":9},
                                              {"etiqueta":"38","qtdEstoque":10},
                                              {"etiqueta":"39","qtdEstoque":9},
                                              {"etiqueta":"40","qtdEstoque":8},
                                              {"etiqueta":"41","qtdEstoque":7},
                                              {"etiqueta":"42","qtdEstoque":6},
                                              {"etiqueta":"43","qtdEstoque":5},
                                              {"etiqueta":"44","qtdEstoque":4},
                                              {"etiqueta":"45","qtdEstoque":3},
                                              {"etiqueta":"46","qtdEstoque":2}
                                            ]
                                            """),
                                    @ExampleObject(name = "Bolsas",
                                            value = """
                                            [
                                              {"qtdEstoque": 5}
                                            ]
                                            """)
                            }
                    )
            ) List<ProdutoTamanhoDTO> itens);

    // =============================================================
    // ATUALIZAR ESTOQUE POR ETIQUETA
    // =============================================================

    @Operation(
            summary = "Atualizar estoque de uma etiqueta (roupas/sapatos)",
            description = """
            Atualiza o estoque **de uma única etiqueta** usando:
            - `modo=set` → define exatamente o valor  
            - `modo=inc` → incrementa  
            - `modo=dec` → decrementa (nunca fica negativo)
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque atualizado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Roupas (BR)",
                                            value = """
                                            {
                                              "dados": [
                                                {"etiqueta":"M","qtdEstoque":15}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Estoque atualizado com sucesso"
                                            }
                                            """),
                                    @ExampleObject(name = "Roupas (USA)",
                                            value = """
                                            {
                                              "dados": [
                                                {"etiqueta":"L","qtdEstoque":12}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Estoque atualizado com sucesso"
                                            }
                                            """),
                                    @ExampleObject(name = "Sapatos",
                                            value = """
                                            {
                                              "dados": [
                                                {"etiqueta":"40","qtdEstoque":9}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Estoque atualizado com sucesso"
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Requisição inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Etiqueta ausente",
                                            value = """
                                            { "sucesso": false, "mensagem": "Informe a etiqueta de tamanho para este produto." }
                                            """),
                                    @ExampleObject(name = "Modo inválido",
                                            value = """
                                            { "sucesso": false, "mensagem": "Modo inválido. Use: set, inc ou dec." }
                                            """),
                                    @ExampleObject(name = "Etiqueta incompatível com o produto",
                                            value = """
                                            { "sucesso": false, "mensagem": "Etiqueta 'PP' não pertence ao padrão de tamanhos do produto" }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao atualizar estoque: detalhes técnicos" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> atualizarEstoquePorEtiqueta(
            Long id,
            String etiqueta,
            String modo,
            int valor
    );

    // =============================================================
    // ATUALIZAR ESTOQUE SEM TAMANHO (BOLSAS)
    // =============================================================

    @Operation(
            summary = "Atualizar estoque (bolsas, sem etiqueta)",
            description = """
            Atualiza o estoque do produto **sem tamanho**:
            - `modo=set` → define exatamente o valor  
            - `modo=inc` → incrementa  
            - `modo=dec` → decrementa (nunca fica negativo)
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque de bolsas atualizado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "dados": [
                                {"etiqueta": null, "qtdEstoque": 7}
                              ],
                              "sucesso": true,
                              "mensagem": "Estoque atualizado com sucesso"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Modo inválido",
                                            value = """
                                            { "sucesso": false, "mensagem": "Modo inválido. Use: set, inc ou dec." }
                                            """),
                                    @ExampleObject(name = "Valor negativo para set",
                                            value = """
                                            { "sucesso": false, "mensagem": "Valor inválido. Estoque não pode ser negativo." }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao atualizar estoque: detalhes técnicos" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoTamanhoDTO>>> atualizarEstoqueSemTamanho(
            Long id,
            String modo,
            int valor
    );
}