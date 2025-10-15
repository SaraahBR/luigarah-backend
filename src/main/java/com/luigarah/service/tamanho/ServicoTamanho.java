package com.luigarah.service.tamanho;

import java.util.List;

public interface ServicoTamanho {
    // catálogo
    List<String> listarCatalogoPorCategoria(String categoria, String padrao);

    // por produto
    List<String> listarTamanhosDoProduto(Long produtoId);
    List<String> substituirTamanhosDoProduto(Long produtoId, List<String> etiquetas);
    List<String> adicionarTamanhosAoProduto(Long produtoId, List<String> etiquetas);
    boolean removerTamanhoDoProduto(Long produtoId, String etiqueta);
}
