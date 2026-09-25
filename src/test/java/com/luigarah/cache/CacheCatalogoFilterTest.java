package com.luigarah.cache;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cache das rotas do catálogo, sem subir o Spring: o "controller" é um FilterChain
 * que conta quantas vezes foi chamado e responde um JSON diferente a cada chamada.
 */
class CacheCatalogoFilterTest {

    private CacheRespostasCatalogo cache;
    private CacheCatalogoFilter filtro;
    private AtomicInteger chamadas;

    /** Simula o controller: responde {"n":<número da chamada>} com o status pedido. */
    private FilterChain controller(int status) {
        return (req, res) -> {
            int n = chamadas.incrementAndGet();
            HttpServletResponse r = (HttpServletResponse) res;
            r.setStatus(status);
            r.setContentType("application/json");
            r.getOutputStream().write(("{\"n\":" + n + "}").getBytes(StandardCharsets.UTF_8));
        };
    }

    private MockHttpServletResponse executar(String metodo, String uri, String idioma, FilterChain chain) throws Exception {
        // como no Tomcat: o caminho e a query string chegam separados
        String[] partes = uri.split("\\?", 2);
        MockHttpServletRequest req = new MockHttpServletRequest(metodo, partes[0]);
        if (partes.length > 1) req.setQueryString(partes[1]);
        if (idioma != null) req.addHeader("Accept-Language", idioma);
        MockHttpServletResponse res = new MockHttpServletResponse();
        filtro.doFilter(req, res, chain);
        return res;
    }

    private MockHttpServletResponse get(String uri, String idioma) throws Exception {
        return executar("GET", uri, idioma, controller(200));
    }

    @BeforeEach
    void preparar() {
        cache = new CacheRespostasCatalogo(true, 10, 50);
        filtro = new CacheCatalogoFilter(cache);
        chamadas = new AtomicInteger();
    }

    @Test
    @DisplayName("Segunda leitura igual vem do cache, com o mesmo corpo e sem chamar o controller")
    void deveServirDoCache() throws Exception {
        MockHttpServletResponse primeira = get("/api/produtos/1", "pt-BR");
        MockHttpServletResponse segunda = get("/api/produtos/1", "pt-BR");

        assertEquals(1, chamadas.get());
        assertEquals("MISS", primeira.getHeader("X-Cache"));
        assertEquals("HIT", segunda.getHeader("X-Cache"));
        assertEquals(primeira.getContentAsString(), segunda.getContentAsString());
        assertEquals(200, segunda.getStatus());
        assertTrue(segunda.getContentType().contains("json"));
        assertEquals("Accept-Language", segunda.getHeader("Vary"));
    }

    @Test
    @DisplayName("Idioma e parâmetros diferentes são respostas diferentes")
    void deveSepararPorIdiomaEQuery() throws Exception {
        get("/api/produtos?pagina=0", "pt-BR");
        get("/api/produtos?pagina=0", "en-US");
        get("/api/produtos?pagina=1", "pt-BR");
        assertEquals(3, chamadas.get());

        get("/api/produtos?pagina=0", "en-US");
        assertEquals(3, chamadas.get());
    }

    @Test
    @DisplayName("Escrita no catálogo limpa o cache, mesmo quando falha")
    void deveLimparNaEscrita() throws Exception {
        get("/api/produtos/1", null);
        executar("PUT", "/api/produtos/1", null, controller(200));
        assertEquals(0, cache.tamanho());

        get("/api/estoque/produtos/1/estoque", null);
        FilterChain falha = (req, res) -> { throw new IllegalStateException("erro no meio da escrita"); };
        assertThrows(IllegalStateException.class,
                () -> executar("DELETE", "/api/estoque/produtos/1/estoque/M", null, falha));
        assertEquals(0, cache.tamanho());
    }

    @Test
    @DisplayName("Erros não são guardados")
    void naoDeveGuardarErro() throws Exception {
        executar("GET", "/api/produtos/999", null, controller(404));
        executar("GET", "/api/produtos/999", null, controller(404));
        assertEquals(2, chamadas.get());
        assertEquals(0, cache.tamanho());
    }

    @Test
    @DisplayName("Rotas fora do catálogo (carrinho, usuário) nunca passam pelo cache")
    void deveIgnorarRotasDoUsuario() throws Exception {
        get("/api/carrinho", null);
        get("/api/carrinho", null);
        get("/api/produtosx", null);
        assertEquals(3, chamadas.get());
        assertEquals(0, cache.tamanho());
    }

    @Test
    @DisplayName("Resposta lida antes de uma alteração não é gravada depois dela")
    void naoDeveGravarRespostaAntigaDepoisDeAlteracao() throws Exception {
        // a alteração acontece enquanto o GET ainda está sendo processado
        FilterChain lentoComAlteracao = (req, res) -> {
            controller(200).doFilter(req, res);
            cache.invalidar();
        };
        executar("GET", "/api/produtos/1", null, lentoComAlteracao);
        assertEquals(0, cache.tamanho());
    }

    @Test
    @DisplayName("Desativado, tudo vai direto para o controller")
    void deveRespeitarDesativado() throws Exception {
        cache = new CacheRespostasCatalogo(false, 10, 50);
        filtro = new CacheCatalogoFilter(cache);
        get("/api/produtos/1", null);
        get("/api/produtos/1", null);
        assertEquals(2, chamadas.get());
    }
}
