
UPDATE votacao.votos SET voto = 'YES' WHERE voto = 'SIM';
UPDATE votacao.votos SET voto = 'NO' WHERE voto = 'NAO';

ALTER TABLE votacao.votos DROP CONSTRAINT IF EXISTS votos_voto_check;
ALTER TABLE votacao.votos ADD CONSTRAINT votos_voto_check CHECK (voto IN ('YES', 'NO'));
