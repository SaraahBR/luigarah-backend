package com.luigarah.dto.produto;

import com.luigarah.dto.identidade.IdentidadeDTO;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonRawValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de Produto para entrada/saída na API.
 *
 * ⚠️ Sanitização via setters:
 * - URL (imagem / imagemHover): cleanUrl()
 * - imagens (JSON em String com array de URLs): cleanUrlArrayJson()
 * - Demais Strings: cleanText()
 *
 * Motivo: alguns dados chegam com '\n' e muitos espaços antes da aspa final da URL,
 * quebrando o carregamento no front. Os setters garantem o formato correto.
 */
@Schema(description = "DTO para transferência de dados de produto")
public class ProdutoDTO {

    @Schema(description = "ID único do produto (gerado no backend; ignore no POST de criação)",
            example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    @Schema(description = "Nome da marca", example = "Gucci", required = true)
    private String titulo;

    @NotBlank(message = "Subtítulo é obrigatório")
    @Size(max = 255, message = "Subtítulo deve ter no máximo 255 caracteres")
    @Schema(description = "Tipo do produto", example = "Tiracolo", required = true)
    private String subtitulo;

    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 255, message = "Autor deve ter no máximo 255 caracteres")
    @Schema(description = "Designer/Autor", example = "Demna Gvasalia", required = true)
    private String autor;

    @NotBlank(message = "Descrição é obrigatória")
    @Schema(description = "Descrição detalhada do produto", example = "Bolsa tiracolo Dionysus mini", required = true)
    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    @Schema(description = "Preço do produto em reais", example = "9399.00", required = true)
    private BigDecimal preco;

    @NotBlank(message = "Dimensão é obrigatória")
    @Size(max = 100, message = "Dimensão deve ter no máximo 100 caracteres")
    @Schema(description = "Tamanho do produto", example = "Média", required = true)
    private String dimensao;

    @NotBlank(message = "Imagem principal é obrigatória")
    @Schema(description = "URL da imagem principal", example = "https://example.com/image.jpg", required = true)
    private String imagem;

    @Schema(description = "URL da imagem de hover", example = "https://example.com/image-hover.jpg")
    private String imagemHover;

    /** Conteúdo JSON em string – será emitido cru no JSON de resposta. */
    @JsonRawValue
    @Schema(description = "Array JSON de URLs de imagens (string JSON crua)", example = "[\"https://ex.com/1.jpg\",\"https://ex.com/2.jpg\"]")
    private String imagens;

    @NotBlank(message = "Composição é obrigatória")
    @Schema(description = "Composição do material", example = "Exterior: camurça 100%, canvas 100%", required = true)
    private String composicao;

    /** Conteúdo JSON em string – será emitido cru no JSON de resposta. */
    @JsonRawValue
    @Schema(description = "Características destacadas em JSON (string JSON crua)", example = "[\"bege\",\"canvas GG Supreme\"]")
    private String destaques;

    @NotBlank(message = "Categoria é obrigatória")
    @Pattern(regexp = "bolsas|roupas|sapatos", message = "Categoria deve ser: bolsas, roupas ou sapatos")
    @Schema(description = "Categoria do produto", example = "bolsas", allowableValues = {"bolsas", "roupas", "sapatos"}, required = true)
    private String categoria;

    /** Conteúdo JSON em string – será emitido cru no JSON de resposta. */
    @JsonRawValue
    @Schema(description = "Informações do modelo em JSON (string JSON crua)", example = "{\"altura_cm\":179,\"busto_cm\":84}")
    private String modelo;

    @Schema(description = "Data de criação do produto")
    private LocalDateTime dataCriacao;

    @Schema(description = "Data da última atualização do produto")
    private LocalDateTime dataAtualizacao;

    @Schema(description = "Identidade do produto (Masculino, Feminino, Unissex, Infantil)")
    private IdentidadeDTO identidade;

    public ProdutoDTO() {}

    public ProdutoDTO(String titulo, String subtitulo, String autor, String descricao,
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

    /** URL não pode ter whitespace; remove CR/LF, poda e tira \\s+ em qualquer posição. */
    private static String cleanUrl(String raw) {
        if (raw == null) return null;
        String s = cleanText(raw);
        return s.replaceAll("\\s+", "");
    }

    /**
     * Limpa JSON de array de URLs em String:
     * - remove CR/LF
     * - trim
     * - remove espaços imediatamente antes/ depois das aspas dos itens
     */
    private static String cleanUrlArrayJson(String raw) {
        if (raw == null) return null;
        String s = raw.replace("\r", "").replace("\n", "").trim();
        s = s.replaceAll("\\s+\"", "\""); // ...jpg[espaços]"
        s = s.replaceAll("\"\\s+", "\""); // "[espaços]https://...
        return s;
    }

    // =========================
    // Getters e Setters
    // =========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } // será ignorado no create pelo service

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

    public IdentidadeDTO getIdentidade() { return identidade; }
    public void setIdentidade(IdentidadeDTO identidade) { this.identidade = identidade; }
}
