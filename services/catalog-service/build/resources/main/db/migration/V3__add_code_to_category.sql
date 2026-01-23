ALTER TABLE categories
    ADD COLUMN code VARCHAR(50) NOT NULL,
    ADD UNIQUE KEY uk_category_code(code);
