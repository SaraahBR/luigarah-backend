package com.luigarah.repository.produto;

import com.luigarah.model.produto.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Repositório de Produtos.
 *
 * Observações (PostgreSQL / Supabase):
 * - As queries nativas não qualificam o schema (ex.: FROM produtos); o schema
 *   app_luigarah é resolvido pelo search_path definido em
 *   spring.datasource.hikari.connection-init-sql (application.properties).
 * - Colunas longas (descricao, imagens...) são TEXT, então LOWER/LIKE funcionam direto.
 * - COALESCE/GREATEST são usados para tratar nulos e nunca deixar estoque negativo.
 * - INSERT ... ON CONFLICT é utilizado para "upsert" (criar ou atualizar) registros de estoque.
 * - Parâmetros opcionais usam CAST(:param AS varchar) para o Postgres inferir o tipo quando nulos.
 *
 * Convenções:
 * - Métodos derivados de nomes (findBy...) usam JPQL/Criteria gerada pelo Spring.
 * - Métodos com @Query(nativeQuery=true) usam SQL nativo diretamente quando
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

    /**
     * Define (upsert) o estoque do produto. Se não existir cria, senão atualiza.
     * ON CONFLICT usa a PK produtos_estoque(produto_id); EXCLUDED é a linha que seria inserida.
     */
    @Modifying
    @Query(value = """
        INSERT INTO produtos_estoque (produto_id, qtd_estoque)
        VALUES (:produtoId, :qtd)
        ON CONFLICT (produto_id) DO UPDATE SET qtd_estoque = EXCLUDED.qtd_estoque
        """, nativeQuery = true)
    void upsertEstoqueProduto(@Param("produtoId") Long produtoId, @Param("qtd") int qtd);

    /** Incrementa/decrementa (delta pode ser negativo) o estoque do produto. */
    @Modifying
    @Query(value = """
        UPDATE produtos_estoque
           SET qtd_estoque = GREATEST(0, COALESCE(qtd_estoque,0) + :delta)
         WHERE produto_id = :produtoId
        """, nativeQuery = true)
    int incrementarEstoqueProduto(@Param("produtoId") Long produtoId, @Param("delta") int delta);

    // ---------------------------------------------------------------------
    // BUSCAS PADRÃO (categoria / subtítulo / termo / autor / contagem)
    // ---------------------------------------------------------------------

    Page<Produto> findByCategoria(String categoria, Pageable pageable);

    Page<Produto> findByCategoriaAndSubtituloContainingIgnoreCase(
            String categoria, String subtitulo, Pageable pageable);

    /** Busca "fulltext simples": título, autor e descrição (TEXT). */
    @Query(value = """
            SELECT *
              FROM produtos p
             WHERE LOWER(p.titulo) LIKE '%' || :busca || '%'
                OR LOWER(p.autor)  LIKE '%' || :busca || '%'
                OR LOWER(p.descricao) LIKE '%' || :busca || '%'
            """,
            countQuery = """
            SELECT COUNT(*)
              FROM produtos p
             WHERE LOWER(p.titulo) LIKE '%' || :busca || '%'
                OR LOWER(p.autor)  LIKE '%' || :busca || '%'
                OR LOWER(p.descricao) LIKE '%' || :busca || '%'
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
                  OR LOWER(p.descricao) LIKE '%' || :busca || '%'
               )
            """,
            countQuery = """
            SELECT COUNT(*)
              FROM produtos p
             WHERE p.categoria = :categoria
               AND (
                     LOWER(p.titulo) LIKE '%' || :busca || '%'
                  OR LOWER(p.autor)  LIKE '%' || :busca || '%'
                  OR LOWER(p.descricao) LIKE '%' || :busca || '%'
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

    /*
     * Catálogo de etiquetas (usado nos filtros do site). Sem padrão, a categoria "roupas"
     * traz USA e BR juntos: o GROUP BY remove etiquetas repetidas (ex.: "M" existe nos dois)
     * e a ordenação lista primeiro USA, depois BR, cada um na ordem do catálogo.
     */

    /**
     * LEGADO: Lista catálogo por categoria (sem filtrar padrao).
     * Mantido para compatibilidade. Prefira o método com parâmetro padrao.
     */
    @Query(value = """
            SELECT t.etiqueta
              FROM tamanhos t
             WHERE t.categoria = :categoria
             GROUP BY t.etiqueta
             ORDER BY MIN(CASE t.padrao WHEN 'usa' THEN 0 WHEN 'br' THEN 100 ELSE 200 END
                          + COALESCE(t.ordem, 0)), t.etiqueta
            """, nativeQuery = true)
    List<String> listarCatalogoEtiquetas(@Param("categoria") String categoria);

    /** Lista catálogo por categoria com PADRÃO opcional (usa|br|sapatos). */
    @Query(value = """
            SELECT t.etiqueta
              FROM tamanhos t
             WHERE t.categoria = :categoria
               AND (CAST(:padrao AS varchar) IS NULL OR t.padrao = CAST(:padrao AS varchar))
             GROUP BY t.etiqueta
             ORDER BY MIN(CASE t.padrao WHEN 'usa' THEN 0 WHEN 'br' THEN 100 ELSE 200 END
                          + COALESCE(t.ordem, 0)), t.etiqueta
            """, nativeQuery = true)
    // CAST(:padrao AS varchar): quando padrao é null o Hibernate envia o parâmetro sem tipo,
    // e o PostgreSQL recusa "? IS NULL" sem saber o tipo. O CAST resolve.
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
           AND COALESCE(pt.qtd_estoque,0) > 0
        """,
            countQuery = """
        SELECT COUNT(*)
          FROM produtos p
          JOIN produtos_tamanhos pt ON pt.produto_id = p.id
          JOIN tamanhos t          ON t.id = pt.tamanho_id
         WHERE p.categoria = :categoria
           AND UPPER(t.etiqueta) = UPPER(:etiqueta)
           AND COALESCE(pt.qtd_estoque,0) > 0
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

    /**
     * Etiquetas de vários produtos numa única consulta: cada linha é [produto_id, etiqueta].
     * Usado pelas listagens, que antes pediam os tamanhos produto por produto.
     * Com somenteComEstoque, ignora tamanhos zerados (mesma regra do filtro por tamanho).
     */
    @Query(value = """
            SELECT pt.produto_id, t.etiqueta
              FROM produtos_tamanhos pt
              JOIN tamanhos t ON t.id = pt.tamanho_id
             WHERE pt.produto_id IN (:ids)
               AND (:somenteComEstoque = FALSE OR COALESCE(pt.qtd_estoque, 0) > 0)
             ORDER BY pt.produto_id, t.ordem NULLS FIRST, t.etiqueta
            """, nativeQuery = true)
    List<Object[]> listarEtiquetasPorProdutos(@Param("ids") Collection<Long> ids,
                                              @Param("somenteComEstoque") boolean somenteComEstoque);

    /** Mesma consulta para todos os produtos de uma categoria: cada linha é [produto_id, etiqueta]. */
    @Query(value = """
            SELECT pt.produto_id, t.etiqueta
              FROM produtos_tamanhos pt
              JOIN tamanhos t ON t.id = pt.tamanho_id
              JOIN produtos p ON p.id = pt.produto_id
             WHERE p.categoria = :categoria
               AND (:somenteComEstoque = FALSE OR COALESCE(pt.qtd_estoque, 0) > 0)
             ORDER BY pt.produto_id, t.ordem NULLS FIRST, t.etiqueta
            """, nativeQuery = true)
    List<Object[]> listarEtiquetasPorCategoria(@Param("categoria") String categoria,
                                               @Param("somenteComEstoque") boolean somenteComEstoque);

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

    /** Lista (id, etiqueta, qtd_estoque) para o produto, ordenado pela ORDEM do catálogo. */
    @Query(value = """
            SELECT t.id, t.etiqueta, pt.qtd_estoque
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
            INSERT INTO produtos_tamanhos (produto_id, tamanho_id, qtd_estoque)
            SELECT :produtoId, t.id, :qtd
              FROM tamanhos t
             WHERE t.etiqueta  = :etiqueta
               AND t.categoria = (SELECT categoria       FROM produtos WHERE id = :produtoId)
               AND t.padrao    = (SELECT padrao_tamanho  FROM produtos WHERE id = :produtoId)
            ON CONFLICT (produto_id, tamanho_id) DO UPDATE SET qtd_estoque = EXCLUDED.qtd_estoque
            -- ON CONFLICT usa a PK pk_produtos_tamanhos(produto_id, tamanho_id)
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
            -- No PostgreSQL a coluna do SET não pode ter o alias da tabela (pt.)
            UPDATE produtos_tamanhos pt
               SET qtd_estoque = GREATEST(0, COALESCE(pt.qtd_estoque,0) + :delta)
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
