package com.luigarah.repository;

import com.luigarah.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioPadraoProduto extends JpaRepository<Produto, Long> {

    // Lista produtos por padrao (usa, br, sapatos, null)
    @Query(value = """
            SELECT * FROM app_luigarah.produtos
             WHERE (:padrao IS NULL AND padrao_tamanho IS NULL)
                OR (:padrao IS NOT NULL AND LOWER(padrao_tamanho) = LOWER(:padrao))
            """, nativeQuery = true)
    List<Produto> listarPorPadrao(@Param("padrao") String padrao);

    // Projeção (id, padrao)
    @Query(value = """
            SELECT p.id, p.padrao_tamanho
              FROM app_luigarah.produtos p
             WHERE (:padrao IS NULL AND p.padrao_tamanho IS NULL)
                OR (:padrao IS NOT NULL AND LOWER(p.padrao_tamanho) = LOWER(:padrao))
            """, nativeQuery = true)
    List<Object[]> listarIdsEPadrao(@Param("padrao") String padrao);

    // Bulk set padrao
    @Modifying
    @Query(value = """
            UPDATE app_luigarah.produtos
               SET padrao_tamanho = :padrao
             WHERE id IN (:ids)
            """, nativeQuery = true)
    int definirPadrao(@Param("padrao") String padrao, @Param("ids") List<Long> ids);

    // Bulk clear padrao (indefinido = NULL)
    @Modifying
    @Query(value = """
            UPDATE app_luigarah.produtos
               SET padrao_tamanho = NULL
             WHERE id IN (:ids)
            """, nativeQuery = true)
    int limparPadrao(@Param("ids") List<Long> ids);

    // Set individual
    @Modifying
    @Query(value = """
            UPDATE app_luigarah.produtos
               SET padrao_tamanho = :padrao
             WHERE id = :id
            """, nativeQuery = true)
    int definirPadraoUm(@Param("id") Long id, @Param("padrao") String padrao);

    @Modifying
    @Query(value = """
            UPDATE app_luigarah.produtos
               SET padrao_tamanho = NULL
             WHERE id = :id
            """, nativeQuery = true)
    int limparPadraoUm(@Param("id") Long id);
}
