package com.luigarah.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luigarah.config.TraducaoRespostaAdvice;
import com.luigarah.dto.carrinho.CarrinhoItemDTO;
import com.luigarah.dto.produto.ProdutoDTO;
import com.luigarah.dto.produto.RespostaProdutoDTO;
import com.luigarah.model.produto.Produto;
import com.luigarah.repository.produto.RepositorioProduto;
import com.luigarah.repository.produto.RepositorioProdutoTraducao;
import com.luigarah.service.traducao.GoogleTranslateClient;
import com.luigarah.service.traducao.ServicoTraducaoProduto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tradução automática dos produtos (V7) contra PostgreSQL real.
 * O Google Cloud Translation é substituído por um tradutor falso que só
 * prefixa o idioma: "[en] texto".
 */
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({RepositoriosPostgresTest.PostgresEmbarcadoConfig.class, ServicoTraducaoProduto.class})
class TraducaoProdutoPostgresTest {

    @MockBean GoogleTranslateClient cliente;

    @Autowired ServicoTraducaoProduto servico;
    @Autowired RepositorioProduto repoProduto;
    @Autowired RepositorioProdutoTraducao repoTraducao;

    private static final long BOLSA_ID = 1L; // Gucci / Tiracolo
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void tradutorFalso() throws Exception {
        when(cliente.habilitado()).thenReturn(true);
        when(cliente.traduzir(anyList(), anyString())).thenAnswer(inv -> {
            List<String> textos = inv.getArgument(0);
            String idioma = inv.getArgument(1);
            return textos.stream().map(t -> t.isBlank() ? t : "[" + idioma + "] " + t).toList();
        });
    }

    /** Mesmo caminho do ControladorProduto: entidade -> DTO via BeanUtils. */
    private ProdutoDTO dto(long id) {
        Produto p = repoProduto.findById(id).orElseThrow();
        ProdutoDTO dto = new ProdutoDTO();
        BeanUtils.copyProperties(p, dto);
        return dto;
    }

    @Test
    @DisplayName("Idioma: aceita en/es/fr (com ou sem região) e ignora português")
    void deveReconhecerIdiomas() {
        assertEquals("en", ServicoTraducaoProduto.idiomaSuportado("en-US"));
        assertEquals("fr", ServicoTraducaoProduto.idiomaSuportado("FR"));
        assertNull(ServicoTraducaoProduto.idiomaSuportado("pt-BR"));
        assertNull(ServicoTraducaoProduto.idiomaSuportado("de"));
        assertNull(ServicoTraducaoProduto.idiomaSuportado(null));
    }

    @Test
    @DisplayName("Traduz os 3 idiomas e não refaz se o texto não mudou")
    void deveTraduzirUmaVez() throws Exception {
        assertEquals(3, servico.traduzirProduto(BOLSA_ID));
        assertEquals(3, repoTraducao.findByProdutoId(BOLSA_ID).size());

        assertEquals(0, servico.traduzirProduto(BOLSA_ID));
        verify(cliente, times(3)).traduzir(anyList(), anyString());
    }

    @Test
    @DisplayName("Aplica a tradução no DTO e mantém o subtítulo original para os filtros")
    void deveAplicarTraducao() throws Exception {
        servico.traduzirProduto(BOLSA_ID);
        ProdutoDTO dto = dto(BOLSA_ID);
        String descricaoPt = dto.getDescricao();

        servico.aplicar(List.of(dto), "es");

        assertEquals("[es] " + descricaoPt, dto.getDescricao());
        assertEquals("Tiracolo", dto.getSubtitulo());
        assertEquals("[es] Tiracolo", dto.getSubtituloTraduzido());
        List<?> destaques = mapper.readValue(dto.getDestaques(), List.class);
        assertFalse(destaques.isEmpty());
        assertTrue(destaques.stream().allMatch(d -> d.toString().startsWith("[es] ")));
    }

    @Test
    @DisplayName("Produto editado depois da tradução volta a aparecer em português")
    void deveIgnorarTraducaoDesatualizada() throws Exception {
        servico.traduzirProduto(BOLSA_ID);
        ProdutoDTO dto = dto(BOLSA_ID);
        dto.setDescricao("Descrição nova ainda não traduzida");

        servico.aplicar(List.of(dto), "en");

        assertEquals("Descrição nova ainda não traduzida", dto.getDescricao());
        assertNull(dto.getSubtituloTraduzido());
    }

    @Test
    @DisplayName("Sem chave do Google nada é traduzido")
    void naoDeveTraduzirSemChave() throws Exception {
        when(cliente.habilitado()).thenReturn(false);
        assertEquals(0, servico.traduzirProduto(BOLSA_ID));
        verify(cliente, never()).traduzir(anyList(), anyString());
    }

    @Test
    @DisplayName("Resposta da API: traduz produtos em listas, mapas e carrinho conforme Accept-Language")
    void deveTraduzirRespostaDaApi() throws Exception {
        servico.traduzirProduto(BOLSA_ID);
        TraducaoRespostaAdvice advice = new TraducaoRespostaAdvice(servico);

        ProdutoDTO naLista = dto(BOLSA_ID);
        CarrinhoItemDTO noCarrinho = new CarrinhoItemDTO();
        noCarrinho.setProduto(dto(BOLSA_ID));
        Object corpo = Map.of(
                "produtos", RespostaProdutoDTO.sucesso(List.of(naLista), "ok"),
                "carrinho", List.of(noCarrinho));

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Accept-Language", "fr");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        advice.beforeBodyWrite(corpo, null, MediaType.APPLICATION_JSON, null,
                new ServletServerHttpRequest(req), new ServletServerHttpResponse(resp));

        assertTrue(naLista.getDescricao().startsWith("[fr] "));
        assertTrue(noCarrinho.getProduto().getDescricao().startsWith("[fr] "));

        // Português (ou sem cabeçalho): resposta intacta
        ProdutoDTO emPortugues = dto(BOLSA_ID);
        String original = emPortugues.getDescricao();
        MockHttpServletRequest reqPt = new MockHttpServletRequest();
        reqPt.addHeader("Accept-Language", "pt-BR,pt;q=0.9,en;q=0.8");
        advice.beforeBodyWrite(emPortugues, null, MediaType.APPLICATION_JSON, null,
                new ServletServerHttpRequest(reqPt), new ServletServerHttpResponse(new MockHttpServletResponse()));
        assertEquals(original, emPortugues.getDescricao());
    }
}
