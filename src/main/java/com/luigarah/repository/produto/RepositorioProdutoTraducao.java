package com.luigarah.repository.produto;

import com.luigarah.model.produto.ProdutoTraducao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RepositorioProdutoTraducao extends JpaRepository<ProdutoTraducao, ProdutoTraducao.Id> {

    /** Traduções de vários produtos num idioma (uma consulta por resposta da API). */
    List<ProdutoTraducao> findByProdutoIdInAndIdioma(Collection<Long> produtoIds, String idioma);

    List<ProdutoTraducao> findByProdutoId(Long produtoId);
}
