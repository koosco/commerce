-- Fix UNIQUE constraint issue with NULL parent_id
-- Use virtual computed column to treat NULL as -1

-- Step 1: Drop existing UNIQUE constraint
ALTER TABLE categories
    DROP INDEX uq_category_name_parent;

-- Step 2: Add virtual computed column that converts NULL to -1
ALTER TABLE categories
    ADD COLUMN parent_id_for_index BIGINT AS (COALESCE(parent_id, -1)) VIRTUAL;

-- Step 3: Create UNIQUE index on name and computed column
CREATE UNIQUE INDEX uq_category_name_parent
    ON categories (name, parent_id_for_index);