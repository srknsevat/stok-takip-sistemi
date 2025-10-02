-- Kullanıcı yönetimi tabloları
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    last_login TIMESTAMP,
    failed_login_attempts INT DEFAULT 0,
    locked_until TIMESTAMP,
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP,
    email_verification_token VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- İndeksler
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(is_active);
CREATE INDEX idx_users_email_verified ON users(email_verified);
CREATE INDEX idx_users_last_login ON users(last_login);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Varsayılan admin kullanıcısı (şifre: admin123)
INSERT INTO users (username, email, password, first_name, last_name, is_active, email_verified, created_at, updated_at) VALUES
('admin', 'admin@stoktakip.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin', 'User', TRUE, TRUE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Admin rolü atama
INSERT INTO user_roles (user_id, role) VALUES
((SELECT id FROM users WHERE username = 'admin'), 'SUPER_ADMIN');

-- Demo operatör kullanıcısı (şifre: operator123)
INSERT INTO users (username, email, password, first_name, last_name, is_active, email_verified, created_at, updated_at) VALUES
('operator', 'operator@stoktakip.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Operatör', 'User', TRUE, TRUE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Operatör rolü atama
INSERT INTO user_roles (user_id, role) VALUES
((SELECT id FROM users WHERE username = 'operator'), 'OPERATOR');
