package com.luigarah.repository;

import com.luigarah.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório de Produtos.
 *
 * Observações (Oracle):
 * - Para buscar em campos CLOB (descricao) usamos DBMS_LOB.SUBSTR.
 * - Funções NVL/GREATEST são usadas para tratar nulos e nunca deixar estoque negativo.
 * - MERGE é utilizado para "upsert" (criar ou atualizar) registros de estoque.
 *
 * Convenções:
 * - Métodos derivados de nomes (findBy...) usam JPQL/Criteria gerada pelo Spring.
 * - Métodos com @Query(nativeQuery=true) usam SQL Oracle diretamente quando
 *   precisamos de JOINs/otimizações específicas.
 */
@Repository
public interface RepositorioProduto extends JpaRepository<Produto, Long> {

    // ---------------------------------------------------------------------
    // UTILIDADES
    // ---------------------------------------------------------------------

    /** Categoria do produto (roupas|sapatos|bolsas). */
    @Query("select p.categoria from Produto p where p.id = :produtoId")
    String obterCategoriaDoProduto(@Param("produtoId") Long produtoId);

    /** Padrão de tamanho do produto (usa|br|sapatos) — coluna PADRAO_TAMANHO. */
    @Query("select p.padraoTamanho from Produto p where p.id = :produtoId")
    String obterPadraoTamanhoProduto(@Param("produtoId") Long produtoId);

    // ---------------------------------------------------------------------
    // ESTOQUE POR PRODUTO (bolsas = sem tamanho)
    // Tabela: produtos_estoque(produto_id, qtd_estoque)
    // ---------------------------------------------------------------------

    /** Lê o estoque consolidado do produto (para BOLSAS ou itens sem tamanho). */
    @Query(value = "SELECT qtd_estoque FROM produtos_estoque WHERE produto_id = :produtoId", nativeQuery = true)
    Integer obterEstoqueProduto(@Param("produtoId") Long produtoId);

    /** Define (upsert) o estoque do produto. Se não existir cria, senão atualiza. */
    @Modifying
    @Query(value = """
        MERGE INTO produtos_estoque pe
        USING (SELECT :produtoId AS produto_id, :qtd AS qtd FROM dual) x
        ON (pe.produto_id = x.produto_id)
        WHEN MATCHED THEN UPDATE SET pe.qtd_estoque = x.qtd
        WHEN NOT MATCHED THEN INSERT (produto_id, qtd_estoque) VALUES (x.produto_id, x.qtd)
        """, nativeQuery = true)
    void upsertEstoqueProduto(@Param("produtoId") Long produtoId, @Param("qtd") int qtd);

    /** Incrementa/decrementa (delta pode ser negativo) o estoque do produto. */
    @Modifying
    @Query(value = """
        UPDATE produtos_estoque
           SET qtd_estoque = GREATEST(0, NVL(qtd_estoque,0) + :delta)
         WHERE produto_id = :produtoId
        """, nativeQuery = true)
    int incrementarEstoqueProduto(@Param("produtoId") Long produtoId, @Param("delta") int delta);

    // ---------------------------------------------------------------------
    // BUSCAS PADRÃO (categoria / subtítulo / termo / autor / contagem)
    // ---------------------------------------------------------------------

    Page<Produto> findByCategoria(String categoria, Pageable pageable);

    Page<Produto> findByCategoriaAndSubtituloContainingIgnoreCase(
            String categoria, String subtitulo, Pageable pageable);

    /** Busca "fulltext simples": título, autor e descrição (CLOB). */
    @Query(value = """
            SELECT *
              FROM produtos p
             WHERE LOWER(p.titulo) LIKE '%' || :busca || '%'
                OR LOWER(p.autor)  LIKE '%' || :busca || '%'
                OR LOWER(DBMS_LOB.SUBSTR(p.descricao, 4000, 1)) LIKE '%' || :busca || '%'
            """,
            countQuery = """
            SELECT COUNT(*)
              FROM produtos p
             WHERE LOWER(p.titulo) LIKE '%' || :busca || '%'
                OR LOWER(p.autor)  LIKE '%' || :busca || '%'
                OR LOWER(DBMS_LOB.SUBSTR(p.descricao, 4000, 1)) LIKE '%' || :busca || '%'
            """,
            nativeQuery = true)
    Page<Produto> buscarPorTermoPesquisa(@Param("busca") String busca, Pageable pageable);

    /** Combina categoria + termo de busca. */
    @Query(value = """
            SELECT *
              FROM produtos p
             WHERE p.categoria = :categoria
               AND (
                     LOWER(p.titulo) LIKE '%' || :busca || '%'
                  OR LOWER(p.autor)  LIKE '%' || :busca || '%'
                  OR LOWER(DBMS_LOB.SUBSTR(p.descricao, 4000, 1)) LIKE '%' || :busca || '%'
               )
            """,
            countQuery = """
            SELECT COUNT(*)
              FROM produtos p
             WHERE p.categoria = :categoria
               AND (
                     LOWER(p.titulo) LIKE '%' || :busca || '%'
                  OR LOWER(p.autor)  LIKE '%' || :busca || '%'
                  OR LOWER(DBMS_LOB.SUBSTR(p.descricao, 4000, 1)) LIKE '%' || :busca || '%'
               )
            """,
            nativeQuery = true)
    Page<Produto> buscarPorCategoriaETermoPesquisa(
            @Param("categoria") String categoria,
            @Param("busca") String busca,
            Pageable pageable);

    List<Produto> findByAutorContainingIgnoreCase(String autor);

    long countByCategoria(String categoria);

    // ---------------------------------------------------------------------
    // DIMENSÃO (Grande, Médio, Média, Pequena, Mini, ...)
    // ---------------------------------------------------------------------

    Page<Produto> findByDimensaoIgnoreCase(String dimensao, Pageable pageable);

    Page<Produto> findByCategoriaAndDimensaoIgnoreCase(String categoria, String dimensao, Pageable pageable);

    // ---------------------------------------------------------------------
    // CATÁLOGO DE TAMANHOS
    // ---------------------------------------------------------------------

    /**
     * LEGADO: Lista catálogo por categoria (sem filtrar padrao).
     * Mantido para compatibilidade. Prefira o método com parâmetro padrao.
     */
    @Query(value = """
            SELECT t.etiqueta
              FROM tamanhos t
             WHERE t.categoria = :categoria
             ORDER BY t.ordem NULLS FIRST, t.etiqueta
            """, nativeQuery = true)
    List<String> listarCatalogoEtiquetas(@Param("categoria") String categoria);

    /** Lista catálogo por categoria com PADRÃO opcional (usa|br|sapatos). */
    @Query(value = """
            SELECT t.etiqueta
              FROM tamanhos t
             WHERE t.categoria = :categoria
               AND (:padrao IS NULL OR t.padrao = :padrao)
             ORDER BY t.ordem NULLS FIRST, t.etiqueta
            """, nativeQuery = true)
    List<String> listarCatalogoEtiquetas(@Param("categoria") String categoria,
                                         @Param("padrao") String padrao);

    // ---------------------------------------------------------------------
    // PRODUTOS POR CATEGORIA + ETIQUETA (TAMANHO)
    // ---------------------------------------------------------------------

    /** Filtra roupas/sapatos por etiqueta de tamanho (somente com estoque > 0). */
    @Query(value = """
        SELECT p.*
          FROM produtos p
          JOIN produtos_tamanhos pt ON pt.produto_id = p.id
          JOIN tamanhos t          ON t.id = pt.tamanho_id
         WHERE p.categoria = :categoria
           AND UPPER(t.etiqueta) = UPPER(:etiqueta)
           AND NVL(pt.qtd_estoque,0) > 0
        """,
            countQuery = """
        SELECT COUNT(*)
          FROM produtos p
          JOIN produtos_tamanhos pt ON pt.produto_id = p.id
          JOIN tamanhos t          ON t.id = pt.tamanho_id
         WHERE p.categoria = :categoria
           AND UPPER(t.etiqueta) = UPPER(:etiqueta)
           AND NVL(pt.qtd_estoque,0) > 0
        """,
            nativeQuery = true)
    Page<Produto> buscarPorCategoriaETamanho(@Param("categoria") String categoria,
                                             @Param("etiqueta") String etiqueta,
                                             Pageable pageable);

    // ---------------------------------------------------------------------
    // TAMANHOS VINCULADOS A UM PRODUTO
    // ---------------------------------------------------------------------

    /** Lista as etiquetas já vinculadas ao produto (ordenadas por ORDEM). */
    @Query(value = """
            SELECT t.etiqueta
              FROM produtos_tamanhos pt
              JOIN tamanhos t ON t.id = pt.tamanho_id
             WHERE pt.produto_id = :produtoId
             ORDER BY t.ordem NULLS FIRST, t.etiqueta
            """, nativeQuery = true)
    List<String> listarEtiquetasPorProduto(@Param("produtoId") Long produtoId);

    /** Remove todos os vínculos de tamanho do produto. */
    @Modifying
    @Query(value = "DELETE FROM produtos_tamanhos WHERE produto_id = :produtoId", nativeQuery = true)
    void deletarTamanhosDoProduto(@Param("produtoId") Long produtoId);

    /**
     * Insere um vínculo produto+tamanho (se existir no catálogo da mesma categoria)
     * com uma quantidade inicial de estoque. Ignora duplicidade (NOT EXISTS).
     * AGORA com filtro pelo PADRÃO do produto para diferenciar BR vs USA.
     */
    @Modifying
    @Query(value = """
            INSERT INTO produtos_tamanhos (produto_id, tamanho_id, qtd_estoque)
            SELECT :produtoId,
                   t.id,
                   :qtd
              FROM tamanhos t
             WHERE t.etiqueta  = :etiqueta
               AND t.categoria = (SELECT categoria       FROM produtos WHERE id = :produtoId)
               AND t.padrao    = (SELECT padrao_tamanho  FROM produtos WHERE id = :produtoId)
               AND NOT EXISTS (
                   SELECT 1 FROM produtos_tamanhos x
                    WHERE x.produto_id = :produtoId
                      AND x.tamanho_id = t.id
               )
            """, nativeQuery = true)
    void inserirTamanho(@Param("produtoId") Long produtoId,
                        @Param("etiqueta") String etiqueta,
                        @Param("qtd") int qtdEstoqueInicial);

    /** Remove UMA etiqueta específica do produto. Retorna nº de linhas afetadas. (agora também filtra por padrão) */
    @Modifying
    @Query(value = """
            DELETE FROM produtos_tamanhos
             WHERE produto_id = :produtoId
               AND tamanho_id = (
                   SELECT id FROM tamanhos
                    WHERE etiqueta  = :etiqueta
                      AND categoria = (SELECT categoria       FROM produtos WHERE id = :produtoId)
                      AND padrao    = (SELECT padrao_tamanho  FROM produtos WHERE id = :produtoId)
               )
            """, nativeQuery = true)
    int removerTamanho(@Param("produtoId") Long produtoId, @Param("etiqueta") String etiqueta);

    // ---------------------------------------------------------------------
    // ESTOQUE POR PRODUTO/TAMANHO (roupas/sapatos)
    // ---------------------------------------------------------------------

    /** Lista (etiqueta, qtd_estoque) para o produto, ordenado pela ORDEM do catálogo. */
    @Query(value = """
            SELECT t.etiqueta, pt.qtd_estoque
              FROM produtos_tamanhos pt
              JOIN tamanhos t ON t.id = pt.tamanho_id
             WHERE pt.produto_id = :produtoId
             ORDER BY t.ordem NULLS FIRST, t.etiqueta
            """, nativeQuery = true)
    List<Object[]> listarEstoquePorProduto(@Param("produtoId") Long produtoId);

    /**
     * Upsert de quantidade por etiqueta para o produto (cria o vínculo se necessário).
     * AGORA garantindo que buscamos o tamanho pelo mesmo PADRÃO do produto.
     */
    @Modifying
    @Query(value = """
            MERGE INTO produtos_tamanhos pt
            USING (
                SELECT :produtoId AS produto_id,
                       t.id      AS tamanho_id,
                       :qtd      AS qtd
                  FROM tamanhos t
                 WHERE t.etiqueta  = :etiqueta
                   AND t.categoria = (SELECT categoria       FROM produtos WHERE id = :produtoId)
                   AND t.padrao    = (SELECT padrao_tamanho  FROM produtos WHERE id = :produtoId)
            ) x
            ON (pt.produto_id = x.produto_id AND pt.tamanho_id = x.tamanho_id)
            WHEN MATCHED THEN UPDATE SET pt.qtd_estoque = x.qtd
            WHEN NOT MATCHED THEN INSERT (produto_id, tamanho_id, qtd_estoque)
                 VALUES (x.produto_id, x.tamanho_id, x.qtd)
            """, nativeQuery = true)
    void upsertEstoquePorEtiqueta(@Param("produtoId") Long produtoId,
                                  @Param("etiqueta") String etiqueta,
                                  @Param("qtd") int qtd);

    /**
     * Incrementa/decrementa a quantidade para uma etiqueta específica do produto.
     * AGORA garantindo que identificamos o tamanho pelo PADRÃO do produto.
     */
    @Modifying
    @Query(value = """
            UPDATE produtos_tamanhos pt
               SET pt.qtd_estoque = GREATEST(0, NVL(pt.qtd_estoque,0) + :delta)
             WHERE pt.produto_id = :produtoId
               AND pt.tamanho_id = (
                 SELECT id FROM tamanhos
                  WHERE etiqueta  = :etiqueta
                    AND categoria = (SELECT categoria       FROM produtos WHERE id = :produtoId)
                    AND padrao    = (SELECT padrao_tamanho  FROM produtos WHERE id = :produtoId)
               )
            """, nativeQuery = true)
    int incrementarEstoquePorEtiqueta(@Param("produtoId") Long produtoId,
                                      @Param("etiqueta") String etiqueta,
                                      @Param("delta") int delta);

    // ---------------------------------------------------------------------
    // BUSCAS POR IDENTIDADE
    // ---------------------------------------------------------------------

    /** Busca todos os produtos que possuem identidade */
    List<Produto> findByIdentidadeIsNotNull();

    /** Busca todos os produtos que não possuem identidade */
    List<Produto> findByIdentidadeIsNull();

    /** Busca produtos por ID da identidade */
    List<Produto> findByIdentidadeId(Long identidadeId);

    /** Busca produtos por ID da identidade com paginação */
    Page<Produto> findByIdentidadeId(Long identidadeId, Pageable pageable);

    /** Busca produtos por código da identidade */
    @Query("SELECT p FROM Produto p WHERE p.identidade.codigo = :codigo")
    List<Produto> findByIdentidadeCodigo(@Param("codigo") String codigo);

    /** Busca produtos por código da identidade com paginação */
    @Query("SELECT p FROM Produto p WHERE p.identidade.codigo = :codigo")
    Page<Produto> findByIdentidadeCodigo(@Param("codigo") String codigo, Pageable pageable);
}
