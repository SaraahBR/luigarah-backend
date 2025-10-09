/* V3__data.sql - dados iniciais (compatível com Flyway via JDBC) */

ALTER SESSION SET CURRENT_SCHEMA = APP_LUIGARAH;
ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS';
ALTER SESSION SET NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS';
ALTER SESSION SET NLS_TIMESTAMP_TZ_FORMAT = 'YYYY-MM-DD"T"HH24:MI:SSTZH:TZM';
ALTER SESSION SET NLS_NUMERIC_CHARACTERS = '.,';

-- NOTA:
-- * Para reduzir tamanho e evitar sobrecarga, este script mantém apenas 4 itens
-- por categoria (bolsas, roupas e sapatos), usando MERGE (UPSERT) idempotente.
-- * ALTER SESSION fica fora do bloco PL/SQL; os MERGEs rodam dentro de uma
-- transação única. Em caso de erro, faz ROLLBACK e repropaga.
BEGIN
    -- BOLSAS (mantidos 4 itens; ids fixos 1..12 eram do ambiente local)

-- id: 1 - Gucci / Tiracolo
    MERGE INTO produtos p
    USING (
        SELECT
            1 AS id,
            'Gucci' AS titulo,'Tiracolo' AS subtitulo,'Demna Gvasalia' AS autor,'Bolsa tiracolo Dionysus mini' AS descricao,9399 AS preco,'Média' AS dimensao,
            'https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458138_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458229_2048.jpg
            ' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58501117_2048.jpg","https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458220_2048.jpg","https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458193_2048.jpg
            "]' AS imagens,
            'Exterior: camurça 100%, canvas 100%' AS composicao,
            '["bege","canvas GG Supreme","recortes de camurça","placa Dionysus frontal","fechamento por aba dobrável com botão de pressão","alça de ombro removível em corrente","compartimento principal","bolso interno com lapela","patch interno de logo","completamente forrado","ferragem prateada","produzido em: Itália","Este item é enviado com uma bolsa protetora."]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Gucci' AND p.subtitulo='Tiracolo' AND p.categoria='bolsas')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor = s.autor,
                                 p.descricao = s.descricao,
                                 p.preco = s.preco,
                                 p.dimensao = s.dimensao,
                                 p.imagem = s.imagem,
                                 p.imagem_hover = s.imagem_hover,
                                 p.imagens = s.imagens,
                                 p.composicao = s.composicao,
                                 p.destaques = s.destaques,
                                 p.data_atualizacao = SYSTIMESTAMP
    WHEN NOT MATCHED THEN INSERT
                          (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
                          VALUES
                              (s.id, s.titulo, s.subtitulo, s.autor, s.descricao, s.preco, s.dimensao, s.imagem, s.imagem_hover, s.imagens, s.composicao, s.destaques, s.categoria, SYSTIMESTAMP, SYSTIMESTAMP);

-- id: 2 - Saint Laurent / Transversal
    MERGE INTO produtos p
    USING (
        SELECT
            2 AS id,
            'Saint Laurent' AS titulo,'Transversal' AS subtitulo,'Yves Saint Laurent' AS autor,'Bolsa transversal com logo' AS descricao,10911 AS preco,'Média' AS dimensao,
            'https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720140_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720161_2048.jpg
            ' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720143_2048.jpg","https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720142_2048.jpg","https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_53679149_2048.jpg
            "]' AS imagens,
            'Couro 100%' AS composicao,
            '["Preto","couro","placa de logo","ferragem dourada","matelassê chevron","parte superior dobrável","compartimento principal","alça de ombro de corrente","produzido em: Itália"]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Saint Laurent' AND p.subtitulo='Transversal' AND p.categoria='bolsas')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
                                 p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques,
                                 p.data_atualizacao=SYSTIMESTAMP
    WHEN NOT MATCHED THEN INSERT
                          (id,titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,data_criacao,data_atualizacao)
                          VALUES
                              (s.id,s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,SYSTIMESTAMP,SYSTIMESTAMP);

-- id: 4 - DeMellier / Tote
    MERGE INTO produtos p
    USING (
        SELECT
            4 AS id,
            'DeMellier' AS titulo,'Tote' AS subtitulo,'Mireia Llusia-Lindh' AS autor,'Bolsa tote The New York efeito pele de crocodilo' AS descricao,5216 AS preco,'Grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398188_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398271_2048.jpg
            ' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398227_2048.jpg","https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398117_2048.jpg","https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398251_2048.jpg
            "]' AS imagens,
            'Exterior: camurça 100%' AS composicao,
            '["marrom cedro","ferragem dourada","alça de mão dupla arredondada","fecho magnético oculto","fechamento lateral por botões de pressão","compartimento principal","bolso interno com zíper","pé de metal","produzido em: Itália"]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (p.titulo='DeMellier' AND p.subtitulo='Tote' AND p.categoria='bolsas')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
                                 p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques,
                                 p.data_atualizacao=SYSTIMESTAMP
    WHEN NOT MATCHED THEN INSERT
                          (id,titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,data_criacao,data_atualizacao)
                          VALUES
                              (s.id,s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,SYSTIMESTAMP,SYSTIMESTAMP);

-- id: 10 - Prada / Bucket
    MERGE INTO produtos p
    USING (
        SELECT
            10 AS id,
            'Prada' AS titulo,'Bucket' AS subtitulo,'Miuccia Prada' AS autor,'Bolsa bucket Buckle' AS descricao,38500 AS preco,'Grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124720_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124722_2048.jpg
            ' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124727_2048.jpg","https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124718_2048.jpg","https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124719_2048.jpg
            "]' AS imagens,
            'Exterior: couro de bezerro 100%' AS composicao,
            '["preto","fechamento por fivelas","ombro único","bolso interno","Este item é enviado com uma bolsa protetora."]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Prada' AND p.subtitulo='Bucket' AND p.categoria='bolsas')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
                                 p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques,
                                 p.data_atualizacao=SYSTIMESTAMP
    WHEN NOT MATCHED THEN INSERT
                          (id,titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,data_criacao,data_atualizacao)
                          VALUES
                              (s.id,s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,SYSTIMESTAMP,SYSTIMESTAMP);

    -- ROUPAS (mantidos 4 itens)

-- MARANT ÉTOILE / Saia
    MERGE INTO produtos p
    USING (
        SELECT
            'MARANT ÉTOILE' AS titulo,'Saia' AS subtitulo,'Isabel Marant' AS autor,'Saia Berenicia' AS descricao,10966 AS preco,'pequeno' AS dimensao,
            'https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923679_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923678_2048.jpg
            ' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923690_2048.jpg","https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923669_2048.jpg","https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923682_2048.jpg
"]]' AS imagens,
            'Tecido 100%' AS composicao,
            q'[["vermelho escuro","fechamento lateral oculto","detalhe drapeado","bainha assimétrica","estampa de padronagem abstrata"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":179,"busto_cm":84,"cintura_cm":64,"quadril_cm":90,"veste":"38"}]' AS modelo
        FROM dual
    ) s
    ON (p.titulo='MARANT ÉTOILE' AND p.subtitulo='Saia' AND p.categoria='roupas')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
                                 p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques, p.modelo=s.modelo
    WHEN NOT MATCHED THEN INSERT
                          (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,modelo)
                          VALUES
                              (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,s.modelo);

-- Maria Lucia Hohan / Vestido (Noor de seda)
    MERGE INTO produtos p
    USING (
        SELECT
            'Maria Lucia Hohan' AS titulo,'Vestido' AS subtitulo,'Maria Lucia Hohan' AS autor,'Vestido Noor de seda' AS descricao,20753 AS preco,'grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131249_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131148_2048.jpg
            ' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131193_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131276_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131163_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131233_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131166_2048.jpg
"]]' AS imagens,
            'Seda 100%' AS composicao,
            q'[["vermelho","fechamento posterior por tiras fixadoras","fechamento posterior por zíper","gola V","fenda frontal"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":177,"busto_cm":86,"cintura_cm":62,"quadril_cm":90,"veste":"M"}]' AS modelo
        FROM dual
    ) s
    ON (p.titulo='Maria Lucia Hohan' AND p.subtitulo='Vestido' AND p.categoria='roupas' AND p.descricao LIKE 'Vestido Noor%')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor=s.autor, p.preco=s.preco, p.dimensao=s.dimensao,
                                 p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques, p.modelo=s.modelo
    WHEN NOT MATCHED THEN INSERT
                          (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,modelo)
                          VALUES
                              (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,s.modelo);

-- Casa Blanca / Vestido (gola polo)
    MERGE INTO produtos p
    USING (
        SELECT
            'Casa Blanca' AS titulo,'Vestido' AS subtitulo,'Charaf Tajer' AS autor,'Vestido canelado com gola polo' AS descricao,6588 AS preco,'médio' AS dimensao,
            'https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60249561_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60250342_2048.jpg
            ' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60249561_2048.jpg","https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60250342_2048.jpg","https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60249561_2048.jpg
"]]' AS imagens,
            'Viscose 100%' AS composicao,
            q'[["preto","mangas curtas","textura canelada","gola V","acabamento em verde claro","logo pequeno"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":181,"busto_cm":78,"cintura_cm":62,"quadril_cm":81,"veste":"S"}]' AS modelo
        FROM dual
    ) s
    ON (p.titulo='Casa Blanca' AND p.subtitulo='Vestido' AND p.categoria='roupas')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
                                 p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques, p.modelo=s.modelo
    WHEN NOT MATCHED THEN INSERT
                          (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,modelo)
                          VALUES
                              (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,s.modelo);

-- Prada / Desfile (Blazer camurça)
    MERGE INTO produtos p
    USING (
        SELECT
            'Prada' AS titulo,'Desfile' AS subtitulo,'Miuccia Prada' AS autor,'Blazer de camurça com abotoamento simples' AS descricao,56000 AS preco,'grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388489_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388450_2048.jpg
            ' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388443_2048.jpg","https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388442_2048.jpg","https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388472_2048.jpg","https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388477_2048.jpg
"]]' AS imagens,
            'camurça 100%' AS composicao,
            q'[["rosa","camurça","patch de logo posterior","lapelas","fechamento frontal por botões","mangas longas","dois bolsos laterais com lapelas","barra reta","completamente forrado"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":175,"busto_cm":83,"cintura_cm":62,"quadril_cm":90,"veste":"ÚNICO"}]' AS modelo
        FROM dual
    ) s
    ON (p.titulo='Prada' AND p.subtitulo='Desfile' AND p.categoria='roupas')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
                                 p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques, p.modelo=s.modelo
    WHEN NOT MATCHED THEN INSERT
                          (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,modelo)
                          VALUES
                              (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,s.modelo);

    -- SAPATOS (mantidos 4 itens)

-- Christian Louboutin / Scarpin Kate 120
    MERGE INTO produtos p
    USING (
        SELECT
            'Christian Louboutin' AS titulo,'Scarpin' AS subtitulo,'Christian Louboutin' AS autor,'Scarpin Kate 120' AS descricao,8969 AS preco,'Pequeno' AS dimensao,
            'https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192293_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192296_2048.jpg
            ' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192298_2048.jpg","https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192295_2048.jpg
"]]' AS imagens,
            'Forro: couro 100% | Exterior: camurça 100% | Solado: couro 100%' AS composicao,
            q'[["bico fino","signature Louboutin red lacquered sole","salto agulha alto 120mm","produzido em: Itália"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Christian Louboutin' AND p.subtitulo='Scarpin' AND p.categoria='sapatos' AND p.descricao LIKE 'Scarpin Kate%')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor=s.autor, p.preco=s.preco, p.dimensao=s.dimensao,
                                 p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques
    WHEN NOT MATCHED THEN INSERT
                          (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria)
                          VALUES
                              (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria);

-- Balenciaga / Scarpin Monday 140mm
    MERGE INTO produtos p
    USING (
        SELECT
            'Balenciaga' AS titulo,'Scarpin' AS subtitulo,'Pierpaolo Piccioli' AS autor,'Scarpin Monday de couro com salto 140mm' AS descricao,10788 AS preco,'Pequeno' AS dimensao,
            'https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60310370_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60348565_2048.jpg
            ' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60310388_2048.jpg","https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60348573_2048.jpg
"]]' AS imagens,
            'Exterior: couro de bezerro 100%, Tecido 100% | Forro: Tecido 100% | Solado: borracha 100%' AS composicao,
            q'[["salto 140mm","preto","couro","fechamento por amarração","solado esculpido","detalhe de logo"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Balenciaga' AND p.subtitulo='Scarpin' AND p.categoria='sapatos')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
                                 p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques
    WHEN NOT MATCHED THEN INSERT
                          (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria)
                          VALUES
                              (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria);

-- Versace / Bota couro salto alto
    MERGE INTO produtos p
    USING (
        SELECT
            'Versace' AS titulo,'Bota' AS subtitulo,'Donatella Versace' AS autor,'Bota de couro com salto alto' AS descricao,13385 AS preco,'Grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36772936_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36781973_2048.jpg
            ' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36780941_2048.jpg","https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36862601_2048.jpg
"]]' AS imagens,
            'Exterior: couro 100% | Forro: couro 100% | Solado: couro 100%' AS composicao,
            q'[["Preto","bico quadrado","fechamento por zíper lateral","cano alto","plataforma e salto alto bloco","couro"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Versace' AND p.subtitulo='Bota' AND p.categoria='sapatos')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
                                 p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques
    WHEN NOT MATCHED THEN INSERT
                          (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria)
                          VALUES
                              (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria);

-- Prada / Mocassim Chocolate
    MERGE INTO produtos p
    USING (
        SELECT
            'Prada' AS titulo,'Mocassim' AS subtitulo,'Miuccia Prada' AS autor,'Mocassim Chocolate de couro' AS descricao,8200 AS preco,'Médio' AS dimensao,
            'https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824339_2048.jpg
            ' AS imagem,
            'https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824340_2048.jpg
            ' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824337_2048.jpg","https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824336_2048.jpg","https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824334_2048.jpg
"]]' AS imagens,
            'Couro 100%' AS composicao,
            q'[["preto","couro","acabamento escovado","logo triangular esmaltado","bico arredondado","palmilha de couro com logo","solado flat em couro"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Prada' AND p.subtitulo='Mocassim' AND p.categoria='sapatos')
    WHEN MATCHED THEN UPDATE SET
                                 p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
                                 p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques
    WHEN NOT MATCHED THEN INSERT
                          (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria)
                          VALUES
                              (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria);

-- Fim dos MERGE

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/