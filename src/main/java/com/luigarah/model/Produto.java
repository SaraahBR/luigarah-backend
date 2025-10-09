package com.luigarah.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "Subtítulo é obrigatório")
    @Size(max = 255, message = "Subtítulo deve ter no máximo 255 caracteres")
    @Column(nullable = false)
    private String subtitulo;

    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 255, message = "Autor deve ter no máximo 255 caracteres")
    @Column(nullable = false)
    private String autor;

    @NotBlank(message = "Descrição é obrigatória")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @NotBlank(message = "Dimensão é obrigatória")
    @Size(max = 100, message = "Dimensão deve ter no máximo 100 caracteres")
    @Column(nullable = false)
    private String dimensao;

    @NotBlank(message = "Imagem principal é obrigatória")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private String imagem;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "imagem_hover")
    private String imagemHover;

    /** JSON em texto: array de URLs. */
    @Lob
    @Basic(fetch = FetchType.EAGER) // <<< garantir que venha carregado
    @Column(name = "imagens")
    private String imagens;

    @NotBlank(message = "Composição é obrigatória")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private String composicao;

    /** JSON em texto: array de strings. */
    @Lob
    @Basic(fetch = FetchType.EAGER) // <<< garantir que venha carregado
    @Column(name = "destaques")
    private String destaques;

    @NotBlank(message = "Categoria é obrigatória")
    @Pattern(regexp = "bolsas|roupas|sapatos", message = "Categoria deve ser: bolsas, roupas ou sapatos")
    @Column(nullable = false, length = 50)
    private String categoria;

    /** JSON em texto: objeto com medidas. */
    @Lob
    @Basic(fetch = FetchType.EAGER) // <<< garantir que venha carregado
    @Column(name = "modelo")
    private String modelo;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    public Produto() {}

    public Produto(String titulo, String subtitulo, String autor, String descricao,
                   BigDecimal preco, String dimensao, String imagem, String categoria) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.autor = autor;
        this.descricao = descricao;
        this.preco = preco;
        this.dimensao = dimensao;
        this.imagem = imagem;
        this.categoria = categoria;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String subtitulo) { this.subtitulo = subtitulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getDimensao() { return dimensao; }
    public void setDimensao(String dimensao) { this.dimensao = dimensao; }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }

    public String getImagemHover() { return imagemHover; }
    public void setImagemHover(String imagemHover) { this.imagemHover = imagemHover; }

    public String getImagens() { return imagens; }
    public void setImagens(String imagens) { this.imagens = imagens; }

    public String getComposicao() { return composicao; }
    public void setComposicao(String composicao) { this.composicao = composicao; }

    public String getDestaques() { return destaques; }
    public void setDestaques(String destaques) { this.destaques = destaques; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
