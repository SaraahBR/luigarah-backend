package com.luigarah.dto.produto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para resposta de operações com produtos")
public class RespostaProdutoDTO<T> {

    @Schema(description = "Dados retornados")
    private T dados;

    @Schema(description = "Indica se a operação foi bem-sucedida", example = "true")
    private boolean sucesso;

    @Schema(description = "Mensagem descritiva", example = "Operação realizada com sucesso")
    private String mensagem;

    @Schema(description = "Total de registros encontrados", example = "150")
    private Long total;

    @Schema(description = "Página atual", example = "0")
    private Integer paginaAtual;

    @Schema(description = "Total de páginas", example = "10")
    private Integer totalPaginas;

    @Schema(description = "Tamanho da página", example = "15")
    private Integer tamanhoPagina;

    // Construtores
    public RespostaProdutoDTO() {}

    public RespostaProdutoDTO(T dados, boolean sucesso, String mensagem) {
        this.dados = dados;
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    public RespostaProdutoDTO(T dados, boolean sucesso, String mensagem, Long total,
                              Integer paginaAtual, Integer totalPaginas, Integer tamanhoPagina) {
        this.dados = dados;
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.total = total;
        this.paginaAtual = paginaAtual;
        this.totalPaginas = totalPaginas;
        this.tamanhoPagina = tamanhoPagina;
    }

    // Métodos estáticos para facilitar criação
    public static <T> RespostaProdutoDTO<T> sucesso(T dados) {
        return new RespostaProdutoDTO<>(dados, true, "Operação realizada com sucesso");
    }

    public static <T> RespostaProdutoDTO<T> sucesso(T dados, String mensagem) {
        return new RespostaProdutoDTO<>(dados, true, mensagem);
    }

    public static <T> RespostaProdutoDTO<T> erro(String mensagem) {
        return new RespostaProdutoDTO<>(null, false, mensagem);
    }

    public static <T> RespostaProdutoDTO<T> sucessoComPaginacao(T dados, Long total,
                                                                Integer paginaAtual, Integer totalPaginas, Integer tamanhoPagina) {
        return new RespostaProdutoDTO<>(dados, true, "Produtos encontrados com sucesso",
                total, paginaAtual, totalPaginas, tamanhoPagina);
    }

    // Getters e Setters
    public T getDados() { return dados; }
    public void setDados(T dados) { this.dados = dados; }

    public boolean isSucesso() { return sucesso; }
    public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }

    public Integer getPaginaAtual() { return paginaAtual; }
    public void setPaginaAtual(Integer paginaAtual) { this.paginaAtual = paginaAtual; }

    public Integer getTotalPaginas() { return totalPaginas; }
    public void setTotalPaginas(Integer totalPaginas) { this.totalPaginas = totalPaginas; }

    public Integer getTamanhoPagina() { return tamanhoPagina; }
    public void setTamanhoPagina(Integer tamanhoPagina) { this.tamanhoPagina = tamanhoPagina; }
}