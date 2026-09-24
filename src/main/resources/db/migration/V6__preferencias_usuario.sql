-- ============================================================
-- V6 - Preferencias do usuario (tela "Minha Conta" > Preferencias)
--   receber_novidades : receber novidades e lancamentos (marcado por padrao)
--   alertas_reposicao : alertas de reposicao de estoque (desmarcado por padrao)
-- Os valores padrao sao os mesmos que a tela ja exibia antes de salvar.
-- ============================================================

ALTER TABLE usuarios
    ADD COLUMN receber_novidades BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN alertas_reposicao BOOLEAN NOT NULL DEFAULT FALSE;
