CREATE TABLE IF NOT EXISTS notificador (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(20) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    endpoint TEXT NOT NULL,
    p256dh TEXT NOT NULL,
    auth TEXT NOT NULL
    );

CREATE INDEX idx_notificador_user_id ON notificador(user_id);