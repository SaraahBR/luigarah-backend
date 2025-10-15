package com.luigarah.repository.identidade;

import com.luigarah.model.identidade.Identidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioIdentidade extends JpaRepository<Identidade, Long> {

    Optional<Identidade> findByCodigo(String codigo);

    List<Identidade> findByAtivoOrderByOrdemAsc(String ativo);

    List<Identidade> findAllByOrderByOrdemAsc();
}
