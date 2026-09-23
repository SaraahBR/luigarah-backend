package com.luigarah.repository.tamanho;

import com.luigarah.model.tamanho.Tamanho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Catálogo de tamanhos (tabela tamanhos: categoria + padrao + etiqueta).
 *
 * PostgreSQL: o filtro de padrão é opcional; CAST(:padrao AS varchar) é necessário
 * porque, com padrao = null, o Hibernate envia o parâmetro sem tipo e o Postgres
 * não consegue avaliar "? IS NULL".
 */
public interface RepositorioTamanho extends JpaRepository<Tamanho, Long> {

    // Catálogo por categoria/padrão (padrão opcional).
    @Query(value = """
            SELECT t.*
              FROM tamanhos t
             WHERE t.categoria = :categoria
               AND (CAST(:padrao AS varchar) IS NULL OR t.padrao = CAST(:padrao AS varchar))
             ORDER BY t.ordem NULLS FIRST, t.etiqueta
            """, nativeQuery = true)
    List<Tamanho> findByCategoriaAndPadraoOrder(@Param("categoria") String categoria,
                                                @Param("padrao") String padrao);

    // Somente etiquetas, sem repetição (sem padrão: USA primeiro, depois BR)
    @Query(value = """
            SELECT t.etiqueta
              FROM tamanhos t
             WHERE t.categoria = :categoria
               AND (CAST(:padrao AS varchar) IS NULL OR t.padrao = CAST(:padrao AS varchar))
             GROUP BY t.etiqueta
             ORDER BY MIN(CASE t.padrao WHEN 'usa' THEN 0 WHEN 'br' THEN 100 ELSE 200 END
                          + COALESCE(t.ordem, 0)), t.etiqueta
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
