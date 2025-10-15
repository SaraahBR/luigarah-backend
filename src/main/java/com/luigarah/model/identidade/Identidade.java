package com.luigarah.model.identidade;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "IDENTIDADES", schema = "APP_LUIGARAH")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Identidade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "identidade_seq")
    @SequenceGenerator(name = "identidade_seq", sequenceName = "IDENTIDADE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "ORDEM")
    private Integer ordem;

    @Column(name = "ATIVO", length = 1)
    private String ativo;

    @CreationTimestamp
    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;
}
