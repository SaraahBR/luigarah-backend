package com.luigarah.repository.autenticacao;

import com.luigarah.model.autenticacao.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthProviderRepository extends JpaRepository<OAuthProvider, Long> {

    /**
     * Busca um provider OAuth específico de um usuário.
     */
    Optional<OAuthProvider> findByUsuarioIdAndProvider(Long usuarioId, String provider);

    /**
     * Busca um provider OAuth pelo ID do provider.
     */
    Optional<OAuthProvider> findByProviderAndProviderId(String provider, String providerId);
}

