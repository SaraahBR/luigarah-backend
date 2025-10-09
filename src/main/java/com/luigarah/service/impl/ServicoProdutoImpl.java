package com.luigarah.service.impl;

import com.luigarah.exception.ProductNotFoundException;
import com.luigarah.model.Produto;
import com.luigarah.repository.RepositorioProduto;
import com.luigarah.service.ServicoProduto;
import com.luigarah.util.JsonStringCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Implementação do serviço de Produtos.
 *
 * Mantém:
 *  - CRUD de Produto
 *  - Listagens e buscas (categoria, subtítulo, termo)
 *  - Filtro por TAMANHO/ETIQUETA (para bolsas é ignorado)
 *  - Filtro por DIMENSÃO (global e por categoria)
 *  - Gestão de tamanhos vinculados ao produto (listar/substituir/adicionar/remover)
 *  - Estoque:
 *      • Bolsas: estoque por produto (tabela produtos_estoque)
 *      • Roupas/Sapatos: estoque por etiqueta de tamanho (tabela produtos_tamanhos)
 *
 * Observações:
 *  1) Campos texto que guardam JSON ("imagens", "destaques", "modelo")
 *     são limpos/normalizados via {@link JsonStringCleaner} antes de salvar/retornar.
 *  2) Para tamanhos/estoque, o repositório executa SQL nativo nas tabelas auxiliares.
 */
@Service
@Transactional
public class ServicoProdutoImpl implements ServicoProduto {

    @Autowired
    private RepositorioProduto repositorioProduto;

    // =========================================================================
    // LISTAGENS BÁSICAS / CRUD
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarTodosProdutos(Pageable pageable) {
        // Quando o Pageable vier "unpaged", evitamos consulta paginada
        if (pageable == null || pageable.isUnpaged()) {
            List<Produto> todos = repositorioProduto.findAll();
            return new PageImpl<>(todos, Pageable.unpaged(), todos.size());
        }
        return repositorioProduto.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Produto> buscarProdutoPorId(Long id) {
        return repositorioProduto.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosPorCategoria(String categoria, Pageable pageable) {
        return repositorioProduto.findByCategoria(categoria, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosPorCategoriaESubtitulo(String categoria, String subtitulo, Pageable pageable) {
        return repositorioProduto.findByCategoriaAndSubtituloContainingIgnoreCase(categoria, subtitulo, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosPorTermoPesquisa(String termoPesquisa, Pageable pageable) {
        final String busca = termoPesquisa == null ? "" : termoPesquisa.toLowerCase(Locale.ROOT);
        return repositorioProduto.buscarPorTermoPesquisa(busca, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosPorCategoriaETermoPesquisa(String categoria, String termoPesquisa, Pageable pageable) {
        final String busca = termoPesquisa == null ? "" : termoPesquisa.toLowerCase(Locale.ROOT);
        return repositorioProduto.buscarPorCategoriaETermoPesquisa(categoria, busca, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produto> buscarProdutosPorAutor(String autor) {
        return repositorioProduto.findByAutorContainingIgnoreCase(autor);
    }

    @Override
    public Produto criarProduto(Produto produto) {
        // Normaliza campos JSON guardados como String antes de persistir
        produto.setImagens(JsonStringCleaner.clean(produto.getImagens()));
        produto.setDestaques(JsonStringCleaner.clean(produto.getDestaques()));
        produto.setModelo(JsonStringCleaner.clean(produto.getModelo()));
        return repositorioProduto.save(produto);
    }

    @Override
    public Produto atualizarProduto(Long id, Produto produtoAtualizado) {
        Produto p = repositorioProduto.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto com ID " + id + " não encontrado"));

        // Atualiza apenas campos não-nulos
        if (produtoAtualizado.getTitulo() != null)           p.setTitulo(produtoAtualizado.getTitulo());
        if (produtoAtualizado.getSubtitulo() != null)        p.setSubtitulo(produtoAtualizado.getSubtitulo());
        if (produtoAtualizado.getAutor() != null)            p.setAutor(produtoAtualizado.getAutor());
        if (produtoAtualizado.getDescricao() != null)        p.setDescricao(produtoAtualizado.getDescricao());
        if (produtoAtualizado.getPreco() != null)            p.setPreco(produtoAtualizado.getPreco());
        if (produtoAtualizado.getDimensao() != null)         p.setDimensao(produtoAtualizado.getDimensao());
        if (produtoAtualizado.getImagem() != null)           p.setImagem(produtoAtualizado.getImagem());
        if (produtoAtualizado.getImagemHover() != null)      p.setImagemHover(produtoAtualizado.getImagemHover());
        if (produtoAtualizado.getImagens() != null)          p.setImagens(JsonStringCleaner.clean(produtoAtualizado.getImagens()));
        if (produtoAtualizado.getComposicao() != null)       p.setComposicao(produtoAtualizado.getComposicao());
        if (produtoAtualizado.getDestaques() != null)        p.setDestaques(JsonStringCleaner.clean(produtoAtualizado.getDestaques()));
        if (produtoAtualizado.getCategoria() != null)        p.setCategoria(produtoAtualizado.getCategoria());
        if (produtoAtualizado.getModelo() != null)           p.setModelo(JsonStringCleaner.clean(produtoAtualizado.getModelo()));

        return repositorioProduto.save(p);
    }

    @Override
    public void deletarProduto(Long id) {
        if (!repositorioProduto.existsById(id)) {
            throw new ProductNotFoundException("Produto com ID " + id + " não encontrado");
        }
        repositorioProduto.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarProdutosPorCategoria(String categoria) {
        return repositorioProduto.countByCategoria(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean produtoExiste(Long id) {
        return repositorioProduto.existsById(id);
    }

    // =========================================================================
    // FILTROS AVANÇADOS (TAMANHO / DIMENSÃO)
    // =========================================================================

    /**
     * Busca por categoria e etiqueta de tamanho.
     * Para BOLSAS, o filtro de tamanho é ignorado (todas são "únicas"/sem etiqueta).
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosPorCategoriaETamanho(String categoria, String etiqueta, Pageable pageable) {
        if (categoria == null || categoria.isBlank()) {
            // sem categoria definida, retorna geral
            return buscarTodosProdutos(pageable);
        }
        if ("bolsas".equalsIgnoreCase(categoria) || etiqueta == null || etiqueta.isBlank()) {
            // bolsas não têm etiqueta por tamanho OU etiqueta vazia => cai no filtro simples por categoria
            return repositorioProduto.findByCategoria(categoria, pageable);
        }
        // Para roupas/sapatos com etiqueta
        return repositorioProduto.buscarPorCategoriaETamanho(categoria, etiqueta, pageable);
    }

    /**
     * Busca todos os produtos por dimensão (ex.: Grande, Média, Mini).
     * Case-insensitive.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosPorDimensao(String dimensao, Pageable pageable) {
        if (dimensao == null || dimensao.isBlank()) {
            return buscarTodosProdutos(pageable);
        }
        // Requer no RepositorioProduto: Page<Produto> findByDimensaoIgnoreCase(String, Pageable)
        return repositorioProduto.findByDimensaoIgnoreCase(dimensao, pageable);
    }

    /**
     * Busca produtos por categoria E dimensão (ex.: bolsas Mini, roupas Média).
     * Case-insensitive em dimensão.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosPorCategoriaEDimensao(String categoria, String dimensao, Pageable pageable) {
        if (categoria == null || categoria.isBlank()) {
            return buscarProdutosPorDimensao(dimensao, pageable);
        }
        if (dimensao == null || dimensao.isBlank()) {
            return buscarProdutosPorCategoria(categoria, pageable);
        }
        // Requer no RepositorioProduto: Page<Produto> findByCategoriaAndDimensaoIgnoreCase(String, String, Pageable)
        return repositorioProduto.findByCategoriaAndDimensaoIgnoreCase(categoria, dimensao, pageable);
    }

    // =========================================================================
    // OPERAÇÕES DE TAMANHOS VINCULADOS AO PRODUTO
    // =========================================================================

    /**
     * Retorna a lista de etiquetas de tamanho já vinculadas ao produto.
     * A ordenação respeita a coluna ORDEM do catálogo (tamanhos.ordem), quando existir.
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> listarTamanhosDoProduto(Long produtoId) {
        return repositorioProduto.listarEtiquetasPorProduto(produtoId);
    }

    /**
     * Substitui TODAS as etiquetas de tamanho do produto pelas informadas.
     * Implementado como: DELETE de vínculos + re-inserção controlada.
     *
     * @param produtoId id do produto
     * @param etiquetas lista de etiquetas (ex.: ["XS","S","M"])
     * @return lista final de etiquetas vinculadas após a operação
     */
    @Override
    public List<String> substituirTamanhosDoProduto(Long produtoId, List<String> etiquetas) {
        repositorioProduto.deletarTamanhosDoProduto(produtoId);
        return adicionarTamanhosAoProduto(produtoId, etiquetas);
    }

    /**
     * Adiciona etiquetas ao produto sem remover as existentes.
     * Válida etiquetas em branco/nulas e ignora duplicidade via regra SQL no repositório.
     * Ao inserir, definimos um estoque inicial padrão (ex.: 10 unidades).
     */
    @Override
    public List<String> adicionarTamanhosAoProduto(Long produtoId, List<String> etiquetas) {
        if (etiquetas == null || etiquetas.isEmpty()) {
            return listarTamanhosDoProduto(produtoId);
        }
        for (String et : new ArrayList<>(etiquetas)) {
            if (et == null || et.isBlank()) continue;
            // estoque inicial padrão = 10 (ajuste conforme sua necessidade)
            repositorioProduto.inserirTamanho(produtoId, et.trim(), 10);
        }
        return listarTamanhosDoProduto(produtoId);
    }

    /**
     * Remove uma etiqueta específica do produto.
     *
     * @return true se removeu pelo menos 1 linha; false se não havia vínculo
     */
    @Override
    public boolean removerTamanhoDoProduto(Long produtoId, String etiqueta) {
        if (etiqueta == null || etiqueta.isBlank()) return false;
        int linhas = repositorioProduto.removerTamanho(produtoId, etiqueta.trim());
        return linhas > 0;
    }

    // =========================================================================
    // ESTOQUE - BOLSAS (por produto) e ROUPAS/SAPATOS (por etiqueta)
    // =========================================================================

    /**
     * Lê o estoque consolidado de um produto.
     * - Para BOLSAS: retorna a quantidade de produtos_estoque.
     * - Para ROUPAS/SAPATOS: costuma-se usar o estoque por etiqueta (listarEstoquePorProduto),
     *   mas aqui retornamos null (sem significado único) para não confundir.
     */
    @Override
    @Transactional(readOnly = true)
    public Integer obterEstoqueProduto(Long produtoId) {
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            return repositorioProduto.obterEstoqueProduto(produtoId);
        }
        // Para roupas/sapatos, o estoque é por tamanho (use listarEstoquePorProduto)
        return null;
    }

    /**
     * Define (upsert) a quantidade de estoque de um produto.
     * - Para BOLSAS: grava em produtos_estoque.
     * - Para ROUPAS/SAPATOS: como o estoque é por etiqueta, este método não se aplica;
     *   não faremos nada para essas categorias.
     */
    @Override
    public void definirEstoqueProduto(Long produtoId, int qtd) {
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            repositorioProduto.upsertEstoqueProduto(produtoId, qtd);
        }
        // roupas/sapatos -> usar upsertEstoquePorEtiqueta
    }

    /**
     * Incrementa/decrementa o estoque de um produto (delta pode ser negativo).
     * - Para BOLSAS: atualiza linha única em produtos_estoque.
     * - Para ROUPAS/SAPATOS: não se aplica (usar incrementarEstoquePorEtiqueta).
     *
     * @return linhas atualizadas (para bolsas) ou 0 (quando não aplicável)
     */
    @Override
    public int incrementarEstoqueProduto(Long produtoId, int delta) {
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            return repositorioProduto.incrementarEstoqueProduto(produtoId, delta);
        }
        return 0; // roupas/sapatos -> use incrementarEstoquePorEtiqueta
    }

    /**
     * Lista o estoque por etiqueta/tamanho do produto (somente para roupas/sapatos).
     * O array retorna: [0] = etiqueta (String), [1] = qtd (Number).
     * Para BOLSAS, retorna lista vazia (estoque é por produto).
     */
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> listarEstoquePorProduto(Long produtoId) {
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            return List.of(); // bolsas não têm estoque por tamanho
        }
        return repositorioProduto.listarEstoquePorProduto(produtoId);
    }

    /**
     * Upsert de estoque por etiqueta/tamanho (roupas/sapatos).
     * Para BOLSAS, este método não se aplica; se desejar tratar bolsas aqui,
     * poderíamos redirecionar para definirEstoqueProduto(produtoId, qtd).
     */
    @Override
    public void upsertEstoquePorEtiqueta(Long produtoId, String etiqueta, int qtd) {
        if (etiqueta == null || etiqueta.isBlank()) return;
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            // Opcional: redirecionar para estoque por produto
            // repositorioProduto.upsertEstoqueProduto(produtoId, qtd);
            return;
        }
        repositorioProduto.upsertEstoquePorEtiqueta(produtoId, etiqueta.trim(), qtd);
    }

    /**
     * Incrementa/decrementa o estoque por etiqueta/tamanho (delta pode ser negativo).
     * Se não existir vínculo ainda e delta > 0, fazemos um upsert como fallback.
     *
     * @return linhas atualizadas (>=1 quando houve mudança)
     */
    @Override
    public int incrementarEstoquePorEtiqueta(Long produtoId, String etiqueta, int delta) {
        if (etiqueta == null || etiqueta.isBlank()) return 0;
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            // bolsas não usam etiqueta; para efeito prático, não aplicamos
            return 0;
        }

        int linhas = repositorioProduto.incrementarEstoquePorEtiqueta(produtoId, etiqueta.trim(), delta);
        if (linhas == 0 && delta > 0) {
            // não existia vínculo; cria com a quantidade informada
            repositorioProduto.upsertEstoquePorEtiqueta(produtoId, etiqueta.trim(), delta);
            return 1;
        }
        return linhas;
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Tenta descobrir a categoria do produto pelo repositório.
     * Se não encontrar, devolve string vazia.
     */
    @Transactional(readOnly = true)
    protected String obterCategoriaSegura(Long produtoId) {
        try {
            String cat = repositorioProduto.obterCategoriaDoProduto(produtoId);
            return cat == null ? "" : cat;
        } catch (Exception e) {
            return "";
        }
    }
}
