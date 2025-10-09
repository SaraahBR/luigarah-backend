package com.luigarah.model.id;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProdutoTamanhoId implements Serializable {
    private Long produtoId;
    private Long tamanhoId;

    public ProdutoTamanhoId() {}

    public ProdutoTamanhoId(Long produtoId, Long tamanhoId) {
        this.produtoId = produtoId;
        this.tamanhoId = tamanhoId;
    }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public Long getTamanhoId() { return tamanhoId; }
    public void setTamanhoId(Long tamanhoId) { this.tamanhoId = tamanhoId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProdutoTamanhoId)) return false;
        ProdutoTamanhoId that = (ProdutoTamanhoId) o;
        return Objects.equals(produtoId, that.produtoId) &&
                Objects.equals(tamanhoId, that.tamanhoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produtoId, tamanhoId);
    }
}
