package com.luigarah.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Atualização de padrão (em lote)")
public class PadraoAtualizacaoDTO {

    @Schema(description = "Padrão desejado ('usa'|'br'|'sapatos' ou null para limpar/indefinido)", example = "usa", nullable = true)
    private String padrao;

    @Schema(description = "IDs de produtos a atualizar (opcional)", example = "[1,2,3]")
    private List<Long> produtoIds;

    @Schema(description = "IDs de tamanhos a atualizar (opcional)", example = "[10,11]")
    private List<Long> tamanhoIds;

    public String getPadrao() { return padrao; }
    public void setPadrao(String padrao) { this.padrao = padrao; }
    public List<Long> getProdutoIds() { return produtoIds; }
    public void setProdutoIds(List<Long> produtoIds) { this.produtoIds = produtoIds; }
    public List<Long> getTamanhoIds() { return tamanhoIds; }
    public void setTamanhoIds(List<Long> tamanhoIds) { this.tamanhoIds = tamanhoIds; }
}