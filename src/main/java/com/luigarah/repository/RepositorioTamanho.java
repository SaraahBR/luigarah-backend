package com.luigarah.repository;

import com.luigarah.model.Tamanho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioTamanho extends JpaRepository<Tamanho, Long> {

    // Retorna registros Tamanho ordenados por ORDEM (se existir) e ETIQUETA.
    // Como é native, não exige que a propriedade 'ordem' exista na entidade.
    @Query(value = """
            SELECT *
              FROM tamanhos
             WHERE categoria = :categoria
             ORDER BY ordem NULLS FIRST, etiqueta
            """, nativeQuery = true)
    List<Tamanho> findByCategoriaOrderByOrdemAscEtiquetaAsc(@Param("categoria") String categoria);

    // Somente as etiquetas já ordenadas (útil para catálogos).
    @Query(value = """
            SELECT t.etiqueta
              FROM tamanhos t
             WHERE t.categoria = :categoria
             ORDER BY t.ordem NULLS FIRST, t.etiqueta
            """, nativeQuery = true)
    List<String> listarEtiquetasOrdenadas(@Param("categoria") String categoria);
}
