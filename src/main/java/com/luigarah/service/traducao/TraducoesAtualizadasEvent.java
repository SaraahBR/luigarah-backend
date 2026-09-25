package com.luigarah.service.traducao;

/** Publicado quando as traduções de um produto são gravadas: as respostas em cache ficam desatualizadas. */
public record TraducoesAtualizadasEvent(Long produtoId) {}
