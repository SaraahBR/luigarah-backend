package com.luigarah.model.produto;

import com.luigarah.model.identidade.Identidade;
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
 * - Campo imagens (JSON em String contendo array de URLs) passa por cleanUrlArrayJson().
 * - Demais Strings passam por cleanText(): remove CR/LF e trim, preservando espaços internos.
 */
@Entity
@Table(name = "PRODUTOS", schema = "APP_LUIGARAH")
@SequenceGenerator(
        name = "produtos_seq_gen",
        sequenceName = "APP_LUIGARAH.PRODUTOS_SEQ",
        allocationSize = 1
)
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produtos_seq_gen")
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    @Column(name = "TITULO", nullable = false, length = 255)
    private String titulo;

    @NotBlank(message = "Subtítulo é obrigatório")
    @Size(max = 255, message = "Subtítulo deve ter no máximo 255 caracteres")
    @Column(name = "SUBTITULO", nullable = false, length = 255)
    private String subtitulo;

    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 255, message = "Autor deve ter no máximo 255 caracteres")
    @Column(name = "AUTOR", nullable = false, length = 255)
    private String autor;

    @NotBlank(message = "Descrição é obrigatória")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "DESCRICAO", nullable = false)
    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    @Column(name = "PRECO", nullable = false, precision = 19, scale = 2)
    private BigDecimal preco;

    @NotBlank(message = "Dimensão é obrigatória")
    @Size(max = 100, message = "Dimensão deve ter no máximo 100 caracteres")
    @Column(name = "DIMENSAO", nullable = false, length = 100)
    private String dimensao;

    @NotBlank(message = "Imagem principal é obrigatória")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "IMAGEM", nullable = false)
    private String imagem;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "IMAGEM_HOVER")
    private String imagemHover;

    /** JSON em texto: array de URLs. */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "IMAGENS")
    private String imagens;

    @NotBlank(message = "Composição é obrigatória")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "COMPOSICAO", nullable = false)
    private String composicao;

    /** JSON em texto: objeto com medidas. */
    /** Destaques como texto longo (VARCHAR2(16000) no Oracle). */
    @Column(name = "DESTAQUES", length = 16000)
    private String destaques;

    @NotBlank(message = "Categoria é obrigatória")
    @Pattern(regexp = "bolsas|roupas|sapatos", message = "Categoria deve ser: bolsas, roupas ou sapatos")
    @Column(name = "CATEGORIA", nullable = false, length = 50)
    private String categoria;

    /** JSON em texto: objeto com medidas. */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "MODELO")
    private String modelo;

    /**
     * Padrão de tamanho do produto (usa|br|sapatos).
     * Mapeia a coluna PADRAO_TAMANHO no Oracle (conforme CK_PROD_PADRAO_TAMANHO).
     */
    @Pattern(regexp = "usa|br|sapatos", message = "PADRAO_TAMANHO deve ser usa, br ou sapatos")
    @Column(name = "PADRAO_TAMANHO", length = 10)
    private String padraoTamanho;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IDENTIDADE_ID")
    private Identidade identidade;

    @CreationTimestamp
    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "DATA_ATUALIZACAO", nullable = false)
    private LocalDateTime dataAtualizacao;

    public Produto() {}

    public Produto(String titulo, String subtitulo, String autor, String descricao,
                   BigDecimal preco, String dimensao, String imagem, String categoria) {
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

    /** Sanitiza URL removendo CR/LF, trim e QUALQUER whitespace interno. */
    private static String cleanUrl(String raw) {
        if (raw == null) return null;
        String s = cleanText(raw);
        return s.replaceAll("\\s+", "");
    }

    /** Sanitiza JSON (array de URLs) removendo CR/LF e espaços grudados nas aspas. */
    private static String cleanUrlArrayJson(String raw) {
        if (raw == null) return null;
        String s = raw.replace("\r", "").replace("\n", "").trim();
        s = s.replaceAll("\\s+\"", "\"");
        s = s.replaceAll("\"\\s+", "\"");
        return s;
    }

    // =========================
    // Getters/Setters
    // =========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } // ignorado no create pelo service

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

    public String getPadraoTamanho() { return padraoTamanho; }
    public void setPadraoTamanho(String padraoTamanho) {
        // mantém sanitização simples; se quiser normalizar para minúsculas, descomente a linha abaixo
        // this.padraoTamanho = cleanText(padraoTamanho == null ? null : padraoTamanho.toLowerCase());
        this.padraoTamanho = cleanText(padraoTamanho);
    }

    public Identidade getIdentidade() { return identidade; }
    public void setIdentidade(Identidade identidade) { this.identidade = identidade; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
