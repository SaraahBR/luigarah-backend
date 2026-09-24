-- ============================================================
-- V7 - Traducoes automaticas dos produtos (en, es, fr)
-- Geradas pelo Google Cloud Translation ao criar/editar um produto e,
-- para os produtos existentes, pela rotina de preenchimento ao iniciar.
-- hash_origem guarda o hash do texto em portugues usado na traducao:
-- se o produto for editado, a traducao antiga deixa de ser usada ate
-- ser refeita.
-- ============================================================

CREATE TABLE produto_traducoes (
    produto_id    BIGINT      NOT NULL REFERENCES produtos (id) ON DELETE CASCADE,
    idioma        VARCHAR(5)  NOT NULL CHECK (idioma IN ('en', 'es', 'fr')),
    subtitulo     VARCHAR(255),
    descricao     TEXT,
    composicao    TEXT,
    destaques     TEXT,
    hash_origem   VARCHAR(64) NOT NULL,
    atualizado_em TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT pk_produto_traducoes PRIMARY KEY (produto_id, idioma)
);

CREATE INDEX idx_produto_traducoes_idioma ON produto_traducoes (idioma);

ALTER TABLE produto_traducoes ENABLE ROW LEVEL SECURITY;
