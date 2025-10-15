package com.luigarah.model.carrinho;

import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.model.produto.Produto;
import com.luigarah.model.tamanho.Tamanho;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Item do carrinho de compras de um usuário.
 * Relaciona um produto com quantidade e tamanho selecionado.
 */
@Entity
@Table(name = "CARRINHO_ITENS", schema = "APP_LUIGARAH",
        uniqueConstraints = @UniqueConstraint(columnNames = {"USUARIO_ID", "PRODUTO_ID", "TAMANHO_ID"}))
@SequenceGenerator(
        name = "carrinho_itens_seq_gen",
        sequenceName = "APP_LUIGARAH.CARRINHO_ITENS_SEQ",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarrinhoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carrinho_itens_seq_gen")
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUTO_ID", nullable = false)
    private Produto produto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TAMANHO_ID")
    private Tamanho tamanho;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima é 1")
    @Max(value = 99, message = "Quantidade máxima é 99")
    @Column(name = "QUANTIDADE", nullable = false)
    private Integer quantidade;

    @CreationTimestamp
    @Column(name = "DATA_ADICAO", nullable = false, updatable = false)
    private LocalDateTime dataAdicao;

    @UpdateTimestamp
    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;
}
