package com.luigarah.controller.doc;

import com.luigarah.dto.tamanho.PadraoAtualizacaoDTO;
import com.luigarah.dto.tamanho.PadraoItemDTO;
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

/**
 * Documentação dos endpoints de PADRÃO DE TAMANHO (usa|br|sapatos|null).
 * - Produtos: coluna PRODUTOS.PADRAO_TAMANHO
 * - Tamanhos: coluna TAMANHOS.PADRAO
 * null = indefinido
 */
@Tag(
        name = "Padrão de Tamanho",
        description = "Gerencia o padrão de tamanho de produtos (PADRAO_TAMANHO) e de tamanhos (PADRAO). " +
                "Aceita: 'usa', 'br', 'sapatos' ou null (indefinido)."
)
public interface PadraoTamanhoControllerDoc {

    // =============================================================
    // LISTAGENS
    // =============================================================

    @Operation(
            summary = "Listar produtos por padrão",
            description = "Lista (id, padrao) dos produtos filtrando por 'usa' | 'br' | 'sapatos' | null (indefinido). " +
                    "Se omitir o parâmetro, retorna os indefinidos (padrao_tamanho IS NULL)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RespostaProdutoDTO.class),
                            examples = {
                                    @ExampleObject(name = "Produtos - USA",
                                            value = """
                                            {
                                              "dados": [
                                                {"id": 41, "padrao": "usa"},
                                                {"id": 57, "padrao": "usa"}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Produtos listados por padrão."
                                            }
                                            """),
                                    @ExampleObject(name = "Produtos - BR",
                                            value = """
                                            {
                                              "dados": [
                                                {"id": 12, "padrao": "br"},
                                                {"id": 19, "padrao": "br"}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Produtos listados por padrão."
                                            }
                                            """),
                                    @ExampleObject(name = "Produtos - Indefinido (null)",
                                            value = """
                                            {
                                              "dados": [
                                                {"id": 3, "padrao": null},
                                                {"id": 5, "padrao": null}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Produtos listados por padrão."
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Padrão inválido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Padrão inválido. Use: usa | br | sapatos | null (indefinido)." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<PadraoItemDTO>>> listarProdutos(String padrao);

    @Operation(
            summary = "Listar tamanhos por padrão",
            description = "Lista (id, padrao) dos registros de Tamanho filtrando por 'usa' | 'br' | 'sapatos' | null (indefinido)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RespostaProdutoDTO.class),
                            examples = {
                                    @ExampleObject(name = "Tamanhos - USA",
                                            value = """
                                            {
                                              "dados": [
                                                {"id": 101, "padrao": "usa"},
                                                {"id": 102, "padrao": "usa"}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Tamanhos listados por padrão."
                                            }
                                            """),
                                    @ExampleObject(name = "Tamanhos - BR",
                                            value = """
                                            {
                                              "dados": [
                                                {"id": 201, "padrao": "br"},
                                                {"id": 202, "padrao": "br"}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Tamanhos listados por padrão."
                                            }
                                            """),
                                    @ExampleObject(name = "Tamanhos - Indefinido (null)",
                                            value = """
                                            {
                                              "dados": [
                                                {"id": 301, "padrao": null},
                                                {"id": 302, "padrao": null}
                                              ],
                                              "sucesso": true,
                                              "mensagem": "Tamanhos listados por padrão."
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Padrão inválido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Padrão inválido. Use: usa | br | sapatos | null (indefinido)." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<PadraoItemDTO>>> listarTamanhos(String padrao);

    // =============================================================
    // PRODUTO: SET/CLEAR (UM) E LOTE
    // =============================================================

    @Operation(
            summary = "Definir padrão de UM produto",
            description = "Atualiza PRODUTOS.PADRAO_TAMANHO para 'usa' | 'br' | 'sapatos' | null (indefinido)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Produto -> USA",
                                            value = """
                                            {
                                              "dados": {"id": 41, "padrao": "usa"},
                                              "sucesso": true,
                                              "mensagem": "Padrão do produto atualizado."
                                            }
                                            """),
                                    @ExampleObject(name = "Produto -> null (limpar)",
                                            value = """
                                            {
                                              "dados": {"id": 41, "padrao": null},
                                              "sucesso": true,
                                              "mensagem": "Padrão do produto atualizado."
                                            }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Padrão inválido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Padrão inválido. Use: usa | br | sapatos | null (indefinido)." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<PadraoItemDTO>> setPadraoProduto(Long id, String padrao);

    @Operation(
            summary = "Limpar padrão de UM produto",
            description = "Define PRODUTOS.PADRAO_TAMANHO = NULL (indefinido)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Limpado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "dados": {"id": 41, "padrao": null}, "sucesso": true, "mensagem": "Padrão do produto limpo (indefinido)." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<PadraoItemDTO>> clearPadraoProduto(Long id);

    @Operation(
            summary = "Definir padrão em LOTE (produtos)",
            description = "Atualiza vários IDs de PRODUTOS.PADRAO_TAMANHO. " +
                    "Se 'padrao' null no corpo, limpa (NULL) para todos os IDs."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aplicado",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Lote -> USA",
                                            value = """
                                            { "dados": null, "sucesso": true, "mensagem": "Padrão aplicado aos produtos." }
                                            """),
                                    @ExampleObject(name = "Lote -> limpar (null)",
                                            value = """
                                            { "dados": null, "sucesso": true, "mensagem": "Padrão aplicado aos produtos." }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Padrão inválido / payload inválido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Padrão inválido. Use: usa | br | sapatos | null (indefinido)." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<Object>> setPadraoProdutos(
            @RequestBody(
                    required = true,
                    description = "Informe 'padrao' e 'produtoIds'. Se padrao=null, limpa para todos os IDs.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PadraoAtualizacaoDTO.class),
                            examples = {
                                    @ExampleObject(name = "USA em [41,57,60]",
                                            value = """
                                            { "padrao": "usa", "produtoIds": [41,57,60] }
                                            """),
                                    @ExampleObject(name = "Limpar (null) em [41,57]",
                                            value = """
                                            { "padrao": null, "produtoIds": [41,57] }
                                            """)
                            }))
            PadraoAtualizacaoDTO dto);

    // =============================================================
    // TAMANHO: SET/CLEAR (UM) E LOTE
    // =============================================================

    @Operation(
            summary = "Definir padrão de UM tamanho",
            description = "Atualiza TAMANHOS.PADRAO para 'usa' | 'br' | 'sapatos' | null (indefinido)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Tamanho -> BR",
                                            value = """
                                            { "dados": {"id": 120, "padrao": "br"}, "sucesso": true, "mensagem": "Padrão do tamanho atualizado." }
                                            """),
                                    @ExampleObject(name = "Tamanho -> null (limpar)",
                                            value = """
                                            { "dados": {"id": 120, "padrao": null}, "sucesso": true, "mensagem": "Padrão do tamanho atualizado." }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Padrão inválido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Padrão inválido. Use: usa | br | sapatos | null (indefinido)." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<PadraoItemDTO>> setPadraoTamanho(Long id, String padrao);

    @Operation(
            summary = "Limpar padrão de UM tamanho",
            description = "Define TAMANHOS.PADRAO = NULL (indefinido)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Limpado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "dados": {"id": 120, "padrao": null}, "sucesso": true, "mensagem": "Padrão do tamanho limpo (indefinido)." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<PadraoItemDTO>> clearPadraoTamanho(Long id);

    @Operation(
            summary = "Definir padrão em LOTE (tamanhos)",
            description = "Atualiza vários IDs de TAMANHOS.PADRAO. Se 'padrao' null no corpo, limpa (NULL)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aplicado",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Lote -> SAPATOS",
                                            value = """
                                            { "dados": null, "sucesso": true, "mensagem": "Padrão aplicado aos tamanhos." }
                                            """),
                                    @ExampleObject(name = "Lote -> limpar (null)",
                                            value = """
                                            { "dados": null, "sucesso": true, "mensagem": "Padrão aplicado aos tamanhos." }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Padrão inválido / payload inválido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Padrão inválido. Use: usa | br | sapatos | null (indefinido)." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<Object>> setPadraoTamanhos(
            @RequestBody(
                    required = true,
                    description = "Informe 'padrao' e 'tamanhoIds'. Se padrao=null, limpa para todos os IDs.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PadraoAtualizacaoDTO.class),
                            examples = {
                                    @ExampleObject(name = "BR em [101,102,103]",
                                            value = """
                                            { "padrao": "br", "tamanhoIds": [101,102,103] }
                                            """),
                                    @ExampleObject(name = "Limpar (null) em [101,102]",
                                            value = """
                                            { "padrao": null, "tamanhoIds": [101,102] }
                                            """)
                            }))
            PadraoAtualizacaoDTO dto);

    // =============================================================
    // OPERAÇÃO COMPOSTA (produtos e tamanhos no mesmo payload)
    // =============================================================

    @Operation(
            summary = "Aplicar padrão em lote (composto)",
            description = "Aceita 'produtoIds' e/ou 'tamanhoIds' no mesmo payload. Se 'padrao' = null, limpa todos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aplicado",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Definir USA em produtos [41,57] e tamanhos [101,102]",
                                            value = """
                                            { "dados": null, "sucesso": true, "mensagem": "Padrão aplicado (composto)." }
                                            """),
                                    @ExampleObject(name = "Limpar (null) em produtos [41] e tamanhos [101]",
                                            value = """
                                            { "dados": null, "sucesso": true, "mensagem": "Padrão aplicado (composto)." }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Padrão inválido / payload inválido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Padrão inválido. Use: usa | br | sapatos | null (indefinido)." }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<Object>> atualizarComposto(
            @RequestBody(
                    required = true,
                    description = "padrao + produtoIds e/ou tamanhoIds. null = limpar",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PadraoAtualizacaoDTO.class),
                            examples = {
                                    @ExampleObject(name = "USA - composto",
                                            value = """
                                            { "padrao": "usa", "produtoIds": [41,57], "tamanhoIds": [101,102] }
                                            """),
                                    @ExampleObject(name = "Limpar - composto",
                                            value = """
                                            { "padrao": null, "produtoIds": [41], "tamanhoIds": [101] }
                                            """)
                            }))
            PadraoAtualizacaoDTO dto);
}
