package com.luigarah.model.listadesejos;

import com.luigarah.model.autenticacao.Usuario;
import com.luigarah.model.produto.Produto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Item da lista de desejos de um usuário.
 * Relaciona produtos que o usuário tem interesse.
 */
@Entity
@Table(name = "LISTA_DESEJO_ITENS", schema = "APP_LUIGARAH",
        uniqueConstraints = @UniqueConstraint(columnNames = {"USUARIO_ID", "PRODUTO_ID"}))
@SequenceGenerator(
        name = "lista_desejo_itens_seq_gen",
        sequenceName = "APP_LUIGARAH.LISTA_DESEJO_ITENS_SEQ",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaDesejoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lista_desejo_itens_seq_gen")
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUTO_ID", nullable = false)
    private Produto produto;

    @CreationTimestamp
    @Column(name = "DATA_ADICAO", nullable = false, updatable = false)
    private LocalDateTime dataAdicao;
}
