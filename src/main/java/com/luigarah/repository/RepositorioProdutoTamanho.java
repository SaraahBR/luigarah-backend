package com.luigarah.repository;

import com.luigarah.model.ProdutoTamanho;
import com.luigarah.model.id.ProdutoTamanhoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepositorioProdutoTamanho extends JpaRepository<ProdutoTamanho, ProdutoTamanhoId> {
    List<ProdutoTamanho> findByProdutoId(Long produtoId);
    Optional<ProdutoTamanho> findByProdutoIdAndTamanhoId(Long produtoId, Long tamanhoId);
    void deleteByProdutoId(Long produtoId);
    void deleteByProdutoIdAndTamanhoId(Long produtoId, Long tamanhoId);
}
