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
class RepositoriosPostgresTest {

    @TestConfiguration
    static class PostgresEmbarcadoConfig {

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
        assertEquals(29, repoTamanho.count());
        assertEquals(26, repoProduto.countByCategoria("bolsas"));
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
        assertEquals(14, repoProduto.listarCatalogoEtiquetas("roupas", null).size());
        assertEquals(List.of("PP", "P", "M", "G", "XG", "G1", "G2"), repoTamanho.listarEtiquetas("roupas", "br"));
        assertEquals(15, repoTamanho.findByCategoriaAndPadraoOrder("sapatos", null).size());
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
        assertEquals(7, repoPadraoTamanho.listarPorPadrao("usa").size());
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
