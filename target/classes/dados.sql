-- Arquivo dados.sql para inserção inicial
-- Este arquivo será carregado automaticamente pelo Spring Boot

-- Inserção de dados de bolsas
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria) VALUES
    ('Gucci', 'Tiracolo', 'Demna Gvasalia', 'Bolsa tiracolo Dionysus mini', 9399.00, 'Média',
     'https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458138_2048.jpg',
     'https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458229_2048.jpg',
     '["https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58501117_2048.jpg","https://cdn-images.farfetch-contents.com/12/30/15/79/12301579_58458220_2048.jpg"]',
     'Exterior: camurça 100%, canvas 100%',
     '["bege","canvas GG Supreme","recortes de camurça","placa Dionysus frontal","fechamento por aba dobrável com botão de pressão"]',
     'bolsas');

INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria) VALUES
    ('Saint Laurent', 'Transversal', 'Yves Saint Laurent', 'Bolsa transversal com logo', 10911.00, 'Média',
     'https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720140_2048.jpg',
     'https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720161_2048.jpg',
     '["https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720143_2048.jpg","https://cdn-images.farfetch-contents.com/19/36/20/78/19362078_55720142_2048.jpg"]',
     'Couro 100%',
     '["preto","couro de textura granulada","fecho de zíper","bolso interno","alça de ombro ajustável"]',
     'bolsas');

-- Inserção de dados de sapatos
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria) VALUES
    ('Christian Louboutin', 'Scarpin', 'Christian Louboutin', 'Scarpin Kate 120', 8969.00, 'Pequeno',
     'https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192293_2048.jpg',
     'https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192296_2048.jpg',
     '["https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192298_2048.jpg","https://cdn-images.farfetch-contents.com/14/58/49/78/14584978_23192295_2048.jpg"]',
     'Forro: couro 100% | Exterior: camurça 100% | Solado: couro 100%',
     '["bico fino","signature Louboutin red lacquered sole","salto agulha alto 120mm","produzido em: Itália"]',
     'sapatos');

INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria) VALUES
    ('Balenciaga', 'Scarpin', 'Pierpaolo Piccioli', 'Scarpin Monday de couro com salto 140mm', 10788.00, 'Pequeno',
     'https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60310370_2048.jpg',
     'https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60348565_2048.jpg',
     '["https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60310388_2048.jpg","https://cdn-images.farfetch-contents.com/31/07/04/67/31070467_60348573_2048.jpg"]',
     'Exterior: couro de bezerro 100%, Tecido 100% | Forro: Tecido 100% | Solado: borracha 100%',
     '["salto 140mm","preto","couro","fechamento por amarração","solado esculpido","detalhe de logo"]',
     'sapatos');

-- Inserção de dados de roupas
INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo) VALUES
    ('MARANT ÉTOILE', 'Saia', 'Isabel Marant', 'Saia Berenicia', 10966.00, 'pequeno',
     'https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923679_2048.jpg',
     'https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923678_2048.jpg',
     '["https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923690_2048.jpg","https://cdn-images.farfetch-contents.com/28/33/14/38/28331438_57923669_2048.jpg"]',
     'Tecido 100%',
     '["vermelho escuro","fechamento lateral oculto","detalhe drapeado","bainha assimétrica","estampa de padronagem abstrata"]',
     'roupas',
     '{"height_cm":179,"bust_cm":84,"waist_cm":64,"hip_cm":90,"wears":"38"}');

INSERT INTO produtos (titulo, subtitulo, autor, descricao, preco, dimensao, imagem, imagem_hover, imagens, composicao, destaques, categoria, modelo) VALUES
    ('Maria Lucia Hohan', 'Vestido', 'Maria Lucia Hohan', 'Vestido Noor de seda', 20753.00, 'grande',
     'https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131249_2048.jpg',
     'https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131148_2048.jpg',
     '["https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131193_2048.jpg","https://cdn-images.farfetch-contents.com/29/44/37/45/29443745_59131276_2048.jpg"]',
     'Seda 100%',
     '["azul marinho","decote em V","mangas longas","detalhe de cristais","comprimento longo"]',
     'roupas',
     '{"height_cm":175,"bust_cm":86,"waist_cm":66,"hip_cm":92,"wears":"40"}');