package com.luigarah.service.listadesejos;

import com.luigarah.dto.listadesejos.ListaDesejoItemDTO;
import com.luigarah.exception.RecursoNaoEncontradoException;
import com.luigarah.exception.RegraDeNegocioException;
import com.luigarah.mapper.listadesejos.ListaDesejoItemMapper;
import com.luigarah.model.listadesejos.ListaDesejoItem;
import com.luigarah.model.produto.Produto;
import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.repository.listadesejos.ListaDesejoItemRepository;
import com.luigarah.repository.produto.RepositorioProduto;
import com.luigarah.service.autenticacao.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de gerenciamento da lista de desejos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ListaDesejoService {

    private final ListaDesejoItemRepository listaDesejoItemRepository;
    private final RepositorioProduto repositorioProduto;
    private final AuthService authService;
    private final ListaDesejoItemMapper listaDesejoItemMapper;

    /**
     * Lista todos os itens da lista de desejos do usuário autenticado.
     */
    @Transactional(readOnly = true)
    public List<ListaDesejoItemDTO> listarItens() {
        Usuario usuario = authService.getUsuarioAutenticado();
        List<ListaDesejoItem> itens = listaDesejoItemRepository.findByUsuarioIdOrderByDataAdicaoDesc(usuario.getId());
        return itens.stream()
                .map(listaDesejoItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Adiciona um produto à lista de desejos.
     */
    @Transactional
    public ListaDesejoItemDTO adicionarItem(Long produtoId) {
        Usuario usuario = authService.getUsuarioAutenticado();

        Produto produto = repositorioProduto.findById(produtoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

        // Verificar se o produto já está na lista
        if (listaDesejoItemRepository.existsByUsuarioIdAndProdutoId(usuario.getId(), produtoId)) {
            throw new RegraDeNegocioException("Produto já está na lista de desejos");
        }

        ListaDesejoItem item = ListaDesejoItem.builder()
                .usuario(usuario)
                .produto(produto)
                .build();

        item = listaDesejoItemRepository.save(item);

        return listaDesejoItemMapper.toDTO(item);
    }

    /**
     * Remove um item da lista de desejos.
     */
    @Transactional
    public void removerItem(Long itemId) {
        Usuario usuario = authService.getUsuarioAutenticado();

        ListaDesejoItem item = listaDesejoItemRepository.findById(itemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item da lista de desejos não encontrado"));

        // Verificar se o item pertence ao usuário
        if (!item.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraDeNegocioException("Você não tem permissão para remover este item");
        }

        listaDesejoItemRepository.delete(item);
    }

    /**
     * Remove um produto da lista de desejos pelo ID do produto.
     */
    @Transactional
    public void removerPorProdutoId(Long produtoId) {
        Usuario usuario = authService.getUsuarioAutenticado();

        if (!listaDesejoItemRepository.existsByUsuarioIdAndProdutoId(usuario.getId(), produtoId)) {
            throw new RecursoNaoEncontradoException("Produto não está na lista de desejos");
        }

        listaDesejoItemRepository.deleteByUsuarioIdAndProdutoId(usuario.getId(), produtoId);
    }

    /**
     * Verifica se um produto está na lista de desejos.
     */
    @Transactional(readOnly = true)
    public Boolean verificarSeEstaNosFavoritos(Long produtoId) {
        Usuario usuario = authService.getUsuarioAutenticado();
        return listaDesejoItemRepository.existsByUsuarioIdAndProdutoId(usuario.getId(), produtoId);
    }

    /**
     * Limpa toda a lista de desejos do usuário.
     */
    @Transactional
    public void limparLista() {
        Usuario usuario = authService.getUsuarioAutenticado();
        listaDesejoItemRepository.deleteByUsuarioId(usuario.getId());
    }

    /**
     * Conta a quantidade de itens na lista de desejos.
     */
    @Transactional(readOnly = true)
    public Long contarItens() {
        Usuario usuario = authService.getUsuarioAutenticado();
        return listaDesejoItemRepository.countByUsuarioId(usuario.getId());
    }
}
