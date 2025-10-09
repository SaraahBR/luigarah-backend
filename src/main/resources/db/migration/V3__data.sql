/* V3__data.sql - dados iniciais (compatível com Flyway via JDBC) */
ALTER SESSION SET CURRENT_SCHEMA = APP_LUIGARAH;

-------------------------------------------------------------------------------
-- BOLSAS (ids 1..12 vieram do ambiente local; mantidos, mas protegidos por NOT EXISTS)
-------------------------------------------------------------------------------

-- id: 1 - Gucci / Tiracolo
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 1, 'Gucci','Tiracolo','Demna Gvasalia','Bolsa tiracolo Dionysus mini',9399,'Média',
       'https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458138_2048.jpg',
       'https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458229_2048.jpg',
       '["https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58501117_2048.jpg","https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458220_2048.jpg","https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458193_2048.jpg"]',
       'Exterior: camurça 100%, canvas 100%',
       '["bege","canvas GG Supreme","recortes de camurça","placa Dionysus frontal","fechamento por aba dobrável com botão de pressão","alça de ombro removível em corrente","compartimento principal","bolso interno com lapela","patch interno de logo","completamente forrado","ferragem prateada","produzido em: Itália","Este item é enviado com uma bolsa protetora."]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Gucci' AND subtitulo='Tiracolo' AND categoria='bolsas'
);

-- id: 2 - Saint Laurent / Transversal
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 2,'Saint Laurent','Transversal','Yves Saint Laurent','Bolsa transversal com logo',10911,'Média',
       'https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720140_2048.jpg',
       'https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720161_2048.jpg',
       '["https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720143_2048.jpg","https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720142_2048.jpg","https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_53679149_2048.jpg"]',
       'Couro 100%',
       '["Preto","couro","placa de logo","ferragem dourada","matelassê chevron","parte superior dobrável","compartimento principal","alça de ombro de corrente","produzido em: Itália"]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Saint Laurent' AND subtitulo='Transversal' AND categoria='bolsas'
);

-- id: 3 - Zadig&Voltaire / Ombro
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 3,'Zadig&Voltaire','Ombro','Cecilia Bönström','Bolsa de ombro em couro',3239,'Média',
       'https://cdn-images.farfetch-contents.com/30/94/59/07/30945907_59858416_2048.jpg',
       'https://cdn-images.farfetch-contents.com/30/94/59/07/30945907_59874112_2048.jpg',
       '["https://cdn-images.farfetch-contents.com/30/94/59/07/30945907_59858424_2048.jpg","https://cdn-images.farfetch-contents.com/30/94/59/07/30945907_59858428_2048.jpg"]',
       'Exterior: couro de bezerro 100%',
       '["preto","parte superior dobrável","corrente de elos","placa de logo","alça de ombro ajustável e removível","bolso posterior com zíper"]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Zadig&Voltaire' AND subtitulo='Ombro' AND categoria='bolsas'
);

-- id: 4 - DeMellier / Tote
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 4,'DeMellier','Tote','Mireia Llusia-Lindh','Bolsa tote The New York efeito pele de crocodilo',5216,'Grande',
       'https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398188_2048.jpg',
       'https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398271_2048.jpg',
       '["https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398227_2048.jpg","https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398117_2048.jpg","https://cdn-images.farfetch-contents.com/21/28/52/27/21285227_61398251_2048.jpg"]',
       'Exterior: camurça 100%',
       '["marrom cedro","ferragem dourada","alça de mão dupla arredondada","fecho magnético oculto","fechamento lateral por botões de pressão","compartimento principal","bolso interno com zíper","pé de metal","produzido em: Itália"]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='DeMellier' AND subtitulo='Tote' AND categoria='bolsas'
);

-- id: 5 - The Attico / Tote
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 5,'The Attico','Tote','Gilda Ambrosio & Giorgia Tordin','Bolsa tote La Passeggiata pequena',13471,'Média',
       'https://cdn-images.farfetch-contents.com/28/45/94/25/28459425_59683498_2048.jpg',
       'https://cdn-images.farfetch-contents.com/28/45/94/25/28459425_59683458_2048.jpg',
       '["https://cdn-images.farfetch-contents.com/27/09/49/25/27094925_58366280_2048.jpg","https://cdn-images.farfetch-contents.com/27/09/49/25/27094925_58366277_2048.jpg","https://cdn-images.farfetch-contents.com/27/09/49/25/27094925_58366240_2048.jpg"]',
       'Exterior: couro de bezerro 100% | Forro: camurça 100%',
       '["preto","fechamento por zíper na parte superior","alça de mão dupla arredondada","pé de metal","produzido em: Itália","Este item é enviado com uma bolsa protetora."]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='The Attico' AND subtitulo='Tote' AND categoria='bolsas'
);

-- id: 6 - Rabanne / Transversal
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 6,'Rabanne','Transversal','Paco Rabanne','Necessaire Paco 1969',13345,'Média',
       'https://cdn-images.farfetch-contents.com/28/39/76/93/28397693_59339333_2048.jpg',
       'https://cdn-images.farfetch-contents.com/28/39/76/93/28397693_59362288_2048.jpg',
       '["https://cdn-images.farfetch-contents.com/28/39/76/93/28397693_59339196_2048.jpg","https://cdn-images.farfetch-contents.com/28/39/76/93/28397693_59339036_2048.jpg","https://cdn-images.farfetch-contents.com/28/39/76/93/28397693_59339380_2048.jpg"]',
       'Exterior: metal 100%',
       '["prateado","design de mesh","acabamento polido","design assinatura 1969"]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Rabanne' AND subtitulo='Transversal' AND categoria='bolsas'
);

-- id: 7 - KHAITE / Tote
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 7,'KHAITE','Tote','Catherine Holstein','Bolsa tote Lotus pequena',22838,'Grande',
       'https://cdn-images.farfetch-contents.com/26/74/51/75/26745175_56439516_2048.jpg',
       'https://cdn-images.farfetch-contents.com/26/74/51/75/26745175_56439527_2048.jpg',
       NULL,
       'Exterior: couro de bezerro 100%',
       '["preto","couro","textura granulada","fechamento por alamares","alça de mão única","estampa de logo na frente","compartimento principal","completamente forrado","produzido em: Itália","Este item é enviado com uma bolsa protetora."]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='KHAITE' AND subtitulo='Tote' AND categoria='bolsas'
);

-- id: 8 - ZIMMERMANN / Tote
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 8,'ZIMMERMANN','Tote','Nicky Zimmermann','Bolsa tote Goldentime',7180,'Grande',
       'https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59409670_2048.jpg',
       'https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59356213_2048.jpg',
       '["https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59338797_2048.jpg","https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59338176_2048.jpg","https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59351338_2048.jpg","https://cdn-images.farfetch-contents.com/30/25/62/83/30256283_59348089_2048.jpg"]',
       'Ráfia 100%',
       '["natural","detalhe de franjas","compartimento interior espaçoso","alça de mão","alça de ombro ajustável","design aberto"]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='ZIMMERMANN' AND subtitulo='Tote' AND categoria='bolsas'
);

-- id: 9 - Dolce & Gabbana / Tote
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 9,'Dolce & Gabbana','Tote','Domenico Dolce & Stefano Gabbana','Bolsa tote Dolce Box com aplicação floral',79250,'Pequena',
       'https://cdn-images.farfetch-contents.com/22/05/29/42/22052942_52044094_2048.jpg',
       'https://cdn-images.farfetch-contents.com/22/05/29/42/22052942_52044096_2048.jpg',
       '["https://cdn-images.farfetch-contents.com/22/05/29/42/22052942_52044107_2048.jpg","https://cdn-images.farfetch-contents.com/22/05/29/42/22052942_52044091_2048.jpg"]',
       'Exterior: Seda 50%, Viscose 35%, Vidro 10%, couro de bezerro 5% | Forro: couro de bezerro 95%, Contraplacado em choupo 5%',
       '["vermelho sangue/multicolorido","couro","aplicação floral","acabamento dourado","ferragem dourada","fechamento por alça com fecho giratório","alça de ombro ajustável e removível","alça de mão única","compartimento principal","compartimento interno para cartão","placa de logo interna"]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Dolce & Gabbana' AND subtitulo='Tote' AND categoria='bolsas'
);

-- id: 10 - Prada / Bucket
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 10,'Prada','Bucket','Miuccia Prada','Bolsa bucket Buckle',38500,'Grande',
       'https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124720_2048.jpg',
       'https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124722_2048.jpg',
       '["https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124727_2048.jpg","https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124718_2048.jpg","https://cdn-images.farfetch-contents.com/29/84/37/69/29843769_59124719_2048.jpg"]',
       'Exterior: couro de bezerro 100%',
       '["preto","fechamento por fivelas","ombro único","bolso interno","Este item é enviado com uma bolsa protetora."]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Prada' AND subtitulo='Bucket' AND categoria='bolsas'
);

-- id: 11 - Dolce & Gabbana / Tote (floral)
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 11,'Dolce & Gabbana','Tote','Domenico Dolce & Stefano Gabbana','Bolsa tote com estampa floral',79250,'Média',
       'https://cdn-images.farfetch-contents.com/15/74/83/45/15748345_32187784_2048.jpg',
       'https://cdn-images.farfetch-contents.com/15/74/83/45/15748345_32187788_2048.jpg',
       '["https://cdn-images.farfetch-contents.com/15/74/83/45/15748345_32187785_2048.jpg","https://cdn-images.farfetch-contents.com/15/74/83/45/15748345_32187786_2048.jpg"]',
       'Exterior: PLA 90%, couro de bezerro 10% | Forro: couro de bezerro 100%',
       '["Alça de mão única","alça de ombro ajustável e removível","fechamento por cadeado","compartimento principal","compartimento interno para cartão","estampa floral","produzido em: Itália"]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Dolce & Gabbana' AND subtitulo='Tote' AND categoria='bolsas' AND descricao LIKE 'Bolsa tote com estampa floral%'
);

-- id: 12 - Prada / Desfile (Galleria mini)
INSERT INTO produtos (id, titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, data_criacao, data_atualizacao)
SELECT 12,'Prada','Desfile','Miuccia Prada','Bolsa Galleria mini',45000,'Média',
       'https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54334086_2048.jpg',
       'https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54302413_2048.jpg',
       '["https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54302418_2048.jpg","https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54302415_2048.jpg","https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54302404_2048.jpg","https://cdn-images.farfetch-contents.com/24/31/71/19/24317119_54302403_2048.jpg"]',
       'Exterior: couro 100%, couro envernizado 100% | Forro: couro nappa 100%',
       '["preto","couro","acabamento envernizado","alça de ombro ajustável e removível","detalhe de acabamento de plumas","porta-chaves","logo triangular","fecho deslizante","bolso interno com zíper"]',
       'bolsas', SYSTIMESTAMP, SYSTIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Prada' AND subtitulo='Desfile' AND categoria='bolsas'
);

-------------------------------------------------------------------------------
-- ROUPAS
-------------------------------------------------------------------------------

-- MARANT ÉTOILE / Saia
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'MARANT ÉTOILE','Saia','Isabel Marant','Saia Berenicia',10966,'pequeno',
       'https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923679_2048.jpg',
       'https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923678_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923690_2048.jpg","https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923669_2048.jpg","https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923682_2048.jpg"]]',
    'Tecido 100%',
       q'[["vermelho escuro","fechamento lateral oculto","detalhe drapeado","bainha assimétrica","estampa de padronagem abstrata"]]',
    'roupas',
       q'[{"altura_cm":179,"busto_cm":84,"cintura_cm":64,"quadril_cm":90,"veste":"38"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='MARANT ÉTOILE' AND subtitulo='Saia' AND categoria='roupas'
);

-- Maria Lucia Hohan / Vestido (Noor de seda)
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'Maria Lucia Hohan','Vestido','Maria Lucia Hohan','Vestido Noor de seda',20753,'grande',
       'https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131249_2048.jpg',
       'https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131148_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131193_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131276_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131163_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131233_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131166_2048.jpg"]]',
    'Seda 100%',
       q'[["vermelho","fechamento posterior por tiras fixadoras","fechamento posterior por zíper","gola V","fenda frontal"]]',
    'roupas',
       q'[{"altura_cm":177,"busto_cm":86,"cintura_cm":62,"quadril_cm":90,"veste":"M"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Maria Lucia Hohan' AND subtitulo='Vestido' AND categoria='roupas' AND descricao LIKE 'Vestido Noor%'
);

-- MARANT ÉTOILE / Colete
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'MARANT ÉTOILE','Colete','Isabel Marant','Colete Clemencia de pelos com bordado',2757,'pequeno',
       'https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221705_2048.jpg',
       'https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221811_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221705_2048.jpg","https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221811_2048.jpg","https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221705_2048.jpg","https://cdn-images.farfetch-contents.com/31/63/86/32/31638632_61221811_2048.jpg"]]',
    'Exterior: Seda 100%, camurça 100%, Poliuretano 100% | Forro: algodão 100%, Poliéster 100%',
       q'[["bege taupe","acabamento em pelo","detalhes bordados","modelagem sem mangas","fechamento por botão","padronagem geométrica"]]',
    'roupas',
       q'[{"altura_cm":181,"busto_cm":82,"cintura_cm":63,"quadril_cm":89,"veste":"M"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='MARANT ÉTOILE' AND subtitulo='Colete' AND categoria='roupas'
);

-- Casa Blanca / Vestido (gola polo)
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'Casa Blanca','Vestido','Charaf Tajer','Vestido canelado com gola polo',6588,'médio',
       'https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60249561_2048.jpg',
       'https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60250342_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60249561_2048.jpg","https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60250342_2048.jpg","https://cdn-images.farfetch-contents.com/30/26/65/27/30266527_60249561_2048.jpg"]]',
    'Viscose 100%',
       q'[["preto","mangas curtas","textura canelada","gola V","acabamento em verde claro","logo pequeno"]]',
    'roupas',
       q'[{"altura_cm":181,"busto_cm":78,"cintura_cm":62,"quadril_cm":81,"veste":"S"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Casa Blanca' AND subtitulo='Vestido' AND categoria='roupas'
);

-- Maria Lucia Hohan / Vestido (de festa sem alças)
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'Maria Lucia Hohan','Vestido','Maria Lucia Hohan','Vestido de festa sem alças',25945,'grande',
       'https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819593_2048.jpg',
       'https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819592_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819589_2048.jpg","https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819585_2048.jpg","https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819586_2048.jpg","https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819583_2048.jpg","https://cdn-images.farfetch-contents.com/23/03/20/18/23032018_53819581_2048.jpg"]]',
    'Tecido 100%',
       q'[["platina","plissado","decote coração","modelagem sem mangas","fenda lateral","fechamento posterior por amarração","comprimento longo"]]',
    'roupas',
       q'[{"altura_cm":179,"busto_cm":86,"cintura_cm":60,"quadril_cm":90,"veste":"M"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Maria Lucia Hohan' AND subtitulo='Vestido' AND categoria='roupas' AND descricao LIKE 'Vestido de festa%'
);

-- Missoni / Vestido longo sem mangas com paetês
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'Missoni','Vestido','Ottavio & Rosita Missoni','Vestido longo sem mangas com paetês',23271,'grande',
       'https://cdn-images.farfetch-contents.com/31/49/64/78/31496478_60929181_2048.jpg',
       'https://cdn-images.farfetch-contents.com/31/49/64/78/31496478_60933522_2048.jpg',
       NULL,
       'Poliéster 88%, Elastano 12%',
       q'[["preto","semitranslúcido","aplicação de paetês","padronagem de listra","modelagem sem mangas","decote canoa","O item recebido pode diferir levemente da foto. Isso se deve à natureza artesanal com que todos os produtos Missoni são confeccionados.","Esta peça possui detalhes delicados. Manuseie com muito cuidado para evitar danos, como penas quebradas ou perda de paetês."]]',
    'roupas',
       q'[{"altura_cm":176,"busto_cm":83,"cintura_cm":63,"quadril_cm":91,"veste":"S"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Missoni' AND subtitulo='Vestido' AND categoria='roupas'
);

-- NISSA / Vestido longo com design pelerine
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'NISSA','Vestido','NISSA','Vestido longo com design pelerine',10414,'grande',
       'https://cdn-images.farfetch-contents.com/30/53/95/75/30539575_59549520_2048.jpg',
       'https://cdn-images.farfetch-contents.com/30/53/95/75/30539575_59549552_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099981_2048.jpg","https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099980_2048.jpg","https://cdn-images.farfetch-contents.com/30/53/95/75/30539575_59549527_2048.jpg"]]',
    'Forro: Viscose 100% | Exterior: Poliéster 100%',
       q'[["bege","fechamento posterior por zíper","aplicações de cristais","cós elástico","saia com pregas"]]',
    'roupas',
       q'[{"altura_cm":175,"busto_cm":80,"cintura_cm":61,"quadril_cm":88,"veste":"36"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='NISSA' AND subtitulo='Vestido' AND categoria='roupas'
);

-- Karl Lagerfeld / Vestido polo mangas longas
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'Karl Lagerfeld','Vestido','Karl Lagerfeld','Vestido polo mangas longas',3274,'médio',
       'https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099999_2048.jpg',
       'https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099996_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099981_2048.jpg","https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099980_2048.jpg","https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099979_2048.jpg","https://cdn-images.farfetch-contents.com/26/15/68/47/26156847_56099975_2048.jpg"]]',
    'Poliéster 95%, Elastano 5%',
       q'[["preto/branco","tecido com stretch","mangas longas","gola polo","fechamento frontal por botões","acabamento contrastante","punhos largos","detalhe franzido","comprimento midi"]]',
    'roupas',
       q'[{"altura_cm":178,"busto_cm":87,"cintura_cm":66,"quadril_cm":94,"veste":"M"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Karl Lagerfeld' AND subtitulo='Vestido' AND categoria='roupas'
);

-- Prada / Desfile (Blazer camurça)
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'Prada','Desfile','Miuccia Prada','Blazer de camurça com abotoamento simples',56000,'grande',
       'https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388489_2048.jpg',
       'https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388450_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388443_2048.jpg","https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388442_2048.jpg","https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388472_2048.jpg","https://cdn-images.farfetch-contents.com/21/41/42/84/21414284_51388477_2048.jpg"]]',
    'camurça 100%',
       q'[["rosa","camurça","patch de logo posterior","lapelas","fechamento frontal por botões","mangas longas","dois bolsos laterais com lapelas","barra reta","completamente forrado"]]',
    'roupas',
       q'[{"altura_cm":175,"busto_cm":83,"cintura_cm":62,"quadril_cm":90,"veste":"ÚNICO"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Prada' AND subtitulo='Desfile' AND categoria='roupas'
);

-- Polo / Calça
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'Polo','Calça','Ralph Lauren','Calça reta com vinco',6327,'grande',
       'https://cdn-images.farfetch-contents.com/21/71/65/27/21716527_51868131_2048.jpg',
       'https://cdn-images.farfetch-contents.com/21/71/65/27/21716527_51868144_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/21/71/65/27/21716527_51868138_2048.jpg","https://cdn-images.farfetch-contents.com/21/71/65/27/21716527_51868136_2048.jpg","https://cdn-images.farfetch-contents.com/21/71/65/27/21716527_51868143_2048.jpg"]]',
    'Exterior: lã 100% | Forro: Seda 100%',
       q'[["Preto","efeito amassado","fechamento oculto por botão","zíper","corte reto","passantes para cinto","dois bolsos laterais internos, dois bolsos posteriores com botão e debrum","barra reta"]]',
    'roupas',
       q'[{"altura_cm":177,"busto_cm":86,"cintura_cm":64,"quadril_cm":92,"veste":"M"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Polo' AND subtitulo='Calça' AND categoria='roupas'
);

-- Dolce & Gabbana / Camisa renda floral
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'Dolce & Gabbana','Camisa','Domenico Dolce & Stefano Gabbana','Camisa com renda floral',32250,'grande',
       'https://cdn-images.farfetch-contents.com/26/51/83/61/26518361_57438081_2048.jpg',
       'https://cdn-images.farfetch-contents.com/26/51/83/61/26518361_57438087_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/26/51/83/61/26518361_57438086_2048.jpg","https://cdn-images.farfetch-contents.com/26/51/83/61/26518361_57438084_2048.jpg","https://cdn-images.farfetch-contents.com/26/51/83/61/26518361_57438083_2048.jpg"]]',
    'Viscose 30%, algodão 30%, Seda 20%, Poliamida 20%',
       q'[["estampa barroca exclusiva","fecho frontal com botões","punhos no mesmo tecido","caimento fluido"]]',
    'roupas',
       q'[{"altura_cm":174,"busto_cm":82,"cintura_cm":61,"quadril_cm":89,"veste":"S"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Dolce & Gabbana' AND subtitulo='Camisa' AND categoria='roupas'
);

-- Dolce & Gabbana / Jaqueta cropped de pelos
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo)
SELECT 'Dolce & Gabbana','Jaqueta','Domenico Dolce & Stefano Gabbana','Jaqueta cropped de pelos',32250,'pequeno',
       'https://cdn-images.farfetch-contents.com/20/04/51/81/20045181_50414802_2048.jpg',
       'https://cdn-images.farfetch-contents.com/20/04/51/81/20045181_50414786_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/20/04/51/81/20045181_50414779_2048.jpg","https://cdn-images.farfetch-contents.com/20/04/51/81/20045181_50414780_2048.jpg","https://cdn-images.farfetch-contents.com/20/04/51/81/20045181_50415881_2048.jpg"]]',
    'Forro: Poliéster 70%, Nylon 28%, Elastano 2% | Exterior: Modacrílica 49%, rayon 26%, Poliéster 25%',
       q'[["Preto","design de pelos","gola redonda","fechamento frontal oculto","mangas longas","comprimento cropped","Produzido em: Itália"]]',
    'roupas',
       q'[{"altura_cm":176,"busto_cm":84,"cintura_cm":63,"quadril_cm":90,"veste":"S"}]'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Dolce & Gabbana' AND subtitulo='Jaqueta' AND categoria='roupas'
);

-------------------------------------------------------------------------------
-- SAPATOS
-------------------------------------------------------------------------------

-- Christian Louboutin / Scarpin Kate 120
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'Christian Louboutin','Scarpin','Christian Louboutin','Scarpin Kate 120',8969,'Pequeno',
       'https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192293_2048.jpg',
       'https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192296_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192298_2048.jpg","https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192295_2048.jpg"]]',
    'Forro: couro 100% | Exterior: camurça 100% | Solado: couro 100%',
       q'[["bico fino","signature Louboutin red lacquered sole","salto agulha alto 120mm","produzido em: Itália"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Christian Louboutin' AND subtitulo='Scarpin' AND categoria='sapatos' AND descricao LIKE 'Scarpin Kate%'
);

-- Balenciaga / Scarpin Monday 140mm
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'Balenciaga','Scarpin','Pierpaolo Piccioli','Scarpin Monday de couro com salto 140mm',10788,'Pequeno',
       'https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60310370_2048.jpg',
       'https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60348565_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60310388_2048.jpg","https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60348573_2048.jpg"]]',
    'Exterior: couro de bezerro 100%, Tecido 100% | Forro: Tecido 100% | Solado: borracha 100%',
       q'[["salto 140mm","preto","couro","fechamento por amarração","solado esculpido","detalhe de logo"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Balenciaga' AND subtitulo='Scarpin' AND categoria='sapatos'
);

-- GCDS / Scarpin Morso Mirror 110mm
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'GCDS','Scarpin','Giuliano Calza','Scarpin Morso Mirror com salto 110mm',7290,'Pequeno',
       'https://cdn-images.farfetch-contents.com/31/75/77/95/31757795_61341820_2048.jpg',
       'https://cdn-images.farfetch-contents.com/31/75/77/95/31757795_61341817_2048.jpg',
       NULL,
       'Exterior: Poliuretano 100% | Solado: couro de bezerro 100% | Forro: Couro de cabra 100%',
       q'[["prateado","bico fino","solado de couro","salto Morso","produzido em: Itália"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='GCDS' AND subtitulo='Scarpin' AND categoria='sapatos'
);

-- Christian Louboutin / Scarpin tassel 110mm
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'Christian Louboutin','Scarpin','Christian Louboutin','Scarpin com tassel e salto 110mm',12597,'Pequeno',
       'https://cdn-images.farfetch-contents.com/19/33/81/01/19338101_42343373_2048.jpg',
       'https://cdn-images.farfetch-contents.com/19/33/81/01/19338101_42345093_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/19/33/81/01/19338101_42345090_2048.jpg","https://cdn-images.farfetch-contents.com/19/33/81/01/19338101_42343363_2048.jpg"]]',
    'Solado: couro de bezerro 100% | Forro: couro de bezerro 100% | Exterior: Tecido 100%',
       q'[["vermelho bordô","veludo","detalhe de tassel","logo na palmilha","bico fino","salto agulha alto","modelagem slip-on"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Christian Louboutin' AND subtitulo='Scarpin' AND categoria='sapatos' AND descricao LIKE 'Scarpin com tassel%'
);

-- Versace / Bota couro salto alto
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'Versace','Bota','Donatella Versace','Bota de couro com salto alto',13385,'Grande',
       'https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36772936_2048.jpg',
       'https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36781973_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36780941_2048.jpg","https://cdn-images.farfetch-contents.com/17/33/29/69/17332969_36862601_2048.jpg"]]',
    'Exterior: couro 100% | Forro: couro 100% | Solado: couro 100%',
       q'[["Preto","bico quadrado","fechamento por zíper lateral","cano alto","plataforma e salto alto bloco","couro"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Versace' AND subtitulo='Bota' AND categoria='sapatos'
);

-- Le Silla / Ankle boot Eva
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'Le Silla','Ankle Boot','Enio Silla','Ankle boot Eva de couro',8159,'Médio',
       'https://cdn-images.farfetch-contents.com/23/89/76/01/23897601_53922434_2048.jpg',
       'https://cdn-images.farfetch-contents.com/23/89/76/01/23897601_53922438_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/23/89/76/01/23897601_53922436_2048.jpg","https://cdn-images.farfetch-contents.com/23/89/76/01/23897601_53922425_2048.jpg"]]',
    'Exterior: Pele de cordeiro 100% | Forro: couro nappa 100% | Solado: couro de bezerro 100%',
       q'[["vermelho escuro","couro","textura leve","comprimento longo","fechamento por zíper lateral","bico fino","salto agulha alto"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Le Silla' AND subtitulo='Ankle Boot' AND categoria='sapatos'
);

-- Stuart Weitzman / Bota Ultrastuart 110mm
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'Stuart Weitzman','Bota','Edmundo Castillo','Bota Ultrastuart com salto 110mm',7692,'Grande',
       'https://cdn-images.farfetch-contents.com/18/61/71/46/18617146_40317181_2048.jpg',
       'https://cdn-images.farfetch-contents.com/18/61/71/46/18617146_40316460_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/18/61/71/46/18617146_40317179_2048.jpg","https://cdn-images.farfetch-contents.com/18/61/71/46/18617146_40317173_2048.jpg"]]',
    'Exterior: couro de bezerro 100% | Solado: couro de bezerro 100% | Forro: Tecido 100%',
       q'[["modelagem slip-on","cano over-the-knee","bico fino","salto agulha alto"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Stuart Weitzman' AND subtitulo='Bota' AND categoria='sapatos'
);

-- Giuseppe Zanotti / Bota Brytta High 90mm
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'Giuseppe Zanotti','Bota','Giuseppe Zanotti','Bota Brytta High com salto 90mm',4498,'Médio',
       'https://cdn-images.farfetch-contents.com/20/27/75/63/20277563_50877163_2048.jpg',
       'https://cdn-images.farfetch-contents.com/20/27/75/63/20277563_50877161_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/20/27/75/63/20277563_50877160_2048.jpg","https://cdn-images.farfetch-contents.com/20/27/75/63/20277563_50877158_2048.jpg","https://cdn-images.farfetch-contents.com/20/27/75/63/20277563_50877154_2048.jpg"]]',
    'Outras fibras 100%',
       q'[["preto","couro","acabamento envernizado","bico fino","fechamento por zíper lateral oculto","salto agulha alto 90mm","produzido em: Itália"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Giuseppe Zanotti' AND subtitulo='Bota' AND categoria='sapatos'
);

-- Dolce & Gabbana / Tênis Airmaster
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'Dolce & Gabbana','Tênis','Domenico Dolce & Stefano Gabbana','Tênis Airmaster com recortes',5400,'Médio',
       'https://cdn-images.farfetch-contents.com/18/89/30/23/18893023_41054170_2048.jpg',
       'https://cdn-images.farfetch-contents.com/18/89/30/23/18893023_41052685_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/18/89/30/23/18893023_41052676_2048.jpg","https://cdn-images.farfetch-contents.com/18/89/30/23/18893023_41052692_2048.jpg"]]',
    'Couro 100%',
       q'[["branco","couro","fechamento frontal por amarração","patch de logo na lingueta","solado flat de borracha","recorte matelassê no calcanhar","produzido em: Itália"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Dolce & Gabbana' AND subtitulo='Tênis' AND categoria='sapatos'
);

-- Dolce & Gabbana / Bota KIM DOLCE&GABBANA
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'Dolce & Gabbana','Bota','Domenico Dolce & Stefano Gabbana','Bota KIM DOLCE&GABBANA de cetim com strass',39000,'Grande',
       'https://cdn-images.farfetch-contents.com/19/39/11/53/19391153_43486606_2048.jpg',
       'https://cdn-images.farfetch-contents.com/19/39/11/53/19391153_43486610_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/19/39/11/53/19391153_43486602_2048.jpg"]]',
    'Seda 100%',
       q'[["aplicação de cristais","produzido em: Itália"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Dolce & Gabbana' AND subtitulo='Bota' AND categoria='sapatos' AND descricao LIKE 'Bota KIM%'
);

-- Gucci / Mule Interlocking G 70mm
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'Gucci','Mule','Gucci','Mule Interlocking G com salto 70mm',13590,'Pequeno',
       'https://cdn-images.farfetch-contents.com/22/52/60/38/22526038_54799743_2048.jpg',
       'https://cdn-images.farfetch-contents.com/22/52/60/38/22526038_54799765_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/22/52/60/38/22526038_54799770_2048.jpg","https://cdn-images.farfetch-contents.com/22/52/60/38/22526038_54799857_2048.jpg"]]',
    'Solado: couro de bezerro 100%, borracha 100% | Forro: couro de bezerro 100% | Exterior: couro de bezerro 100%, metal 100%',
       q'[["preto","couro","modelo slip-on","salto médio esculpido 70mm","salto Interlocking G prateado","abertura frontal","palmilha com logo","produzido em: Itália"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Gucci' AND subtitulo='Mule' AND categoria='sapatos'
);

-- Prada / Mocassim Chocolate
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria)
SELECT 'Prada','Mocassim','Miuccia Prada','Mocassim Chocolate de couro',8200,'Médio',
       'https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824339_2048.jpg',
       'https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824340_2048.jpg',
       q'[["https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824337_2048.jpg","https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824336_2048.jpg","https://cdn-images.farfetch-contents.com/22/08/13/82/22081382_51824334_2048.jpg"]]',
    'Couro 100%',
       q'[["preto","couro","acabamento escovado","logo triangular esmaltado","bico arredondado","palmilha de couro com logo","solado flat em couro"]]',
    'sapatos'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM produtos WHERE titulo='Prada' AND subtitulo='Mocassim' AND categoria='sapatos'
);
