package com.luigarah.repository;

import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.model.autenticacao.VerificationToken;
import com.luigarah.model.carrinho.CarrinhoItem;
import com.luigarah.model.produto.Produto;
import com.luigarah.repository.autenticacao.UsuarioRepository;
import com.luigarah.repository.autenticacao.VerificationTokenRepository;
import com.luigarah.repository.carrinho.CarrinhoItemRepository;
import com.luigarah.repository.identidade.RepositorioIdentidade;
import com.luigarah.repository.produto.RepositorioProduto;
import com.luigarah.repository.tamanho.RepositorioPadraoProduto;
import com.luigarah.repository.tamanho.RepositorioPadraoTamanho;
import com.luigarah.repository.tamanho.RepositorioTamanho;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração contra um PostgreSQL real (embarcado):
 * - Aplica as migrations Flyway (schema + seeds)
 * - Valida o mapeamento das entidades contra o schema (ddl-auto=validate)
 * - Executa as queries nativas (sintaxe PostgreSQL) dos repositórios
 *
 * O Postgres embarcado (io.zonky.test) baixa os binários pelo Maven e não precisa de Docker.
 */
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(RepositoriosPostgresTest.PostgresEmbarcadoConfig.class)
public class RepositoriosPostgresTest {

    /** PostgreSQL embarcado; também usado pelo teste de segurança da API. */
    @TestConfiguration
    public static class PostgresEmbarcadoConfig {

        @Bean(destroyMethod = "close")
        EmbeddedPostgres embeddedPostgres() throws IOException {
            return EmbeddedPostgres.start();
        }

        @Bean
        DataSource dataSource(EmbeddedPostgres pg) {
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(pg.getJdbcUrl("postgres", "postgres"));
            cfg.setUsername("postgres");
            // mesmo ajuste de application.properties
            cfg.setConnectionInitSql("SET search_path TO app_luigarah, public");
            return new HikariDataSource(cfg);
        }
    }

    @Autowired RepositorioProduto repoProduto;
    @Autowired RepositorioTamanho repoTamanho;
    @Autowired RepositorioPadraoProduto repoPadraoProduto;
    @Autowired RepositorioPadraoTamanho repoPadraoTamanho;
    @Autowired RepositorioIdentidade repoIdentidade;
    @Autowired UsuarioRepository repoUsuario;
    @Autowired CarrinhoItemRepository repoCarrinho;
    @Autowired VerificationTokenRepository repoToken;
    @Autowired EntityManager entityManager;

    private static final long BOLSA_ID = 1L;   // Gucci / Tiracolo
    private static final long ROUPA_BR_ID = 15L;   // MARANT ÉTOILE / Colete (padrão br)
    private static final long PRODUTO_ARRAY_CORROMPIDO_ID = 149L; // GCDS / Jaqueta

    // ---------------------------------------------------------------------
    // Seeds
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Deve carregar produtos, identidades e catálogo de tamanhos")
    void deveCarregarSeeds() {
        assertEquals(135, repoProduto.count());
        assertEquals(4, repoIdentidade.count());
        assertEquals(33, repoTamanho.count()); // 29 da V2 + XXL, XXXL, 30 e 31 da V5
        assertEquals(26, repoProduto.countByCategoria("bolsas"));
    }

    @Test
    @DisplayName("V5 deve sortear os tamanhos novos só para produtos compatíveis")
    void deveSortearTamanhosNovosSoParaProdutosCompativeis() {
        // Cada linha: categoria, padrão do produto, etiqueta, estoque
        List<Object[]> vinculos = entityManager.createNativeQuery("""
                SELECT p.categoria, p.padrao_tamanho, t.etiqueta, pt.qtd_estoque
                  FROM produtos_tamanhos pt
                  JOIN produtos p ON p.id = pt.produto_id
                  JOIN tamanhos t ON t.id = pt.tamanho_id
                 WHERE t.etiqueta IN ('XXL', 'XXXL', '30', '31')
                """).getResultList();

        assertFalse(vinculos.isEmpty());
        for (Object[] v : vinculos) {
            String categoria = (String) v[0], padrao = (String) v[1], etiqueta = (String) v[2];
            int qtd = ((Number) v[3]).intValue();
            if (etiqueta.startsWith("X")) {
                assertEquals("roupas", categoria);
                assertEquals("usa", padrao);
            } else {
                assertEquals("sapatos", categoria);
            }
            assertTrue(qtd >= 1 && qtd <= 15, "estoque fora do intervalo: " + qtd);
        }
        // Sorteio: nem todos os produtos elegíveis recebem os tamanhos novos
        long sapatosCom30 = vinculos.stream().filter(v -> "30".equals(v[2])).count();
        assertTrue(sapatosCom30 > 0 && sapatosCom30 < 49, "sapatos com 30: " + sapatosCom30);
    }

    @Test
    @DisplayName("Deve restaurar arrays JSON corrompidos no backup")
    void deveRestaurarArraysJson() {
        Produto p = repoProduto.findById(PRODUTO_ARRAY_CORROMPIDO_ID).orElseThrow();
        assertTrue(p.getImagens().startsWith("[\"https://"), p.getImagens());
        assertTrue(p.getDestaques().startsWith("[\"vermelho\""), p.getDestaques());
    }

    // ---------------------------------------------------------------------
    // Buscas nativas
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Deve buscar por termo em título, autor e descrição")
    void deveBuscarPorTermo() {
        assertTrue(repoProduto.buscarPorTermoPesquisa("gucci", PageRequest.of(0, 10)).getTotalElements() > 0);
        assertTrue(repoProduto.buscarPorCategoriaETermoPesquisa("bolsas", "tiracolo", PageRequest.of(0, 10))
                .getTotalElements() > 0);
    }

    @Test
    @DisplayName("Deve listar catálogo com padrão nulo e informado")
    void deveListarCatalogo() {
        // Sem padrão: USA (XXXS..XXXL) e depois BR, sem repetir o "M"
        assertEquals(List.of("XXXS", "XXS", "XS", "S", "M", "L", "XL", "XXL", "XXXL", "PP", "P", "G", "XG", "G1", "G2"),
                repoProduto.listarCatalogoEtiquetas("roupas"));
        assertEquals(15, repoProduto.listarCatalogoEtiquetas("roupas", null).size());
        assertEquals(List.of("PP", "P", "M", "G", "XG", "G1", "G2"), repoTamanho.listarEtiquetas("roupas", "br"));
        assertEquals(17, repoTamanho.findByCategoriaAndPadraoOrder("sapatos", null).size()); // 30 a 46
        assertEquals(1, repoTamanho.existsEtiqueta("sapatos", "br", "38"));
    }

    @Test
    @DisplayName("Deve filtrar por categoria e tamanho com estoque")
    void deveFiltrarPorTamanho() {
        assertTrue(repoProduto.buscarPorCategoriaETamanho("roupas", "m", PageRequest.of(0, 10))
                .getTotalElements() > 0);
    }

    @Test
    @DisplayName("Deve listar produtos e tamanhos por padrão (incluindo nulo)")
    void deveListarPorPadrao() {
        assertEquals(26, repoPadraoProduto.listarPorPadrao(null).size());
        assertFalse(repoPadraoProduto.listarIdsEPadrao("BR").isEmpty());
        assertEquals(9, repoPadraoTamanho.listarPorPadrao("usa").size());
        assertTrue(repoPadraoTamanho.listarPorPadrao(null).isEmpty());
    }

    // ---------------------------------------------------------------------
    // Estoque
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Deve fazer upsert e incremento do estoque de bolsa sem ficar negativo")
    void deveAtualizarEstoqueBolsa() {
        assertEquals(10, repoProduto.obterEstoqueProduto(BOLSA_ID));
        repoProduto.upsertEstoqueProduto(BOLSA_ID, 3);
        assertEquals(3, repoProduto.obterEstoqueProduto(BOLSA_ID));
        repoProduto.incrementarEstoqueProduto(BOLSA_ID, -5);
        assertEquals(0, repoProduto.obterEstoqueProduto(BOLSA_ID));
    }

    @Test
    @DisplayName("Deve fazer upsert e incremento do estoque por etiqueta")
    void deveAtualizarEstoquePorEtiqueta() {
        repoProduto.upsertEstoquePorEtiqueta(ROUPA_BR_ID, "M", 7);
        assertEquals(1, repoProduto.incrementarEstoquePorEtiqueta(ROUPA_BR_ID, "M", 2));
        Object[] linhaM = repoProduto.listarEstoquePorProduto(ROUPA_BR_ID).stream()
                .filter(r -> "M".equals(r[1])).findFirst().orElseThrow();
        assertEquals(9, ((Number) linhaM[2]).intValue());
    }

    @Test
    @DisplayName("Deve remover e reinserir tamanho respeitando o padrão do produto")
    void deveRemoverEReinserirTamanho() {
        assertEquals(1, repoProduto.removerTamanho(ROUPA_BR_ID, "PP"));
        repoProduto.inserirTamanho(ROUPA_BR_ID, "PP", 10);
        assertTrue(repoProduto.listarEtiquetasPorProduto(ROUPA_BR_ID).contains("PP"));
    }

    @Test
    @DisplayName("Deve listar os tamanhos de vários produtos numa consulta, na mesma ordem da consulta individual")
    void deveListarTamanhosDeVariosProdutos() {
        List<Object[]> linhas = repoProduto.listarEtiquetasPorProdutos(List.of(ROUPA_BR_ID, BOLSA_ID), false);

        List<String> daRoupa = linhas.stream()
                .filter(l -> ((Number) l[0]).longValue() == ROUPA_BR_ID)
                .map(l -> (String) l[1])
                .toList();
        assertEquals(repoProduto.listarEtiquetasPorProduto(ROUPA_BR_ID), daRoupa);
        // bolsa não tem tamanho: não aparece nas linhas
        assertTrue(linhas.stream().noneMatch(l -> ((Number) l[0]).longValue() == BOLSA_ID));
    }

    @Test
    @DisplayName("Tamanhos por categoria com estoque seguem a mesma regra do filtro por tamanho")
    void deveListarTamanhosDaCategoriaComEstoque() {
        // zera um tamanho da roupa: some da lista com estoque, continua na lista completa
        repoProduto.upsertEstoquePorEtiqueta(ROUPA_BR_ID, "PP", 0);

        List<String> comEstoque = repoProduto.listarEtiquetasPorCategoria("roupas", true).stream()
                .filter(l -> ((Number) l[0]).longValue() == ROUPA_BR_ID)
                .map(l -> (String) l[1])
                .toList();
        List<String> todos = repoProduto.listarEtiquetasPorCategoria("roupas", false).stream()
                .filter(l -> ((Number) l[0]).longValue() == ROUPA_BR_ID)
                .map(l -> (String) l[1])
                .toList();

        assertFalse(comEstoque.contains("PP"));
        assertTrue(todos.contains("PP"));
        // o filtro antigo (categoria + tamanho) concorda: produto sem estoque em PP não aparece
        assertTrue(repoProduto.buscarPorCategoriaETamanho("roupas", "PP", PageRequest.of(0, 200))
                .stream().noneMatch(p -> p.getId() == ROUPA_BR_ID));
        // só a categoria pedida
        assertTrue(repoProduto.listarEtiquetasPorCategoria("sapatos", false).stream()
                .noneMatch(l -> ((Number) l[0]).longValue() == ROUPA_BR_ID));
    }

    // ---------------------------------------------------------------------
    // Sequences e identity
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Deve gerar IDs após os produtos importados")
    void deveGerarIdsDeProduto() {
        Produto novo = new Produto("Teste", "Teste", "Autor", "Descrição",
                new BigDecimal("100.00"), "Médio", "https://exemplo.com/a.jpg", "bolsas");
        novo.setComposicao("Couro 100%");
        assertTrue(repoProduto.saveAndFlush(novo).getId() > 151L); // setval após o seed
    }

    @Test
    @DisplayName("Deve salvar usuário, carrinho de bolsa (tamanho nulo) e token")
    void deveSalvarUsuarioCarrinhoEToken() {
        Usuario u = repoUsuario.saveAndFlush(Usuario.builder()
                .nome("Maria").email("maria@exemplo.com").senha("hash").build());
        assertNotNull(u.getId());

        repoCarrinho.saveAndFlush(CarrinhoItem.builder()
                .usuario(u).produto(repoProduto.getReferenceById(BOLSA_ID)).quantidade(1).build());
        assertTrue(repoCarrinho.findByUsuarioIdAndProdutoIdAndTamanhoIsNull(u.getId(), BOLSA_ID).isPresent());

        VerificationToken t = repoToken.saveAndFlush(VerificationToken.builder()
                .codigo("123456").token("uuid-1").email(u.getEmail())
                .tipo(VerificationToken.TipoToken.VERIFICACAO_EMAIL)
                .criadoEm(LocalDateTime.now()).expiraEm(LocalDateTime.now().plusHours(1))
                .usado(false).build());
        assertNotNull(t.getId());
        assertTrue(repoToken.findLatestByEmailAndTipo(u.getEmail(), VerificationToken.TipoToken.VERIFICACAO_EMAIL)
                .isPresent());
    }
}
