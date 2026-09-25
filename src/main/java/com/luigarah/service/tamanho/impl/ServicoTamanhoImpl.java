package com.luigarah.service.tamanho.impl;

import com.luigarah.repository.produto.RepositorioProduto;
import com.luigarah.repository.tamanho.RepositorioTamanho;
import com.luigarah.service.tamanho.ServicoTamanho;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ServicoTamanhoImpl implements ServicoTamanho {

    private final RepositorioProduto repo;
    private final RepositorioTamanho repoTam;

    public ServicoTamanhoImpl(RepositorioProduto repo, RepositorioTamanho repoTam) {
        this.repo = repo;
        this.repoTam = repoTam;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarCatalogoPorCategoria(String categoria, String padrao) {
        if (categoria == null || !categoria.matches("bolsas|roupas|sapatos")) {
            throw new IllegalArgumentException("Categoria inválida. Use: bolsas, roupas ou sapatos");
        }
        String p = (padrao == null || padrao.isBlank()) ? null : padrao.toLowerCase();
        if (p != null && !p.matches("usa|br|sapatos")) {
            throw new IllegalArgumentException("Padrão inválido. Use: usa, br ou sapatos");
        }
        return repoTam.listarEtiquetas(categoria.toLowerCase(), p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarTamanhosDoProduto(Long produtoId) {
        return repo.listarEtiquetasPorProduto(produtoId);
    }

    /** Limite de produtos por consulta, para a URL e o IN (...) não crescerem sem controle. */
    private static final int MAX_PRODUTOS_POR_CONSULTA = 500;

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<String>> listarTamanhosDosProdutos(List<Long> produtoIds, boolean somenteComEstoque) {
        Map<Long, List<String>> resultado = new LinkedHashMap<>();
        if (produtoIds == null || produtoIds.isEmpty()) return resultado;

        List<Long> ids = new ArrayList<>(new LinkedHashSet<>(produtoIds));
        if (ids.size() > MAX_PRODUTOS_POR_CONSULTA) {
            throw new IllegalArgumentException("Informe no máximo " + MAX_PRODUTOS_POR_CONSULTA + " produtos por consulta");
        }

        // produtos sem tamanho (ex.: bolsas) também aparecem, com lista vazia
        ids.forEach(id -> resultado.put(id, new ArrayList<>()));
        agrupar(repo.listarEtiquetasPorProdutos(ids, somenteComEstoque), resultado);
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<String>> listarTamanhosDaCategoria(String categoria, boolean somenteComEstoque) {
        if (categoria == null || !categoria.matches("bolsas|roupas|sapatos")) {
            throw new IllegalArgumentException("Categoria inválida. Use: bolsas, roupas ou sapatos");
        }
        Map<Long, List<String>> resultado = new LinkedHashMap<>();
        agrupar(repo.listarEtiquetasPorCategoria(categoria, somenteComEstoque), resultado);
        return resultado;
    }

    /** Junta as linhas [produto_id, etiqueta] por produto, mantendo a ordem da consulta. */
    private static void agrupar(List<Object[]> linhas, Map<Long, List<String>> resultado) {
        for (Object[] linha : linhas) {
            Long produtoId = ((Number) linha[0]).longValue();
            resultado.computeIfAbsent(produtoId, id -> new ArrayList<>()).add((String) linha[1]);
        }
    }

    @Override
    public List<String> substituirTamanhosDoProduto(Long produtoId, List<String> etiquetas) {
        repo.deletarTamanhosDoProduto(produtoId);
        return adicionarTamanhosAoProduto(produtoId, etiquetas);
    }

    @Override
    public List<String> adicionarTamanhosAoProduto(Long produtoId, List<String> etiquetas) {
        if (etiquetas == null || etiquetas.isEmpty()) return listarTamanhosDoProduto(produtoId);

        // validações de coerência: categoria e padrão do produto
        String categoria = repo.obterCategoriaDoProduto(produtoId); // "roupas", "sapatos", "bolsas"
        if ("bolsas".equalsIgnoreCase(categoria)) {
            throw new IllegalArgumentException("BOLSAS não aceitam tamanhos. Utilize /estoque para ajustar a quantidade.");
        }
        String padrao = repo.obterPadraoTamanhoProduto(produtoId); // "usa" | "br" | "sapatos"
        if (padrao == null || !padrao.toLowerCase().matches("usa|br|sapatos")) {
            throw new IllegalArgumentException("Defina PADRAO_TAMANHO do produto (usa|br|sapatos) antes de vincular tamanhos.");
        }

        for (String et : new ArrayList<>(etiquetas)) {
            if (et == null || et.isBlank()) continue;
            String etiqueta = et.trim();

            // valida se a etiqueta existe no catálogo da categoria + padrão do produto
            int ok = repoTam.existsEtiqueta(categoria.toLowerCase(), padrao.toLowerCase(), etiqueta);
            if (ok == 0) {
                throw new IllegalArgumentException(
                        "Etiqueta '%s' não pertence ao catálogo de %s (%s)."
                                .formatted(etiqueta, categoria.toUpperCase(), padrao.toLowerCase()));
            }

            // estoque inicial padrão: 10 (mantido)
            repo.inserirTamanho(produtoId, etiqueta, 10);
        }
        return listarTamanhosDoProduto(produtoId);
    }

    @Override
    public boolean removerTamanhoDoProduto(Long produtoId, String etiqueta) {
        return repo.removerTamanho(produtoId, etiqueta) > 0;
    }
}
