package com.luigarah.dto.tamanho;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item com id e padrão de tamanho")
public class PadraoItemDTO {
    @Schema(example = "41")
    private Long id;

    @Schema(description = "Padrão ('usa'|'br'|'sapatos' ou null para indefinido)", example = "usa", nullable = true)
    private String padrao;

    public PadraoItemDTO() {}
    public PadraoItemDTO(Long id, String padrao) {
        this.id = id;
        this.padrao = padrao;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPadrao() { return padrao; }
    public void setPadrao(String padrao) { this.padrao = padrao; }
}
