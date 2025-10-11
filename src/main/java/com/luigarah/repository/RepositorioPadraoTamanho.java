package com.luigarah.repository;

import com.luigarah.model.Tamanho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioPadraoTamanho extends JpaRepository<Tamanho, Long> {

    // Lista tamanhos por padrao (usa, br, sapatos, null)
    @Query(value = """
            SELECT * FROM app_luigarah.tamanhos
             WHERE (:padrao IS NULL AND padrao IS NULL)
                OR (:padrao IS NOT NULL AND LOWER(padrao) = LOWER(:padrao))
            """, nativeQuery = true)
    List<Tamanho> listarPorPadrao(@Param("padrao") String padrao);

    // Projeção (id, padrao)
    @Query(value = """
            SELECT t.id, t.padrao
              FROM app_luigarah.tamanhos t
             WHERE (:padrao IS NULL AND t.padrao IS NULL)
                OR (:padrao IS NOT NULL AND LOWER(t.padrao) = LOWER(:padrao))
            """, nativeQuery = true)
    List<Object[]> listarIdsEPadrao(@Param("padrao") String padrao);

    // Bulk set padrao
    @Modifying
    @Query(value = """
            UPDATE app_luigarah.tamanhos
               SET padrao = :padrao
             WHERE id IN (:ids)
            """, nativeQuery = true)
    int definirPadrao(@Param("padrao") String padrao, @Param("ids") List<Long> ids);

    // Bulk clear padrao
    @Modifying
    @Query(value = """
            UPDATE app_luigarah.tamanhos
               SET padrao = NULL
             WHERE id IN (:ids)
            """, nativeQuery = true)
    int limparPadrao(@Param("ids") List<Long> ids);

    // Individual
    @Modifying
    @Query(value = """
            UPDATE app_luigarah.tamanhos
               SET padrao = :padrao
             WHERE id = :id
            """, nativeQuery = true)
    int definirPadraoUm(@Param("id") Long id, @Param("padrao") String padrao);

    @Modifying
    @Query(value = """
            UPDATE app_luigarah.tamanhos
               SET padrao = NULL
             WHERE id = :id
            """, nativeQuery = true)
    int limparPadraoUm(@Param("id") Long id);
}
