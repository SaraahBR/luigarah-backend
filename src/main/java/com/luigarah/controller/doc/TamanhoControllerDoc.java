package com.luigarah.controller.doc;

import com.luigarah.dto.produto.RespostaProdutoDTO;
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
 * Documentação dos endpoints de TAMANHO.
 * Catálogo por categoria/padrão e vínculo de tamanhos aos produtos.
 */
@Tag(name = "Tamanhos", description = "Catálogo de tamanhos por categoria e vínculo de tamanhos aos produtos.")
public interface TamanhoControllerDoc {

    // =============================================================
    // CATÁLOGO
    // =============================================================

    @Operation(
            summary = "Catálogo de tamanhos por categoria",
            description = """
                Retorna a lista oficial de etiquetas disponíveis para a <b>categoria</b> fornecida.
                <br>Use-a para popular dropdowns de seleção no frontend.
                <br>Se informar o <code>padrao</code> (usa|br|sapatos), filtra pelo padrão.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo retornado.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Roupas (BR)",
                                            value = """
                                            { "dados": ["PP","P","M","G","XG","G1","G2"], "sucesso": true, "mensagem": "Catálogo de tamanhos encontrado" }
                                            """),
                                    @ExampleObject(name = "Roupas (USA)",
                                            value = """
                                            { "dados": ["XXXS","XXS","XS","S","M","L","XL","XXL","XXXL"], "sucesso": true, "mensagem": "Catálogo de tamanhos encontrado" }
                                            """),
                                    @ExampleObject(name = "Sapatos",
                                            value = """
                                            { "dados": ["30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46"], "sucesso": true, "mensagem": "Catálogo de tamanhos encontrado" }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Categoria inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Categoria inválida. Use: bolsas, roupas ou sapatos." }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao listar catálogo de tamanhos: detalhes técnicos." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<String>>> listarCatalogoPorCategoria(
            @Parameter(description = "Categoria (bolsas|roupas|sapatos).", example = "roupas") String categoria,
            @Parameter(description = "Padrão (usa|br|sapatos). Opcional; se omitido, retorna todos os padrões da categoria.", example = "usa")
            String padrao
    );

    // =============================================================
    // LISTAR TAMANHOS DO PRODUTO
    // =============================================================

    @Operation(
            summary = "Listar tamanhos do produto",
            description = "Retorna as etiquetas vinculadas ao produto em ordem semântica."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tamanhos listados com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "dados": ["S","M","L"], "sucesso": true, "mensagem": "Tamanhos do produto listados com sucesso" }
                            """))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Produto não encontrado para o ID especificado." }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao listar tamanhos do produto: detalhes técnicos." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<String>>> listarTamanhosDoProduto(
            @Parameter(description = "ID do produto.", example = "13") Long id
    );

    // =============================================================
    // SUBSTITUIR TAMANHOS (PUT)
    // =============================================================

    @Operation(
            summary = "Substituir tamanhos do produto (PUT)",
            description = """
                Substitui todos os tamanhos existentes pelos informados.
                <br><b>Observação:</b> vínculos inexistentes serão criados; os demais removidos.
                Estoque inicial padrão é 10 (ajustável depois em /estoque).
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tamanhos substituídos com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Roupas (BR)",
                                            value = """
                                            { "dados": ["PP","P","M","G","XG"], "sucesso": true, "mensagem": "Tamanhos substituídos com sucesso." }
                                            """),
                                    @ExampleObject(name = "Roupas (USA)",
                                            value = """
                                            { "dados": ["XS","S","M","L","XL"], "sucesso": true, "mensagem": "Tamanhos substituídos com sucesso." }
                                            """),
                                    @ExampleObject(name = "Sapatos",
                                            value = """
                                            { "dados": ["38","39","40","41"], "sucesso": true, "mensagem": "Tamanhos substituídos com sucesso." }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Requisição inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Array vazio",
                                            value = """
                                            { "sucesso": false, "mensagem": "Envie um array de etiquetas válidas para substituição." }
                                            """),
                                    @ExampleObject(name = "Etiqueta inválida",
                                            value = """
                                            { "sucesso": false, "mensagem": "Etiqueta 'PP' não pertence ao padrão de tamanhos do produto." }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao substituir tamanhos: detalhes técnicos." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<String>>> substituirTamanhosDoProduto(
            @Parameter(description = "ID do produto.", example = "13") Long id,
            @RequestBody(
                    required = true,
                    description = "Array de etiquetas válidas para a categoria/padrão do produto.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class),
                            examples = {
                                    @ExampleObject(name = "Roupas (BR - todos os tamanhos)",
                                            value = """
                                            ["PP","P","M","G","XG","G1","G2"]
                                            """
                                    ),
                                    @ExampleObject(name = "Roupas (USA - todos os tamanhos)",
                                            value = """
                                            ["XXXS","XXS","XS","S","M","L","XL","XXL","XXXL"]
                                            """
                                    ),
                                    @ExampleObject(name = "Sapatos (30..46)",
                                            value = """
                                            ["30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46"]
                                            """
                                    )
                            }
                    )
            )
            List<String> etiquetas
    );

    // =============================================================
    // ADICIONAR TAMANHOS (PATCH)
    // =============================================================

    @Operation(
            summary = "Adicionar tamanhos ao produto (PATCH)",
            description = "Acrescenta novas etiquetas sem remover as existentes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tamanhos adicionados com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Roupas (BR)",
                                            value = """
                                            { "dados": ["G","XG"], "sucesso": true, "mensagem": "Tamanhos adicionados com sucesso." }
                                            """),
                                    @ExampleObject(name = "Roupas (USA)",
                                            value = """
                                            { "dados": ["L","XL"], "sucesso": true, "mensagem": "Tamanhos adicionados com sucesso." }
                                            """),
                                    @ExampleObject(name = "Sapatos",
                                            value = """
                                            { "dados": ["40","41"], "sucesso": true, "mensagem": "Tamanhos adicionados com sucesso." }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Requisição inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Array vazio",
                                            value = """
                                            { "sucesso": false, "mensagem": "Envie ao menos uma etiqueta válida para adicionar." }
                                            """),
                                    @ExampleObject(name = "Etiqueta duplicada",
                                            value = """
                                            { "sucesso": false, "mensagem": "Etiqueta 'M' já está vinculada ao produto." }
                                            """)
                            })),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao adicionar tamanhos: detalhes técnicos." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<String>>> adicionarTamanhosAoProduto(
            @Parameter(description = "ID do produto.", example = "13") Long id,
            @RequestBody(
                    required = true,
                    description = "Array de etiquetas a adicionar (respeitando o padrão do produto).",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class),
                            examples = {
                                    @ExampleObject(name = "Roupas (BR)",
                                            value = """
                                            ["PP","P","M","G","XG","G1","G2"]
                                            """
                                    ),
                                    @ExampleObject(name = "Roupas (USA)",
                                            value = """
                                            ["XXXS","XXS","XS","S","M","L","XL","XXL","XXXL"]
                                            """
                                    ),
                                    @ExampleObject(name = "Sapatos",
                                            value = """
                                            ["30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46"]
                                            """
                                    )
                            }
                    )
            )
            List<String> etiquetas
    );

    // =============================================================
    // REMOVER TAMANHO
    // =============================================================

    @Operation(
            summary = "Remover tamanho do produto",
            description = "Remove a etiqueta especificada do produto, se existir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tamanho removido.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": true, "mensagem": "Tamanho removido com sucesso." }
                            """))),
            @ApiResponse(responseCode = "404", description = "Tamanho não encontrado para o produto.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Tamanho 'M' não encontrado para o produto 13." }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao remover tamanho: detalhes técnicos." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<Object>> removerTamanhoDoProduto(
            @Parameter(description = "ID do produto.", example = "13") Long id,
            @Parameter(description = "Etiqueta a remover.", example = "M") String etiqueta
    );
}
