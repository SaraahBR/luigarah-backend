package com.luigarah.service.carrinho;

import com.luigarah.dto.carrinho.AtualizarCarrinhoItemDTO;
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

        String categoria = produto.getCategoria();
        Tamanho tamanho = null;

        // Validar categoria e tamanho
        if ("bolsas".equalsIgnoreCase(categoria)) {
            // Bolsas não devem ter tamanho
            if (request.getTamanhoId() != null) {
                throw new RegraDeNegocioException("Bolsas não possuem tamanho");
            }
        } else {
            // Roupas e sapatos precisam de tamanho
            if (request.getTamanhoId() == null) {
                throw new RegraDeNegocioException("Produtos da categoria " + categoria + " requerem tamanho");
            }

            tamanho = repositorioTamanho.findById(request.getTamanhoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Tamanho não encontrado"));
        }

        // Validar estoque antes de adicionar
        int estoqueDisponivel = obterEstoqueDisponivel(produto.getId(), categoria, tamanho);
        if (estoqueDisponivel < request.getQuantidade()) {
            throw new RegraDeNegocioException(
                "Estoque insuficiente. Disponível: " + estoqueDisponivel + ", solicitado: " + request.getQuantidade()
            );
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

            // Validar nova quantidade contra estoque
            if (novaQuantidade > estoqueDisponivel) {
                throw new RegraDeNegocioException(
                    "Estoque insuficiente. Disponível: " + estoqueDisponivel +
                    ", já no carrinho: " + itemExistente.getQuantidade() +
                    ", tentando adicionar: " + request.getQuantidade()
                );
            }

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

        // Validar estoque
        String categoria = item.getProduto().getCategoria();
        int estoqueDisponivel = obterEstoqueDisponivel(item.getProduto().getId(), categoria, item.getTamanho());

        if (novaQuantidade > estoqueDisponivel) {
            throw new RegraDeNegocioException(
                "Estoque insuficiente. Disponível: " + estoqueDisponivel + ", solicitado: " + novaQuantidade
            );
        }

        item.setQuantidade(novaQuantidade);
        item = carrinhoItemRepository.save(item);

        return carrinhoItemMapper.toDTO(item);
    }

    /**
     * Atualiza tamanho e quantidade de um item do carrinho.
     * - Para BOLSAS: tamanhoId deve ser null, apenas valida estoque geral
     * - Para ROUPAS/SAPATOS: tamanhoId é obrigatório, valida estoque do tamanho específico
     */
    @Transactional
    public CarrinhoItemDTO atualizarItem(Long itemId, AtualizarCarrinhoItemDTO dto) {
        Usuario usuario = authService.getUsuarioAutenticado();

        CarrinhoItem item = carrinhoItemRepository.findById(itemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item do carrinho não encontrado"));

        // Verificar se o item pertence ao usuário
        if (!item.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraDeNegocioException("Você não tem permissão para modificar este item");
        }

        Produto produto = item.getProduto();
        String categoria = produto.getCategoria();

        // Validar categoria e tamanho
        Tamanho novoTamanho = null;
        if ("bolsas".equalsIgnoreCase(categoria)) {
            // Bolsas não devem ter tamanho
            if (dto.getTamanhoId() != null) {
                throw new RegraDeNegocioException("Bolsas não possuem tamanho");
            }
        } else {
            // Roupas e sapatos precisam de tamanho
            if (dto.getTamanhoId() == null) {
                throw new RegraDeNegocioException("Produtos da categoria " + categoria + " requerem tamanho");
            }

            novoTamanho = repositorioTamanho.findById(dto.getTamanhoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Tamanho não encontrado"));
        }

        // Validar quantidade
        if (dto.getQuantidade() < 1 || dto.getQuantidade() > 99) {
            throw new RegraDeNegocioException("Quantidade deve estar entre 1 e 99");
        }

        // Validar estoque do novo tamanho
        int estoqueDisponivel = obterEstoqueDisponivel(produto.getId(), categoria, novoTamanho);

        // Se estiver mudando o tamanho, verificar se já existe outro item com esse tamanho
        if (novoTamanho != null && !novoTamanho.getId().equals(item.getTamanho() != null ? item.getTamanho().getId() : null)) {
            CarrinhoItem itemExistenteComNovoTamanho = carrinhoItemRepository
                    .findByUsuarioIdAndProdutoIdAndTamanhoId(usuario.getId(), produto.getId(), novoTamanho.getId())
                    .orElse(null);

            if (itemExistenteComNovoTamanho != null) {
                // Já existe item com esse tamanho - NÃO PERMITIR alteração
                throw new RegraDeNegocioException(
                    "Você já possui este produto no carrinho com o tamanho " + novoTamanho.getEtiqueta() +
                    ". Para adicionar mais unidades, ajuste a quantidade do item existente."
                );
            }
        }

        // Se for bolsa e estiver tentando mudar para um tamanho null quando já existe
        if (categoria.equalsIgnoreCase("bolsas") && novoTamanho == null && item.getTamanho() != null) {
            CarrinhoItem itemExistenteSemTamanho = carrinhoItemRepository
                    .findByUsuarioIdAndProdutoIdAndTamanhoIsNull(usuario.getId(), produto.getId())
                    .orElse(null);

            if (itemExistenteSemTamanho != null && !itemExistenteSemTamanho.getId().equals(itemId)) {
                throw new RegraDeNegocioException(
                    "Você já possui este produto no carrinho. Para adicionar mais unidades, ajuste a quantidade do item existente."
                );
            }
        }

        // Validar estoque para a quantidade solicitada
        if (dto.getQuantidade() > estoqueDisponivel) {
            throw new RegraDeNegocioException(
                "Estoque insuficiente. Disponível: " + estoqueDisponivel + ", solicitado: " + dto.getQuantidade()
            );
        }

        // Atualizar item
        item.setTamanho(novoTamanho);
        item.setQuantidade(dto.getQuantidade());
        item = carrinhoItemRepository.save(item);

        return carrinhoItemMapper.toDTO(item);
    }

    /**
     * Obtém o estoque disponível de um produto.
     * - BOLSAS: consulta produtos_estoque
     * - ROUPAS/SAPATOS: consulta produtos_tamanhos (por tamanho)
     */
    private int obterEstoqueDisponivel(Long produtoId, String categoria, Tamanho tamanho) {
        if ("bolsas".equalsIgnoreCase(categoria)) {
            Integer estoque = repositorioProduto.obterEstoqueProduto(produtoId);
            return estoque != null ? estoque : 0;
        } else {
            if (tamanho == null || tamanho.getEtiqueta() == null) {
                return 0;
            }

            var estoques = repositorioProduto.listarEstoquePorProduto(produtoId);
            for (Object[] row : estoques) {
                // Query retorna: [id, etiqueta, qtd_estoque]
                String etiqueta = (String) row[1];
                Number qtd = (Number) row[2];

                if (etiqueta != null && etiqueta.equalsIgnoreCase(tamanho.getEtiqueta())) {
                    return qtd != null ? qtd.intValue() : 0;
                }
            }
            return 0;
        }
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
