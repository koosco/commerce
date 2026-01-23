-- Payment Service Database Schema

-- ============================================================================
-- Payment Table
-- ============================================================================
-- 결제 기본 정보를 저장하는 테이블
-- payment_id: 외부 노출용 UUID 식별자
-- order_id: 주문 ID (외부 시스템 참조)
-- user_id: 사용자 ID
-- amount: 결제 금액
-- status: 결제 상태 (READY, APPROVED, FAILED, CANCELED)
-- ============================================================================
CREATE TABLE payment
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id CHAR(36)    NOT NULL UNIQUE COMMENT 'UUID 외부 식별자',
    order_id   BIGINT      NOT NULL COMMENT '주문 ID',
    user_id    BIGINT      NOT NULL COMMENT '사용자 ID',
    amount     BIGINT      NOT NULL COMMENT '결제 금액',
    status     VARCHAR(20) NOT NULL COMMENT '결제 상태 (READY, APPROVED, FAILED, CANCELED)',
    CONSTRAINT ck_payment_amount CHECK (amount >= 0),
    CONSTRAINT ck_payment_status CHECK (status IN ('READY', 'APPROVED', 'FAILED', 'CANCELED'))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '결제 기본 정보';


-- ============================================================================
-- Payment Transaction Table
-- ============================================================================
-- 결제 트랜잭션 이력을 저장하는 테이블
-- payment_id: Payment 테이블 외래키
-- type: 트랜잭션 타입 (APPROVAL, CANCEL)
-- status: 트랜잭션 상태 (SUCCESS, FAILED)
-- pg_transaction_id: PG사 트랜잭션 ID (Nullable)
-- amount: 트랜잭션 금액
-- occurred_at: 트랜잭션 발생 시각
-- ============================================================================
CREATE TABLE payment_transaction
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id        BIGINT      NOT NULL COMMENT 'Payment 외래키 (payment.id)',
    type              VARCHAR(20) NOT NULL COMMENT '트랜잭션 타입 (APPROVAL, CANCEL)',
    status            VARCHAR(20) NOT NULL COMMENT '트랜잭션 상태 (SUCCESS, FAILED)',
    pg_transaction_id VARCHAR(255) DEFAULT NULL COMMENT 'PG사 트랜잭션 ID',
    amount            BIGINT      NOT NULL COMMENT '트랜잭션 금액',
    occurred_at       DATETIME(6) NOT NULL COMMENT '트랜잭션 발생 시각',
    CONSTRAINT fk_payment_transaction_payment
        FOREIGN KEY (payment_id) REFERENCES payment (id)
            ON DELETE RESTRICT
            ON UPDATE RESTRICT,
    CONSTRAINT ck_payment_transaction_type CHECK (type IN ('APPROVAL', 'CANCEL')),
    CONSTRAINT ck_payment_transaction_status CHECK (status IN ('SUCCESS', 'FAILED')),
    CONSTRAINT ck_payment_transaction_amount CHECK (amount >= 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '결제 트랜잭션 이력';


-- ============================================================================
-- Payment Idempotency Table
-- ============================================================================
-- 결제 작업의 멱등성을 보장하기 위한 테이블
-- order_id: 주문 ID
-- action: 결제 작업 타입 (CREATE, APPROVE, CANCEL)
-- idempotency_key: 멱등성 키
-- created_at: 생성 시각
-- ============================================================================
CREATE TABLE payment_idempotency
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT       NOT NULL COMMENT '주문 ID',
    action          VARCHAR(20)  NOT NULL COMMENT '결제 작업 타입 (CREATE, APPROVE, CANCEL)',
    idempotency_key VARCHAR(255) NOT NULL COMMENT '멱등성 키',
    created_at      DATETIME(6)  NOT NULL COMMENT '생성 시각',
    CONSTRAINT uq_payment_idempotency UNIQUE (order_id, action, idempotency_key),
    CONSTRAINT ck_payment_idempotency_action CHECK (action IN ('CREATE', 'APPROVE', 'CANCEL'))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '결제 멱등성 보장';
