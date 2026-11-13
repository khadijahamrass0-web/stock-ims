-- ============================================================
-- StockIMS — remise à zéro des données de démonstration
-- Conserve les trois tables, efface les mouvements et les articles,
-- puis réinsère le jeu de test.
-- À exécuter application arrêtée, avec le compte stock_app.
-- ============================================================

USE stock_ims;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE sortie;
TRUNCATE TABLE entree;
TRUNCATE TABLE article;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO article (reference, designation) VALUES
    ('TIS-001', 'Rouleau de coton'),
    ('TIS-002', 'Rouleau de lin'),
    ('FIL-001', 'Bobine de fil');

SET @id_tis1 = (SELECT id FROM article WHERE reference = 'TIS-001');
SET @id_fil1 = (SELECT id FROM article WHERE reference = 'FIL-001');

INSERT INTO entree (article_id, quantite, date_mouvement) VALUES
    (@id_tis1, 20, DATE_SUB(CURDATE(), INTERVAL 4 DAY)),
    (@id_fil1, 40, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),
    (@id_fil1, 15, DATE_SUB(CURDATE(), INTERVAL 3 DAY));

INSERT INTO sortie (article_id, quantite, date_mouvement) VALUES
    (@id_tis1, 7, DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
    (@id_fil1, 12, DATE_SUB(CURDATE(), INTERVAL 1 DAY));
