package com.luigarah.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonRawValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "DTO para transferência de dados de produto")
public class ProdutoDTO {

    @Schema(description = "ID único do produto", example = "1")
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

    public ProdutoDTO() {}

    public ProdutoDTO(String titulo, String subtitulo, String autor, String descricao,
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

    // Getters e Setters
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
