-- category uk
ALTER TABLE categories
    ADD UNIQUE KEY uq_category_name_parent(name, parent_id);

-- product: add product_code column with default value first
ALTER TABLE products
    ADD COLUMN product_code VARCHAR(50) NULL AFTER id;

-- then make it NOT NULL and add unique constraint
ALTER TABLE products
    MODIFY COLUMN product_code VARCHAR(50) NOT NULL,
    ADD UNIQUE KEY uq_product_code(product_code);

-- product option groups uk
ALTER TABLE product_option_groups
    ADD UNIQUE KEY uq_option_group_name(product_id, name);

-- product options uk
ALTER TABLE product_options
    ADD UNIQUE KEY uq_option_name(option_group_id, name);

-- product_skus.sku_id already has UNIQUE constraint from V2, skip
