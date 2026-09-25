package com.luigarah.cache;

import com.luigarah.repository.produto.RepositorioProduto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AquecimentoCacheCatalogoTest {

    /** Registra os pedidos em vez de chamar o servidor. */
    static class AquecimentoFalso extends AquecimentoCacheCatalogo {
        final List<String> pedidos = new ArrayList<>();

        AquecimentoFalso(CacheRespostasCatalogo cache) {
            super(cache, repositorioCom(List.of(1L, 2L)), true);
        }

        static RepositorioProduto repositorioCom(List<Long> ids) {
            RepositorioProduto repo = Mockito.mock(RepositorioProduto.class);
            Mockito.when(repo.listarIds()).thenReturn(ids);
            return repo;
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

        int idiomas = AquecimentoCacheCatalogo.IDIOMAS.size();
        // (o pedir falso não grava no cache, então cada idioma pede de novo)
        int listagens = AquecimentoCacheCatalogo.ROTAS.size() * idiomas;
        int estoques = 2;              // um por produto, sem idioma
        int detalhes = 2 * idiomas;    // um por produto e idioma
        assertEquals(listagens + estoques + detalhes - 1, pedidas);
        assertFalse(aquecimento.pedidos.contains("/api/produtos/categoria/bolsas?pagina=0&tamanho=1000 pt-BR"));
        assertTrue(aquecimento.pedidos.contains("/api/produtos/categoria/bolsas?pagina=0&tamanho=1000 en-US"));
    }

    @Test
    @DisplayName("Estoque e tamanhos têm uma chave só para todos os idiomas; produtos, uma por idioma")
    void chavePorIdioma() {
        assertEquals(CacheCatalogoFilter.chave("/api/estoque/produtos/1/estoque", null, "pt-BR"),
                CacheCatalogoFilter.chave("/api/estoque/produtos/1/estoque", null, "fr-FR"));
        assertEquals(CacheCatalogoFilter.chave("/api/tamanhos/produtos", "categoria=roupas", "en-US"),
                CacheCatalogoFilter.chave("/api/tamanhos/produtos", "categoria=roupas", "es-ES"));
        assertNotEquals(CacheCatalogoFilter.chave("/api/produtos/1", null, "pt-BR"),
                CacheCatalogoFilter.chave("/api/produtos/1", null, "en-US"));
    }

    @Test
    @DisplayName("Sem servidor web no ar (porta desconhecida) não faz nada")
    void semServidor() {
        AquecimentoFalso aquecimento = new AquecimentoFalso(new CacheRespostasCatalogo(true, 30, 50));
        aquecimento.aquecer();
        assertTrue(aquecimento.pedidos.isEmpty());
    }
}
