CREATE TABLE inventory
(
    sku_id         VARCHAR(50)   NOT NULL,
    total_stock    INT      NOT NULL DEFAULT 0,
    reserved_stock INT      NOT NULL DEFAULT 0,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (sku_id)
);

-- total_stock - reserved_stock >= 0 보장
ALTER TABLE inventory
    ADD CONSTRAINT ck_inventory_stock_non_negative
        CHECK (total_stock >= 0 AND reserved_stock >= 0 AND total_stock >= reserved_stock);
