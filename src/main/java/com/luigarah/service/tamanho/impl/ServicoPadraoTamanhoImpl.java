package com.luigarah.service.tamanho.impl;

import com.luigarah.dto.tamanho.PadraoAtualizacaoDTO;
import com.luigarah.dto.tamanho.PadraoItemDTO;
import com.luigarah.model.enums.PadraoTamanho;
import com.luigarah.repository.tamanho.RepositorioPadraoProduto;
import com.luigarah.repository.tamanho.RepositorioPadraoTamanho;
import com.luigarah.service.tamanho.ServicoPadraoTamanho;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ServicoPadraoTamanhoImpl implements ServicoPadraoTamanho {

    private final RepositorioPadraoProduto repoProd;
    private final RepositorioPadraoTamanho repoTam;

    public ServicoPadraoTamanhoImpl(RepositorioPadraoProduto repoProd, RepositorioPadraoTamanho repoTam) {
        this.repoProd = repoProd;
        this.repoTam = repoTam;
    }

    private String norm(String padrao) {
        return PadraoTamanho.normalize(padrao);
    }

    private void validarPadrao(String padraoOuNull) {
        if (!PadraoTamanho.isValid(norm(padraoOuNull))) {
            throw new IllegalArgumentException("Padrão inválido. Use: usa | br | sapatos | null (indefinido).");
        }
    }

    // -------------------- Listagens --------------------

    @Override
    @Transactional(readOnly = true)
    public List<PadraoItemDTO> listarProdutosPorPadrao(String padraoOuNull) {
        String p = norm(padraoOuNull);
        List<Object[]> rows = repoProd.listarIdsEPadrao(p);
        List<PadraoItemDTO> out = new ArrayList<>(rows.size());
        for (Object[] r : rows) out.add(new PadraoItemDTO(((Number) r[0]).longValue(), (String) r[1]));
        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PadraoItemDTO> listarTamanhosPorPadrao(String padraoOuNull) {
        String p = norm(padraoOuNull);
        List<Object[]> rows = repoTam.listarIdsEPadrao(p);
        List<PadraoItemDTO> out = new ArrayList<>(rows.size());
        for (Object[] r : rows) out.add(new PadraoItemDTO(((Number) r[0]).longValue(), (String) r[1]));
        return out;
    }

    // -------------------- Produtos --------------------

    @Override
    public List<PadraoItemDTO> definirPadraoEmProdutos(String padraoOuNull, List<Long> ids) {
        String p = norm(padraoOuNull);
        validarPadrao(p);
        if (ids == null || ids.isEmpty()) return List.of();
        if (p == null) repoProd.limparPadrao(ids); else repoProd.definirPadrao(p, ids);
        return listarProdutosPorPadrao(p);
    }

    @Override
    public PadraoItemDTO definirPadraoEmProduto(Long id, String padraoOuNull) {
        String p = norm(padraoOuNull);
        validarPadrao(p);
        if (p == null) repoProd.limparPadraoUm(id); else repoProd.definirPadraoUm(id, p);
        return new PadraoItemDTO(id, p);
    }

    @Override
    public PadraoItemDTO limparPadraoDeProduto(Long id) {
        repoProd.limparPadraoUm(id);
        return new PadraoItemDTO(id, null);
    }

    // -------------------- Tamanhos --------------------

    @Override
    public List<PadraoItemDTO> definirPadraoEmTamanhos(String padraoOuNull, List<Long> ids) {
        String p = norm(padraoOuNull);
        validarPadrao(p);
        if (ids == null || ids.isEmpty()) return List.of();
        if (p == null) repoTam.limparPadrao(ids); else repoTam.definirPadrao(p, ids);
        return listarTamanhosPorPadrao(p);
    }

    @Override
    public PadraoItemDTO definirPadraoEmTamanho(Long id, String padraoOuNull) {
        String p = norm(padraoOuNull);
        validarPadrao(p);
        if (p == null) repoTam.limparPadraoUm(id); else repoTam.definirPadraoUm(id, p);
        return new PadraoItemDTO(id, p);
    }

    @Override
    public PadraoItemDTO limparPadraoDeTamanho(Long id) {
        repoTam.limparPadraoUm(id);
        return new PadraoItemDTO(id, null);
    }

    // -------------------- Composto --------------------

    @Override
    public void atualizarEmLote(PadraoAtualizacaoDTO dto) {
        String p = norm(dto.getPadrao());
        validarPadrao(p);
        if (dto.getProdutoIds() != null && !dto.getProdutoIds().isEmpty()) {
            if (p == null) repoProd.limparPadrao(dto.getProdutoIds());
            else repoProd.definirPadrao(p, dto.getProdutoIds());
        }
        if (dto.getTamanhoIds() != null && !dto.getTamanhoIds().isEmpty()) {
            if (p == null) repoTam.limparPadrao(dto.getTamanhoIds());
            else repoTam.definirPadrao(p, dto.getTamanhoIds());
        }
    }
}
