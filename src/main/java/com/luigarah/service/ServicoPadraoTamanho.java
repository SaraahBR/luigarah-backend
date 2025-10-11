package com.luigarah.service;

import com.luigarah.dto.PadraoAtualizacaoDTO;
import com.luigarah.dto.PadraoItemDTO;

import java.util.List;

public interface ServicoPadraoTamanho {

    // Listagens
    List<PadraoItemDTO> listarProdutosPorPadrao(String padraoOuNull);
    List<PadraoItemDTO> listarTamanhosPorPadrao(String padraoOuNull);

    // Produto: set/clear (um e em lote)
    List<PadraoItemDTO> definirPadraoEmProdutos(String padraoOuNull, List<Long> ids);
    PadraoItemDTO definirPadraoEmProduto(Long id, String padraoOuNull);
    PadraoItemDTO limparPadraoDeProduto(Long id);

    // Tamanho: set/clear (um e em lote)
    List<PadraoItemDTO> definirPadraoEmTamanhos(String padraoOuNull, List<Long> ids);
    PadraoItemDTO definirPadraoEmTamanho(Long id, String padraoOuNull);
    PadraoItemDTO limparPadraoDeTamanho(Long id);

    // Operação composta via DTO
    void atualizarEmLote(PadraoAtualizacaoDTO dto);
}
