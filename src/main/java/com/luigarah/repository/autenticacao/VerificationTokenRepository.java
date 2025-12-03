package com.luigarah.repository.autenticacao;

import com.luigarah.model.autenticacao.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * Busca token por código e tipo
     */
    Optional<VerificationToken> findByCodigoAndTipo(String codigo, VerificationToken.TipoToken tipo);

    /**
     * Busca todos os tokens de um email por tipo
     */
    List<VerificationToken> findByEmailAndTipo(String email, VerificationToken.TipoToken tipo);

    /**
     * Busca o token mais recente de um email por tipo
     */
    @Query("SELECT vt FROM VerificationToken vt WHERE vt.email = :email AND vt.tipo = :tipo ORDER BY vt.criadoEm DESC")
    Optional<VerificationToken> findLatestByEmailAndTipo(@Param("email") String email, @Param("tipo") VerificationToken.TipoToken tipo);

    /**
     * Busca todos os tokens de um email
     */
    List<VerificationToken> findByEmail(String email);

    /**
     * Deleta todos os tokens de um email por tipo
     */
    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.email = :email AND vt.tipo = :tipo")
    void deleteByEmailAndTipo(@Param("email") String email, @Param("tipo") VerificationToken.TipoToken tipo);

    /**
     * Deleta tokens expirados
     */
    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.expiraEm < :agora")
    void deleteExpiredTokens(@Param("agora") LocalDateTime agora);

    /**
     * Busca tokens expirados não usados
     */
    @Query("SELECT vt FROM VerificationToken vt WHERE vt.expiraEm < :agora AND vt.usado = false")
    List<VerificationToken> findExpiredTokens(@Param("agora") LocalDateTime agora);
}

