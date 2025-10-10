package com.luigarah.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.luigarah.dto.jackson.StringListFlexDeserializer;
import com.luigarah.dto.jackson.ObjectFlexDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Schema(description = "DTO para criação de produto (entrada flexível)")
public class ProdutoCreateDTO {

    @NotBlank @Size(max = 255) private String titulo;
    @NotBlank @Size(max = 255) private String subtitulo;
    @NotBlank @Size(max = 255) private String autor;
    @NotBlank private String descricao;

    @NotNull @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal preco;

    @NotBlank @Size(max = 100) private String dimensao;

    @NotBlank private String imagem;
    private String imagemHover;

    // Aceita: "https://x.jpg" | "https://x.jpg, https://y.jpg" | ["https://x.jpg","https://y.jpg"]
    @JsonDeserialize(using = StringListFlexDeserializer.class)
    private List<String> imagens;

    @NotBlank private String composicao;

    // Aceita: "bordô, sem alças" | ["bordô","sem alças"]
    @JsonDeserialize(using = StringListFlexDeserializer.class)
    private List<String> destaques;

    @NotBlank
    @Pattern(regexp = "bolsas|roupas|sapatos", message = "Categoria deve ser: bolsas, roupas ou sapatos")
    private String categoria;

    // Aceita: {"altura_cm":177,...} | "altura_cm:177; busto_cm:86; veste:M"
    @JsonDeserialize(using = ObjectFlexDeserializer.class)
    private Map<String,Object> modelo;

    // getters/setters com pequenas limpezas
    public String getTitulo() { return titulo; }
    public void setTitulo(String v) { this.titulo = trim(v); }
    public String getSubtitulo() { return subtitulo; }
    public void setSubtitulo(String v) { this.subtitulo = trim(v); }
    public String getAutor() { return autor; }
    public void setAutor(String v) { this.autor = trim(v); }
    public String getDescricao() { return descricao; }
    public void setDescricao(String v) { this.descricao = trim(v); }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getDimensao() { return dimensao; }
    public void setDimensao(String v) { this.dimensao = trim(v); }
    public String getImagem() { return imagem; }
    public void setImagem(String v) { this.imagem = noWs(trim(v)); }
    public String getImagemHover() { return imagemHover; }
    public void setImagemHover(String v) { this.imagemHover = noWs(trim(v)); }
    public List<String> getImagens() { return imagens; }
    public void setImagens(List<String> imagens) { this.imagens = imagens; }
    public String getComposicao() { return composicao; }
    public void setComposicao(String v) { this.composicao = trim(v); }
    public List<String> getDestaques() { return destaques; }
    public void setDestaques(List<String> destaques) { this.destaques = destaques; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String v) { this.categoria = trim(v); }
    public Map<String, Object> getModelo() { return modelo; }
    public void setModelo(Map<String, Object> modelo) { this.modelo = modelo; }

    private static String trim(String v){ return v==null? null : v.replace("\r","").replace("\n","").trim(); }
    private static String noWs(String v){ return v==null? null : v.replaceAll("\\s+",""); }
}
