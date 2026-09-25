package com.luigarah.service.tamanho;

import java.util.List;
import java.util.Map;

public interface ServicoTamanho {
    // catálogo
    List<String> listarCatalogoPorCategoria(String categoria, String padrao);

    // por produto
    List<String> listarTamanhosDoProduto(Long produtoId);
    Map<Long, List<String>> listarTamanhosDosProdutos(List<Long> produtoIds, boolean somenteComEstoque);
    Map<Long, List<String>> listarTamanhosDaCategoria(String categoria, boolean somenteComEstoque);
    List<String> substituirTamanhosDoProduto(Long produtoId, List<String> etiquetas);
    List<String> adicionarTamanhosAoProduto(Long produtoId, List<String> etiquetas);
    boolean removerTamanhoDoProduto(Long produtoId, String etiqueta);
}
