-- ==============================================
-- USERS TABLE
-- ==============================================
CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    name       VARCHAR(100) NOT NULL,
    phone      VARCHAR(20) NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / INACTIVE / BLOCKED
    provider   VARCHAR(50)  NOT NULL DEFAULT 'LOCAL', -- LOCAL / KAKAO
    role       VARCHAR(50) NOT NULL DEFAULT 'USER',


    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT uq_user_email_provider UNIQUE (email, provider)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- provider별 이메일 로그인 조회 빠르게
CREATE INDEX idx_users_email_provider ON users (email, provider);


-- ==============================================
-- USER ADDRESS TABLE
-- ==============================================
CREATE TABLE user_address
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,

    address_name VARCHAR(100) NOT NULL,
    recipient    VARCHAR(100) NOT NULL,
    phone        VARCHAR(20)  NOT NULL,

    postal_code  VARCHAR(20)  NOT NULL,
    address1     VARCHAR(255) NOT NULL,
    address2     VARCHAR(255) NULL,

    is_default   TINYINT(1) NOT NULL DEFAULT 0,

    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_address_user_id FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_user_address_user_id ON user_address (user_id);

