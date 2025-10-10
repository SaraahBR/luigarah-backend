package com.luigarah.service.impl;

import com.luigarah.dto.ProdutoTamanhoDTO;
import com.luigarah.repository.RepositorioProduto;
import com.luigarah.service.ServicoEstoque;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoEstoqueImplTest {

    @Mock
    RepositorioProduto repo;

    @Test
    void listarEstoque_bolsasRetornaItemUnico() {
        when(repo.obterCategoriaDoProduto(1L)).thenReturn("bolsas");
        when(repo.obterEstoqueProduto(1L)).thenReturn(7);

        ServicoEstoque svc = new ServicoEstoqueImpl(repo);
        List<ProdutoTamanhoDTO> out = svc.listarEstoqueDoProduto(1L);

        assertEquals(1, out.size());
        assertNull(out.get(0).getEtiqueta());
        assertEquals(7, out.get(0).getQtdEstoque());
    }

    @Test
    void atualizarEstoqueUnitario_bolsa_incCriaSeNaoExiste() {
        when(repo.obterCategoriaDoProduto(2L)).thenReturn("bolsas");
        when(repo.incrementarEstoqueProduto(2L, 5)).thenReturn(0); // não existia
        ServicoEstoque svc = new ServicoEstoqueImpl(repo);

        svc.atualizarEstoqueUnitario(2L, null, "inc", 5);

        verify(repo).incrementarEstoqueProduto(2L, 5);
        verify(repo).upsertEstoqueProduto(2L, 5);
    }
}
