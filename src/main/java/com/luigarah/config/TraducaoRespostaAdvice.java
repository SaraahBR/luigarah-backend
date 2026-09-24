package com.luigarah.config;

import com.luigarah.dto.carrinho.CarrinhoItemDTO;
import com.luigarah.dto.listadesejos.ListaDesejoItemDTO;
import com.luigarah.dto.produto.ProdutoDTO;
import com.luigarah.dto.produto.RespostaProdutoDTO;
import com.luigarah.service.traducao.ServicoTraducaoProduto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.*;

/**
 * Aplica as traduções dos produtos em qualquer resposta da API que contenha
 * ProdutoDTO (listas, páginas, detalhe, carrinho, lista de desejos), conforme o
 * idioma pedido no cabeçalho Accept-Language (en, es ou fr). Sem cabeçalho ou em
 * português, a resposta não é alterada.
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class TraducaoRespostaAdvice implements ResponseBodyAdvice<Object> {

    private final ServicoTraducaoProduto servicoTraducao;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) return null;

        // A mesma URL responde diferente por idioma: caches devem considerar o cabeçalho
        response.getHeaders().add(HttpHeaders.VARY, HttpHeaders.ACCEPT_LANGUAGE);

        String idioma = request.getHeaders().getAcceptLanguageAsLocales().stream()
                .map(Locale::getLanguage)
                .findFirst()
                .orElse(null);
        if (ServicoTraducaoProduto.idiomaSuportado(idioma) == null) return body;

        List<ProdutoDTO> produtos = new ArrayList<>();
        coletar(body, produtos, Collections.newSetFromMap(new IdentityHashMap<>()));
        servicoTraducao.aplicar(produtos, idioma);
        return body;
    }

    /** Procura ProdutoDTO nos formatos de resposta usados pelos controllers. */
    private void coletar(Object obj, List<ProdutoDTO> saida, Set<Object> visitados) {
        if (obj == null || !visitados.add(obj)) return;

        if (obj instanceof ProdutoDTO p) {
            saida.add(p);
        } else if (obj instanceof RespostaProdutoDTO<?> r) {
            coletar(r.getDados(), saida, visitados);
        } else if (obj instanceof CarrinhoItemDTO c) {
            coletar(c.getProduto(), saida, visitados);
        } else if (obj instanceof ListaDesejoItemDTO l) {
            coletar(l.getProduto(), saida, visitados);
        } else if (obj instanceof Page<?> page) {
            page.getContent().forEach(o -> coletar(o, saida, visitados));
        } else if (obj instanceof Map<?, ?> map) {
            map.values().forEach(o -> coletar(o, saida, visitados));
        } else if (obj instanceof Iterable<?> it) {
            it.forEach(o -> coletar(o, saida, visitados));
        }
    }
}
