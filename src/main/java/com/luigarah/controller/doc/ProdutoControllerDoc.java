package com.luigarah.controller.doc;

import com.luigarah.dto.ProdutoCreateDTO;
import com.luigarah.dto.ProdutoDTO;
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
 * Documentação central dos endpoints de PRODUTO (Swagger/OpenAPI).
 *
 * Convenções de resposta:
 *  • Sucesso: RespostaProdutoDTO<T> com { dados, sucesso=true, mensagem, paginação (quando aplicável) }
 *  • Erro:    RespostaProdutoDTO<?> com { dados=null, sucesso=false, mensagem }
 *
 * Tratamento de erros (exemplos em cada operação):
 *  • 400 – parâmetros inválidos / validação (Bean Validation)
 *  • 404 – recurso não encontrado
 *  • 409 – conflito de regra de negócio (quando aplicável)
 *  • 500 – erro interno inesperado
 */
@Tag(
        name = "Produtos",
        description = "Endpoints para gerenciamento de produtos de luxo (CRUD, filtros e buscas)."
)
public interface ProdutoControllerDoc {

    // ============================================================
    // LISTAGEM SIMPLES (sem paginação explícita)
    // ============================================================

    @Operation(
            summary = "Listar todos os produtos (simples)",
            description = """
                Retorna até <b>limite</b> produtos ordenados por <code>id ASC</code>, \
                sem exigir configuração de paginação. Útil para vitrines iniciais.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Exemplo 200",
                                    value = """
                                    {
                                      "dados": [
                                        {
                                          "id": 14,
                                          "titulo": "Maria Lucia Hohan",
                                          "subtitulo": "Vestido",
                                          "autor": "Maria Lucia Hohan",
                                          "descricao": "Vestido Noor de seda",
                                          "preco": 20753,
                                          "dimensao": "Grande",
                                          "imagem": "https://.../29443745_59131249_2048.jpg",
                                          "imagemHover": "https://.../29443745_59131148_2048.jpg",
                                          "imagens": "[\\"https://.../59131193_2048.jpg\\"]",
                                          "composicao": "Seda 100%",
                                          "destaques": "[\\"vermelho\\",\\"gola V\\"]",
                                          "categoria": "roupas",
                                          "modelo": "{\\"altura_cm\\":177,\\"veste\\":\\"M\\"}"
                                        }
                                      ],
                                      "sucesso": true,
                                      "mensagem": "Produtos encontrados com sucesso",
                                      "total": 36,
                                      "paginaAtual": 0,
                                      "totalPaginas": 1,
                                      "tamanhoPagina": 100
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Parâmetro inválido.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Exemplo 400",
                                    value = """
                                    { "sucesso": false, "mensagem": "O parâmetro 'limite' deve estar entre 1 e 1000" }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Exemplo 500",
                                    value = """
                                    { "sucesso": false, "mensagem": "Erro ao listar todos os produtos: <detalhe>" }
                                    """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarTodosSimples(
            @Parameter(description = "Quantidade máxima de itens a retornar (1..1000).", example = "100")
            int limite
    );

    // ============================================================
    // LISTAGEM COM PAGINAÇÃO/FILTROS
    // ============================================================

    @Operation(
            summary = "Listar produtos (com filtros/paginação)",
            description = """
                Lista produtos com suporte a filtro por <b>categoria</b>, termo de <b>busca</b>, \
                <b>tamanho (etiqueta)</b>, além de ordenação e paginação.
                <ul>
                  <li><b>categoria</b>: bolsas | roupas | sapatos</li>
                  <li><b>busca</b>: LIKE case-insensitive em título, autor e descrição</li>
                  <li><b>tamanho</b>:
                    <ul>
                      <li>Sapatos: 34..46 (ex.: 36)</li>
                      <li>Roupas: XXS..XL (ex.: XS)</li>
                      <li>Bolsas: ignorado</li>
                    </ul>
                  </li>
                  <li><b>ordenarPor</b>: id, preco, titulo, ...</li>
                  <li><b>direcao</b>: asc | desc</li>
                </ul>
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao buscar produtos: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarTodosProdutos(
            @Parameter(description = "Índice da página (base 0).", example = "0") int pagina,
            @Parameter(description = "Tamanho da página.", example = "15") int tamanho,
            @Parameter(description = "Campo para ordenação (ex.: id, preco, titulo).", example = "id") String ordenarPor,
            @Parameter(description = "Direção da ordenação.", example = "asc") String direcao,
            @Parameter(description = "Filtro por categoria (bolsas|roupas|sapatos).", example = "roupas") String categoria,
            @Parameter(description = "Termo de busca (LIKE case-insensitive).", example = "prada") String busca,
            @Parameter(
                    name = "etiquetaTamanho",
                    description = """
                        Etiqueta/tamanho para filtrar:
                        <br>- Sapatos: 34..46 (ex.: <code>36</code>)
                        <br>- Roupas: XXS..XL (ex.: <code>XS</code>)
                        <br>- Bolsas: ignorado
                        """,
                    examples = {
                            @ExampleObject(name = "Sapatos 36", value = "36"),
                            @ExampleObject(name = "Roupas XS", value = "XS")
                    }
            )
            String etiquetaTamanho
    );

    // ============================================================
    // ROTA DIRETA: CATEGORIA + TAMANHO
    // ============================================================

    @Operation(
            summary = "Listar produtos por categoria e tamanho (etiqueta)",
            description = """
                Rota direta para filtrar por <b>categoria</b> e etiqueta de <b>tamanho</b>.
                <br><b>Exemplos</b>:
                <ul>
                  <li><code>/produtos/categoria/sapatos/tamanho/36</code></li>
                  <li><code>/produtos/categoria/roupas/tamanho/XS</code></li>
                </ul>
                <b>Observação</b>: para <i>bolsas</i>, o tamanho é ignorado (não possuem etiqueta).
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Categoria inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Categoria inválida. Use: bolsas, roupas ou sapatos" }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao buscar por categoria/tamanho: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarPorCategoriaETamanho(
            @Parameter(description = "Categoria (bolsas|roupas|sapatos).", required = true,
                    examples = {
                            @ExampleObject(name = "Bolsas", value = "bolsas"),
                            @ExampleObject(name = "Roupas", value = "roupas"),
                            @ExampleObject(name = "Sapatos", value = "sapatos")
                    })
            String categoria,

            @Parameter(description = "Etiqueta do tamanho (ex.: 36, 37 | XS, M, XL).", required = true,
                    examples = {
                            @ExampleObject(name = "Sapatos 36", value = "36"),
                            @ExampleObject(name = "Roupas XS", value = "XS")
                    })
            String etiqueta,

            @Parameter(description = "Página (base 0).", example = "0") int pagina,
            @Parameter(description = "Tamanho da página.", example = "15") int tamanho,
            @Parameter(description = "Campo de ordenação.", example = "id") String ordenarPor,
            @Parameter(description = "Direção da ordenação.", example = "asc") String direcao
    );

    // ============================================================
    // CATÁLOGO DE TAMANHOS POR CATEGORIA
    // ============================================================

    @Operation(
            summary = "Catálogo de tamanhos por categoria",
            description = """
                Retorna a lista de etiquetas (tamanhos) disponíveis no catálogo da categoria informada.
                <br>• <i>Bolsas</i>: não possuem tamanhos → retorna lista vazia.
                <br>• <i>Roupas / Sapatos</i>: ordenação por <code>ordem NULLS FIRST, etiqueta</code>.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo retornado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Roupas", value = """
                                            { "dados": ["XXS","XS","S","M","L","XL"], "sucesso": true, "mensagem": "Catálogo de tamanhos para roupas retornado com sucesso" }
                                            """),
                                    @ExampleObject(name = "Sapatos", value = """
                                            { "dados": ["34","35","36","37","38","39","40","41","42","43","44","45","46"], "sucesso": true, "mensagem": "Catálogo de tamanhos para sapatos retornado com sucesso" }
                                            """),
                                    @ExampleObject(name = "Bolsas", value = """
                                            { "dados": [], "sucesso": true, "mensagem": "Bolsas não possuem tamanhos" }
                                            """)
                            })),
            @ApiResponse(responseCode = "400", description = "Categoria inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Categoria inválida. Use: bolsas, roupas ou sapatos" }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao listar catálogo de tamanhos: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<String>>> listarCatalogoTamanhosPorCategoria(
            @Parameter(description = "Categoria (bolsas|roupas|sapatos).", example = "sapatos") String categoria
    );

    // ============================================================
    // FILTROS POR DIMENSÃO
    // ============================================================

    @Operation(
            summary = "Listar produtos por dimensão (global)",
            description = "Lista produtos pela dimensão (ex.: Mini, Média, Grande) em todas as categorias."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao listar por dimensão: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarPorDimensao(
            @Parameter(description = "Dimensão (ex.: Mini, Média, Grande).", example = "Mini") String dimensao,
            @Parameter(description = "Página (base 0).", example = "0") int pagina,
            @Parameter(description = "Tamanho da página.", example = "15") int tamanho,
            @Parameter(description = "Campo de ordenação.", example = "id") String ordenarPor,
            @Parameter(description = "Direção da ordenação.", example = "asc") String direcao
    );

    @Operation(
            summary = "Listar produtos por categoria e dimensão",
            description = "Lista produtos de uma categoria filtrando pela dimensão informada (ex.: bolsas Mini)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Categoria inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Categoria inválida. Use: bolsas, roupas ou sapatos" }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao listar por categoria e dimensão: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarPorCategoriaEDimensao(
            @Parameter(description = "Categoria (bolsas|roupas|sapatos).", example = "bolsas") String categoria,
            @Parameter(description = "Dimensão (ex.: Mini, Média, Grande).", example = "Mini") String dimensao,
            @Parameter(description = "Página (base 0).", example = "0") int pagina,
            @Parameter(description = "Tamanho da página.", example = "15") int tamanho,
            @Parameter(description = "Campo de ordenação.", example = "id") String ordenarPor,
            @Parameter(description = "Direção da ordenação.", example = "asc") String direcao
    );

    // ============================================================
    // CRUD
    // ============================================================

    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna os dados completos de um produto específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado."),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Produto com ID 999 não encontrado" }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao buscar produto: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<ProdutoDTO>> buscarProdutoPorId(
            @Parameter(description = "ID do produto.", example = "14") Long id
    );

    /**
     * Criar novo produto (entrada flexível).
     * • imagens: String | CSV | Array JSON
     * • destaques: String | CSV | Array JSON
     * • modelo: Objeto JSON | String no formato "k:v; k2:v2"
     */
    @Operation(
            summary = "Criar novo produto",
            description = "Aceita imagens/destaques como string única, CSV ou array; e modelo como objeto JSON ou string 'k:v; k2:v2'."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro de validação: <campo> <motivo>" }
                            """))),
            @ApiResponse(responseCode = "409", description = "Conflito (regra de negócio).", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao criar produto: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<ProdutoDTO>> criarProduto(
            @RequestBody(
                    required = true,
                    description = "Dados do produto (entrada flexível).",
                    content = @Content(schema = @Schema(implementation = ProdutoCreateDTO.class),
                            examples = {
                                    @ExampleObject(name = "Completo (arrays/objeto JSON)", value = """
                                            {
                                              "titulo": "Self-Portrait",
                                              "subtitulo": "Vestido",
                                              "autor": "Han Chong",
                                              "descricao": "Vestido longo sem alças",
                                              "preco": 6141,
                                              "dimensao": "Grande",
                                              "imagem": "https://.../31313088_60957373_2048.jpg",
                                              "imagemHover": "https://.../31313088_60957390_2048.jpg",
                                              "imagens": ["https://.../31313088_60957344_2048.jpg"],
                                              "composicao": "Poliéster 100%",
                                              "destaques": ["bordô", "sem alças", "fenda lateral"],
                                              "categoria": "roupas",
                                              "modelo": {"altura_cm":177,"busto_cm":86,"cintura_cm":62,"quadril_cm":90,"veste":"M"}
                                            }
                                            """),
                                    @ExampleObject(name = "Simplificado (strings/CSV/pares)", value = """
                                            {
                                              "titulo": "Self-Portrait",
                                              "subtitulo": "Vestido",
                                              "autor": "Han Chong",
                                              "descricao": "Vestido longo sem alças",
                                              "preco": 6141,
                                              "dimensao": "Grande",
                                              "imagem": "https://.../31313088_60957373_2048.jpg",
                                              "imagemHover": "https://.../31313088_60957390_2048.jpg",
                                              "imagens": "https://.../31313088_60957344_2048.jpg",
                                              "composicao": "Poliéster 100%",
                                              "destaques": "bordô, sem alças, fenda lateral",
                                              "categoria": "roupas",
                                              "modelo": "altura_cm:177; busto_cm:86; cintura_cm:62; quadril_cm:90; veste:M"
                                            }
                                            """)
                            }))
            ProdutoCreateDTO body
    );

    @Operation(
            summary = "Atualizar produto",
            description = "Atualiza um produto existente com os novos dados fornecidos (substitui somente campos não nulos)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Produto com ID 999 não encontrado" }
                            """))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao atualizar produto: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<ProdutoDTO>> atualizarProduto(
            @Parameter(description = "ID do produto a atualizar.", example = "14") Long id,
            @RequestBody(
                    required = true,
                    description = "Dados a serem atualizados (DTO tradicional).",
                    content = @Content(schema = @Schema(implementation = ProdutoDTO.class),
                            examples = @ExampleObject(name = "Exemplo mínimo",
                                    value = """
                                    { "preco": 39990, "imagens": "[\\"https://.../foto1.jpg\\"]" }
                                    """)))
            ProdutoDTO produtoDTO
    );

    @Operation(
            summary = "Deletar produto",
            description = "Remove um produto do sistema permanentemente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": true, "mensagem": "Produto deletado com sucesso" }
                            """))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Produto com ID 999 não encontrado" }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao deletar produto: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<Object>> deletarProduto(
            @Parameter(description = "ID do produto.", example = "14") Long id
    );

    // ============================================================
    // CATEGORIA / AUTOR / CONTAGEM
    // ============================================================

    @Operation(
            summary = "Listar por categoria",
            description = "Filtra por categoria (bolsas|roupas|sapatos) e, opcionalmente, por subtítulo (LIKE)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada."),
            @ApiResponse(responseCode = "400", description = "Categoria inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Categoria inválida. Use: bolsas, roupas ou sapatos" }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao buscar produtos por categoria: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarProdutosPorCategoria(
            @Parameter(description = "Categoria alvo (bolsas|roupas|sapatos).", example = "roupas") String categoria,
            @Parameter(description = "Página (base 0).", example = "0") int pagina,
            @Parameter(description = "Tamanho da página.", example = "15") int tamanho,
            @Parameter(description = "Filtro opcional pelo subtítulo (LIKE).", example = "Vestido") String subtitulo
    );

    @Operation(
            summary = "Buscar por autor",
            description = "Pesquisa case-insensitive no campo autor/designer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada."),
            @ApiResponse(responseCode = "400", description = "Autor vazio/ inválido.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Nome do autor não pode estar vazio" }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao buscar produtos por autor: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> buscarProdutosPorAutor(
            @Parameter(description = "Nome (ou parte) do autor.", example = "Prada") String autor
    );

    @Operation(
            summary = "Contar por categoria",
            description = "Retorna a quantidade total de produtos na categoria informada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem retornada.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "dados": 123, "sucesso": true, "mensagem": "Contagem de produtos na categoria roupas realizada com sucesso" }
                            """))),
            @ApiResponse(responseCode = "400", description = "Categoria inválida.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Categoria inválida. Use: bolsas, roupas ou sapatos" }
                            """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            { "sucesso": false, "mensagem": "Erro ao contar produtos: <detalhe>" }
                            """)))
    })
    ResponseEntity<RespostaProdutoDTO<Long>> contarProdutosPorCategoria(
            @Parameter(description = "Categoria alvo (bolsas|roupas|sapatos).", example = "bolsas") String categoria
    );
}

