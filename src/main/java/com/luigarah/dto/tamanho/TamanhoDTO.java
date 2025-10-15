package com.luigarah.dto.tamanho;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de tamanho (com estoque quando aplicável)")
public class TamanhoDTO {
    @Schema(example = "42")
    private String etiqueta;

    @Schema(example = "sapatos", allowableValues = {"roupas", "sapatos"})
    private String categoria;

    @Schema(description = "Quantidade em estoque para o produto", example = "10")
    private Integer qtdEstoque;

    public TamanhoDTO() {}

    public TamanhoDTO(String etiqueta, String categoria, Integer qtdEstoque) {
        this.etiqueta = etiqueta;
        this.categoria = categoria;
        this.qtdEstoque = qtdEstoque;
    }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getQtdEstoque() { return qtdEstoque; }
    public void setQtdEstoque(Integer qtdEstoque) { this.qtdEstoque = qtdEstoque; }
}
