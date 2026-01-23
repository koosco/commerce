CREATE TABLE categories
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    parent_id  BIGINT NULL,
    depth      INT          NOT NULL DEFAULT 0,
    ordering   INT          NOT NULL DEFAULT 0,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id) REFERENCES categories (id)
);

CREATE TABLE products
(
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    description         TEXT NULL,
    price               BIGINT       NOT NULL,
    status              VARCHAR(20)  NOT NULL, -- ACTIVE, INACTIVE, DELETED
    category_id         BIGINT NULL,
    thumbnail_image_url VARCHAR(500) NULL,
    brand               VARCHAR(100) NULL,
    created_at          DATETIME     NOT NULL,
    updated_at          DATETIME     NOT NULL,
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE product_option_groups
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT       NOT NULL,
    name       VARCHAR(100) NOT NULL,
    ordering   INT          NOT NULL DEFAULT 0,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    CONSTRAINT fk_option_group_product
        FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE product_options
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_group_id  BIGINT       NOT NULL,
    name             VARCHAR(100) NOT NULL,
    additional_price BIGINT       NOT NULL DEFAULT 0,
    ordering         INT          NOT NULL DEFAULT 0,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL,
    CONSTRAINT fk_option_group
        FOREIGN KEY (option_group_id) REFERENCES product_option_groups (id)
);
