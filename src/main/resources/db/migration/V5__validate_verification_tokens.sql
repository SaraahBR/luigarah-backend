-- Migration: Validar existência da tabela verification_tokens
-- Esta migration não cria nada, apenas valida que a tabela já existe
-- Autor: Sistema Luigarah
-- Data: 2025-12-02

ALTER SESSION SET CURRENT_SCHEMA = APP_LUIGARAH;

-- Validar que a tabela verification_tokens existe
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_tables
    WHERE table_name = 'VERIFICATION_TOKENS';

    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Tabela verification_tokens não existe. Execute a criação manual primeiro.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✅ Tabela verification_tokens já existe. Migration V5 concluída.');
    END IF;
END;
/

-- Validar que a sequence existe
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_sequences
    WHERE sequence_name = 'VERIFICATION_TOKENS_SEQ';

    IF v_count = 0 THEN
        -- Criar sequence se não existir
        EXECUTE IMMEDIATE 'CREATE SEQUENCE verification_tokens_seq START WITH 1 INCREMENT BY 1 NOCACHE';
        DBMS_OUTPUT.PUT_LINE('✅ Sequence verification_tokens_seq criada.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✅ Sequence verification_tokens_seq já existe.');
    END IF;
END;
/

-- Validar que o trigger existe
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_triggers
    WHERE trigger_name = 'VERIFICATION_TOKENS_BI';

    IF v_count = 0 THEN
        -- Criar trigger se não existir
        EXECUTE IMMEDIATE '
        CREATE OR REPLACE TRIGGER verification_tokens_bi
        BEFORE INSERT ON verification_tokens
        FOR EACH ROW
        BEGIN
            IF :NEW.id IS NULL THEN
                SELECT verification_tokens_seq.NEXTVAL INTO :NEW.id FROM dual;
            END IF;
        END;';
        DBMS_OUTPUT.PUT_LINE('✅ Trigger verification_tokens_bi criado.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✅ Trigger verification_tokens_bi já existe.');
    END IF;
END;
/

-- Validar índices (criar se não existirem)
DECLARE
    v_count NUMBER;
BEGIN
    -- Índice: idx_vtoken_codigo
    SELECT COUNT(*) INTO v_count FROM user_indexes WHERE index_name = 'IDX_VTOKEN_CODIGO';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'CREATE INDEX idx_vtoken_codigo ON verification_tokens(codigo)';
    END IF;

    -- Índice: idx_vtoken_email
    SELECT COUNT(*) INTO v_count FROM user_indexes WHERE index_name = 'IDX_VTOKEN_EMAIL';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'CREATE INDEX idx_vtoken_email ON verification_tokens(email)';
    END IF;

    -- Índice: idx_vtoken_tipo
    SELECT COUNT(*) INTO v_count FROM user_indexes WHERE index_name = 'IDX_VTOKEN_TIPO';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'CREATE INDEX idx_vtoken_tipo ON verification_tokens(tipo)';
    END IF;

    -- Índice: idx_vtoken_email_tipo
    SELECT COUNT(*) INTO v_count FROM user_indexes WHERE index_name = 'IDX_VTOKEN_EMAIL_TIPO';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'CREATE INDEX idx_vtoken_email_tipo ON verification_tokens(email, tipo)';
    END IF;

    -- Índice: idx_vtoken_expira_em
    SELECT COUNT(*) INTO v_count FROM user_indexes WHERE index_name = 'IDX_VTOKEN_EXPIRA_EM';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'CREATE INDEX idx_vtoken_expira_em ON verification_tokens(expira_em)';
    END IF;

    -- Índice: idx_vtoken_usado
    SELECT COUNT(*) INTO v_count FROM user_indexes WHERE index_name = 'IDX_VTOKEN_USADO';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'CREATE INDEX idx_vtoken_usado ON verification_tokens(usado)';
    END IF;

    DBMS_OUTPUT.PUT_LINE('✅ Todos os índices validados/criados.');
END;
/

-- Comentário final
SELECT '✅ Migration V5 concluída com sucesso!' AS status FROM dual;

