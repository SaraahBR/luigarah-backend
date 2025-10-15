package com.luigarah.model.tamanho;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "tamanhos")
public class Tamanho {

    @Id
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Pattern(regexp = "roupas|sapatos")
    @Column(nullable = false, length = 20)
    private String categoria;

    @NotBlank
    @Column(nullable = false, length = 40)
    private String etiqueta;

    // padrão (usa|br|sapatos) — refletindo PADRAO NOT NULL no Oracle
    @NotBlank
    @Pattern(regexp = "usa|br|sapatos")
    @Column(name = "padrao", nullable = false, length = 10)
    private String padrao;

    // ordem semântica (usa para ordenação no catálogo)
    @Column(name = "ordem")
    private Integer ordem;

    public Tamanho() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public String getPadrao() { return padrao; }
    public void setPadrao(String padrao) { this.padrao = padrao; }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
}
