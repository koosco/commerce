CREATE TABLE product_skus
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku_id        VARCHAR(100) NOT NULL UNIQUE COMMENT '비즈니스 SKU 식별자',
    product_id    BIGINT       NOT NULL COMMENT 'products 테이블 FK',
    price         BIGINT       NOT NULL COMMENT 'SKU 가격',
    option_values JSON COMMENT '옵션 조합 JSON 형태',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_skus_product
        FOREIGN KEY (product_id) REFERENCES products (id)
            ON DELETE CASCADE
);

-- 조회 성능 향상을 위해 인덱스 추가
CREATE INDEX idx_product_skus_product_id ON product_skus (product_id);
