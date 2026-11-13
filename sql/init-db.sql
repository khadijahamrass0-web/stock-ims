-- ============================================================
-- StockIMS — création de la base et du compte applicatif
-- À exécuter une seule fois avec un compte administrateur MySQL
-- (root), par exemple dans MySQL Workbench.
--
-- Ce script ne modifie pas le mot de passe root.
-- Il crée la base stock_ims et l'utilisateur stock_app utilisé
-- par l'application Spring Boot.
-- ============================================================

CREATE DATABASE IF NOT EXISTS stock_ims
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Compte utilisé par l'application (mot de passe local de TP).
CREATE USER IF NOT EXISTS 'stock_app'@'localhost' IDENTIFIED BY 'StockApp!2026';
CREATE USER IF NOT EXISTS 'stock_app'@'127.0.0.1' IDENTIFIED BY 'StockApp!2026';

-- Si le compte existait déjà avec un autre mot de passe, on l'aligne.
ALTER USER 'stock_app'@'localhost' IDENTIFIED BY 'StockApp!2026';
ALTER USER 'stock_app'@'127.0.0.1' IDENTIFIED BY 'StockApp!2026';

GRANT ALL PRIVILEGES ON stock_ims.* TO 'stock_app'@'localhost';
GRANT ALL PRIVILEGES ON stock_ims.* TO 'stock_app'@'127.0.0.1';

FLUSH PRIVILEGES;

-- Vérification rapide :
SHOW DATABASES LIKE 'stock_ims';
