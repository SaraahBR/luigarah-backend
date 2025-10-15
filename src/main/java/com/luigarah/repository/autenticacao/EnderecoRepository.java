package com.luigarah.repository.autenticacao;

import com.luigarah.model.usuario.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    List<Endereco> findByUsuarioIdOrderByPrincipalDescDataCriacaoDesc(Long usuarioId);

    List<Endereco> findByUsuarioId(Long usuarioId);

    Optional<Endereco> findByUsuarioIdAndPrincipalTrue(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);

    @Modifying
    @Query("UPDATE Endereco e SET e.principal = false WHERE e.usuario.id = :usuarioId")
    void desmarcarTodosPrincipais(@Param("usuarioId") Long usuarioId);
}
