package com.luigarah.service.traducao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Dispara as traduções em segundo plano, sem atrasar a resposta da API:
 * - ao criar/editar um produto (depois do commit)
 * - ao iniciar a aplicação, para os produtos que ainda não têm tradução
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TraducaoProdutoListener {

    private final ServicoTraducaoProduto servico;

    @Async("traducaoExecutor")
    @TransactionalEventListener(fallbackExecution = true)
    public void aoSalvarProduto(ProdutoSalvoEvent evento) {
        try {
            servico.traduzirProduto(evento.produtoId());
        } catch (Exception e) {
            log.warn("🌐 Não foi possível traduzir o produto {}: {}", evento.produtoId(), e.getMessage());
        }
    }

    @Async("traducaoExecutor")
    @EventListener(ApplicationReadyEvent.class)
    public void aoIniciar() {
        servico.preencherFaltantes();
    }
}
