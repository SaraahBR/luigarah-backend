package com.luigarah.model;

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

    public Tamanho() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }
}
