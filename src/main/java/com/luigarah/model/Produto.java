package com.luigarah.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade de Produto.
 *
 * ⚠️ Sanitização:
 * - Campos de URL (imagem / imagemHover) passam por cleanUrl(): remove CR/LF, espaços nas pontas e QUALQUER whitespace no meio.
 *   (URL não deve ter espaços; se vierem, quebram o front)
 * - Campo imagens (JSON em String contendo array de URLs) passa por cleanUrlArrayJson():
 *      - remove CR/LF
 *      - trim
 *      - remove espaços imediatamente antes/ depois de aspas dentro do JSON (ex.: "https://...jpg    " -> "https://...jpg")
 * - Demais Strings passam por cleanText(): remove CR/LF e trim, preservando espaços internos.
 */
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
    @Basic(fetch = FetchType.EAGER) // garantir que venha carregado
    @Column(name = "imagens")
    private String imagens;

    @NotBlank(message = "Composição é obrigatória")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private String composicao;

    /** JSON em texto: array de strings. */
    @Lob
    @Basic(fetch = FetchType.EAGER) // garantir que venha carregado
    @Column(name = "destaques")
    private String destaques;

    @NotBlank(message = "Categoria é obrigatória")
    @Pattern(regexp = "bolsas|roupas|sapatos", message = "Categoria deve ser: bolsas, roupas ou sapatos")
    @Column(nullable = false, length = 50)
    private String categoria;

    /** JSON em texto: objeto com medidas. */
    @Lob
    @Basic(fetch = FetchType.EAGER) // garantir que venha carregado
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
        // usa setters para garantir limpeza
        setTitulo(titulo);
        setSubtitulo(subtitulo);
        setAutor(autor);
        setDescricao(descricao);
        setPreco(preco);
        setDimensao(dimensao);
        setImagem(imagem);
        setCategoria(categoria);
    }

    // =========================
    // Helpers de sanitização
    // =========================

    /** Remove CR/LF e espaços nas pontas; preserva espaços internos. */
    private static String cleanText(String raw) {
        if (raw == null) return null;
        String s = raw.replace("\r", "").replace("\n", "").trim();
        return s;
    }

    /**
     * Sanitiza URL:
     * - remove CR/LF
     * - remove espaços nas pontas
     * - remove QUALQUER whitespace no meio (\\s+), pois URL com espaço é inválida.
     */
    private static String cleanUrl(String raw) {
        if (raw == null) return null;
        // primeiro tira CR/LF e poda pontas
        String s = cleanText(raw);
        // remove qualquer whitespace restante (inclui espaço, tab, etc.)
        s = s.replaceAll("\\s+", "");
        return s;
    }

    /**
     * Sanitiza JSON com array de URLs em String.
     * Objetivo: remover CR/LF e espaços que ficam grudados antes/ depois das aspas dos itens do array.
     *
     * Ex.: ["https://a.jpg\\n            ", "https://b.jpg"] => ["https://a.jpg","https://b.jpg"]
     *
     * Obs.: não removemos espaços "internos" normais fora das Strings; JSON tolera, mas mantemos compacto.
     */
    private static String cleanUrlArrayJson(String raw) {
        if (raw == null) return null;
        String s = raw.replace("\r", "").replace("\n", "").trim();
        // remove espaços imediatamente antes de uma aspa de fechamento dentro do array
        s = s.replaceAll("\\s+\"", "\"");
        // (defensivo) remove espaços imediatamente após abertura de aspa
        s = s.replaceAll("\"\\s+", "\"");
        return s;
    }

    // =========================
    // Getters/Setters
    // =========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = cleanText(titulo); }

    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String subtitulo) { this.subtitulo = cleanText(subtitulo); }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = cleanText(autor); }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = cleanText(descricao); }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getDimensao() { return dimensao; }
    public void setDimensao(String dimensao) { this.dimensao = cleanText(dimensao); }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = cleanUrl(imagem); }

    public String getImagemHover() { return imagemHover; }
    public void setImagemHover(String imagemHover) { this.imagemHover = cleanUrl(imagemHover); }

    public String getImagens() { return imagens; }
    public void setImagens(String imagens) { this.imagens = cleanUrlArrayJson(imagens); }

    public String getComposicao() { return composicao; }
    public void setComposicao(String composicao) { this.composicao = cleanText(composicao); }

    public String getDestaques() { return destaques; }
    public void setDestaques(String destaques) { this.destaques = cleanText(destaques); }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = cleanText(categoria); }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = cleanText(modelo); }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
