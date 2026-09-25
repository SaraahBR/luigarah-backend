package com.luigarah.model.autenticacao;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6)
    private String codigo;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoToken tipo;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime expiraEm;

    @Column(nullable = false)
    private Boolean usado = false;

    @Column
    private LocalDateTime usadoEm;

    /** Códigos errados digitados para este token (migration V8). */
    @Column(nullable = false)
    @Builder.Default
    private Integer tentativas = 0;

    /** Depois de tantos erros o código deixa de valer (evita força bruta nos 6 dígitos). */
    public static final int MAX_TENTATIVAS = 5;

    public enum TipoToken {
        VERIFICACAO_EMAIL,
        RESET_SENHA
    }

    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(expiraEm);
    }

    public boolean isValido() {
        return !usado && !isExpirado() && !isBloqueado();
    }

    public boolean isBloqueado() {
        return tentativas != null && tentativas >= MAX_TENTATIVAS;
    }
}

