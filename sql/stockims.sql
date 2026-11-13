-- ============================================================
-- StockIMS — structure et données de test
-- Base : stock_ims (MySQL 8)
--
-- ATTENTION : ce script supprime puis recrée les trois tables.
-- À exécuter avec le compte stock_app (ou root), application arrêtée.
--
-- Jeu de test :
--   TIS-001  Rouleau de coton : +20 − 7           = 13
--   TIS-002  Rouleau de lin   : aucun mouvement    = 0
--   FIL-001  Bobine de fil    : +40 + 15 − 12      = 43
-- ============================================================

CREATE DATABASE IF NOT EXISTS stock_ims
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE stock_ims;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS sortie;
DROP TABLE IF EXISTS entree;
DROP TABLE IF EXISTS article;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE article (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    reference   VARCHAR(20)  NOT NULL,
    designation VARCHAR(80)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_article_reference (reference)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE entree (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    article_id      BIGINT NOT NULL,
    quantite        INT    NOT NULL,
    date_mouvement  DATE   NOT NULL,
    PRIMARY KEY (id),
    KEY idx_entree_article (article_id),
    CONSTRAINT fk_entree_article
        FOREIGN KEY (article_id) REFERENCES article (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE sortie (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    article_id      BIGINT NOT NULL,
    quantite        INT    NOT NULL,
    date_mouvement  DATE   NOT NULL,
    PRIMARY KEY (id),
    KEY idx_sortie_article (article_id),
    CONSTRAINT fk_sortie_article
        FOREIGN KEY (article_id) REFERENCES article (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO article (reference, designation) VALUES
    ('TIS-001', 'Rouleau de coton'),
    ('TIS-002', 'Rouleau de lin'),
    ('FIL-001', 'Bobine de fil');

SET @id_tis1 = (SELECT id FROM article WHERE reference = 'TIS-001');
SET @id_tis2 = (SELECT id FROM article WHERE reference = 'TIS-002');
SET @id_fil1 = (SELECT id FROM article WHERE reference = 'FIL-001');

INSERT INTO entree (article_id, quantite, date_mouvement) VALUES
    (@id_tis1, 20, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
    (@id_fil1, 40, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
    (@id_fil1, 15, DATE_SUB(CURDATE(), INTERVAL 3 DAY));

INSERT INTO sortie (article_id, quantite, date_mouvement) VALUES
    (@id_tis1, 7, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
    (@id_fil1, 12, DATE_SUB(CURDATE(), INTERVAL 1 DAY));

-- Contrôles attendus
SELECT a.reference,
       a.designation,
       COALESCE((SELECT SUM(e.quantite) FROM entree e WHERE e.article_id = a.id), 0)
     - COALESCE((SELECT SUM(s.quantite) FROM sortie s WHERE s.article_id = a.id), 0) AS stock
FROM article a
ORDER BY a.reference;
