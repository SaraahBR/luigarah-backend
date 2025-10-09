package com.luigarah.service.impl;

import com.luigarah.dto.ProdutoTamanhoDTO;
import com.luigarah.repository.RepositorioProduto;
import com.luigarah.service.ServicoEstoque;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ServicoEstoqueImpl implements ServicoEstoque {

    private final RepositorioProduto repo;

    public ServicoEstoqueImpl(RepositorioProduto repo) {
        this.repo = repo;
    }

    private boolean isBolsa(Long produtoId) {
        String cat = repo.obterCategoriaDoProduto(produtoId);
        return "bolsas".equalsIgnoreCase(cat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoTamanhoDTO> listarEstoqueDoProduto(Long produtoId) {
        if (isBolsa(produtoId)) {
            Integer qtd = repo.obterEstoqueProduto(produtoId);
            if (qtd == null) qtd = 0;

            ProdutoTamanhoDTO dto = new ProdutoTamanhoDTO();
            dto.setEtiqueta(null);       // sem tamanho
            dto.setQtdEstoque(qtd);

            List<ProdutoTamanhoDTO> out = new ArrayList<>(1);
            out.add(dto);
            return out;
        }

        List<Object[]> rows = repo.listarEstoquePorProduto(produtoId);
        List<ProdutoTamanhoDTO> out = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            String etiqueta = (String) r[0];
            Number qn = (Number) r[1];

            ProdutoTamanhoDTO dto = new ProdutoTamanhoDTO();
            dto.setEtiqueta(etiqueta);
            dto.setQtdEstoque(qn == null ? 0 : qn.intValue());
            out.add(dto);
        }
        return out;
    }

    @Override
    public List<ProdutoTamanhoDTO> atualizarEstoqueEmMassa(Long produtoId, List<ProdutoTamanhoDTO> itens) {
        if (isBolsa(produtoId)) {
            int qtd = (itens == null || itens.isEmpty() || itens.get(0).getQtdEstoque() == null)
                    ? 0 : Math.max(0, itens.get(0).getQtdEstoque());
            repo.upsertEstoqueProduto(produtoId, qtd);
            return listarEstoqueDoProduto(produtoId);
        }

        if (itens != null) {
            for (ProdutoTamanhoDTO dto : itens) {
                if (dto == null || dto.getEtiqueta() == null || dto.getEtiqueta().isBlank()) continue;
                int q = dto.getQtdEstoque() == null ? 0 : Math.max(0, dto.getQtdEstoque());
                repo.upsertEstoquePorEtiqueta(produtoId, dto.getEtiqueta().trim(), q);
            }
        }
        return listarEstoqueDoProduto(produtoId);
    }

    @Override
    public List<ProdutoTamanhoDTO> atualizarEstoqueUnitario(Long produtoId, String etiqueta, String modo, int valor) {
        String m = (modo == null ? "set" : modo.toLowerCase());

        if (isBolsa(produtoId)) {
            switch (m) {
                case "inc" -> {
                    int upd = repo.incrementarEstoqueProduto(produtoId, valor);
                    if (upd == 0) repo.upsertEstoqueProduto(produtoId, Math.max(0, valor));
                }
                case "dec" -> repo.incrementarEstoqueProduto(produtoId, -valor);
                default     -> repo.upsertEstoqueProduto(produtoId, Math.max(0, valor));
            }
            return listarEstoqueDoProduto(produtoId);
        }

        // com tamanho
        if (etiqueta == null || etiqueta.isBlank()) {
            throw new IllegalArgumentException("Informe a etiqueta de tamanho para este produto.");
        }

        switch (m) {
            case "inc" -> {
                int linhas = repo.incrementarEstoquePorEtiqueta(produtoId, etiqueta, valor);
                if (linhas == 0) repo.upsertEstoquePorEtiqueta(produtoId, etiqueta, Math.max(0, valor));
            }
            case "dec" -> repo.incrementarEstoquePorEtiqueta(produtoId, etiqueta, -valor);
            default     -> repo.upsertEstoquePorEtiqueta(produtoId, etiqueta, Math.max(0, valor));
        }
        return listarEstoqueDoProduto(produtoId);
    }
}
