package com.luigarah.service.produto.impl;

import com.luigarah.exception.ProductNotFoundException;
import com.luigarah.model.produto.Produto;
import com.luigarah.repository.produto.RepositorioProduto;
import com.luigarah.service.produto.ServicoProduto;
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

@Service
@Transactional
public class ServicoProdutoImpl implements ServicoProduto {

    @Autowired
    private RepositorioProduto repositorioProduto;

    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarTodosProdutos(Pageable pageable) {
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
        // ⚠️ garantir que a SEQUENCE gere o ID mesmo que o front envie id no DTO
        produto.setId(null);

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

    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosPorDimensao(String dimensao, Pageable pageable) {
        if (dimensao == null || dimensao.isBlank()) {
            return buscarTodosProdutos(pageable);
        }
        return repositorioProduto.findByDimensaoIgnoreCase(dimensao, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosPorCategoriaEDimensao(String categoria, String dimensao, Pageable pageable) {
        if (categoria == null || categoria.isBlank()) {
            return buscarProdutosPorDimensao(dimensao, pageable);
        }
        if (dimensao == null || dimensao.isBlank()) {
            return buscarProdutosPorCategoria(categoria, pageable);
        }
        return repositorioProduto.findByCategoriaAndDimensaoIgnoreCase(categoria, dimensao, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutosPorCategoriaETamanho(String categoria, String etiqueta, Pageable pageable) {
        if (categoria == null || categoria.isBlank()) {
            return buscarTodosProdutos(pageable);
        }
        if ("bolsas".equalsIgnoreCase(categoria) || etiqueta == null || etiqueta.isBlank()) {
            return repositorioProduto.findByCategoria(categoria, pageable);
        }
        return repositorioProduto.buscarPorCategoriaETamanho(categoria, etiqueta, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarTamanhosDoProduto(Long produtoId) {
        return repositorioProduto.listarEtiquetasPorProduto(produtoId);
    }

    @Override
    public List<String> substituirTamanhosDoProduto(Long produtoId, List<String> etiquetas) {
        repositorioProduto.deletarTamanhosDoProduto(produtoId);
        return adicionarTamanhosAoProduto(produtoId, etiquetas);
    }

    @Override
    public List<String> adicionarTamanhosAoProduto(Long produtoId, List<String> etiquetas) {
        if (etiquetas == null || etiquetas.isEmpty()) {
            return listarTamanhosDoProduto(produtoId);
        }
        for (String et : new ArrayList<>(etiquetas)) {
            if (et == null || et.isBlank()) continue;
            repositorioProduto.inserirTamanho(produtoId, et.trim(), 10);
        }
        return listarTamanhosDoProduto(produtoId);
    }

    @Override
    public boolean removerTamanhoDoProduto(Long produtoId, String etiqueta) {
        if (etiqueta == null || etiqueta.isBlank()) return false;
        int linhas = repositorioProduto.removerTamanho(produtoId, etiqueta.trim());
        return linhas > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer obterEstoqueProduto(Long produtoId) {
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            return repositorioProduto.obterEstoqueProduto(produtoId);
        }
        return null;
    }

    @Override
    public void definirEstoqueProduto(Long produtoId, int qtd) {
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            repositorioProduto.upsertEstoqueProduto(produtoId, qtd);
        }
    }

    @Override
    public int incrementarEstoqueProduto(Long produtoId, int delta) {
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            return repositorioProduto.incrementarEstoqueProduto(produtoId, delta);
        }
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> listarEstoquePorProduto(Long produtoId) {
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            return List.of();
        }
        return repositorioProduto.listarEstoquePorProduto(produtoId);
    }

    @Override
    public void upsertEstoquePorEtiqueta(Long produtoId, String etiqueta, int qtd) {
        if (etiqueta == null || etiqueta.isBlank()) return;
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            return;
        }
        repositorioProduto.upsertEstoquePorEtiqueta(produtoId, etiqueta.trim(), qtd);
    }

    @Override
    public int incrementarEstoquePorEtiqueta(Long produtoId, String etiqueta, int delta) {
        if (etiqueta == null || etiqueta.isBlank()) return 0;
        String categoria = obterCategoriaSegura(produtoId);
        if ("bolsas".equalsIgnoreCase(categoria)) {
            return 0;
        }

        int linhas = repositorioProduto.incrementarEstoquePorEtiqueta(produtoId, etiqueta.trim(), delta);
        if (linhas == 0 && delta > 0) {
            repositorioProduto.upsertEstoquePorEtiqueta(produtoId, etiqueta.trim(), delta);
            return 1;
        }
        return linhas;
    }

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
