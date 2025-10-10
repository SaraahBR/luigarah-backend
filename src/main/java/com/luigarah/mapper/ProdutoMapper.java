package com.luigarah.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luigarah.dto.ProdutoCreateDTO;
import com.luigarah.model.Produto;

public final class ProdutoMapper {
    private static final ObjectMapper M = new ObjectMapper();
    private ProdutoMapper(){}

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
}
