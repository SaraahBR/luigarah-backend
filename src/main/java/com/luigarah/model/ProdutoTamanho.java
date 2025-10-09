package com.luigarah.model;

import com.luigarah.model.id.ProdutoTamanhoId;
import jakarta.persistence.*;

@Entity
@Table(name = "produtos_tamanhos")
public class ProdutoTamanho {

    @EmbeddedId
    private ProdutoTamanhoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produtoId")
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tamanhoId")
    @JoinColumn(name = "tamanho_id")
    private Tamanho tamanho;

    @Column(name = "qtd_estoque")
    private Integer qtdEstoque;

    public ProdutoTamanho() {}

    public ProdutoTamanho(Produto produto, Tamanho tamanho, Integer qtdEstoque) {
        this.produto = produto;
        this.tamanho = tamanho;
        this.qtdEstoque = qtdEstoque;
        this.id = new ProdutoTamanhoId(produto.getId(), tamanho.getId());
    }

    public ProdutoTamanhoId getId() { return id; }
    public void setId(ProdutoTamanhoId id) { this.id = id; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Tamanho getTamanho() { return tamanho; }
    public void setTamanho(Tamanho tamanho) { this.tamanho = tamanho; }

    public Integer getQtdEstoque() { return qtdEstoque; }
    public void setQtdEstoque(Integer qtdEstoque) { this.qtdEstoque = qtdEstoque; }
}
