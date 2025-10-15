package com.luigarah.model.usuario;

import com.luigarah.model.autenticacao.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Endereço de entrega do usuário.
 * Um usuário pode ter múltiplos endereços.
 */
@Entity
@Table(name = "ENDERECOS", schema = "APP_LUIGARAH")
@SequenceGenerator(
        name = "enderecos_seq_gen",
        sequenceName = "APP_LUIGARAH.ENDERECOS_SEQ",
        allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enderecos_seq_gen")
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "País é obrigatório")
    @Size(max = 100, message = "País deve ter no máximo 100 caracteres")
    @Column(name = "PAIS", nullable = false, length = 100)
    private String pais;

    @NotBlank(message = "Estado é obrigatório")
    @Size(max = 100, message = "Estado deve ter no máximo 100 caracteres")
    @Column(name = "ESTADO", nullable = false, length = 100)
    private String estado;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Column(name = "CIDADE", nullable = false, length = 100)
    private String cidade;

    @NotBlank(message = "CEP é obrigatório")
    @Size(max = 20, message = "CEP deve ter no máximo 20 caracteres")
    @Column(name = "CEP", nullable = false, length = 20)
    private String cep;

    @Size(max = 200, message = "Bairro deve ter no máximo 200 caracteres")
    @Column(name = "BAIRRO", length = 200)
    private String bairro;

    @Size(max = 300, message = "Rua deve ter no máximo 300 caracteres")
    @Column(name = "RUA", length = 300)
    private String rua;

    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    @Column(name = "NUMERO", length = 20)
    private String numero;

    @Size(max = 200, message = "Complemento deve ter no máximo 200 caracteres")
    @Column(name = "COMPLEMENTO", length = 200)
    private String complemento;

    @Column(name = "PRINCIPAL", nullable = false)
    @Builder.Default
    private Boolean principal = false;

    @CreationTimestamp
    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;
}
