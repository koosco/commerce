-- =========================
-- CATEGORY
-- =========================

INSERT INTO categories (id, name, parent_id, depth, ordering, created_at, updated_at)
VALUES
    (1, '패션', NULL, 0, 0, NOW(), NOW()),
    (2, '여성 의류', 1, 1, 0, NOW(), NOW()),
    (3, '남성 의류', 1, 1, 1, NOW(), NOW()),
    (4, '아우터', 2, 2, 0, NOW(), NOW()),
    (5, '상의', 3, 2, 0, NOW(), NOW()),
    (6, '바지', 3, 2, 1, NOW(), NOW());


-- =========================
-- PRODUCT
-- =========================

INSERT INTO products (id, name, description, price, status, category_id, thumbnail_image_url, brand, created_at, updated_at)
VALUES
    (100, '기본 울 코트', '겨울용 고급 울 코트입니다.', 129000, 'ACTIVE', 4,
     'https://example.com/images/coat1.jpg', 'KOOSCO', NOW(), NOW()),

    (101, '오버핏 양털 자켓', '보온성이 뛰어나며 가벼운 오버핏 양털 자켓입니다.', 89000, 'ACTIVE', 4,
     'https://example.com/images/jacket1.jpg', 'KOOSCO', NOW(), NOW()),

    (102, '남성 크루넥 니트', '부드러운 촉감의 남성용 크루넥 니트.', 49000, 'ACTIVE', 5,
     'https://example.com/images/knit1.jpg', 'KOOSCO', NOW(), NOW()),

    (103, '남성 슬림핏 청바지', '슬림하게 떨어지는 핏의 기본 청바지.', 59000, 'ACTIVE', 6,
     'https://example.com/images/jeans1.jpg', 'KOOSCO', NOW(), NOW());


-- =========================
-- PRODUCT OPTION GROUPS
-- =========================

INSERT INTO product_option_groups (id, product_id, name, ordering, created_at, updated_at)
VALUES
    (1001, 100, '색상', 0, NOW(), NOW()),
    (1002, 100, '사이즈', 1, NOW(), NOW()),
    (1003, 101, '색상', 0, NOW(), NOW()),
    (1004, 102, '색상', 0, NOW(), NOW()),
    (1005, 102, '사이즈', 1, NOW(), NOW()),
    (1006, 103, '사이즈', 0, NOW(), NOW());


-- =========================
-- PRODUCT OPTIONS
-- =========================

INSERT INTO product_options (id, option_group_id, name, additional_price, ordering, created_at, updated_at)
VALUES
    -- 기본 울 코트(100)
    (2001, 1001, '블랙', 0, 0, NOW(), NOW()),
    (2002, 1001, '베이지', 0, 1, NOW(), NOW()),
    (2003, 1002, 'S', 0, 0, NOW(), NOW()),
    (2004, 1002, 'M', 0, 1, NOW(), NOW()),
    (2005, 1002, 'L', 0, 2, NOW(), NOW()),

    -- 오버핏 양털 자켓(101)
    (2006, 1003, '아이보리', 0, 0, NOW(), NOW()),
    (2007, 1003, '브라운', 0, 1, NOW(), NOW()),

    -- 남성 니트(102)
    (2008, 1004, '네이비', 0, 0, NOW(), NOW()),
    (2009, 1004, '그레이', 0, 1, NOW(), NOW()),
    (2010, 1005, 'M', 0, 0, NOW(), NOW()),
    (2011, 1005, 'L', 0, 1, NOW(), NOW()),

    -- 남성 청바지(103)
    (2012, 1006, '28', 0, 0, NOW(), NOW()),
    (2013, 1006, '30', 0, 1, NOW(), NOW()),
    (2014, 1006, '32', 0, 2, NOW(), NOW());
