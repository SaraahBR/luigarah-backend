package com.luigarah.service.impl;

import com.luigarah.repository.RepositorioProduto;
import com.luigarah.service.ServicoTamanho;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ServicoTamanhoImpl implements ServicoTamanho {

    private final RepositorioProduto repo;

    public ServicoTamanhoImpl(RepositorioProduto repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarCatalogoPorCategoria(String categoria) {
        if (categoria == null || !categoria.matches("bolsas|roupas|sapatos")) {
            throw new IllegalArgumentException("Categoria inválida. Use: bolsas, roupas ou sapatos");
        }
        return repo.listarCatalogoEtiquetas(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarTamanhosDoProduto(Long produtoId) {
        return repo.listarEtiquetasPorProduto(produtoId);
    }

    @Override
    public List<String> substituirTamanhosDoProduto(Long produtoId, List<String> etiquetas) {
        repo.deletarTamanhosDoProduto(produtoId);
        return adicionarTamanhosAoProduto(produtoId, etiquetas);
    }

    @Override
    public List<String> adicionarTamanhosAoProduto(Long produtoId, List<String> etiquetas) {
        if (etiquetas == null || etiquetas.isEmpty()) return listarTamanhosDoProduto(produtoId);
        for (String et : new ArrayList<>(etiquetas)) {
            if (et == null || et.isBlank()) continue;
            repo.inserirTamanho(produtoId, et.trim(), 10); // estoque inicial padrão: 10
        }
        return listarTamanhosDoProduto(produtoId);
    }

    @Override
    public boolean removerTamanhoDoProduto(Long produtoId, String etiqueta) {
        return repo.removerTamanho(produtoId, etiqueta) > 0;
    }
}
