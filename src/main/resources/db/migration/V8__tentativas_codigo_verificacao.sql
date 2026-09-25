-- ============================================================
-- V8 - Limite de tentativas nos códigos de verificação
--   Os códigos têm 6 dígitos: sem limite, dava para testar todos
--   (força bruta) e redefinir a senha de qualquer conta.
--   Depois de 5 erros o código é invalidado e é preciso pedir outro.
-- ============================================================

ALTER TABLE verification_tokens
    ADD COLUMN tentativas INTEGER NOT NULL DEFAULT 0;
