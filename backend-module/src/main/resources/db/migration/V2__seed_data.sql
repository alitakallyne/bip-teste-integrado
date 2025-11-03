-- =========================================================
-- 🌱 Dados iniciais da tabela BENEFICIO
-- =========================================================

INSERT INTO BENEFICIO (NOME, DESCRICAO, SALDO, ATIVO)
VALUES
  ('Vale Alimentação', 'Benefício de alimentação mensal', 1500.00, TRUE),
  ('Vale Transporte', 'Créditos para transporte público', 300.00, TRUE),
  ('Auxílio Educação', 'Ajuda para cursos e treinamentos', 1000.00, TRUE),
  ('Auxílio Saúde', 'Auxílio médico e odontológico', 800.00, TRUE);

-- =========================================================
-- 💰 Dados iniciais da tabela TRANSFERENCIA (teste)
-- =========================================================

INSERT INTO TRANSFERENCIA (ID_ORIGEM, ID_DESTINO, VALOR, STATUS, IDEMPOTENCY_KEY)
VALUES
  (1, 2, 100.00, 'SUCESSO', 'TEST-KEY-001');
