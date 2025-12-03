-- Migration: Criar tabela verification_tokens para códigos de verificação e reset de senha
-- Autor: Sistema Luigarah
-- Data: 2025-12-02

ALTER SESSION SET CURRENT_SCHEMA = APP_LUIGARAH;

-- Criar tabela verification_tokens
CREATE TABLE verification_tokens (
    id NUMBER PRIMARY KEY,
    codigo VARCHAR2(6) NOT NULL,
    token VARCHAR2(500) NOT NULL UNIQUE,
    email VARCHAR2(255) NOT NULL,
    tipo VARCHAR2(50) NOT NULL CHECK (tipo IN ('VERIFICACAO_EMAIL', 'RESET_SENHA')),
    criado_em TIMESTAMP NOT NULL,
    expira_em TIMESTAMP NOT NULL,
    usado NUMBER(1) DEFAULT 0 NOT NULL,
    usado_em TIMESTAMP,
    CONSTRAINT chk_usado CHECK (usado IN (0, 1))
);

-- Criar índices para melhorar performance
CREATE INDEX idx_vtoken_codigo ON verification_tokens(codigo);
CREATE INDEX idx_vtoken_email ON verification_tokens(email);
CREATE INDEX idx_vtoken_tipo ON verification_tokens(tipo);
CREATE INDEX idx_vtoken_email_tipo ON verification_tokens(email, tipo);
CREATE INDEX idx_vtoken_expira_em ON verification_tokens(expira_em);
CREATE INDEX idx_vtoken_usado ON verification_tokens(usado);

-- Criar sequence
CREATE SEQUENCE verification_tokens_seq START WITH 1 INCREMENT BY 1 NOCACHE;

-- Criar trigger para auto-increment
CREATE OR REPLACE TRIGGER verification_tokens_bi
BEFORE INSERT ON verification_tokens
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT verification_tokens_seq.NEXTVAL INTO :NEW.id FROM dual;
    END IF;
END;
/

-- Comentários nas colunas
COMMENT ON TABLE verification_tokens IS 'Tabela para armazenar códigos de verificação de email e reset de senha';
COMMENT ON COLUMN verification_tokens.id IS 'ID único do token';
COMMENT ON COLUMN verification_tokens.codigo IS 'Código de 6 dígitos enviado por email';
COMMENT ON COLUMN verification_tokens.token IS 'Token UUID único para identificação';
COMMENT ON COLUMN verification_tokens.email IS 'Email do usuário';
COMMENT ON COLUMN verification_tokens.tipo IS 'Tipo do token: VERIFICACAO_EMAIL ou RESET_SENHA';
COMMENT ON COLUMN verification_tokens.criado_em IS 'Data e hora de criação do token';
COMMENT ON COLUMN verification_tokens.expira_em IS 'Data e hora de expiração do token';
COMMENT ON COLUMN verification_tokens.usado IS 'Flag indicando se o token já foi usado (0 = não, 1 = sim)';
COMMENT ON COLUMN verification_tokens.usado_em IS 'Data e hora em que o token foi usado';

