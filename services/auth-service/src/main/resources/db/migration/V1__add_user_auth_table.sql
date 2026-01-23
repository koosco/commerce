-- ==============================================
-- USER AUTH TABLE
-- ==============================================
CREATE TABLE user_auth
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    email         VARCHAR(255) NOT NULL,
    provider      VARCHAR(10)  NOT NULL DEFAULT 'LOCAL', -- LOCAL / KAKAO
    password      VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(500) NULL,

    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_user_auth_user_id ON user_auth (user_id);
CREATE INDEX idx_user_auth_email ON user_auth (email);

-- ==============================================
-- LOGIN HISTORY TABLE
-- ==============================================
CREATE TABLE login_history
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,

    ip         VARCHAR(50) NOT NULL,
    user_agent VARCHAR(500) NULL,
    login_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
