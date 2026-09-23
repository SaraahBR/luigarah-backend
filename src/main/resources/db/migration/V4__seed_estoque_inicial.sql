-- ============================================================
-- V4 - Estoque inicial
-- O estoque original nao estava no backup. Cada produto recebe 10 unidades
-- (mesmo valor padrao usado por ServicoTamanhoImpl ao vincular tamanhos):
--   - roupas/sapatos: todos os tamanhos do catalogo da sua categoria + padrao
--   - bolsas: estoque consolidado em produtos_estoque
-- Ajuste depois pelos endpoints de estoque do painel admin.
-- ============================================================

INSERT INTO produtos_tamanhos (produto_id, tamanho_id, qtd_estoque)
SELECT p.id, t.id, 10
  FROM produtos p
  JOIN tamanhos t ON t.categoria = p.categoria
                 AND t.padrao    = p.padrao_tamanho
 WHERE p.categoria IN ('roupas', 'sapatos');

INSERT INTO produtos_estoque (produto_id, qtd_estoque)
SELECT p.id, 10
  FROM produtos p
 WHERE p.categoria = 'bolsas';
