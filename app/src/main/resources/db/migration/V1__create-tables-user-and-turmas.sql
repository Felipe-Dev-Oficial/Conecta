CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(20) PRIMARY KEY,
    nome VARCHAR(250) NOT NULL,
    email TEXT NOT NULL,
    numero TEXT,
    senha TEXT NOT NULL,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('ALUNO', 'PROFESSOR', 'SECRETARIA', 'DESATIVADO')),
    password_updater_token TEXT,
    password_updater_token_expiration TIMESTAMP,
    email_updater_token TEXT,
    email_updater_token_expiration TIMESTAMP
    );
CREATE TABLE IF NOT EXISTS turmas (
    id UUID PRIMARY KEY,
    curso VARCHAR(43) NOT NULL CHECK (curso IN (
        'ADMINISTRACAO',
        'CONTABILIDADE',
        'DESENVOLVIMENTO_DE_SISTEMAS',
        'LOGISTICA',
        'SERVICOS_JURIDICOS',
        'MATUTINO_ADMINISTRACAO_MTEC',
        'MATUTINO_CONTABILIDADE_MTEC',
        'MATUTINO_DESENVOLVIMENTO_DE_SISTEMAS_MTEC',
        'MATUTINO_LOGISTICA_MTEC',
        'MATUTINO_RECURSOS_HUMANOS_MTEC',
        'VESPERTINO_ADMINISTRACAO_MTEC',
        'VESPERTINO_CONTABILIDADE_MTEC',
        'VESPERTINO_DESENVOLVIMENTO_DE_SISTEMAS_MTEC',
        'VESPERTINO_LOGISTICA_MTEC',
        'VESPERTINO_RECURSOS_HUMANOS_MTEC',
        'LOGISTICA_MTEC_N',
        'DESENVOLVIMENTO_DE_SISTEMAS_AMS')
    ),
    modulos INTEGER NOT NULL CHECK (modulos >= 1),
    atual INTEGER NOT NULL CHECK (atual >= 1 AND atual <= modulos),
    status VARCHAR(3)NOT NULL CHECK (status IN ('ON', 'OFF'))
    );
CREATE TABLE IF NOT EXISTS aluno_turmas (
    aluno_id VARCHAR(20) REFERENCES users(id),
    turma_id UUID REFERENCES turmas(id),
    data_matricula TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (aluno_id, turma_id)
    );
CREATE TABLE IF NOT EXISTS professor_turmas (
    professor_id VARCHAR(20) REFERENCES users(id),
    turma_id UUID REFERENCES turmas(id),
    data_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (professor_id, turma_id)
    );
