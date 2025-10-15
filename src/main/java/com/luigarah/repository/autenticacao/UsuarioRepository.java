package com.luigarah.repository.autenticacao;

import com.luigarah.model.autenticacao.AuthProvider;
import com.luigarah.model.autenticacao.Role;
import com.luigarah.model.autenticacao.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<Usuario> findByProviderAndProviderId(AuthProvider provider, String providerId);

    // Métodos para administração de usuários
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    List<Usuario> findByEmailContainingIgnoreCase(String email);

    List<Usuario> findByRole(Role role);

    List<Usuario> findByAtivo(Boolean ativo);

    long countByAtivo(Boolean ativo);

    long countByRole(Role role);
}
