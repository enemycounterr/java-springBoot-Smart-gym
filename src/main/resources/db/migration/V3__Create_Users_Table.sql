-- Шаг 1: Создаем пустую таблицу (её еще нет в базе)
CREATE TABLE system_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Шаг 2: Сразу же наполняем её стартовыми данными
INSERT INTO system_users (username, password, role) VALUES
('admin_danek', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN'),
('guard_vasya', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'GUARD');