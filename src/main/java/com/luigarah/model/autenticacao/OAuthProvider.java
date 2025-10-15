package com.luigarah.model.autenticacao;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade para armazenar vinculações de provedores OAuth aos usuários.
 * Permite que um usuário tenha múltiplos provedores OAuth vinculados.
 */
@Entity
@Table(
    name = "OAUTH_PROVIDERS",
    schema = "APP_LUIGARAH",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"USUARIO_ID", "PROVIDER"})
    }
)
@SequenceGenerator(
    name = "oauth_providers_seq_gen",
    sequenceName = "APP_LUIGARAH.OAUTH_PROVIDERS_SEQ",
    allocationSize = 1
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuthProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oauth_providers_seq_gen")
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private Usuario usuario;

    @Column(name = "PROVIDER", nullable = false, length = 50)
    private String provider; // "google", "facebook", "github"

    @Column(name = "PROVIDER_ID", length = 255)
    private String providerId; // ID do usuário no provider

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}

