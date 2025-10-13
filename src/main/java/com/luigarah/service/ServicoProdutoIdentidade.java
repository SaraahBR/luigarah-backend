package com.luigarah.service;

import com.luigarah.model.Identidade;
import com.luigarah.model.Produto;
import com.luigarah.repository.RepositorioIdentidade;
import com.luigarah.repository.RepositorioProduto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoProdutoIdentidade {

    private final RepositorioProduto repositorioProduto;
    private final RepositorioIdentidade repositorioIdentidade;

    /**
     * Atribui uma identidade a um produto
     */
    @Transactional
    public Produto atribuirIdentidade(Long produtoId, Long identidadeId) {
        Produto produto = repositorioProduto.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));

        Identidade identidade = repositorioIdentidade.findById(identidadeId)
                .orElseThrow(() -> new RuntimeException("Identidade não encontrada com ID: " + identidadeId));

        produto.setIdentidade(identidade);
        return repositorioProduto.save(produto);
    }

    /**
     * Remove a identidade de um produto
     */
    @Transactional
    public Produto removerIdentidade(Long produtoId) {
        Produto produto = repositorioProduto.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));

        produto.setIdentidade(null);
        return repositorioProduto.save(produto);
    }

    /**
     * Busca todos os produtos com identidade
     */
    @Transactional(readOnly = true)
    public List<Produto> buscarProdutosComIdentidade() {
        return repositorioProduto.findByIdentidadeIsNotNull();
    }

    /**
     * Busca todos os produtos sem identidade
     */
    @Transactional(readOnly = true)
    public List<Produto> buscarProdutosSemIdentidade() {
        return repositorioProduto.findByIdentidadeIsNull();
    }

    /**
     * Busca produtos por identidade específica (por ID)
     */
    @Transactional(readOnly = true)
    public List<Produto> buscarProdutosPorIdentidadeId(Long identidadeId) {
        if (!repositorioIdentidade.existsById(identidadeId)) {
            throw new RuntimeException("Identidade não encontrada com ID: " + identidadeId);
        }
        return repositorioProduto.findByIdentidadeId(identidadeId);
    }

    /**
     * Busca produtos por código de identidade (homem, mulher, unissex, infantil)
     */
    @Transactional(readOnly = true)
    public List<Produto> buscarProdutosPorIdentidadeCodigo(String codigo) {
        Identidade identidade = repositorioIdentidade.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Identidade não encontrada com código: " + codigo));
        return repositorioProduto.findByIdentidadeId(identidade.getId());
    }
}

