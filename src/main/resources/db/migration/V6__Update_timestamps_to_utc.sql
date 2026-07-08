-- Обновляем тип колонки для логов проходов
ALTER TABLE access_logs
ALTER COLUMN time_stamp TYPE TIMESTAMP WITH TIME ZONE;

-- Обновляем тип колонки для токенов
ALTER TABLE refresh_tokens
ALTER COLUMN expires_at TYPE TIMESTAMP WITH TIME ZONE;