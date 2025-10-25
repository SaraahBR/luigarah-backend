package com.luigarah.controller.produto;

import com.luigarah.controller.doc.ProdutoControllerDoc;
import com.luigarah.dto.produto.ProdutoCreateDTO;
import com.luigarah.dto.produto.ProdutoDTO;
import com.luigarah.dto.produto.RespostaProdutoDTO;
import com.luigarah.mapper.produto.ProdutoMapper;
import com.luigarah.model.produto.Produto;
import com.luigarah.repository.produto.RepositorioProduto;
import com.luigarah.service.produto.ServicoProduto;
import com.luigarah.util.JsonStringCleaner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST dos Produtos.
 *
 * Funcionalidades:
 *  • Listagem simples (sem paginação explícita)
 *  • Listagem com filtros (categoria, termo de busca, tamanho/etiqueta)
 *  • Filtros por DIMENSÃO (global e por categoria)
 *  • Catálogo de tamanhos por categoria (para os filtros do frontend)
 *  • CRUD completo
 *  • Buscas por categoria, por autor e contagem por categoria
 *
 * Notas de arquitetura:
 *  • O endpoint de criação (POST) aceita um payload “flexível” via {@link ProdutoCreateDTO}:
 *      - imagens: String única | CSV | Array JSON
 *      - destaques: String única | CSV | Array JSON
 *      - modelo: Objeto JSON | String no formato "k:v; k2:v2"
 *    O {@link ProdutoMapper} converte esse DTO em {@link Produto} normalizando
 *    os campos para JSON String antes da persistência.
 *
 *  • Para o endpoint do catálogo de tamanhos, injetamos o {@link RepositorioProduto}
 *    diretamente aqui por simplicidade/performance (SQL nativa). Se preferir manter
 *    100% via serviço, basta expor no {@link ServicoProduto} um método que delegue ao repositório.
 */
@RestController
@RequestMapping("/api/produtos")
public class ControladorProduto implements ProdutoControllerDoc {

    @Autowired
    private ServicoProduto servicoProduto;

    @Autowired
    private RepositorioProduto repositorioProduto; // ✅ usado no catálogo de tamanhos

    // ======================================================================
    // LISTAGEM SIMPLES (sem paginação explícita)
    // ======================================================================

    @Override
    @GetMapping("/simple")
    @Operation(
            summary = "Listar todos os produtos (simples)",
            description = "Retorna até 'limite' produtos ordenados por ID ascendente, sem configurar paginação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "dados": [{ "id": 13, "titulo": "MARANT ÉTOILE", "categoria": "roupas"}],
                                      "sucesso": true,
                                      "mensagem": "Produtos encontrados com sucesso",
                                      "total": 13,
                                      "paginaAtual": 0,
                                      "totalPaginas": 1,
                                      "tamanhoPagina": 100
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Parâmetro inválido.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    { "sucesso": false, "mensagem": "O parâmetro 'limite' deve estar entre 1 e 1000" }
                                    """))),
            @ApiResponse(responseCode = "500", description = "Erro interno.",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarTodosSimples(
            @RequestParam(defaultValue = "100") int limite) {
        try {
            if (limite < 1 || limite > 1000) {
                return ResponseEntity.badRequest().body(
                        RespostaProdutoDTO.erro("O parâmetro 'limite' deve estar entre 1 e 1000")
                );
            }

            Pageable top = PageRequest.of(0, limite, Sort.by(Sort.Direction.ASC, "id"));
            Page<Produto> pagina = servicoProduto.buscarTodosProdutos(top);

            List<ProdutoDTO> lista = pagina.getContent().stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());

            RespostaProdutoDTO<List<ProdutoDTO>> resposta = RespostaProdutoDTO.sucessoComPaginacao(
                    lista,
                    pagina.getTotalElements(),
                    pagina.getNumber(),
                    pagina.getTotalPages(),
                    pagina.getSize()
            );
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao listar todos os produtos: " + e.getMessage()));
        }
    }

    // ======================================================================
    // LISTAGEM COM PAGINAÇÃO/FILTROS (inclui tamanho/etiqueta)
    // ======================================================================

    @Override
    @GetMapping
    @Operation(
            summary = "Listar todos os produtos (com filtros/paginação)",
            description = "Suporta filtro por categoria, termo de busca, tamanho/etiqueta (ex.: 36, 37, XS, M ...), ordenação e paginação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarTodosProdutos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "15") int tamanho,
            @RequestParam(defaultValue = "id") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direcao,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busca,
            @RequestParam(name = "tamanho", required = false) String etiquetaTamanho
    ) {

        try {
            Sort.Direction direcaoOrdenacao = direcao.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable paginacao = PageRequest.of(pagina, tamanho, Sort.by(direcaoOrdenacao, ordenarPor));

            Page<Produto> paginaProdutos;

            if (categoria != null && etiquetaTamanho != null && !etiquetaTamanho.isBlank()) {
                // Categoria + tamanho (para bolsas o service ignora o tamanho)
                paginaProdutos = servicoProduto.buscarProdutosPorCategoriaETamanho(categoria, etiquetaTamanho, paginacao);

            } else if (categoria != null && busca != null && !busca.isBlank()) {
                paginaProdutos = servicoProduto.buscarProdutosPorCategoriaETermoPesquisa(categoria, busca, paginacao);

            } else if (categoria != null) {
                paginaProdutos = servicoProduto.buscarProdutosPorCategoria(categoria, paginacao);

            } else if (busca != null && !busca.isBlank()) {
                paginaProdutos = servicoProduto.buscarProdutosPorTermoPesquisa(busca, paginacao);

            } else {
                paginaProdutos = servicoProduto.buscarTodosProdutos(paginacao);
            }

            List<ProdutoDTO> produtosDTO = paginaProdutos.getContent().stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());

            RespostaProdutoDTO<List<ProdutoDTO>> resposta = RespostaProdutoDTO.sucessoComPaginacao(
                    produtosDTO,
                    paginaProdutos.getTotalElements(),
                    paginaProdutos.getNumber(),
                    paginaProdutos.getTotalPages(),
                    paginaProdutos.getSize()
            );

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            RespostaProdutoDTO<List<ProdutoDTO>> resposta = RespostaProdutoDTO.erro(
                    "Erro ao buscar produtos: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    /**
     * Rota dedicada: categoria + etiqueta (tamanho).
     * Ex.: /produtos/categoria/sapatos/tamanho/36  |  /produtos/categoria/roupas/tamanho/XS
     */
    @Override
    @GetMapping("/categoria/{categoria}/tamanho/{etiqueta}")
    @Operation(
            summary = "Listar produtos por categoria e tamanho (etiqueta)",
            description = "Filtra diretamente por categoria e etiqueta de tamanho. Para 'bolsas', o filtro de tamanho é ignorado."
    )
    public ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarPorCategoriaETamanho(
            @PathVariable String categoria,
            @PathVariable String etiqueta,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "15") int tamanho,
            @RequestParam(defaultValue = "id") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direcao
    ) {
        try {
            if (!categoria.matches("bolsas|roupas|sapatos")) {
                return ResponseEntity.badRequest()
                        .body(RespostaProdutoDTO.erro("Categoria inválida. Use: bolsas, roupas ou sapatos"));
            }

            Sort.Direction dir = direcao.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable paginacao = PageRequest.of(pagina, tamanho, Sort.by(dir, ordenarPor));

            Page<Produto> paginaProdutos = servicoProduto.buscarProdutosPorCategoriaETamanho(categoria, etiqueta, paginacao);

            List<ProdutoDTO> lista = paginaProdutos.getContent().stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    RespostaProdutoDTO.sucessoComPaginacao(
                            lista,
                            paginaProdutos.getTotalElements(),
                            paginaProdutos.getNumber(),
                            paginaProdutos.getTotalPages(),
                            paginaProdutos.getSize()
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao buscar por categoria/tamanho: " + e.getMessage()));
        }
    }

    // ======================================================================
    // CATÁLOGO DE TAMANHOS POR CATEGORIA (para filtros do frontend)
    // ======================================================================

    @Override
    @GetMapping("/categoria/{categoria}/tamanhos/catalogo")
    @Operation(
            summary = "Catálogo de tamanhos por categoria",
            description = """
                Retorna a lista de <b>etiquetas de tamanho</b> disponíveis no catálogo para a categoria informada.
                <br>• <i>Bolsas</i>: não possuem tamanhos → retorna lista vazia.
                <br>• <i>Roupas / Sapatos</i>: retorna as etiquetas ordenadas por <code>t.ordem NULLS FIRST, t.etiqueta</code>.
                """
    )
    public ResponseEntity<RespostaProdutoDTO<List<String>>> listarCatalogoTamanhosPorCategoria(
            @PathVariable String categoria
    ) {
        try {
            if (!categoria.matches("bolsas|roupas|sapatos")) {
                return ResponseEntity.badRequest()
                        .body(RespostaProdutoDTO.erro("Categoria inválida. Use: bolsas, roupas ou sapatos"));
            }
            if ("bolsas".equalsIgnoreCase(categoria)) {
                return ResponseEntity.ok(
                        RespostaProdutoDTO.sucesso(List.of(), "Bolsas não possuem tamanhos")
                );
            }

            // Chamada direta ao repositório (SQL nativa) — simples e performática
            List<String> etiquetas = repositorioProduto.listarCatalogoEtiquetas(categoria);

            return ResponseEntity.ok(
                    RespostaProdutoDTO.sucesso(
                            etiquetas,
                            "Catálogo de tamanhos para " + categoria + " retornado com sucesso"
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao listar catálogo de tamanhos: " + e.getMessage()));
        }
    }

    // ======================================================================
    // FILTROS POR DIMENSÃO
    // ======================================================================

    @Override
    @GetMapping("/dimensao/{dimensao}")
    @Operation(
            summary = "Listar produtos por dimensão (global)",
            description = """
                Retorna produtos filtrando pela <b>dimensão</b> informada (ex.: <code>Mini</code>, <code>Média</code>, <code>Grande</code>).
                <br>Aplica a todas as categorias (bolsas, roupas e sapatos).
                """
    )
    public ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarPorDimensao(
            @PathVariable String dimensao,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "15") int tamanho,
            @RequestParam(defaultValue = "id") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direcao
    ) {
        try {
            Sort.Direction dir = direcao.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable paginacao = PageRequest.of(pagina, tamanho, Sort.by(dir, ordenarPor));

            Page<Produto> page = servicoProduto.buscarProdutosPorDimensao(dimensao, paginacao);

            List<ProdutoDTO> lista = page.getContent().stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    RespostaProdutoDTO.sucessoComPaginacao(
                            lista, page.getTotalElements(), page.getNumber(), page.getTotalPages(), page.getSize()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao listar por dimensão: " + e.getMessage()));
        }
    }

    @Override
    @GetMapping("/categoria/{categoria}/dimensao/{dimensao}")
    @Operation(
            summary = "Listar produtos por categoria e dimensão",
            description = """
                Retorna produtos de uma <b>categoria</b> filtrando pela <b>dimensão</b> informada.
                <br>Ex.: <code>/produtos/categoria/bolsas/dimensao/Mini</code>
                """
    )
    public ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarPorCategoriaEDimensao(
            @PathVariable String categoria,
            @PathVariable String dimensao,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "15") int tamanho,
            @RequestParam(defaultValue = "id") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direcao
    ) {
        try {
            if (!categoria.matches("bolsas|roupas|sapatos")) {
                return ResponseEntity.badRequest()
                        .body(RespostaProdutoDTO.erro("Categoria inválida. Use: bolsas, roupas ou sapatos"));
            }

            Sort.Direction dir = direcao.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable paginacao = PageRequest.of(pagina, tamanho, Sort.by(dir, ordenarPor));

            Page<Produto> page = servicoProduto.buscarProdutosPorCategoriaEDimensao(categoria, dimensao, paginacao);

            List<ProdutoDTO> lista = page.getContent().stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    RespostaProdutoDTO.sucessoComPaginacao(
                            lista, page.getTotalElements(), page.getNumber(), page.getTotalPages(), page.getSize()
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao listar por categoria e dimensão: " + e.getMessage()));
        }
    }

    // ======================================================================
    // CRUD
    // ======================================================================

    @Override
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna os dados completos de um produto específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<RespostaProdutoDTO<ProdutoDTO>> buscarProdutoPorId(@PathVariable Long id) {
        try {
            Optional<Produto> produtoOpcional = servicoProduto.buscarProdutoPorId(id);

            if (produtoOpcional.isPresent()) {
                ProdutoDTO produtoDTO = converterParaDTO(produtoOpcional.get());
                RespostaProdutoDTO<ProdutoDTO> resposta = RespostaProdutoDTO.sucesso(
                        produtoDTO, "Produto encontrado com sucesso"
                );
                return ResponseEntity.ok(resposta);
            } else {
                RespostaProdutoDTO<ProdutoDTO> resposta = RespostaProdutoDTO.erro(
                        "Produto com ID " + id + " não encontrado"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
            }

        } catch (Exception e) {
            RespostaProdutoDTO<ProdutoDTO> resposta = RespostaProdutoDTO.erro(
                    "Erro ao buscar produto: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    /**
     * Criar novo produto (entrada flexível).
     *
     * Aceita as seguintes variações:
     *  • imagens: "https://x.jpg" | "https://x.jpg, https://y.jpg" | ["https://x.jpg","https://y.jpg"]
     *  • destaques: "bordô, sem alças" | ["bordô","sem alças"]
     *  • modelo: {"altura_cm":177,...} | "altura_cm:177; busto_cm:86; veste:M"
     *
     * Os valores são normalizados para JSON String na entidade antes de salvar.
     */
    @Override
    @PostMapping
    @Operation(
            summary = "Criar novo produto",
            description = "Aceita imagens/destaques como string única, CSV ou array; e modelo como objeto JSON ou string 'k:v; k2:v2'.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json", examples = {
                            // Exemplo tradicional (tudo JSON “bonitinho”)
                            @ExampleObject(name = "Completo (arrays/objeto JSON)", value = """
                                    {
                                      "titulo": "Self-Portrait",
                                      "subtitulo": "Vestido",
                                      "autor": "Han Chong",
                                      "descricao": "Vestido longo sem alças",
                                      "preco": 6141,
                                      "dimensao": "Grande",
                                      "imagem": "https://cdn-images.farfetch-contents.com/31/31/30/88/31313088_60957373_2048.jpg",
                                      "imagemHover": "https://cdn-images.farfetch-contents.com/31/31/30/88/31313088_60957390_2048.jpg",
                                      "imagens": ["https://cdn-images.farfetch-contents.com/31/31/30/88/31313088_60957344_2048.jpg"],
                                      "composicao": "Poliéster 100%",
                                      "destaques": ["bordô", "sem alças", "fenda lateral"],
                                      "categoria": "roupas",
                                      "modelo": {"altura_cm":177,"busto_cm":86,"cintura_cm":62,"quadril_cm":90,"veste":"M"}
                                    }
                                    """),
                            // Exemplo simplificado (string/CSV/pares)
                            @ExampleObject(name = "Simplificado (strings/CSV/pares)", value = """
                                    {
                                      "titulo": "Self-Portrait",
                                      "subtitulo": "Vestido",
                                      "autor": "Han Chong",
                                      "descricao": "Vestido longo sem alças",
                                      "preco": 6141,
                                      "dimensao": "Grande",
                                      "imagem": "https://cdn-images.farfetch-contents.com/31/31/30/88/31313088_60957373_2048.jpg",
                                      "imagemHover": "https://cdn-images.farfetch-contents.com/31/31/30/88/31313088_60957390_2048.jpg",
                                      "imagens": "https://cdn-images.farfetch-contents.com/31/31/30/88/31313088_60957344_2048.jpg",
                                      "composicao": "Poliéster 100%",
                                      "destaques": "bordô, sem alças, fenda lateral",
                                      "categoria": "roupas",
                                      "modelo": "altura_cm:177; busto_cm:86; cintura_cm:62; quadril_cm:90; veste:M"
                                    }
                                    """)
                    })
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<RespostaProdutoDTO<ProdutoDTO>> criarProduto(
            @Valid @RequestBody ProdutoCreateDTO body) {
        try {
            // Converte o DTO flexível para a entidade, serializando listas/objeto em JSON String
            Produto entidade = ProdutoMapper.toEntity(body);
            entidade.setId(null); // força uso da SEQUENCE mesmo que venha id no payload

            Produto salvo = servicoProduto.criarProduto(entidade);
            ProdutoDTO dto = converterParaDTO(salvo);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(RespostaProdutoDTO.sucesso(dto, "Produto criado com sucesso"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespostaProdutoDTO.erro("Erro ao criar produto: " + e.getMessage()));
        }
    }

    @Override
    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar produto",
            description = "Atualiza um produto existente com os novos dados fornecidos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<RespostaProdutoDTO<ProdutoDTO>> atualizarProduto(
            @PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoDTO) {

        try {
            // 🔍 Log do payload recebido
            System.out.println("🔍 [Controller] PUT /api/produtos/" + id);
            System.out.println("📥 [Controller] DTO recebido - Destaques: " + produtoDTO.getDestaques());
            System.out.println("📥 [Controller] DTO recebido - Imagens: " + produtoDTO.getImagens());
            System.out.println("📥 [Controller] DTO recebido - Titulo: " + produtoDTO.getTitulo());

            if (!servicoProduto.produtoExiste(id)) {
                RespostaProdutoDTO<ProdutoDTO> resposta = RespostaProdutoDTO.erro(
                        "Produto com ID " + id + " não encontrado"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
            }

            Produto produto = converterParaEntidade(produtoDTO);
            produto.setId(id);

            System.out.println("🔄 [Controller] Entidade convertida - Destaques: " + produto.getDestaques());
            System.out.println("🔄 [Controller] Entidade convertida - Imagens: " + produto.getImagens());

            Produto produtoAtualizado = servicoProduto.atualizarProduto(id, produto);
            ProdutoDTO produtoAtualizadoDTO = converterParaDTO(produtoAtualizado);

            System.out.println("✅ [Controller] Produto atualizado - Destaques: " + produtoAtualizadoDTO.getDestaques());
            System.out.println("✅ [Controller] Produto atualizado - Imagens: " + produtoAtualizadoDTO.getImagens());

            RespostaProdutoDTO<ProdutoDTO> resposta = RespostaProdutoDTO.sucesso(
                    produtoAtualizadoDTO, "Produto atualizado com sucesso"
            );

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            System.err.println("❌ [Controller] Erro ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
            RespostaProdutoDTO<ProdutoDTO> resposta = RespostaProdutoDTO.erro(
                    "Erro ao atualizar produto: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar produto",
            description = "Remove um produto do sistema permanentemente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<RespostaProdutoDTO<Object>> deletarProduto(@PathVariable Long id) {
        try {
            if (!servicoProduto.produtoExiste(id)) {
                RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro(
                        "Produto com ID " + id + " não encontrado"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
            }

            servicoProduto.deletarProduto(id);

            RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.sucesso(
                    null, "Produto deletado com sucesso"
            );

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            RespostaProdutoDTO<Object> resposta = RespostaProdutoDTO.erro(
                    "Erro ao deletar produto: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    // ======================================================================
    // CATEGORIA / AUTOR / CONTAGEM
    // ======================================================================

    @Override
    @GetMapping("/categoria/{categoria}")
    @Operation(
            summary = "Listar produtos por categoria",
            description = "Filtra por categoria (bolsas|roupas|sapatos) e, opcionalmente, por subtítulo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada."),
            @ApiResponse(responseCode = "400", description = "Categoria inválida."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> listarProdutosPorCategoria(
            @PathVariable String categoria,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "15") int tamanho,
            @RequestParam(required = false) String subtitulo) {

        try {
            if (!categoria.matches("bolsas|roupas|sapatos")) {
                RespostaProdutoDTO<List<ProdutoDTO>> resposta = RespostaProdutoDTO.erro(
                        "Categoria inválida. Use: bolsas, roupas ou sapatos"
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
            }

            Pageable paginacao = PageRequest.of(pagina, tamanho);
            Page<Produto> paginaProdutos;

            if (subtitulo != null) {
                paginaProdutos = servicoProduto.buscarProdutosPorCategoriaESubtitulo(categoria, subtitulo, paginacao);
            } else {
                paginaProdutos = servicoProduto.buscarProdutosPorCategoria(categoria, paginacao);
            }

            List<ProdutoDTO> produtosDTO = paginaProdutos.getContent().stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());

            RespostaProdutoDTO<List<ProdutoDTO>> resposta = RespostaProdutoDTO.sucessoComPaginacao(
                    produtosDTO,
                    paginaProdutos.getTotalElements(),
                    paginaProdutos.getNumber(),
                    paginaProdutos.getTotalPages(),
                    paginaProdutos.getSize()
            );

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            RespostaProdutoDTO<List<ProdutoDTO>> resposta = RespostaProdutoDTO.erro(
                    "Erro ao buscar produtos por categoria: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    @Override
    @GetMapping("/autor/{autor}")
    @Operation(
            summary = "Buscar produtos por autor",
            description = "Pesquisa case-insensitive no campo autor/designer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada."),
            @ApiResponse(responseCode = "400", description = "Autor vazio ou inválido."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<RespostaProdutoDTO<List<ProdutoDTO>>> buscarProdutosPorAutor(@PathVariable String autor) {
        try {
            if (autor == null || autor.trim().isEmpty()) {
                RespostaProdutoDTO<List<ProdutoDTO>> resposta = RespostaProdutoDTO.erro(
                        "Nome do autor não pode estar vazio"
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
            }

            List<Produto> produtos = servicoProduto.buscarProdutosPorAutor(autor);
            List<ProdutoDTO> produtosDTO = produtos.stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());

            RespostaProdutoDTO<List<ProdutoDTO>> resposta = RespostaProdutoDTO.sucesso(
                    produtosDTO, "Produtos do autor " + autor + " encontrados com sucesso"
            );

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            RespostaProdutoDTO<List<ProdutoDTO>> resposta = RespostaProdutoDTO.erro(
                    "Erro ao buscar produtos por autor: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    @Override
    @GetMapping("/categoria/{categoria}/contar")
    @Operation(
            summary = "Contar produtos por categoria",
            description = "Retorna a quantidade total de produtos na categoria informada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem retornada."),
            @ApiResponse(responseCode = "400", description = "Categoria inválida."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    public ResponseEntity<RespostaProdutoDTO<Long>> contarProdutosPorCategoria(@PathVariable String categoria) {
        try {
            if (!categoria.matches("bolsas|roupas|sapatos")) {
                RespostaProdutoDTO<Long> resposta = RespostaProdutoDTO.erro(
                        "Categoria inválida. Use: bolsas, roupas ou sapatos"
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
            }

            long quantidade = servicoProduto.contarProdutosPorCategoria(categoria);

            RespostaProdutoDTO<Long> resposta = RespostaProdutoDTO.sucesso(
                    quantidade, "Contagem de produtos na categoria " + categoria + " realizada com sucesso"
            );

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            RespostaProdutoDTO<Long> resposta = RespostaProdutoDTO.erro(
                    "Erro ao contar produtos: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    // ======================================================================
    // Auxiliares de conversão DTO <-> entidade
    // ======================================================================

    /**
     * Converte a entidade para DTO, sanitizando os campos JSON em String
     * para evitar CR/LF e espaços indesejados que quebram o front.
     */
    private ProdutoDTO converterParaDTO(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        BeanUtils.copyProperties(produto, dto);
        dto.setImagens(JsonStringCleaner.clean(dto.getImagens()));
        dto.setDestaques(JsonStringCleaner.clean(dto.getDestaques()));
        dto.setModelo(JsonStringCleaner.clean(dto.getModelo()));
        return dto;
    }

    /**
     * Converte o DTO “tradicional” (usado nos demais endpoints) para a entidade.
     * Aqui os campos que carregam JSON em String também são limpos.
     */
    private Produto converterParaEntidade(ProdutoDTO dto) {
        Produto produto = new Produto();
        BeanUtils.copyProperties(dto, produto);
        produto.setImagens(JsonStringCleaner.clean(produto.getImagens()));
        produto.setDestaques(JsonStringCleaner.clean(produto.getDestaques()));
        produto.setModelo(JsonStringCleaner.clean(produto.getModelo()));
        return produto;
    }
}
