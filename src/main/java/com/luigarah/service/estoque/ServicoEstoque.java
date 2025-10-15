package com.luigarah.service.estoque;

import com.luigarah.dto.tamanho.ProdutoTamanhoDTO;
import java.util.List;

public interface ServicoEstoque {
    List<ProdutoTamanhoDTO> listarEstoqueDoProduto(Long produtoId);
    List<ProdutoTamanhoDTO> atualizarEstoqueEmMassa(Long produtoId, List<ProdutoTamanhoDTO> itens);
    List<ProdutoTamanhoDTO> atualizarEstoqueUnitario(Long produtoId, String etiqueta, String modo, int valor);
}
