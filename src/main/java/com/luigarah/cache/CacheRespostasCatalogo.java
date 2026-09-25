package com.luigarah.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.luigarah.service.traducao.TraducoesAtualizadasEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Guarda em memória as respostas prontas (JSON) das rotas públicas do catálogo.
 *
 * O banco fica em São Paulo e o backend no Render (Oregon): cada consulta SQL
 * atravessa o continente. Como produtos, tamanhos e estoque só mudam pelo painel
 * admin, a mesma resposta é reaproveitada até alguma alteração acontecer.
 *
 * - Qualquer escrita nessas rotas limpa o cache inteiro (ver CacheCatalogoFilter).
 * - Traduções novas também limpam (TraducoesAtualizadasEvent).
 * - Alterações feitas direto no banco (SQL Editor do Supabase) aparecem depois
 *   do tempo de expiração.
 */
@Component
@Slf4j
public class CacheRespostasCatalogo {

    /** Resposta pronta para ser devolvida de novo. */
    public record RespostaCacheada(byte[] corpo, String contentType) {}

    private final boolean habilitado;
    private final Cache<String, RespostaCacheada> cache;

    /**
     * Muda a cada limpeza. Uma requisição que começou antes de uma alteração não
     * grava o resultado (que pode estar desatualizado) depois da limpeza.
     */
    private final AtomicLong geracao = new AtomicLong();

    public CacheRespostasCatalogo(
            @Value("${app.cache.catalogo.habilitado:true}") boolean habilitado,
            @Value("${app.cache.catalogo.ttl-minutos:10}") long ttlMinutos,
            @Value("${app.cache.catalogo.max-mb:50}") long maxMb) {
        this.habilitado = habilitado;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(ttlMinutos))
                .maximumWeight(maxMb * 1024 * 1024)
                .weigher((String chave, RespostaCacheada r) -> r.corpo().length + chave.length())
                .build();
        log.info("⚡ Cache do catálogo {} (expira em {} min)", habilitado ? "ativado" : "desativado", ttlMinutos);
    }

    public boolean habilitado() {
        return habilitado;
    }

    public RespostaCacheada buscar(String chave) {
        return cache.getIfPresent(chave);
    }

    public long geracaoAtual() {
        return geracao.get();
    }

    /** Grava só se nada foi alterado desde que a requisição começou. */
    public void guardar(String chave, long geracaoNoInicio, RespostaCacheada resposta) {
        if (geracao.get() != geracaoNoInicio) return;
        cache.put(chave, resposta);
        // uma limpeza pode ter acontecido entre o teste e o put
        if (geracao.get() != geracaoNoInicio) cache.invalidate(chave);
    }

    public void invalidar() {
        geracao.incrementAndGet();
        cache.invalidateAll();
        log.debug("⚡ Cache do catálogo limpo");
    }

    public long tamanho() {
        cache.cleanUp();
        return cache.estimatedSize();
    }

    @EventListener
    public void aoAtualizarTraducoes(TraducoesAtualizadasEvent evento) {
        invalidar();
    }
}
