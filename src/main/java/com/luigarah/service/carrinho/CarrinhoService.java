package com.luigarah.service.carrinho;

import com.luigarah.dto.carrinho.CarrinhoItemDTO;
import com.luigarah.dto.carrinho.CarrinhoItemRequestDTO;
import com.luigarah.exception.RecursoNaoEncontradoException;
import com.luigarah.exception.RegraDeNegocioException;
import com.luigarah.mapper.carrinho.CarrinhoItemMapper;
import com.luigarah.model.carrinho.CarrinhoItem;
import com.luigarah.model.produto.Produto;
import com.luigarah.model.tamanho.Tamanho;
import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.repository.carrinho.CarrinhoItemRepository;
import com.luigarah.repository.produto.RepositorioProduto;
import com.luigarah.repository.tamanho.RepositorioTamanho;
import com.luigarah.service.autenticacao.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de gerenciamento do carrinho de compras.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CarrinhoService {

    private final CarrinhoItemRepository carrinhoItemRepository;
    private final RepositorioProduto repositorioProduto;
    private final RepositorioTamanho repositorioTamanho;
    private final AuthService authService;
    private final CarrinhoItemMapper carrinhoItemMapper;

    /**
     * Lista todos os itens do carrinho do usuário autenticado.
     */
    @Transactional(readOnly = true)
    public List<CarrinhoItemDTO> listarItens() {
        Usuario usuario = authService.getUsuarioAutenticado();
        List<CarrinhoItem> itens = carrinhoItemRepository.findByUsuarioIdOrderByDataAdicaoDesc(usuario.getId());
        return itens.stream()
                .map(carrinhoItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Adiciona um item ao carrinho.
     */
    @Transactional
    public CarrinhoItemDTO adicionarItem(CarrinhoItemRequestDTO request) {
        Usuario usuario = authService.getUsuarioAutenticado();

        Produto produto = repositorioProduto.findById(request.getProdutoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado"));

        Tamanho tamanho = null;
        if (request.getTamanhoId() != null) {
            tamanho = repositorioTamanho.findById(request.getTamanhoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Tamanho não encontrado"));
        }

        // Verificar se o item já existe no carrinho
        CarrinhoItem itemExistente;
        if (tamanho != null) {
            itemExistente = carrinhoItemRepository
                    .findByUsuarioIdAndProdutoIdAndTamanhoId(usuario.getId(), produto.getId(), tamanho.getId())
                    .orElse(null);
        } else {
            itemExistente = carrinhoItemRepository
                    .findByUsuarioIdAndProdutoIdAndTamanhoIsNull(usuario.getId(), produto.getId())
                    .orElse(null);
        }

        CarrinhoItem item;
        if (itemExistente != null) {
            // Atualizar quantidade
            int novaQuantidade = itemExistente.getQuantidade() + request.getQuantidade();
            if (novaQuantidade > 99) {
                throw new RegraDeNegocioException("Quantidade máxima excedida (99)");
            }
            itemExistente.setQuantidade(novaQuantidade);
            item = carrinhoItemRepository.save(itemExistente);
        } else {
            // Criar novo item
            item = CarrinhoItem.builder()
                    .usuario(usuario)
                    .produto(produto)
                    .tamanho(tamanho)
                    .quantidade(request.getQuantidade())
                    .build();
            item = carrinhoItemRepository.save(item);
        }

        return carrinhoItemMapper.toDTO(item);
    }

    /**
     * Atualiza a quantidade de um item do carrinho.
     */
    @Transactional
    public CarrinhoItemDTO atualizarQuantidade(Long itemId, Integer novaQuantidade) {
        Usuario usuario = authService.getUsuarioAutenticado();

        CarrinhoItem item = carrinhoItemRepository.findById(itemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item do carrinho não encontrado"));

        // Verificar se o item pertence ao usuário
        if (!item.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraDeNegocioException("Você não tem permissão para modificar este item");
        }

        if (novaQuantidade < 1 || novaQuantidade > 99) {
            throw new RegraDeNegocioException("Quantidade deve estar entre 1 e 99");
        }

        item.setQuantidade(novaQuantidade);
        item = carrinhoItemRepository.save(item);

        return carrinhoItemMapper.toDTO(item);
    }

    /**
     * Remove um item do carrinho.
     */
    @Transactional
    public void removerItem(Long itemId) {
        Usuario usuario = authService.getUsuarioAutenticado();

        CarrinhoItem item = carrinhoItemRepository.findById(itemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item do carrinho não encontrado"));

        // Verificar se o item pertence ao usuário
        if (!item.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraDeNegocioException("Você não tem permissão para remover este item");
        }

        carrinhoItemRepository.delete(item);
    }

    /**
     * Limpa todo o carrinho do usuário.
     */
    @Transactional
    public void limparCarrinho() {
        Usuario usuario = authService.getUsuarioAutenticado();
        carrinhoItemRepository.deleteByUsuarioId(usuario.getId());
    }

    /**
     * Conta a quantidade de itens no carrinho.
     */
    @Transactional(readOnly = true)
    public Long contarItens() {
        Usuario usuario = authService.getUsuarioAutenticado();
        return carrinhoItemRepository.countByUsuarioId(usuario.getId());
    }
}
