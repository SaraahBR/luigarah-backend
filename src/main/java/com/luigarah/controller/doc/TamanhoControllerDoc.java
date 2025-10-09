package com.luigarah.controller.doc;

import com.luigarah.dto.RespostaProdutoDTO;
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
 * Foca no catálogo por categoria e no vínculo de tamanhos por produto.
 */
@Tag(name = "Tamanhos", description = "Catálogo de tamanhos por categoria e vínculo de tamanhos aos produtos.")
public interface TamanhoControllerDoc {

    // ========================= CATÁLOGO =========================

    @Operation(
            summary = "Catálogo de tamanhos por categoria",
            description = """
                Retorna a lista oficial de etiquetas disponíveis para a <b>categoria</b> fornecida.
                <br>Use-a para popular dropdowns de seleção no frontend.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo retornado.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "dados": ["XS","S","M","L","XL"], "sucesso": true, "mensagem": "Catálogo de tamanhos encontrado" }
                            """))),
            @ApiResponse(responseCode = "400", description = "Categoria inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Categoria inválida. Use: bolsas, roupas ou sapatos" }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    ResponseEntity<RespostaProdutoDTO<List<String>>> listarCatalogoPorCategoria(
            @Parameter(description = "Categoria (bolsas|roupas|sapatos).", example = "roupas") String categoria
    );

    // ============== VÍNCULO DE TAMANHOS POR PRODUTO ==============

    @Operation(
            summary = "Listar tamanhos do produto",
            description = "Retorna as etiquetas vinculadas ao produto em ordem semântica."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tamanhos listados com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    ResponseEntity<RespostaProdutoDTO<List<String>>> listarTamanhosDoProduto(
            @Parameter(description = "ID do produto.", example = "13") Long id
    );

    @Operation(
            summary = "Substituir tamanhos do produto (PUT)",
            description = """
                Substitui todos os tamanhos existentes pelos informados.
                <br><b>Observação:</b> vínculos inexistentes serão criados; os demais removidos.
                Estoque inicial padrão é 10 (ajustável depois em /estoque).
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tamanhos substituídos com sucesso."),
            @ApiResponse(responseCode = "400", description = "Corpo inválido ou vazio."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    ResponseEntity<RespostaProdutoDTO<List<String>>> substituirTamanhosDoProduto(
            @Parameter(description = "ID do produto.", example = "13") Long id,
            @RequestBody(
                    required = true,
                    description = "Array de etiquetas válidas para a categoria do produto.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class),
                            examples = @ExampleObject(name = "Exemplo de substituição",
                                    value = """
                                    ["XS","S","M"]
                                    """)))
            List<String> etiquetas
    );

    @Operation(
            summary = "Adicionar tamanhos ao produto (PATCH)",
            description = "Acrescenta novas etiquetas sem remover as existentes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tamanhos adicionados com sucesso."),
            @ApiResponse(responseCode = "400", description = "Array vazio ou inválido."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    ResponseEntity<RespostaProdutoDTO<List<String>>> adicionarTamanhosAoProduto(
            @Parameter(description = "ID do produto.", example = "13") Long id,
            @RequestBody(
                    required = true,
                    description = "Array de etiquetas a adicionar.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class),
                            examples = @ExampleObject(name = "Exemplo de adição",
                                    value = """
                                    ["L","XL"]
                                    """)))
            List<String> etiquetas
    );

    @Operation(
            summary = "Remover tamanho do produto",
            description = "Remove a etiqueta especificada do produto, se existir."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tamanho removido.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": true, "mensagem": "Tamanho removido com sucesso" }
                            """))),
            @ApiResponse(responseCode = "404", description = "Tamanho não encontrado para o produto.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Tamanho 'M' não encontrado para o produto 13" }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    ResponseEntity<RespostaProdutoDTO<Object>> removerTamanhoDoProduto(
            @Parameter(description = "ID do produto.", example = "13") Long id,
            @Parameter(description = "Etiqueta a remover.", example = "M") String etiqueta
    );
}
