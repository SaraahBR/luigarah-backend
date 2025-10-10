package com.luigarah.service.impl;

import com.luigarah.repository.RepositorioProduto;
import com.luigarah.repository.RepositorioTamanho;
import com.luigarah.service.ServicoTamanho;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes de unidade para regras de tamanhos:
 * - Padrões (usa|br|sapatos)
 * - Coerência categoria x padrão
 * - Validação de etiqueta contra catálogo
 * - Substituir / adicionar / remover
 */
@ExtendWith(MockitoExtension.class)
class ServicoTamanhoImplTest {

    @Mock
    RepositorioProduto repoProduto;

    @Mock
    RepositorioTamanho repoTamanho;

    ServicoTamanho servico;

    @BeforeEach
    void setUp() {
        servico = new ServicoTamanhoImpl(repoProduto, repoTamanho);
    }

    @Nested
    @DisplayName("Catálogo por categoria")
    class Catalogo {

        @Test
        @DisplayName("Deve listar catálogo por categoria sem padrao (retorna todos os padrões da categoria)")
        void listarCatalogo_semPadrao() {
            when(repoTamanho.listarEtiquetas("roupas", null))
                    .thenReturn(Arrays.asList("XXS", "XS", "S", "M"));
            List<String> out = servico.listarCatalogoPorCategoria("roupas", null);
            assertEquals(4, out.size());
            verify(repoTamanho).listarEtiquetas("roupas", null);
        }

        @Test
        @DisplayName("Deve listar catálogo por categoria com padrao=usa")
        void listarCatalogo_comPadraoUsa() {
            when(repoTamanho.listarEtiquetas("roupas", "usa"))
                    .thenReturn(Arrays.asList("XS", "S", "M"));
            List<String> out = servico.listarCatalogoPorCategoria("roupas", "usa");
            assertEquals(3, out.size());
            verify(repoTamanho).listarEtiquetas("roupas", "usa");
        }

        @Test
        @DisplayName("Categoria inválida deve lançar IllegalArgumentException")
        void listarCatalogo_categoriaInvalida() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> servico.listarCatalogoPorCategoria("acessorios", "usa"));
            assertTrue(ex.getMessage().toLowerCase().contains("categoria inválida"));
            verifyNoInteractions(repoTamanho);
        }

        @Test
        @DisplayName("Padrão inválido deve lançar IllegalArgumentException")
        void listarCatalogo_padraoInvalido() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> servico.listarCatalogoPorCategoria("roupas", "eu"));
            assertTrue(ex.getMessage().toLowerCase().contains("padrão inválido"));
            verifyNoInteractions(repoTamanho);
        }
    }

    @Nested
    @DisplayName("Adicionar tamanhos ao produto")
    class Adicionar {

        @Test
        @DisplayName("BOLSAS não aceitam tamanhos")
        void bolsasNaoAceitamTamanhos() {
            Long produtoId = 10L;
            when(repoProduto.obterCategoriaDoProduto(produtoId)).thenReturn("bolsas");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> servico.adicionarTamanhosAoProduto(produtoId, List.of("M")));

            assertTrue(ex.getMessage().toLowerCase().contains("bolsas"));
            verify(repoProduto).obterCategoriaDoProduto(produtoId);
            verify(repoProduto, never()).obterPadraoTamanhoProduto(any());
            verifyNoInteractions(repoTamanho);
        }

        @Test
        @DisplayName("Produto ROUPAS sem PADRAO_TAMANHO definido deve falhar")
        void roupasSemPadraoFalha() {
            Long produtoId = 11L;
            when(repoProduto.obterCategoriaDoProduto(produtoId)).thenReturn("roupas");
            when(repoProduto.obterPadraoTamanhoProduto(produtoId)).thenReturn(null);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> servico.adicionarTamanhosAoProduto(produtoId, List.of("M")));

            assertTrue(ex.getMessage().toLowerCase().contains("defina padrao_tamanho"));
            verify(repoProduto).obterCategoriaDoProduto(produtoId);
            verify(repoProduto).obterPadraoTamanhoProduto(produtoId);
            verifyNoInteractions(repoTamanho);
        }

        @Test
        @DisplayName("Produto ROUPAS (usa) aceita apenas etiquetas do catálogo USA")
        void roupasUsa_validaEtiquetaContraCatalogo() {
            Long produtoId = 12L;
            when(repoProduto.obterCategoriaDoProduto(produtoId)).thenReturn("roupas");
            when(repoProduto.obterPadraoTamanhoProduto(produtoId)).thenReturn("usa");

            // etiqueta válida: 'M'
            when(repoTamanho.existsEtiqueta("roupas", "usa", "M")).thenReturn(1);

            servico.adicionarTamanhosAoProduto(produtoId, List.of("M"));

            verify(repoTamanho).existsEtiqueta("roupas", "usa", "M");
            verify(repoProduto).inserirTamanho(produtoId, "M", 10);
        }

        @Test
        @DisplayName("Produto ROUPAS (usa) rejeita etiqueta BR (ex.: G)")
        void roupasUsa_rejeitaEtiquetaDeOutroPadrao() {
            Long produtoId = 13L;
            when(repoProduto.obterCategoriaDoProduto(produtoId)).thenReturn("roupas");
            when(repoProduto.obterPadraoTamanhoProduto(produtoId)).thenReturn("usa");

            // etiqueta inválida para USA
            when(repoTamanho.existsEtiqueta("roupas", "usa", "G")).thenReturn(0);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> servico.adicionarTamanhosAoProduto(produtoId, List.of("G")));

            assertTrue(ex.getMessage().contains("não pertence ao catálogo"));
            verify(repoTamanho).existsEtiqueta("roupas", "usa", "G");
            verify(repoProduto, never()).inserirTamanho(any(), any(), anyInt());
        }

        @Test
        @DisplayName("Produto SAPATOS (sapatos) aceita numeração válida e grava estoque inicial 10")
        void sapatos_ok() {
            Long produtoId = 14L;
            when(repoProduto.obterCategoriaDoProduto(produtoId)).thenReturn("sapatos");
            when(repoProduto.obterPadraoTamanhoProduto(produtoId)).thenReturn("sapatos");
            when(repoTamanho.existsEtiqueta("sapatos", "sapatos", "40")).thenReturn(1);

            servico.adicionarTamanhosAoProduto(produtoId, List.of("40"));

            verify(repoTamanho).existsEtiqueta("sapatos", "sapatos", "40");
            verify(repoProduto).inserirTamanho(produtoId, "40", 10);
        }

        @Test
        @DisplayName("Lista vazia não faz nada e só retorna os tamanhos já vinculados")
        void listaVazia() {
            Long produtoId = 15L;
            when(repoProduto.listarEtiquetasPorProduto(produtoId)).thenReturn(Collections.emptyList());

            List<String> out = servico.adicionarTamanhosAoProduto(produtoId, Collections.emptyList());

            assertNotNull(out);
            assertTrue(out.isEmpty());
            verify(repoProduto).listarEtiquetasPorProduto(produtoId);
            verifyNoMoreInteractions(repoProduto);
            verifyNoInteractions(repoTamanho);
        }
    }

    @Nested
    @DisplayName("Substituir tamanhos do produto")
    class Substituir {

        @Test
        @DisplayName("Substituir -> deleta antes e adiciona novamente (validando por padrão)")
        void substituir_flow() {
            Long produtoId = 20L;
            when(repoProduto.obterCategoriaDoProduto(produtoId)).thenReturn("roupas");
            when(repoProduto.obterPadraoTamanhoProduto(produtoId)).thenReturn("br");

            when(repoTamanho.existsEtiqueta("roupas", "br", "P")).thenReturn(1);
            when(repoTamanho.existsEtiqueta("roupas", "br", "M")).thenReturn(1);

            // listar final
            when(repoProduto.listarEtiquetasPorProduto(produtoId)).thenReturn(Arrays.asList("P", "M"));

            List<String> out = servico.substituirTamanhosDoProduto(produtoId, Arrays.asList("P", "M"));

            verify(repoProduto).deletarTamanhosDoProduto(produtoId);
            verify(repoTamanho).existsEtiqueta("roupas", "br", "P");
            verify(repoTamanho).existsEtiqueta("roupas", "br", "M");
            verify(repoProduto).inserirTamanho(produtoId, "P", 10);
            verify(repoProduto).inserirTamanho(produtoId, "M", 10);

            assertEquals(2, out.size());
            assertEquals(List.of("P", "M"), out);
        }
    }

    @Nested
    @DisplayName("Listar tamanhos do produto")
    class ListarDoProduto {

        @Test
        @DisplayName("Retorna lista ordenada do repositório")
        void listar_ok() {
            Long produtoId = 30L;
            when(repoProduto.listarEtiquetasPorProduto(produtoId)).thenReturn(List.of("S", "M", "L"));
            List<String> out = servico.listarTamanhosDoProduto(produtoId);
            assertEquals(List.of("S", "M", "L"), out);
            verify(repoProduto).listarEtiquetasPorProduto(produtoId);
        }
    }
}
