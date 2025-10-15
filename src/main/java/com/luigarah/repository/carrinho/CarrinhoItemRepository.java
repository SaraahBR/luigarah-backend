package com.luigarah.repository.carrinho;

import com.luigarah.model.carrinho.CarrinhoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrinhoItemRepository extends JpaRepository<CarrinhoItem, Long> {

    List<CarrinhoItem> findByUsuarioIdOrderByDataAdicaoDesc(Long usuarioId);

    Optional<CarrinhoItem> findByUsuarioIdAndProdutoIdAndTamanhoId(Long usuarioId, Long produtoId, Long tamanhoId);

    Optional<CarrinhoItem> findByUsuarioIdAndProdutoIdAndTamanhoIsNull(Long usuarioId, Long produtoId);

    void deleteByUsuarioId(Long usuarioId);

    Long countByUsuarioId(Long usuarioId);
}
