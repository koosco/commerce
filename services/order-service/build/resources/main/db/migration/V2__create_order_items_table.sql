-- 주문 아이템 테이블
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '주문 ID',
    sku_id VARCHAR(255) NOT NULL COMMENT 'SKU ID (재고 관리 단위)',
    quantity INT NOT NULL COMMENT '주문 수량',
    unit_price BIGINT NOT NULL COMMENT '상품 단가',
    total_price BIGINT NOT NULL COMMENT '수량 * 단가',
    discount_amount BIGINT NOT NULL COMMENT '이 아이템에 분배된 할인 금액',
    refundable_amount BIGINT NOT NULL COMMENT '환불 가능 금액 (고정값)',
    status VARCHAR(50) NOT NULL COMMENT '주문 아이템 상태',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    FOREIGN KEY (order_id) REFERENCES orders(id),
    INDEX idx_order_id (order_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='주문 아이템 테이블';
