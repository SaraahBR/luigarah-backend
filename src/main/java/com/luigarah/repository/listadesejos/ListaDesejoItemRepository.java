package com.luigarah.repository.listadesejos;

import com.luigarah.model.listadesejos.ListaDesejoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListaDesejoItemRepository extends JpaRepository<ListaDesejoItem, Long> {

    List<ListaDesejoItem> findByUsuarioIdOrderByDataAdicaoDesc(Long usuarioId);

    Optional<ListaDesejoItem> findByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);

    Boolean existsByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);

    void deleteByUsuarioId(Long usuarioId);

    void deleteByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);

    Long countByUsuarioId(Long usuarioId);
}
