-- 주문 테이블
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '주문한 사용자 ID',
    status VARCHAR(50) NOT NULL COMMENT '주문 상태',
    total_amount BIGINT NOT NULL COMMENT '주문 원금 (아이템 합계)',
    discount_amount BIGINT NOT NULL COMMENT '쿠폰으로 할인된 총 금액',
    payable_amount BIGINT NOT NULL COMMENT '실제 결제 요청 금액',
    refunded_amount BIGINT NOT NULL DEFAULT 0 COMMENT '누적 환불 금액',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='주문 테이블';
