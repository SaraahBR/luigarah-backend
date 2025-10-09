package com.luigarah.service;

import com.luigarah.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Contrato do serviço de Produtos.
 *
 * Dica: mantenha o Impl (ServicoProdutoImpl) espelhando estas assinaturas.
 * Todos os métodos aqui mapeiam as necessidades do Controller e das
 * camadas de Estoque/Tamanhos.
 */
public interface ServicoProduto {

    // ----------------------- Listagens básicas / CRUD -----------------------

    /** Lista todos os produtos (paginado quando pageable != unpaged). */
    Page<Produto> buscarTodosProdutos(Pageable pageable);

    /** Busca um produto pelo ID. */
    Optional<Produto> buscarProdutoPorId(Long id);

    /** Lista por categoria (bolsas|roupas|sapatos). */
    Page<Produto> buscarProdutosPorCategoria(String categoria, Pageable pageable);

    /** Lista por categoria filtrando por subtítulo (LIKE, case-insensitive). */
    Page<Produto> buscarProdutosPorCategoriaESubtitulo(String categoria, String subtitulo, Pageable pageable);

    /** Busca por termo aplicando LIKE em título, autor e descrição. */
    Page<Produto> buscarProdutosPorTermoPesquisa(String termoPesquisa, Pageable pageable);

    /** Combina categoria + termo de pesquisa. */
    Page<Produto> buscarProdutosPorCategoriaETermoPesquisa(String categoria, String termoPesquisa, Pageable pageable);

    /** Busca por autor/designer (LIKE case-insensitive). */
    List<Produto> buscarProdutosPorAutor(String autor);

    /** Cria um novo produto (com normalização dos campos JSON string). */
    Produto criarProduto(Produto produto);

    /** Atualiza um produto (apenas campos não nulos). */
    Produto atualizarProduto(Long id, Produto produtoAtualizado);

    /** Remove um produto. */
    void deletarProduto(Long id);

    /** Contagem total por categoria. */
    long contarProdutosPorCategoria(String categoria);

    /** Verifica existência do produto. */
    boolean produtoExiste(Long id);

    // ----------------------- Filtros por dimensão --------------------------

    /** Busca global por dimensão (ex.: "Grande", "Média", "Mini"). */
    Page<Produto> buscarProdutosPorDimensao(String dimensao, Pageable pageable);

    /** Busca por categoria + dimensão. */
    Page<Produto> buscarProdutosPorCategoriaEDimensao(String categoria, String dimensao, Pageable pageable);

    // ----------------------- Filtro por tamanho/etiqueta -------------------

    /**
     * Busca por categoria + etiqueta (ex.: sapatos nº 37, roupas tamanho "M").
     * Para BOLSAS o filtro de tamanho é ignorado.
     */
    Page<Produto> buscarProdutosPorCategoriaETamanho(String categoria, String etiqueta, Pageable pageable);

    // ----------------------- Gestão de tamanhos do produto -----------------

    /** Lista as etiquetas já vinculadas ao produto (ordenadas pela coluna ORDEM do catálogo). */
    List<String> listarTamanhosDoProduto(Long produtoId);

    /** Substitui todas as etiquetas vinculadas pelo conjunto informado. */
    List<String> substituirTamanhosDoProduto(Long produtoId, List<String> etiquetas);

    /** Adiciona etiquetas ao produto, sem remover as atuais (ignora duplicidades). */
    List<String> adicionarTamanhosAoProduto(Long produtoId, List<String> etiquetas);

    /** Remove uma etiqueta específica do produto. */
    boolean removerTamanhoDoProduto(Long produtoId, String etiqueta);

    // ----------------------- Estoque (bolsas = por produto) ----------------

    /** Lê o estoque consolidado de um produto (para BOLSAS). */
    Integer obterEstoqueProduto(Long produtoId);

    /** Define (upsert) a quantidade de estoque do produto (para BOLSAS). */
    void definirEstoqueProduto(Long produtoId, int qtd);

    /** Incrementa/decrementa o estoque do produto (para BOLSAS). */
    int incrementarEstoqueProduto(Long produtoId, int delta);

    // ----------------------- Estoque por etiqueta (roupas/sapatos) ---------

    /**
     * Lista (etiqueta, quantidade) do estoque por tamanho do produto.
     * O array é retornado como: [0] = etiqueta (String), [1] = qtd (Number).
     */
    List<Object[]> listarEstoquePorProduto(Long produtoId);

    /** Upsert: define a quantidade para uma etiqueta específica do produto. */
    void upsertEstoquePorEtiqueta(Long produtoId, String etiqueta, int qtd);

    /** Incrementa/decrementa a quantidade para uma etiqueta específica do produto. */
    int incrementarEstoquePorEtiqueta(Long produtoId, String etiqueta, int delta);
}
