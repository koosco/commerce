-- ===========================================
-- Commerce DB Schema Initialization
-- ===========================================
-- This script creates all database schemas
-- used by the commerce services.
-- ===========================================

CREATE DATABASE IF NOT EXISTS `commerce-user` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `commerce-catalog` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `commerce-inventory` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `commerce-order` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `commerce-payment` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `commerce-search` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant privileges to admin user
GRANT ALL PRIVILEGES ON `commerce-user`.* TO 'admin'@'%';
GRANT ALL PRIVILEGES ON `commerce-catalog`.* TO 'admin'@'%';
GRANT ALL PRIVILEGES ON `commerce-inventory`.* TO 'admin'@'%';
GRANT ALL PRIVILEGES ON `commerce-order`.* TO 'admin'@'%';
GRANT ALL PRIVILEGES ON `commerce-payment`.* TO 'admin'@'%';
GRANT ALL PRIVILEGES ON `commerce-search`.* TO 'admin'@'%';

FLUSH PRIVILEGES;
