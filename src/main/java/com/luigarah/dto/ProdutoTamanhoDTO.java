package com.luigarah.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item para vincular tamanho ao produto")
public class ProdutoTamanhoDTO {

    @Schema(description = "Etiqueta do tamanho (ex.: XS, M, 38, 42)", example = "M", required = true)
    private String etiqueta;

    @Schema(description = "Estoque para esse tamanho", example = "12")
    private Integer qtdEstoque;

    public ProdutoTamanhoDTO() {}

    public ProdutoTamanhoDTO(String etiqueta, Integer qtdEstoque) {
        this.etiqueta = etiqueta;
        this.qtdEstoque = qtdEstoque;
    }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public Integer getQtdEstoque() { return qtdEstoque; }
    public void setQtdEstoque(Integer qtdEstoque) { this.qtdEstoque = qtdEstoque; }
}
