package com.luigarah.model.produto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Tradução automática dos textos de um produto para um idioma (migration V7).
 * Os campos traduzidos são só os descritivos; título (marca), autor e
 * categoria continuam os originais.
 */
@Entity
@Table(name = "PRODUTO_TRADUCOES", schema = "APP_LUIGARAH")
@IdClass(ProdutoTraducao.Id.class)
@Getter
@Setter
@NoArgsConstructor
public class ProdutoTraducao {

    @jakarta.persistence.Id
    @Column(name = "PRODUTO_ID", nullable = false)
    private Long produtoId;

    @jakarta.persistence.Id
    @Column(name = "IDIOMA", nullable = false, length = 5)
    private String idioma;

    @Column(name = "SUBTITULO", length = 255)
    private String subtitulo;

    @Column(name = "DESCRICAO", columnDefinition = "text")
    private String descricao;

    @Column(name = "COMPOSICAO", columnDefinition = "text")
    private String composicao;

    /** JSON em texto: array de strings, no mesmo formato de Produto.destaques. */
    @Column(name = "DESTAQUES", columnDefinition = "text")
    private String destaques;

    /** Hash do texto em português que originou esta tradução. */
    @Column(name = "HASH_ORIGEM", nullable = false, length = 64)
    private String hashOrigem;

    @Column(name = "ATUALIZADO_EM", nullable = false)
    private LocalDateTime atualizadoEm;

    public ProdutoTraducao(Long produtoId, String idioma) {
        this.produtoId = produtoId;
        this.idioma = idioma;
    }

    public static class Id implements Serializable {
        private Long produtoId;
        private String idioma;

        public Id() {}

        public Id(Long produtoId, String idioma) {
            this.produtoId = produtoId;
            this.idioma = idioma;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id id)) return false;
            return Objects.equals(produtoId, id.produtoId) && Objects.equals(idioma, id.idioma);
        }

        @Override
        public int hashCode() {
            return Objects.hash(produtoId, idioma);
        }
    }
}
