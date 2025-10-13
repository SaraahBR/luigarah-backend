package com.luigarah.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luigarah.dto.ProdutoCreateDTO;
import com.luigarah.dto.ProdutoDTO;
import com.luigarah.model.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {
    private static final ObjectMapper M = new ObjectMapper();

    @Autowired
    private IdentidadeMapper identidadeMapper;

    // Método estático mantido para compatibilidade com código existente
    public static Produto toEntity(ProdutoCreateDTO dto){
        Produto p = new Produto();
        p.setTitulo(dto.getTitulo());
        p.setSubtitulo(dto.getSubtitulo());
        p.setAutor(dto.getAutor());
        p.setDescricao(dto.getDescricao());
        p.setPreco(dto.getPreco());
        p.setDimensao(dto.getDimensao());
        p.setImagem(dto.getImagem());
        p.setImagemHover(dto.getImagemHover());
        p.setComposicao(dto.getComposicao());
        p.setCategoria(dto.getCategoria());

        try {
            if (dto.getImagens() != null)  p.setImagens(M.writeValueAsString(dto.getImagens()));   // JSON array
            if (dto.getDestaques() != null) p.setDestaques(M.writeValueAsString(dto.getDestaques())); // JSON array
            if (dto.getModelo() != null)   p.setModelo(M.writeValueAsString(dto.getModelo()));     // JSON object
        } catch (Exception e) {
            // fallback simples: deixa null se algo vier muito fora
        }
        return p;
    }

    // Método de instância para converter Produto para DTO
    public ProdutoDTO toDTO(Produto produto) {
        if (produto == null) {
            return null;
        }

        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setTitulo(produto.getTitulo());
        dto.setSubtitulo(produto.getSubtitulo());
        dto.setAutor(produto.getAutor());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setDimensao(produto.getDimensao());
        dto.setImagem(produto.getImagem());
        dto.setImagemHover(produto.getImagemHover());
        dto.setImagens(produto.getImagens());
        dto.setComposicao(produto.getComposicao());
        dto.setDestaques(produto.getDestaques());
        dto.setCategoria(produto.getCategoria());
        dto.setModelo(produto.getModelo());
        dto.setDataCriacao(produto.getDataCriacao());
        dto.setDataAtualizacao(produto.getDataAtualizacao());

        if (produto.getIdentidade() != null) {
            dto.setIdentidade(identidadeMapper.toDTO(produto.getIdentidade()));
        }

        return dto;
    }
}
