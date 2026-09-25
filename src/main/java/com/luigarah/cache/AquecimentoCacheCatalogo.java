package com.luigarah.cache;

import com.luigarah.repository.produto.RepositorioProduto;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Mantém no cache as respostas que o site pede ao abrir as páginas (menu, listagens,
 * identidades e filtros de tamanho, nos quatro idiomas) e, de cada produto, o estoque
 * (usado ao adicionar no carrinho) e a página de detalhe.
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

    /** Os ids mudam pouco (só quando o admin cria/remove produto): relê a cada 5 minutos. */
    private static final Duration VALIDADE_IDS = Duration.ofMinutes(5);

    private final CacheRespostasCatalogo cache;
    private final RepositorioProduto repositorioProduto;
    private final boolean habilitado;
    private volatile List<Long> idsProdutos = List.of();
    private volatile Instant idsLidosEm = Instant.EPOCH;
    /** Criado só no primeiro uso: sem servidor web (ex.: testes) nunca é necessário. */
    private volatile HttpClient http;

    /** Porta real do servidor (só existe com o servidor web no ar). */
    private volatile int porta = -1;

    public AquecimentoCacheCatalogo(CacheRespostasCatalogo cache,
                                    RepositorioProduto repositorioProduto,
                                    @Value("${app.cache.catalogo.aquecimento:true}") boolean habilitado) {
        this.cache = cache;
        this.repositorioProduto = repositorioProduto;
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

    /**
     * Pede as rotas que não estão no cache, na ordem de prioridade: listagens, estoque de
     * cada produto e detalhe de cada produto. Devolve quantas foram pedidas.
     */
    int aquecerFaltantes() {
        int pedidas = 0;
        for (String idioma : IDIOMAS) {
            pedidas += aquecer(ROTAS, idioma);
        }

        List<Long> ids = idsDosProdutos();
        List<String> estoques = new ArrayList<>();
        List<String> detalhes = new ArrayList<>();
        for (Long id : ids) {
            estoques.add("/api/estoque/produtos/" + id + "/estoque");
            detalhes.add("/api/produtos/" + id);
        }
        // estoque não depende do idioma: uma vez só
        pedidas += aquecer(estoques, IDIOMAS.get(0));
        for (String idioma : IDIOMAS) {
            pedidas += aquecer(detalhes, idioma);
        }
        return pedidas;
    }

    private int aquecer(List<String> rotas, String idioma) {
        int pedidas = 0;
        for (String rota : rotas) {
            int corte = rota.indexOf('?');
            String caminho = corte < 0 ? rota : rota.substring(0, corte);
            String query = corte < 0 ? null : rota.substring(corte + 1);
            if (cache.buscar(CacheCatalogoFilter.chave(caminho, query, idioma)) != null) continue;
            pedir(rota, idioma);
            pedidas++;
        }
        return pedidas;
    }

    List<Long> idsDosProdutos() {
        if (Duration.between(idsLidosEm, Instant.now()).compareTo(VALIDADE_IDS) > 0) {
            try {
                idsProdutos = repositorioProduto.listarIds();
                idsLidosEm = Instant.now();
            } catch (Exception e) {
                log.debug("⚡ Não foi possível ler os ids dos produtos: {}", e.getMessage());
            }
        }
        return idsProdutos;
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
