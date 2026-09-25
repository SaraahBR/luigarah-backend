package com.luigarah.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Mantém no cache as respostas que o site pede ao abrir as páginas (menu, listagens,
 * identidades e filtros de tamanho), nos quatro idiomas.
 *
 * Sem isso, a primeira visita depois de um deploy, de uma edição no painel admin ou
 * de o cache expirar esperava as consultas ao banco (2 a 3 s cada). A cada 30 s as
 * rotas que não estão no cache são pedidas ao próprio servidor, em segundo plano;
 * quando está tudo no cache, nada é feito.
 */
@Component
@Slf4j
public class AquecimentoCacheCatalogo {

    /** Mesmos valores de Accept-Language que o frontend envia. */
    static final List<String> IDIOMAS = List.of("pt-BR", "en-US", "es-ES", "fr-FR");

    /** Rotas pedidas pelo frontend ao carregar as páginas. */
    static final List<String> ROTAS = List.of(
            "/api/produtos/categoria/bolsas?pagina=0&tamanho=1000",
            "/api/produtos/categoria/roupas?pagina=0&tamanho=1000",
            "/api/produtos/categoria/sapatos?pagina=0&tamanho=1000",
            "/api/produtos/categoria/bolsas?pagina=0&tamanho=100",
            "/api/produtos/categoria/roupas?pagina=0&tamanho=100",
            "/api/produtos/categoria/sapatos?pagina=0&tamanho=100",
            "/api/produtos/identidade/codigo/mulher",
            "/api/produtos/identidade/codigo/homem",
            "/api/produtos/identidade/codigo/unissex",
            "/api/produtos/identidade/codigo/infantil",
            "/api/tamanhos/produtos?categoria=roupas&comEstoque=true",
            "/api/tamanhos/produtos?categoria=sapatos&comEstoque=true"
    );

    private final CacheRespostasCatalogo cache;
    private final boolean habilitado;
    /** Criado só no primeiro uso: sem servidor web (ex.: testes) nunca é necessário. */
    private volatile HttpClient http;

    /** Porta real do servidor (só existe com o servidor web no ar). */
    private volatile int porta = -1;

    public AquecimentoCacheCatalogo(CacheRespostasCatalogo cache,
                                    @Value("${app.cache.catalogo.aquecimento:true}") boolean habilitado) {
        this.cache = cache;
        this.habilitado = habilitado;
    }

    @EventListener
    public void aoIniciarServidor(WebServerInitializedEvent evento) {
        porta = evento.getWebServer().getPort();
    }

    @Scheduled(initialDelayString = "PT15S", fixedDelayString = "PT30S")
    public void aquecer() {
        if (!habilitado || !cache.habilitado() || porta <= 0) return;
        int aquecidas = aquecerFaltantes();
        if (aquecidas > 0) log.info("⚡ Cache do catálogo aquecido: {} resposta(s)", aquecidas);
    }

    /** Pede as rotas que não estão no cache. Devolve quantas foram pedidas. */
    int aquecerFaltantes() {
        int pedidas = 0;
        for (String idioma : IDIOMAS) {
            for (String rota : ROTAS) {
                int corte = rota.indexOf('?');
                String caminho = corte < 0 ? rota : rota.substring(0, corte);
                String query = corte < 0 ? null : rota.substring(corte + 1);
                if (cache.buscar(CacheCatalogoFilter.chave(caminho, query, idioma)) != null) continue;
                pedir(rota, idioma);
                pedidas++;
            }
        }
        return pedidas;
    }

    private HttpClient cliente() {
        if (http == null) {
            http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        }
        return http;
    }

    /** Uma requisição ao próprio servidor: a resposta é guardada pelo CacheCatalogoFilter. */
    void pedir(String rota, String idioma) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create("http://localhost:" + porta + rota))
                    .header("Accept-Language", idioma)
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            cliente().send(req, HttpResponse.BodyHandlers.discarding());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.debug("⚡ Aquecimento de {} ({}) falhou: {}", rota, idioma, e.getMessage());
        }
    }
}
