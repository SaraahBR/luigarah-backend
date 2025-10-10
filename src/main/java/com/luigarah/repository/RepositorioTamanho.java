package com.luigarah.repository;

import com.luigarah.model.Tamanho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioTamanho extends JpaRepository<Tamanho, Long> {

    // Catálogo por categoria/padrão (padrão opcional).
    @Query(value = """
            SELECT t.*
              FROM tamanhos t
             WHERE t.categoria = :categoria
               AND (:padrao IS NULL OR t.padrao = :padrao)
             ORDER BY t.ordem NULLS FIRST, t.etiqueta
            """, nativeQuery = true)
    List<Tamanho> findByCategoriaAndPadraoOrder(@Param("categoria") String categoria,
                                                @Param("padrao") String padrao);

    // Somente etiquetas (ordenadas)
    @Query(value = """
            SELECT t.etiqueta
              FROM tamanhos t
             WHERE t.categoria = :categoria
               AND (:padrao IS NULL OR t.padrao = :padrao)
             ORDER BY t.ordem NULLS FIRST, t.etiqueta
            """, nativeQuery = true)
    List<String> listarEtiquetas(@Param("categoria") String categoria,
                                 @Param("padrao") String padrao);

    // Validação: existe etiqueta nesse cat/padrão?
    @Query(value = """
            SELECT COUNT(1)
              FROM tamanhos t
             WHERE t.categoria = :categoria
               AND t.padrao = :padrao
               AND t.etiqueta = :etiqueta
            """, nativeQuery = true)
    int existsEtiqueta(@Param("categoria") String categoria,
                       @Param("padrao") String padrao,
                       @Param("etiqueta") String etiqueta);
}
