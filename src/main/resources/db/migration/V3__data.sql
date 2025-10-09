/* V3__data.sql - dados iniciais (compatível com Flyway via JDBC) */
ALTER SESSION SET CURRENT_SCHEMA = APP_LUIGARAH;

-------------------------------------------------------------------------------
-- BOLSAS (ids 1..12 vieram do ambiente local; mantidos, mas protegidos por UPSERT)
-------------------------------------------------------------------------------

-- id: 1 - Gucci / Tiracolo
MERGE INTO produtos p
    USING (
        SELECT
            1 AS id,
            'Gucci' AS titulo,'Tiracolo' AS subtitulo,'Demna Gvasalia' AS autor,'Bolsa tiracolo Dionysus mini' AS descricao,9399 AS preco,'Média' AS dimensao,
            'https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458138_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458229_2048.jpg' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58501117_2048.jpg","https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458220_2048.jpg","https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458193_2048.jpg"]' AS imagens,
            'Exterior: camurça 100%, canvas 100%' AS composicao,
            '["bege","canvas GG Supreme","recortes de camurça","placa Dionysus frontal","fechamento por aba dobrável com botão de pressão","alça de ombro removível em corrente","compartimento principal","bolso interno com lapela","patch interno de logo","completamente forrado","ferragem prateada","produzido em: Itália","Este item é enviado com uma bolsa protetora."]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (
        p.titulo='Gucci' AND p.subtitulo='Tiracolo' AND p.categoria='bolsas'
        )
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
            'https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720140_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720161_2048.jpg' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720143_2048.jpg","https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720142_2048.jpg","https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_53679149_2048.jpg"]' AS imagens,
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

-- id: 3 - Zadig&Voltaire / Ombro
MERGE INTO produtos p
    USING (
        SELECT
            3 AS id,
            'Zadig&Voltaire' AS titulo,'Ombro' AS subtitulo,'Cecilia Bönström' AS autor,'Bolsa de ombro em couro' AS descricao,3239 AS preco,'Média' AS dimensao,
            'https://cdn-images.farfetch-contents.com/30/94/59/07/30945907_59858416_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/30/94/59/07/30945907_59874112_2048.jpg' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/30/94/59/07/30945907_59858424_2048.jpg","https://cdn-images.farfetch-contents.com/30/94/59/07/30945907_59858428_2048.jpg"]' AS imagens,
            'Exterior: couro de bezerro 100%' AS composicao,
            '["preto","parte superior dobrável","corrente de elos","placa de logo","alça de ombro ajustável e removível","bolso posterior com zíper"]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Zadig&Voltaire' AND p.subtitulo='Ombro' AND p.categoria='bolsas')
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
            'https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398188_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398271_2048.jpg' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398227_2048.jpg","https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398117_2048.jpg","https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398251_2048.jpg"]' AS imagens,
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

-- id: 5 - The Attico / Tote
MERGE INTO produtos p
    USING (
        SELECT
            5 AS id,
            'The Attico' AS titulo,'Tote' AS subtitulo,'Gilda Ambrosio & Giorgia Tordin' AS autor,'Bolsa tote La Passeggiata pequena' AS descricao,13471 AS preco,'Média' AS dimensao,
            'https://cdn-images.farfetch-contents.com/28/45/94/25/28459425_59683498_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/28/45/94/25/28459425_59683458_2048.jpg' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/27/09/49/25/27094925_58366280_2048.jpg","https://cdn-images.farfetch-contents.com/27/09/49/25/27094925_58366277_2048.jpg","https://cdn-images.farfetch-contents.com/27/09/49/25/27094925_58366240_2048.jpg"]' AS imagens,
            'Exterior: couro de bezerro 100% | Forro: camurça 100%' AS composicao,
            '["preto","fechamento por zíper na parte superior","alça de mão dupla arredondada","pé de metal","produzido em: Itália","Este item é enviado com uma bolsa protetora."]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (p.titulo='The Attico' AND p.subtitulo='Tote' AND p.categoria='bolsas')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques,
        p.data_atualizacao=SYSTIMESTAMP
    WHEN NOT MATCHED THEN INSERT
        (id,titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,data_criacao,data_atualizacao)
        VALUES
            (s.id,s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,SYSTIMESTAMP,SYSTIMESTAMP);

-- id: 6 - Rabanne / Transversal
MERGE INTO produtos p
    USING (
        SELECT
            6 AS id,
            'Rabanne' AS titulo,'Transversal' AS subtitulo,'Paco Rabanne' AS autor,'Necessaire Paco 1969' AS descricao,13345 AS preco,'Média' AS dimensao,
            'https://cdn-images.farfetch-contents.com/28/39/76/93/28397693_59339333_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/28/39/76/93/28397693_59362288_2048.jpg' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/28/39/76/93/28397693_59339196_2048.jpg","https://cdn-images.farfetch-contents.com/28/39/76/93/28397693_59339036_2048.jpg","https://cdn-images.farfetch-contents.com/28/39/76/93/28397693_59339380_2048.jpg"]' AS imagens,
            'Exterior: metal 100%' AS composicao,
            '["prateado","design de mesh","acabamento polido","design assinatura 1969"]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Rabanne' AND p.subtitulo='Transversal' AND p.categoria='bolsas')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques,
        p.data_atualizacao=SYSTIMESTAMP
    WHEN NOT MATCHED THEN INSERT
        (id,titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,data_criacao,data_atualizacao)
        VALUES
            (s.id,s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,SYSTIMESTAMP,SYSTIMESTAMP);

-- id: 7 - KHAITE / Tote
MERGE INTO produtos p
    USING (
        SELECT
            7 AS id,
            'KHAITE' AS titulo,'Tote' AS subtitulo,'Catherine Holstein' AS autor,'Bolsa tote Lotus pequena' AS descricao,22838 AS preco,'Grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/26/74/51/75/26745175_56439516_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/26/74/51/75/26745175_56439527_2048.jpg' AS imagem_hover,
            NULL AS imagens,
            'Exterior: couro de bezerro 100%' AS composicao,
            '["preto","couro","textura granulada","fechamento por alamares","alça de mão única","estampa de logo na frente","compartimento principal","completamente forrado","produzido em: Itália","Este item é enviado com uma bolsa protetora."]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (p.titulo='KHAITE' AND p.subtitulo='Tote' AND p.categoria='bolsas')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques,
        p.data_atualizacao=SYSTIMESTAMP
    WHEN NOT MATCHED THEN INSERT
        (id,titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,data_criacao,data_atualizacao)
        VALUES
            (s.id,s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,SYSTIMESTAMP,SYSTIMESTAMP);

-- id: 8 - ZIMMERMANN / Tote
MERGE INTO produtos p
    USING (
        SELECT
            8 AS id,
            'ZIMMERMANN' AS titulo,'Tote' AS subtitulo,'Nicky Zimmermann' AS autor,'Bolsa tote Goldentime' AS descricao,7180 AS preco,'Grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59409670_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59356213_2048.jpg' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59338797_2048.jpg","https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59338176_2048.jpg","https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59351338_2048.jpg","https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59348089_2048.jpg"]' AS imagens,
            'Ráfia 100%' AS composicao,
            '["natural","detalhe de franjas","compartimento interior espaçoso","alça de mão","alça de ombro ajustável","design aberto"]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (p.titulo='ZIMMERMANN' AND p.subtitulo='Tote' AND p.categoria='bolsas')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques,
        p.data_atualizacao=SYSTIMESTAMP
    WHEN NOT MATCHED THEN INSERT
        (id,titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,data_criacao,data_atualizacao)
        VALUES
            (s.id,s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,SYSTIMESTAMP,SYSTIMESTAMP);

-- id: 9 - Dolce & Gabbana / Tote
MERGE INTO produtos p
    USING (
        SELECT
            9 AS id,
            'Dolce & Gabbana' AS titulo,'Tote' AS subtitulo,'Domenico Dolce & Stefano Gabbana' AS autor,'Bolsa tote Dolce Box com aplicação floral' AS descricao,79250 AS preco,'Pequena' AS dimensao,
            'https://cdn-images.farfetch-contents.com/22/05/29/42/22052942_52044094_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/22/05/29/42/22052942_52044096_2048.jpg' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/22/05/29/42/22052942_52044107_2048.jpg","https://cdn-images.farfetch-contents.com/22/05/29/42/22052942_52044091_2048.jpg"]' AS imagens,
            'Exterior: Seda 50%, Viscose 35%, Vidro 10%, couro de bezerro 5% | Forro: couro de bezerro 95%, Contraplacado em choupo 5%' AS composicao,
            '["vermelho sangue/multicolorido","couro","aplicação floral","acabamento dourado","ferragem dourada","fechamento por alça com fecho giratório","alça de ombro ajustável e removível","alça de mão única","compartimento principal","compartimento interno para cartão","placa de logo interna"]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Dolce & Gabbana' AND p.subtitulo='Tote' AND p.categoria='bolsas')
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
            'https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124720_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124722_2048.jpg' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124727_2048.jpg","https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124718_2048.jpg","https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124719_2048.jpg"]' AS imagens,
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

-- id: 11 - Dolce & Gabbana / Tote (floral)
MERGE INTO produtos p
    USING (
        SELECT
            11 AS id,
            'Dolce & Gabbana' AS titulo,'Tote' AS subtitulo,'Domenico Dolce & Stefano Gabbana' AS autor,'Bolsa tote com estampa floral' AS descricao,79250 AS preco,'Média' AS dimensao,
            'https://cdn-images.farfetch-contents.com/15/74/83/45/15748345_32187784_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/15/74/83/45/15748345_32187788_2048.jpg' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/15/74/83/45/15748345_32187785_2048.jpg","https://cdn-images.farfetch-contents.com/15/74/83/45/15748345_32187786_2048.jpg"]' AS imagens,
            'Exterior: PLA 90%, couro de bezerro 10% | Forro: couro de bezerro 100%' AS composicao,
            '["Alça de mão única","alça de ombro ajustável e removível","fechamento por cadeado","compartimento principal","compartimento interno para cartão","estampa floral","produzido em: Itália"]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (
        p.titulo='Dolce & Gabbana' AND p.subtitulo='Tote' AND p.categoria='bolsas' AND p.descricao LIKE 'Bolsa tote com estampa floral%'
        )
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques,
        p.data_atualizacao=SYSTIMESTAMP
    WHEN NOT MATCHED THEN INSERT
        (id,titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,data_criacao,data_atualizacao)
        VALUES
            (s.id,s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,SYSTIMESTAMP,SYSTIMESTAMP);

-- id: 12 - Prada / Desfile (Galleria mini)
MERGE INTO produtos p
    USING (
        SELECT
            12 AS id,
            'Prada' AS titulo,'Desfile' AS subtitulo,'Miuccia Prada' AS autor,'Bolsa Galleria mini' AS descricao,45000 AS preco,'Média' AS dimensao,
            'https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54334086_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54302413_2048.jpg' AS imagem_hover,
            '["https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54302418_2048.jpg","https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54302415_2048.jpg","https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54302404_2048.jpg","https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54302403_2048.jpg"]' AS imagens,
            'Exterior: couro 100%, couro envernizado 100% | Forro: couro nappa 100%' AS composicao,
            '["preto","couro","acabamento envernizado","alça de ombro ajustável e removível","detalhe de acabamento de plumas","porta-chaves","logo triangular","fecho deslizante","bolso interno com zíper"]' AS destaques,
            'bolsas' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Prada' AND p.subtitulo='Desfile' AND p.categoria='bolsas')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques,
        p.data_atualizacao=SYSTIMESTAMP
    WHEN NOT MATCHED THEN INSERT
        (id,titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,data_criacao,data_atualizacao)
        VALUES
            (s.id,s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,SYSTIMESTAMP,SYSTIMESTAMP);

-------------------------------------------------------------------------------
-- ROUPAS
-------------------------------------------------------------------------------

-- MARANT ÉTOILE / Saia
MERGE INTO produtos p
    USING (
        SELECT
            'MARANT ÉTOILE' AS titulo,'Saia' AS subtitulo,'Isabel Marant' AS autor,'Saia Berenicia' AS descricao,10966 AS preco,'pequeno' AS dimensao,
            'https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923679_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923678_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923690_2048.jpg","https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923669_2048.jpg","https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923682_2048.jpg"]]' AS imagens,
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
            'https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131249_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131148_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131193_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131276_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131163_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131233_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131166_2048.jpg"]]' AS imagens,
            'Seda 100%' AS composicao,
            q'[["vermelho","fechamento posterior por tiras fixadoras","fechamento posterior por zíper","gola V","fenda frontal"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":177,"busto_cm":86,"cintura_cm":62,"quadril_cm":90,"veste":"M"}]' AS modelo
        FROM dual
    ) s
    ON (
        p.titulo='Maria Lucia Hohan' AND p.subtitulo='Vestido' AND p.categoria='roupas' AND p.descricao LIKE 'Vestido Noor%'
        )
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques, p.modelo=s.modelo
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,modelo)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,s.modelo);

-- MARANT ÉTOILE / Colete
MERGE INTO produtos p
    USING (
        SELECT
            'MARANT ÉTOILE' AS titulo,'Colete' AS subtitulo,'Isabel Marant' AS autor,'Colete Clemencia de pelos com bordado' AS descricao,2757 AS preco,'pequeno' AS dimensao,
            'https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221705_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221811_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221705_2048.jpg","https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221811_2048.jpg","https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221705_2048.jpg","https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221811_2048.jpg"]]' AS imagens,
            'Exterior: Seda 100%, camurça 100%, Poliuretano 100% | Forro: algodão 100%, Poliéster 100%' AS composicao,
            q'[["bege taupe","acabamento em pelo","detalhes bordados","modelagem sem mangas","fechamento por botão","padronagem geométrica"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":181,"busto_cm":82,"cintura_cm":63,"quadril_cm":89,"veste":"M"}]' AS modelo
        FROM dual
    ) s
    ON (p.titulo='MARANT ÉTOILE' AND p.subtitulo='Colete' AND p.categoria='roupas')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
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
            'https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60249561_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60250342_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60249561_2048.jpg","https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60250342_2048.jpg","https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60249561_2048.jpg"]]' AS imagens,
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

-- Maria Lucia Hohan / Vestido (de festa sem alças)
MERGE INTO produtos p
    USING (
        SELECT
            'Maria Lucia Hohan' AS titulo,'Vestido' AS subtitulo,'Maria Lucia Hohan' AS autor,'Vestido de festa sem alças' AS descricao,25945 AS preco,'grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819593_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819592_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819589_2048.jpg","https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819585_2048.jpg","https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819586_2048.jpg","https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819583_2048.jpg","https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819581_2048.jpg"]]' AS imagens,
            'Tecido 100%' AS composicao,
            q'[["platina","plissado","decote coração","modelagem sem mangas","fenda lateral","fechamento posterior por amarração","comprimento longo"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":179,"busto_cm":86,"cintura_cm":60,"quadril_cm":90,"veste":"M"}]' AS modelo
        FROM dual
    ) s
    ON (
        p.titulo='Maria Lucia Hohan' AND p.subtitulo='Vestido' AND p.categoria='roupas' AND p.descricao LIKE 'Vestido de festa%'
        )
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques, p.modelo=s.modelo
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,modelo)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,s.modelo);

-- Missoni / Vestido longo sem mangas com paetês
MERGE INTO produtos p
    USING (
        SELECT
            'Missoni' AS titulo,'Vestido' AS subtitulo,'Ottavio & Rosita Missoni' AS autor,'Vestido longo sem mangas com paetês' AS descricao,23271 AS preco,'grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/31/49/64/78/31496478_60929181_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/31/49/64/78/31496478_60933522_2048.jpg' AS imagem_hover,
            NULL AS imagens,
            'Poliéster 88%, Elastano 12%' AS composicao,
            q'[["preto","semitranslúcido","aplicação de paetês","padronagem de listra","modelagem sem mangas","decote canoa","O item recebido pode diferir levemente da foto. Isso se deve à natureza artesanal com que todos os produtos Missoni são confeccionados.","Esta peça possui detalhes delicados. Manuseie com muito cuidado para evitar danos, como penas quebradas ou perda de paetês."]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":176,"busto_cm":83,"cintura_cm":63,"quadril_cm":91,"veste":"S"}]' AS modelo
        FROM dual
    ) s
    ON (p.titulo='Missoni' AND p.subtitulo='Vestido' AND p.categoria='roupas')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques, p.modelo=s.modelo
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,modelo)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,s.modelo);

-- NISSA / Vestido longo com design pelerine
MERGE INTO produtos p
    USING (
        SELECT
            'NISSA' AS titulo,'Vestido' AS subtitulo,'NISSA' AS autor,'Vestido longo com design pelerine' AS descricao,10414 AS preco,'grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/30/53/95/75/30539575_59549520_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/30/53/95/75/30539575_59549552_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099981_2048.jpg","https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099980_2048.jpg","https://cdn-images.farfetch-contents.com/30/53/95/75/30539575_59549527_2048.jpg"]]' AS imagens,
            'Forro: Viscose 100% | Exterior: Poliéster 100%' AS composicao,
            q'[["bege","fechamento posterior por zíper","aplicações de cristais","cós elástico","saia com pregas"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":175,"busto_cm":80,"cintura_cm":61,"quadril_cm":88,"veste":"36"}]' AS modelo
        FROM dual
    ) s
    ON (p.titulo='NISSA' AND p.subtitulo='Vestido' AND p.categoria='roupas')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques, p.modelo=s.modelo
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,modelo)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,s.modelo);

-- Karl Lagerfeld / Vestido polo mangas longas
MERGE INTO produtos p
    USING (
        SELECT
            'Karl Lagerfeld' AS titulo,'Vestido' AS subtitulo,'Karl Lagerfeld' AS autor,'Vestido polo mangas longas' AS descricao,3274 AS preco,'médio' AS dimensao,
            'https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099999_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099996_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099981_2048.jpg","https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099980_2048.jpg","https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099979_2048.jpg","https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099975_2048.jpg"]]' AS imagens,
            'Poliéster 95%, Elastano 5%' AS composicao,
            q'[["preto/branco","tecido com stretch","mangas longas","gola polo","fechamento frontal por botões","acabamento contrastante","punhos largos","detalhe franzido","comprimento midi"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":178,"busto_cm":87,"cintura_cm":66,"quadril_cm":94,"veste":"M"}]' AS modelo
        FROM dual
    ) s
    ON (p.titulo='Karl Lagerfeld' AND p.subtitulo='Vestido' AND p.categoria='roupas')
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
            'https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388489_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388450_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388443_2048.jpg","https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388442_2048.jpg","https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388472_2048.jpg","https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388477_2048.jpg"]]' AS imagens,
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

-- Polo / Calça
MERGE INTO produtos p
    USING (
        SELECT
            'Polo' AS titulo,'Calça' AS subtitulo,'Ralph Lauren' AS autor,'Calça reta com vinco' AS descricao,6327 AS preco,'grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/21/71/65/27/21716527_51868131_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/21/71/65/27/21716527_51868144_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/21/71/65/27/21716527_51868138_2048.jpg","https://cdn-images.farfetch-contents.com/21/71/65/27/21716527_51868136_2048.jpg","https://cdn-images.farfetch-contents.com/21/71/65/27/21716527_51868143_2048.jpg"]]' AS imagens,
            'Exterior: lã 100% | Forro: Seda 100%' AS composicao,
            q'[["Preto","efeito amassado","fechamento oculto por botão","zíper","corte reto","passantes para cinto","dois bolsos laterais internos, dois bolsos posteriores com botão e debrum","barra reta"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":177,"busto_cm":86,"cintura_cm":64,"quadril_cm":92,"veste":"M"}]' AS modelo
        FROM dual
    ) s
    ON (p.titulo='Polo' AND p.subtitulo='Calça' AND p.categoria='roupas')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques, p.modelo=s.modelo
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,modelo)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,s.modelo);

-- Dolce & Gabbana / Camisa renda floral
MERGE INTO produtos p
    USING (
        SELECT
            'Dolce & Gabbana' AS titulo,'Camisa' AS subtitulo,'Domenico Dolce & Stefano Gabbana' AS autor,'Camisa com renda floral' AS descricao,32250 AS preco,'grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/26/51/83/61/26518361_57438081_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/26/51/83/61/26518361_57438087_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/26/51/83/61/26518361_57438086_2048.jpg","https://cdn-images.farfetch-contents.com/26/51/83/61/26518361_57438084_2048.jpg","https://cdn-images.farfetch-contents.com/26/51/83/61/26518361_57438083_2048.jpg"]]' AS imagens,
            'Viscose 30%, algodão 30%, Seda 20%, Poliamida 20%' AS composicao,
            q'[["estampa barroca exclusiva","fecho frontal com botões","punhos no mesmo tecido","caimento fluido"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":174,"busto_cm":82,"cintura_cm":61,"quadril_cm":89,"veste":"S"}]' AS modelo
        FROM dual
    ) s
    ON (p.titulo='Dolce & Gabbana' AND p.subtitulo='Camisa' AND p.categoria='roupas')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques, p.modelo=s.modelo
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,modelo)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,s.modelo);

-- Dolce & Gabbana / Jaqueta cropped de pelos
MERGE INTO produtos p
    USING (
        SELECT
            'Dolce & Gabbana' AS titulo,'Jaqueta' AS subtitulo,'Domenico Dolce & Stefano Gabbana' AS autor,'Jaqueta cropped de pelos' AS descricao,32250 AS preco,'pequeno' AS dimensao,
            'https://cdn-images.farfetch-contents.com/20/04/51/81/20045181_50414802_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/20/04/51/81/20045181_50414786_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/20/04/51/81/20045181_50414779_2048.jpg","https://cdn-images.farfetch-contents.com/20/04/51/81/20045181_50414780_2048.jpg","https://cdn-images.farfetch-contents.com/20/04/51/81/20045181_50415881_2048.jpg"]]' AS imagens,
            'Forro: Poliéster 70%, Nylon 28%, Elastano 2% | Exterior: Modacrílica 49%, rayon 26%, Poliéster 25%' AS composicao,
            q'[["Preto","design de pelos","gola redonda","fechamento frontal oculto","mangas longas","comprimento cropped","Produzido em: Itália"]]' AS destaques,
            'roupas' AS categoria,
            q'[{"altura_cm":176,"busto_cm":84,"cintura_cm":63,"quadril_cm":90,"veste":"S"}]' AS modelo
        FROM dual
    ) s
    ON (p.titulo='Dolce & Gabbana' AND p.subtitulo='Jaqueta' AND p.categoria='roupas')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques, p.modelo=s.modelo
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria,modelo)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria,s.modelo);

-------------------------------------------------------------------------------
-- SAPATOS
-------------------------------------------------------------------------------

-- Christian Louboutin / Scarpin Kate 120
MERGE INTO produtos p
    USING (
        SELECT
            'Christian Louboutin' AS titulo,'Scarpin' AS subtitulo,'Christian Louboutin' AS autor,'Scarpin Kate 120' AS descricao,8969 AS preco,'Pequeno' AS dimensao,
            'https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192293_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192296_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192298_2048.jpg","https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192295_2048.jpg"]]' AS imagens,
            'Forro: couro 100% | Exterior: camurça 100% | Solado: couro 100%' AS composicao,
            q'[["bico fino","signature Louboutin red lacquered sole","salto agulha alto 120mm","produzido em: Itália"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (
        p.titulo='Christian Louboutin' AND p.subtitulo='Scarpin' AND p.categoria='sapatos' AND p.descricao LIKE 'Scarpin Kate%'
        )
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
            'https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60310370_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60348565_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60310388_2048.jpg","https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60348573_2048.jpg"]]' AS imagens,
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

-- GCDS / Scarpin Morso Mirror 110mm
MERGE INTO produtos p
    USING (
        SELECT
            'GCDS' AS titulo,'Scarpin' AS subtitulo,'Giuliano Calza' AS autor,'Scarpin Morso Mirror com salto 110mm' AS descricao,7290 AS preco,'Pequeno' AS dimensao,
            'https://cdn-images.farfetch-contents.com/31/75/77/95/31757795_61341820_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/31/75/77/95/31757795_61341817_2048.jpg' AS imagem_hover,
            NULL AS imagens,
            'Exterior: Poliuretano 100% | Solado: couro de bezerro 100% | Forro: Couro de cabra 100%' AS composicao,
            q'[["prateado","bico fino","solado de couro","salto Morso","produzido em: Itália"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (p.titulo='GCDS' AND p.subtitulo='Scarpin' AND p.categoria='sapatos')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria);

-- Christian Louboutin / Scarpin tassel 110mm
MERGE INTO produtos p
    USING (
        SELECT
            'Christian Louboutin' AS titulo,'Scarpin' AS subtitulo,'Christian Louboutin' AS autor,'Scarpin com tassel e salto 110mm' AS descricao,12597 AS preco,'Pequeno' AS dimensao,
            'https://cdn-images.farfetch-contents.com/19/33/81/01/19338101_42343373_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/19/33/81/01/19338101_42345093_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/19/33/81/01/19338101_42345090_2048.jpg","https://cdn-images.farfetch-contents.com/19/33/81/01/19338101_42343363_2048.jpg"]]' AS imagens,
            'Solado: couro de bezerro 100% | Forro: couro de bezerro 100% | Exterior: Tecido 100%' AS composicao,
            q'[["vermelho bordô","veludo","detalhe de tassel","logo na palmilha","bico fino","salto agulha alto","modelagem slip-on"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (
        p.titulo='Christian Louboutin' AND p.subtitulo='Scarpin' AND p.categoria='sapatos' AND p.descricao LIKE 'Scarpin com tassel%'
        )
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.preco=s.preco, p.dimensao=s.dimensao,
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
            'https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36772936_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36781973_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36780941_2048.jpg","https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36862601_2048.jpg"]]' AS imagens,
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

-- Le Silla / Ankle boot Eva
MERGE INTO produtos p
    USING (
        SELECT
            'Le Silla' AS titulo,'Ankle Boot' AS subtitulo,'Enio Silla' AS autor,'Ankle boot Eva de couro' AS descricao,8159 AS preco,'Médio' AS dimensao,
            'https://cdn-images.farfetch-contents.com/23/89/76/01/23897601_53922434_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/23/89/76/01/23897601_53922438_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/23/89/76/01/23897601_53922436_2048.jpg","https://cdn-images.farfetch-contents.com/23/89/76/01/23897601_53922425_2048.jpg"]]' AS imagens,
            'Exterior: Pele de cordeiro 100% | Forro: couro nappa 100% | Solado: couro de bezerro 100%' AS composicao,
            q'[["vermelho escuro","couro","textura leve","comprimento longo","fechamento por zíper lateral","bico fino","salto agulha alto"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Le Silla' AND p.subtitulo='Ankle Boot' AND p.categoria='sapatos')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria);

-- Stuart Weitzman / Bota Ultrastuart 110mm
MERGE INTO produtos p
    USING (
        SELECT
            'Stuart Weitzman' AS titulo,'Bota' AS subtitulo,'Edmundo Castillo' AS autor,'Bota Ultrastuart com salto 110mm' AS descricao,7692 AS preco,'Grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/18/61/71/46/18617146_40317181_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/18/61/71/46/18617146_40316460_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/18/61/71/46/18617146_40317179_2048.jpg","https://cdn-images.farfetch-contents.com/18/61/71/46/18617146_40317173_2048.jpg"]]' AS imagens,
            'Exterior: couro de bezerro 100% | Solado: couro de bezerro 100% | Forro: Tecido 100%' AS composicao,
            q'[["modelagem slip-on","cano over-the-knee","bico fino","salto agulha alto"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Stuart Weitzman' AND p.subtitulo='Bota' AND p.categoria='sapatos')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria);

-- Giuseppe Zanotti / Bota Brytta High 90mm
MERGE INTO produtos p
    USING (
        SELECT
            'Giuseppe Zanotti' AS titulo,'Bota' AS subtitulo,'Giuseppe Zanotti' AS autor,'Bota Brytta High com salto 90mm' AS descricao,4498 AS preco,'Médio' AS dimensao,
            'https://cdn-images.farfetch-contents.com/20/27/75/63/20277563_50877163_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/20/27/75/63/20277563_50877161_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/20/27/75/63/20277563_50877160_2048.jpg","https://cdn-images.farfetch-contents.com/20/27/75/63/20277563_50877158_2048.jpg","https://cdn-images.farfetch-contents.com/20/27/75/63/20277563_50877154_2048.jpg"]]' AS imagens,
            'Outras fibras 100%' AS composicao,
            q'[["preto","couro","acabamento envernizado","bico fino","fechamento por zíper lateral oculto","salto agulha alto 90mm","produzido em: Itália"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Giuseppe Zanotti' AND p.subtitulo='Bota' AND p.categoria='sapatos')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria);

-- Dolce & Gabbana / Tênis Airmaster
MERGE INTO produtos p
    USING (
        SELECT
            'Dolce & Gabbana' AS titulo,'Tênis' AS subtitulo,'Domenico Dolce & Stefano Gabbana' AS autor,'Tênis Airmaster com recortes' AS descricao,5400 AS preco,'Médio' AS dimensao,
            'https://cdn-images.farfetch-contents.com/18/89/30/23/18893023_41054170_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/18/89/30/23/18893023_41052685_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/18/89/30/23/18893023_41052676_2048.jpg","https://cdn-images.farfetch-contents.com/18/89/30/23/18893023_41052692_2048.jpg"]]' AS imagens,
            'Couro 100%' AS composicao,
            q'[["branco","couro","fechamento frontal por amarração","patch de logo na lingueta","solado flat de borracha","recorte matelassê no calcanhar","produzido em: Itália"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Dolce & Gabbana' AND p.subtitulo='Tênis' AND p.categoria='sapatos')
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.descricao=s.descricao, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria);

-- Dolce & Gabbana / Bota KIM DOLCE&GABBANA
MERGE INTO produtos p
    USING (
        SELECT
            'Dolce & Gabbana' AS titulo,'Bota' AS subtitulo,'Domenico Dolce & Stefano Gabbana' AS autor,'Bota KIM DOLCE&GABBANA de cetim com strass' AS descricao,39000 AS preco,'Grande' AS dimensao,
            'https://cdn-images.farfetch-contents.com/19/39/11/53/19391153_43486606_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/19/39/11/53/19391153_43486610_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/19/39/11/53/19391153_43486602_2048.jpg"]]' AS imagens,
            'Seda 100%' AS composicao,
            q'[["aplicação de cristais","produzido em: Itália"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (
        p.titulo='Dolce & Gabbana' AND p.subtitulo='Bota' AND p.categoria='sapatos' AND p.descricao LIKE 'Bota KIM%'
        )
    WHEN MATCHED THEN UPDATE SET
        p.autor=s.autor, p.preco=s.preco, p.dimensao=s.dimensao,
        p.imagem=s.imagem, p.imagem_hover=s.imagem_hover, p.imagens=s.imagens, p.composicao=s.composicao, p.destaques=s.destaques
    WHEN NOT MATCHED THEN INSERT
        (titulo,subtitulo,autor,descricao,preco,dimensao,imagem,imagem_hover,imagens,composicao,destaques,categoria)
        VALUES
            (s.titulo,s.subtitulo,s.autor,s.descricao,s.preco,s.dimensao,s.imagem,s.imagem_hover,s.imagens,s.composicao,s.destaques,s.categoria);

-- Gucci / Mule Interlocking G 70mm
MERGE INTO produtos p
    USING (
        SELECT
            'Gucci' AS titulo,'Mule' AS subtitulo,'Gucci' AS autor,'Mule Interlocking G com salto 70mm' AS descricao,13590 AS preco,'Pequeno' AS dimensao,
            'https://cdn-images.farfetch-contents.com/22/52/60/38/22526038_54799743_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/22/52/60/38/22526038_54799765_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/22/52/60/38/22526038_54799770_2048.jpg","https://cdn-images.farfetch-contents.com/22/52/60/38/22526038_54799857_2048.jpg"]]' AS imagens,
            'Solado: couro de bezerro 100%, borracha 100% | Forro: couro de bezerro 100% | Exterior: couro de bezerro 100%, metal 100%' AS composicao,
            q'[["preto","couro","modelo slip-on","salto médio esculpido 70mm","salto Interlocking G prateado","abertura frontal","palmilha com logo","produzido em: Itália"]]' AS destaques,
            'sapatos' AS categoria
        FROM dual
    ) s
    ON (p.titulo='Gucci' AND p.subtitulo='Mule' AND p.categoria='sapatos')
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
            'https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824339_2048.jpg' AS imagem,
            'https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824340_2048.jpg' AS imagem_hover,
            q'[["https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824337_2048.jpg","https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824336_2048.jpg","https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824334_2048.jpg"]]' AS imagens,
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
