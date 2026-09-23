-- ============================================================
-- V5 - Novos tamanhos no catalogo e distribuicao entre os produtos
--
-- Catalogo:
--   - roupas / usa : XXL e XXXL (o frontend trabalha com XXXS ate XXXL)
--   - sapatos / br : 30 e 31    (o frontend trabalha com 30 ate 46)
--
-- Distribuicao:
--   Cada tamanho novo e sorteado para os produtos que podem recebe-lo,
--   ou seja, mesma categoria e mesmo padrao do tamanho:
--     - XXL / XXXL -> somente roupas com padrao 'usa'
--     - 30 / 31    -> somente sapatos (padrao 'br')
--   Roupas BR nao recebem nada (o catalogo BR nao mudou) e bolsas ficam de
--   fora, porque nao aceitam padrao nem tamanhos.
--
--   O sorteio usa o md5 de "id do produto:etiqueta" em vez de random(),
--   para o resultado ser o mesmo em qualquer banco onde a migration rodar
--   (producao, local e testes). Cerca de 60% dos produtos elegiveis recebem
--   cada tamanho, com estoque entre 1 e 15 unidades.
-- ============================================================

INSERT INTO tamanhos (categoria, padrao, etiqueta, ordem) VALUES
    ('roupas',  'usa', 'XXL',  8),
    ('roupas',  'usa', 'XXXL', 9),
    ('sapatos', 'br',  '30',   30),
    ('sapatos', 'br',  '31',   31)
ON CONFLICT (categoria, padrao, etiqueta) DO NOTHING;

INSERT INTO produtos_tamanhos (produto_id, tamanho_id, qtd_estoque)
SELECT p.id, t.id, 1 + (sorteio.n % 15)
  FROM produtos p
  JOIN tamanhos t ON t.categoria = p.categoria
                 AND t.padrao    = p.padrao_tamanho
                 AND t.etiqueta IN ('XXL', 'XXXL', '30', '31')
 CROSS JOIN LATERAL (
       -- 7 primeiros hex do md5 -> inteiro positivo de 28 bits
       SELECT ('x' || substr(md5(p.id || ':' || t.etiqueta), 1, 7))::bit(28)::int AS n
 ) sorteio
 WHERE p.categoria IN ('roupas', 'sapatos')
   AND sorteio.n % 100 < 60
ON CONFLICT (produto_id, tamanho_id) DO NOTHING;
