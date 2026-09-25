package com.luigarah.service.traducao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luigarah.dto.produto.ProdutoDTO;
import com.luigarah.model.produto.Produto;
import com.luigarah.model.produto.ProdutoTraducao;
import com.luigarah.repository.produto.RepositorioProduto;
import com.luigarah.repository.produto.RepositorioProdutoTraducao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tradução automática dos produtos para en, es e fr.
 *
 * - Traduz subtítulo (tipo), descrição, composição e destaques com o Google
 *   Cloud Translation e guarda o resultado em produto_traducoes.
 * - Cada tradução guarda o hash do texto em português de origem; se o produto
 *   for editado, a tradução antiga não é usada até ser refeita.
 * - Título (marca), autor, categoria e dimensão não são traduzidos aqui:
 *   marca e autor são nomes próprios, e categoria/dimensão são usados nos
 *   filtros do site (o frontend traduz esses por dicionário).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServicoTraducaoProduto {

    public static final List<String> IDIOMAS = List.of("en", "es", "fr");

    private final GoogleTranslateClient cliente;
    private final RepositorioProduto repositorioProduto;
    private final RepositorioProdutoTraducao repositorioTraducao;
    private final ApplicationEventPublisher eventos;
    private final ObjectMapper mapper = new ObjectMapper();

    /** Textos em português usados como origem da tradução (formato normalizado). */
    record Textos(String subtitulo, String descricao, String composicao, List<String> destaques) {}

    // ------------------------------------------------------------------
    // Idioma
    // ------------------------------------------------------------------

    /** Devolve en/es/fr se o idioma for suportado; null para português ou desconhecido. */
    public static String idiomaSuportado(String idioma) {
        if (idioma == null) return null;
        String base = idioma.trim().toLowerCase(Locale.ROOT);
        int corte = base.indexOf('-');
        if (corte > 0) base = base.substring(0, corte);
        return IDIOMAS.contains(base) ? base : null;
    }

    // ------------------------------------------------------------------
    // Geração das traduções
    // ------------------------------------------------------------------

    /** Traduz (ou refaz) as traduções desatualizadas de um produto. Devolve quantos idiomas foram traduzidos. */
    public int traduzirProduto(Long produtoId) throws java.io.IOException {
        if (!cliente.habilitado()) return 0;

        Produto produto = repositorioProduto.findById(produtoId).orElse(null);
        if (produto == null) return 0;

        Textos origem = textos(produto.getSubtitulo(), produto.getDescricao(),
                produto.getComposicao(), produto.getDestaques());
        String hash = hash(origem);

        Map<String, ProdutoTraducao> existentes = repositorioTraducao.findByProdutoId(produtoId).stream()
                .collect(Collectors.toMap(ProdutoTraducao::getIdioma, t -> t));

        // Ordem fixa: subtítulo, descrição, composição e depois cada destaque
        List<String> segmentos = new ArrayList<>();
        segmentos.add(origem.subtitulo());
        segmentos.add(origem.descricao());
        segmentos.add(origem.composicao());
        segmentos.addAll(origem.destaques());

        int traduzidos = 0;
        for (String idioma : IDIOMAS) {
            ProdutoTraducao atual = existentes.get(idioma);
            if (atual != null && hash.equals(atual.getHashOrigem())) continue;

            List<String> r = cliente.traduzir(segmentos, idioma);

            ProdutoTraducao t = atual != null ? atual : new ProdutoTraducao(produtoId, idioma);
            t.setSubtitulo(r.get(0));
            t.setDescricao(r.get(1));
            t.setComposicao(r.get(2));
            t.setDestaques(mapper.writeValueAsString(r.subList(3, r.size())));
            t.setHashOrigem(hash);
            t.setAtualizadoEm(LocalDateTime.now());
            repositorioTraducao.save(t);
            traduzidos++;
        }
        if (traduzidos > 0) {
            log.info("🌐 Produto {} traduzido para {} idioma(s)", produtoId, traduzidos);
            // respostas do catálogo guardadas em cache ainda têm o texto antigo
            eventos.publishEvent(new TraducoesAtualizadasEvent(produtoId));
        }
        return traduzidos;
    }

    /** Preenche as traduções que faltam ou estão desatualizadas em todo o catálogo. */
    public void preencherFaltantes() {
        if (!cliente.habilitado()) {
            log.info("🌐 GOOGLE_TRANSLATE_API_KEY não definida: produtos serão exibidos em português");
            return;
        }
        List<Long> ids = repositorioProduto.findAll().stream().map(Produto::getId).sorted().toList();
        int produtos = 0;
        for (Long id : ids) {
            try {
                if (traduzirProduto(id) > 0) produtos++;
            } catch (Exception e) {
                log.warn("🌐 Falha ao traduzir produto {}: {}", id, e.getMessage());
                // cota esgotada ou chave inválida: não adianta seguir agora
                String msg = String.valueOf(e.getMessage());
                if (msg.contains(" 403") || msg.contains(" 429") || msg.contains(" 400")) break;
            }
        }
        log.info("🌐 Preenchimento de traduções concluído: {} produto(s) traduzido(s)", produtos);
    }

    // ------------------------------------------------------------------
    // Aplicação nas respostas da API
    // ------------------------------------------------------------------

    /**
     * Troca descrição, composição e destaques pelos textos traduzidos e preenche
     * subtituloTraduzido. Produtos sem tradução (ou com tradução desatualizada)
     * ficam em português.
     */
    public void aplicar(Collection<ProdutoDTO> produtos, String idioma) {
        String lang = idiomaSuportado(idioma);
        if (lang == null || produtos.isEmpty()) return;

        Set<Long> ids = produtos.stream().map(ProdutoDTO::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) return;

        Map<Long, ProdutoTraducao> traducoes = repositorioTraducao.findByProdutoIdInAndIdioma(ids, lang).stream()
                .collect(Collectors.toMap(ProdutoTraducao::getProdutoId, t -> t));

        for (ProdutoDTO dto : produtos) {
            ProdutoTraducao t = traducoes.get(dto.getId());
            if (t == null) continue;

            String hashAtual = hash(textos(dto.getSubtitulo(), dto.getDescricao(), dto.getComposicao(), dto.getDestaques()));
            if (!hashAtual.equals(t.getHashOrigem())) continue; // produto editado depois da tradução

            dto.setSubtituloTraduzido(t.getSubtitulo());
            if (t.getDescricao() != null) dto.setDescricao(t.getDescricao());
            if (t.getComposicao() != null) dto.setComposicao(t.getComposicao());
            if (t.getDestaques() != null && dto.getDestaques() != null) dto.setDestaques(t.getDestaques());
        }
    }

    // ------------------------------------------------------------------
    // Normalização e hash
    // ------------------------------------------------------------------

    Textos textos(String subtitulo, String descricao, String composicao, String destaquesJson) {
        return new Textos(limpar(subtitulo), limpar(descricao), limpar(composicao), destaques(destaquesJson));
    }

    private List<String> destaques(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            List<String> lista = mapper.readValue(json, new TypeReference<List<String>>() {});
            return lista.stream().map(this::limpar).filter(s -> !s.isEmpty()).toList();
        } catch (Exception e) {
            return List.of(limpar(json));
        }
    }

    private String limpar(String s) {
        return s == null ? "" : s.replace("\r", "").replace("\n", " ").trim();
    }

    String hash(Textos t) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(mapper.writeValueAsString(t).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException | com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
