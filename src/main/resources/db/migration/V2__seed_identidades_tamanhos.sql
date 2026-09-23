-- ============================================================
-- V2 - Identidades e catalogo de tamanhos
-- Reconstruidos a partir da documentacao da API (o banco Oracle foi excluido).
-- Os IDs das identidades batem com IDENTIDADE_ID do backup de produtos.
-- ============================================================

INSERT INTO identidades (id, codigo, nome, ordem, ativo) VALUES
    (1, 'homem',    'Masculino', 1, 'S'),
    (2, 'mulher',   'Feminino',  2, 'S'),
    (3, 'unissex',  'Unissex',   3, 'S'),
    (4, 'infantil', 'Infantil',  4, 'S');

SELECT setval('identidade_seq', (SELECT max(id) FROM identidades));

-- Roupas - padrao BR
INSERT INTO tamanhos (categoria, padrao, etiqueta, ordem)
SELECT 'roupas', 'br', etiqueta, ordem
  FROM unnest(ARRAY['PP', 'P', 'M', 'G', 'XG', 'G1', 'G2']) WITH ORDINALITY AS t (etiqueta, ordem);

-- Roupas - padrao USA
INSERT INTO tamanhos (categoria, padrao, etiqueta, ordem)
SELECT 'roupas', 'usa', etiqueta, ordem
  FROM unnest(ARRAY['XXXS', 'XXS', 'XS', 'S', 'M', 'L', 'XL']) WITH ORDINALITY AS t (etiqueta, ordem);

-- Sapatos - numeracao BR 32..46 (todos os sapatos do backup usam padrao 'br')
INSERT INTO tamanhos (categoria, padrao, etiqueta, ordem)
SELECT 'sapatos', 'br', n::text, n
  FROM generate_series(32, 46) AS n;
