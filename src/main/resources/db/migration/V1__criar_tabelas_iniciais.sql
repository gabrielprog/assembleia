CREATE SCHEMA IF NOT EXISTS votacao;

CREATE TABLE IF NOT EXISTS votacao.sessoes (
    id UUID PRIMARY KEY,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS votacao.pautas (
    id UUID PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    sessao_id UUID UNIQUE,
    CONSTRAINT fk_sessao FOREIGN KEY (sessao_id) REFERENCES votacao.sessoes(id)
);

CREATE TABLE IF NOT EXISTS votacao.votos (
    id UUID PRIMARY KEY,
    pauta_id UUID NOT NULL,
    participante_id VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    voto VARCHAR(3) NOT NULL CHECK (voto IN ('SIM', 'NAO')),
    data_hora TIMESTAMP NOT NULL,
    CONSTRAINT fk_pauta FOREIGN KEY (pauta_id) REFERENCES votacao.pautas(id),
    CONSTRAINT un_participante_pauta UNIQUE (pauta_id, participante_id)
);
