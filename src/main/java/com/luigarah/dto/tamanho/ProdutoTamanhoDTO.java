package com.luigarah.dto.tamanho;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item para vincular tamanho ao produto")
public class ProdutoTamanhoDTO {

    @Schema(description = "ID do tamanho no catálogo", example = "123", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Etiqueta do tamanho (ex.: XS, M, 38, 42)", example = "M")
    private String etiqueta;

    @Schema(description = "Estoque para esse tamanho", example = "12")
    private Integer qtdEstoque;

    public ProdutoTamanhoDTO() {}

    public ProdutoTamanhoDTO(String etiqueta, Integer qtdEstoque) {
        this.etiqueta = etiqueta;
        this.qtdEstoque = qtdEstoque;
    }

    public ProdutoTamanhoDTO(Long id, String etiqueta, Integer qtdEstoque) {
        this.id = id;
        this.etiqueta = etiqueta;
        this.qtdEstoque = qtdEstoque;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public Integer getQtdEstoque() { return qtdEstoque; }
    public void setQtdEstoque(Integer qtdEstoque) { this.qtdEstoque = qtdEstoque; }
}
