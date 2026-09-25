package com.luigarah.cache;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Serve do cache as leituras públicas do catálogo e limpa o cache quando algo muda.
 *
 * Roda depois do Spring Security (ordem padrão dos filtros da aplicação), então CORS
 * e permissões continuam valendo: só chega aqui quem já passou pela autorização.
 *
 * - GET: a chave é URL + query + Accept-Language (a resposta muda com o idioma).
 *   Só respostas 200 em JSON são guardadas.
 * - POST/PUT/PATCH/DELETE nas mesmas rotas: limpa tudo depois de executar.
 * - O cabeçalho X-Cache (HIT/MISS) mostra de onde veio a resposta.
 */
@Component
public class CacheCatalogoFilter extends OncePerRequestFilter {

    /** Rotas do catálogo: não dependem do usuário logado. */
    static final List<String> PREFIXOS = List.of(
            "/api/produtos",
            "/api/estoque",
            "/api/tamanhos",
            "/api/padroes-tamanho",
            "/api/identidades"
    );

    private static final Set<String> ESCRITAS = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final CacheRespostasCatalogo cache;

    public CacheCatalogoFilter(CacheRespostasCatalogo cache) {
        this.cache = cache;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!cache.habilitado()) return true;
        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        return PREFIXOS.stream().noneMatch(p -> caminho.equals(p) || caminho.startsWith(p + "/"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String metodo = request.getMethod();

        if (ESCRITAS.contains(metodo)) {
            try {
                chain.doFilter(request, response);
            } finally {
                // mesmo se a escrita falhar no meio, é mais seguro recarregar do banco
                cache.invalidar();
            }
            return;
        }

        if (!"GET".equals(metodo)) {
            chain.doFilter(request, response);
            return;
        }

        String chave = chave(request);
        CacheRespostasCatalogo.RespostaCacheada salva = cache.buscar(chave);
        if (salva != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(salva.contentType());
            response.addHeader(HttpHeaders.VARY, HttpHeaders.ACCEPT_LANGUAGE);
            response.setHeader("X-Cache", "HIT");
            response.setContentLength(salva.corpo().length);
            response.getOutputStream().write(salva.corpo());
            return;
        }

        long geracao = cache.geracaoAtual();
        ContentCachingResponseWrapper resposta = new ContentCachingResponseWrapper(response);
        try {
            chain.doFilter(request, resposta);

            String contentType = resposta.getContentType();
            boolean json = contentType != null && contentType.contains("json");
            if (resposta.getStatus() == HttpServletResponse.SC_OK && json) {
                cache.guardar(chave, geracao,
                        new CacheRespostasCatalogo.RespostaCacheada(resposta.getContentAsByteArray(), contentType));
            }
            resposta.setHeader("X-Cache", "MISS");
        } finally {
            resposta.copyBodyToResponse();
        }
    }

    static String chave(HttpServletRequest request) {
        return chave(request.getRequestURI(), request.getQueryString(), request.getHeader(HttpHeaders.ACCEPT_LANGUAGE));
    }

    /** Rotas cuja resposta é igual em qualquer idioma (não têm texto traduzido). */
    static final List<String> SEM_IDIOMA = List.of("/api/estoque", "/api/tamanhos", "/api/padroes-tamanho");

    /**
     * Chave do cache: caminho + query + idioma (também usada pelo aquecimento).
     * Estoque e tamanhos são iguais em qualquer idioma: uma entrada só para todos.
     */
    static String chave(String caminho, String query, String idioma) {
        boolean semIdioma = SEM_IDIOMA.stream().anyMatch(p -> caminho.equals(p) || caminho.startsWith(p + "/"));
        return caminho
                + (query != null ? "?" + query : "")
                + "|" + (semIdioma || idioma == null ? "" : idioma.trim().toLowerCase());
    }
}
