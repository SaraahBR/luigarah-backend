ALTER SESSION SET CURRENT_SCHEMA = APP_LUIGARAH;

--------------------------
-- Tabelas
--------------------------
BEGIN EXECUTE IMMEDIATE 'DROP TABLE produtos_tamanhos CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE produtos_estoque  CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE tamanhos          CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE produtos          CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE TABLE produtos (
                          id              NUMBER PRIMARY KEY,
                          titulo          VARCHAR2(255) NOT NULL,
                          subtitulo       VARCHAR2(255) NOT NULL,
                          autor           VARCHAR2(255) NOT NULL,
                          descricao       CLOB          NOT NULL,
                          preco           NUMBER(10,2)  NOT NULL,
                          dimensao        VARCHAR2(100) NOT NULL,
                          imagem          CLOB          NOT NULL,
                          imagem_hover    CLOB,
                          imagens         CLOB,
                          composicao      CLOB          NOT NULL,
                          destaques       CLOB,
                          categoria       VARCHAR2(50)  NOT NULL CHECK (categoria IN ('bolsas','roupas','sapatos')),
                          modelo          CLOB,
                          data_criacao    TIMESTAMP DEFAULT SYSTIMESTAMP,
                          data_atualizacao TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE INDEX idx_produtos_categoria ON produtos(categoria);
CREATE INDEX idx_produtos_titulo    ON produtos(titulo);
CREATE INDEX idx_produtos_autor     ON produtos(autor);

CREATE TABLE tamanhos (
                          id        NUMBER       PRIMARY KEY,
                          categoria VARCHAR2(50) NOT NULL CHECK (categoria IN ('roupas','sapatos')),
                          etiqueta  VARCHAR2(10) NOT NULL,
                          ordem     NUMBER NULL,
                          CONSTRAINT uq_tamanho_cat UNIQUE (categoria, etiqueta)
);

CREATE TABLE produtos_tamanhos (
                                   produto_id  NUMBER NOT NULL,
                                   tamanho_id  NUMBER NOT NULL,
                                   qtd_estoque NUMBER DEFAULT 0 NOT NULL,
                                   CONSTRAINT pk_produtos_tamanhos PRIMARY KEY (produto_id, tamanho_id),
                                   CONSTRAINT fk_pt_produto FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE CASCADE,
                                   CONSTRAINT fk_pt_tamanho FOREIGN KEY (tamanho_id) REFERENCES tamanhos(id) ON DELETE CASCADE
);

CREATE INDEX idx_pt_produto ON produtos_tamanhos(produto_id);
CREATE INDEX idx_pt_tamanho ON produtos_tamanhos(tamanho_id);

CREATE TABLE produtos_estoque (
                                  produto_id  NUMBER      NOT NULL,
                                  qtd_estoque NUMBER(10)  DEFAULT 0 NOT NULL,
                                  CONSTRAINT pk_produtos_estoque PRIMARY KEY (produto_id),
                                  CONSTRAINT fk_pe_prod FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE CASCADE
);

--------------------------
-- Sequences
--------------------------
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE produtos_seq'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE tamanhos_seq'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE SEQUENCE produtos_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE tamanhos_seq START WITH 1 INCREMENT BY 1 NOCACHE;

--------------------------
-- Triggers
--------------------------
CREATE OR REPLACE TRIGGER produtos_bi
BEFORE INSERT ON produtos
FOR EACH ROW
BEGIN
  IF :NEW.id IS NULL THEN
SELECT produtos_seq.NEXTVAL INTO :NEW.id FROM dual;
END IF;
  :NEW.data_criacao     := SYSTIMESTAMP;
  :NEW.data_atualizacao := SYSTIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER tamanhos_bi
BEFORE INSERT ON tamanhos
FOR EACH ROW
BEGIN
  IF :NEW.id IS NULL THEN
SELECT tamanhos_seq.NEXTVAL INTO :NEW.id FROM dual;
END IF;
END;
/

-- Validação de etiquetas por categoria
CREATE OR REPLACE TRIGGER tamanhos_valida_etiqueta
BEFORE INSERT OR UPDATE ON tamanhos
                            FOR EACH ROW
DECLARE v_num NUMBER;
BEGIN
  IF :NEW.categoria = 'roupas' THEN
    IF :NEW.etiqueta NOT IN ('XXXS','XXS','XS','S','M','L','XL') THEN
      RAISE_APPLICATION_ERROR(-20001,'Etiqueta inválida para roupas.');
END IF;
  ELSIF :NEW.categoria = 'sapatos' THEN
SELECT CASE WHEN REGEXP_LIKE(:NEW.etiqueta,'^\d+$') THEN 1 ELSE 0 END INTO v_num FROM dual;
IF v_num = 0 OR TO_NUMBER(:NEW.etiqueta) < 32 OR TO_NUMBER(:NEW.etiqueta) > 46 THEN
      RAISE_APPLICATION_ERROR(-20002,'Sapatos: use 32..46.');
END IF;
END IF;
END;
/

--------------------------
-- View de catálogo
--------------------------
BEGIN EXECUTE IMMEDIATE 'DROP VIEW vw_produtos_com_tamanhos'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
CREATE OR REPLACE VIEW vw_produtos_com_tamanhos AS
SELECT
    p.id, p.titulo, p.subtitulo, p.categoria,
    LISTAGG(tt.etiqueta, ',') WITHIN GROUP (ORDER BY tt.ord) AS tamanhos
FROM produtos p
    LEFT JOIN (
    SELECT pt.produto_id, t.etiqueta,
    CASE
    WHEN t.categoria='sapatos' THEN TO_NUMBER(t.etiqueta)
    WHEN t.etiqueta='XXXS' THEN 1 WHEN t.etiqueta='XXS' THEN 2 WHEN t.etiqueta='XS' THEN 3
    WHEN t.etiqueta='S' THEN 4 WHEN t.etiqueta='M' THEN 5 WHEN t.etiqueta='L' THEN 6
    WHEN t.etiqueta='XL' THEN 7 ELSE 9999
    END ord
    FROM produtos_tamanhos pt
    JOIN tamanhos t ON t.id = pt.tamanho_id
    ) tt ON tt.produto_id = p.id
GROUP BY p.id, p.titulo, p.subtitulo, p.categoria;
