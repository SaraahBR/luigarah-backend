package com.luigarah.repository.autenticacao;

import com.luigarah.model.usuario.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    List<Endereco> findByUsuarioIdOrderByPrincipalDescDataCriacaoDesc(Long usuarioId);

    Optional<Endereco> findByUsuarioIdAndPrincipalTrue(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
