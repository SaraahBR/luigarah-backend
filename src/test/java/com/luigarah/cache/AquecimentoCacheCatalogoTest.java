package com.luigarah.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AquecimentoCacheCatalogoTest {

    /** Registra os pedidos em vez de chamar o servidor. */
    static class AquecimentoFalso extends AquecimentoCacheCatalogo {
        final List<String> pedidos = new ArrayList<>();

        AquecimentoFalso(CacheRespostasCatalogo cache) {
            super(cache, true);
        }

        @Override
        void pedir(String rota, String idioma) {
            pedidos.add(rota + " " + idioma);
        }
    }

    @Test
    @DisplayName("Pede só as rotas que não estão no cache, com a mesma chave do filtro")
    void pedeSoOQueFalta() {
        CacheRespostasCatalogo cache = new CacheRespostasCatalogo(true, 30, 50);
        AquecimentoFalso aquecimento = new AquecimentoFalso(cache);

        // uma rota já está no cache em português, com o cabeçalho que o frontend envia
        String chave = CacheCatalogoFilter.chave("/api/produtos/categoria/bolsas", "pagina=0&tamanho=1000", "pt-BR");
        cache.guardar(chave, cache.geracaoAtual(),
                new CacheRespostasCatalogo.RespostaCacheada("{}".getBytes(), "application/json"));

        int pedidas = aquecimento.aquecerFaltantes();

        int total = AquecimentoCacheCatalogo.ROTAS.size() * AquecimentoCacheCatalogo.IDIOMAS.size();
        assertEquals(total - 1, pedidas);
        assertFalse(aquecimento.pedidos.contains("/api/produtos/categoria/bolsas?pagina=0&tamanho=1000 pt-BR"));
        assertTrue(aquecimento.pedidos.contains("/api/produtos/categoria/bolsas?pagina=0&tamanho=1000 en-US"));
    }

    @Test
    @DisplayName("Sem servidor web no ar (porta desconhecida) não faz nada")
    void semServidor() {
        AquecimentoFalso aquecimento = new AquecimentoFalso(new CacheRespostasCatalogo(true, 30, 50));
        aquecimento.aquecer();
        assertTrue(aquecimento.pedidos.isEmpty());
    }
}
