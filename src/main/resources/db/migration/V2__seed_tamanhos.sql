ALTER SESSION SET CURRENT_SCHEMA = APP_LUIGARAH;

-- Roupas
MERGE INTO tamanhos t
    USING (SELECT 'roupas' categoria, col etiqueta FROM (
                                                            SELECT 'XXXS' col FROM dual UNION ALL SELECT 'XXS' FROM dual UNION ALL SELECT 'XS' FROM dual
                                                            UNION ALL SELECT 'S' FROM dual UNION ALL SELECT 'M' FROM dual UNION ALL SELECT 'L' FROM dual
                                                            UNION ALL SELECT 'XL' FROM dual
                                                        )) s
    ON (t.categoria=s.categoria AND t.etiqueta=s.etiqueta)
    WHEN NOT MATCHED THEN
        INSERT (categoria, etiqueta, ordem) VALUES (s.categoria, s.etiqueta,
                                                    CASE s.etiqueta WHEN 'XXXS' THEN 1 WHEN 'XXS' THEN 2 WHEN 'XS' THEN 3
                                                                    WHEN 'S' THEN 4 WHEN 'M' THEN 5 WHEN 'L' THEN 6 WHEN 'XL' THEN 7 END);

-- Sapatos 32..46
MERGE INTO tamanhos t
    USING (
        SELECT 'sapatos' categoria, TO_CHAR(level+31) etiqueta, (level+31) ordem
        FROM dual CONNECT BY level <= 15
    ) s
    ON (t.categoria=s.categoria AND t.etiqueta=s.etiqueta)
    WHEN NOT MATCHED THEN
        INSERT (categoria, etiqueta, ordem) VALUES (s.categoria, s.etiqueta, s.ordem);

