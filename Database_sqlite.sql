PRAGMA foreign_keys = OFF;

BEGIN TRANSACTION;

DROP TABLE IF EXISTS "Question_Theme";

DROP TABLE IF EXISTS "gap_option";

DROP TABLE IF EXISTS "gap_field";

DROP TABLE IF EXISTS "mc_answer";

DROP TABLE IF EXISTS "question";

DROP TABLE IF EXISTS "question_set";

DROP TABLE IF EXISTS "theme";

DROP TABLE IF EXISTS "team";

CREATE TABLE "team" (
  `team_id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `name` TEXT NOT NULL
);

CREATE TABLE "theme" (
  `theme_id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `name` TEXT NOT NULL,
  `description` TEXT
);

CREATE TABLE "question_set" (
  `question_set_id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `team_id` INTEGER NOT NULL,
  `title` TEXT NOT NULL,
  CONSTRAINT `fk_question_set_team`
    FOREIGN KEY (`team_id`) REFERENCES `team`(`team_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE "question" (
  `question_id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `question_set_id` INTEGER NOT NULL,
  `question_type` TEXT NOT NULL,
  `start_text` TEXT,
  `image_url` TEXT,
  `end_text` TEXT,
  `allows_multiple` INTEGER NOT NULL DEFAULT 0,
  `points` INTEGER NOT NULL DEFAULT 1,
  CONSTRAINT `fk_question_question_set`
    FOREIGN KEY (`question_set_id`) REFERENCES `question_set`(`question_set_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE "Question_Theme" (
  `question_id` INTEGER NOT NULL,
  `theme_id` INTEGER NOT NULL,
  PRIMARY KEY (`question_id`, `theme_id`),
  CONSTRAINT `fk_qt_question`
    FOREIGN KEY (`question_id`) REFERENCES `question`(`question_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_qt_theme`
    FOREIGN KEY (`theme_id`) REFERENCES `theme`(`theme_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

CREATE TABLE "mc_answer" (
  `answer_id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `question_id` INTEGER NOT NULL,
  `option_text` TEXT NOT NULL,
  `is_correct` INTEGER NOT NULL DEFAULT 0,
  `option_order` INTEGER NOT NULL,
  UNIQUE (`question_id`, `option_order`),
  CONSTRAINT `fk_mc_answer_question`
    FOREIGN KEY (`question_id`) REFERENCES `question`(`question_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE "gap_field" (
  `gap_id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `question_id` INTEGER NOT NULL,
  `gap_index` INTEGER NOT NULL,
  `text_before` TEXT,
  `text_after` TEXT,
  UNIQUE (`question_id`, `gap_index`),
  CONSTRAINT `fk_gap_field_question`
    FOREIGN KEY (`question_id`) REFERENCES `question`(`question_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE "gap_option" (
  `gap_option_id` INTEGER PRIMARY KEY AUTOINCREMENT,
  `gap_id` INTEGER NOT NULL,
  `option_text` TEXT NOT NULL,
  `is_correct` INTEGER NOT NULL DEFAULT 0,
  `option_order` INTEGER NOT NULL,
  UNIQUE (`gap_id`, `option_order`),
  CONSTRAINT `fk_gap_option_gap`
    FOREIGN KEY (`gap_id`) REFERENCES `gap_field`(`gap_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (3, 'Wirtschaft', 'Alles, was NICHT mit Recht, aber mit Wirtschaft zu tun hat (Kalkulation, Prozesse,...)');

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (4, 'Datenbanken Modellierung', 'ERD, relationales Tabellenmodell');

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (5, 'Datenbank - SQL', NULL);

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (6, 'UML', 'Klassendiagramm, Sequenzdiagramm, Zustandsdiagramm, Use-Case Diagramm, Aktivitätsdiagramm');

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (7, 'Maschinelles Lernen', NULL);

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (8, 'Programmierung Pseudocode', NULL);

INSERT INTO `team` (`team_id`, `name`) VALUES (1, 'Bazinga');

INSERT INTO `question_set` (`question_set_id`, `team_id`, `title`) VALUES (1, 1, 'Questions');

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (9, 'NORMALISIERUNG', NULL);

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (10, 'SELECT_ABFRAGEN', NULL);

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (11, 'ER_MODELLIERUNG', NULL);

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (12, 'JOINS_SUBQUERIES', NULL);

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (13, 'SQL_GRUNDLAGEN', NULL);

INSERT INTO `theme` (`theme_id`, `name`, `description`) VALUES (14, 'DDL_DML', NULL);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (1, 1, 'MC', 'Welche Aussage zur 2. Normalform (2NF) ist korrekt?', NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (1, 9);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (1, 1, 'Sie erlaubt transitive Abhängigkeite.', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (2, 1, 'Sie beseitigt partielle Abhängigkeiten von einem zusammengesetzten Primärschlüssel', 1, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (3, 1, 'Sie verlangt ausschließlich atomare Attribute', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (4, 1, 'Sie ist nur bei 1:1-Beziehungen relevant', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (2, 1, 'MC', 'Welche Aussagen zur Normalisierung sind korrekt?', NULL, NULL, 1, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (2, 9);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (5, 2, 'Die 1. Normalform verlangt atomare Attribute', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (6, 2, 'Die 2. Normalform verhindert transitive Abhängigkeiten', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (7, 2, 'Die 3. Normalform verhindert partielle Abhängigkeiten', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (8, 2, 'Die 3. Normalform verhindert transitive Abhängigkeiten', 1, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (3, 1, 'MC', 'Welche SQL-Statements sind syntaktisch korrekt und sinnvoll?', NULL, NULL, 1, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (3, 10);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (9, 3, 'SELECT k.name, COUNT(b.id)
FROM kunde k
INNER JOIN bestellung b ON k.id = b.kunden_id
GROUP BY k.name;', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (10, 3, 'SELECT k.name, COUNT(b.id)
FROM kunde k
INNER JOIN bestellung b ON k.id = b.kunden_id;', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (11, 3, 'SELECT k.name
FROM kunde k
LEFT JOIN bestellung b ON k.id = b.kunden_id
WHERE b.id IS NULL;', 1, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (12, 3, 'SELECT name, COUNT(*)
FROM kunde
GROUP BY id;', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (4, 1, 'MC', 'Welche Aussagen zur Umsetzung von Beziehungen im relationalen Modell sind korrekt?', NULL, NULL, 1, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (4, 11);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (13, 4, '1:n-Beziehungen werden durch einen Fremdschlüssel auf der n-Seite umgesetzt', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (14, 4, '1:1-Beziehungen benötigen immer zwei Fremdschlüssel', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (15, 4, 'Beziehungen können durch Fremdschlüssel technisch realisiert werden', 1, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (16, 4, 'n:m-Beziehungen erfordern eine zusätzliche Tabelle', 1, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (5, 1, 'TF', 'Ein Fremdschlüssel kann auf einen Primärschlüssel oder auf einen UNIQUE-Constraint einer anderen Tabelle zeigen.', NULL, NULL, 0, 3);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (5, 11);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (17, 5, 'Wahr', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (18, 5, 'Falsch', 0, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (6, 1, 'TF', 'Ein JOIN in SQL ist ausschließlich für die Anzeige von Daten notwendig, er ändert die Tabellenstruktur nicht.', NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (6, 12);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (19, 6, 'Wahr', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (20, 6, 'Falsch', 0, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (7, 1, 'TF', 'Ein LEFT JOIN liefert nur Datensätze, die in beiden Tabellen übereinstimmen.', NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (7, 12);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (21, 7, 'Wahr', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (22, 7, 'Falsch', 1, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (8, 1, 'TF', 'Ein Subquery in der WHERE-Klausel kann mehrere Werte zurückgeben, wenn die Bedingung = verwendet wird.', NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (8, 12);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (23, 8, 'Wahr', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (24, 8, 'Falsch', 1, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (9, 1, 'GAP', NULL, NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (9, 12);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (1, 9, 1, 'Gegeben sind die Tabellen kunde(id, name) und bestellung(id, kunden_id, betrag). Gesucht wird die Gesamtsumme aller 
Bestellungen pro Kunde auch wenn ein Kunde keine Bestellungen hat.

SELECT k.', ', SUM(b.');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (1, 1, 'name', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (2, 1, 'betrag', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (3, 1, 'kunde', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (4, 1, 'bestellung', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (2, 9, 2, ', SUM(b.', ') 
FROM');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (5, 2, 'betrag', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (6, 2, 'name', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (7, 2, 'kunde', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (8, 2, 'bestellung', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (3, 9, 3, ') 
FROM', 'k 
LEFT JOIN');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (9, 3, 'kunde', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (10, 3, 'name', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (11, 3, 'betrag', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (12, 3, 'bestellung', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (4, 9, 4, 'k 
LEFT JOIN', 'b ON k.id = b.kunden_id 
GROUP BY k.');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (13, 4, 'bestellung', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (14, 4, 'name', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (15, 4, 'betrag', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (16, 4, 'kunde', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (5, 9, 5, 'b ON k.id = b.kunden_id 
GROUP BY k.', ';');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (17, 5, 'name', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (18, 5, 'betrag', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (19, 5, 'kunde', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (20, 5, 'bestellung', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (10, 1, 'GAP', NULL, NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (10, 11);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (6, 10, 1, 'Die', 'beschreibt eine');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (21, 6, 'Kardinalitäten', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (22, 6, 'Beziehung', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (23, 6, '1:n', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (24, 6, 'Fremdschlüssel', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (7, 10, 2, 'beschreibt eine', 'zwischen zwei Entitäten.
Eine');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (25, 7, 'Beziehung', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (26, 7, 'Kardinalitäten', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (27, 7, '1:n', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (28, 7, 'Fremdschlüssel', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (8, 10, 3, 'zwischen zwei Entitäten.
Eine', '-Beziehung bedeutet zum Beispiel, dass ein Datensatz der ersten Entität mehreren Datensätzen der zweiten Entität 
zugeordnet sein kann.
Diese Beziehung wird in relationalen Tabellen durch einen');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (29, 8, '1:n', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (30, 8, 'Kardinalitäten', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (31, 8, 'Beziehung', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (32, 8, 'Fremdschlüssel', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (9, 10, 4, '-Beziehung bedeutet zum Beispiel, dass ein Datensatz der ersten Entität mehreren Datensätzen der zweiten Entität 
zugeordnet sein kann.
Diese Beziehung wird in relationalen Tabellen durch einen', 'auf der');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (33, 9, 'Fremdschlüssel', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (34, 9, 'Kardinalitäten', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (35, 9, 'Beziehung', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (36, 9, '1:n', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (10, 10, 5, 'auf der', '-Seite umgesetzt.');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (37, 10, 'n', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (38, 10, 'Kardinalitäten', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (39, 10, 'Beziehung', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (40, 10, '1:n', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (11, 1, 'MC', 'Welche Aussage zur 3. Normalform (3NF) ist korrekt?', NULL, NULL, 0, 3);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (11, 9);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (25, 11, '3NF bedeutet, dass jede Tabelle genau einen Primärschlüssel haben muss.', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (26, 11, '3NF ist erfüllt, wenn keine transitiven Abhängigkeiten von Nicht-Schlüsselattributen auf den Primärschlüssel bestehen.', 1, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (27, 11, '3NF erlaubt partielle Abhängigkeiten, solange es einen zusammengesetzten Schlüssel gibt.', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (28, 11, '3NF ist nur bei Tabellen ohne Fremdschlüssel relevant.', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (12, 1, 'MC', 'Welche Aussagen zu Primärschlüssel und UNIQUE sind korrekt?', NULL, NULL, 1, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (12, 13);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (29, 12, 'Ein Primärschlüssel ist eindeutig und darf nicht NULL sein.', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (30, 12, 'Ein UNIQUE-Constraint erlaubt grundsätzlich keine NULL-Werte.', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (31, 12, 'Eine Tabelle kann mehrere UNIQUE-Constraints besitzen.', 1, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (32, 12, 'Eine Tabelle kann mehrere Primärschlüssel besitzen.', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (13, 1, 'MC', 'Welche Aussagen zu Indizes sind korrekt?', NULL, NULL, 1, 3);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (13, 13);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (33, 13, 'Indizes können SELECT-Abfragen (z. B. mit WHERE) beschleunigen.', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (34, 13, 'Indizes beschleunigen INSERT/UPDATE/DELETE immer, weil weniger gesucht werden muss.', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (35, 13, 'Ein Index verhindert automatisch doppelte Werte in einer Spalte.', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (36, 13, 'Ein Index benötigt zusätzlichen Speicherplatz.', 1, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (14, 1, 'MC', 'Welche Aussagen zu GROUP BY und Aggregatfunktionen sind korrekt?', NULL, NULL, 1, 3);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (14, 10);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (37, 14, 'In einer SELECT-Liste dürfen bei GROUP BY nur Aggregatfunktionen stehen.', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (38, 14, 'Alle nicht-aggregierten Spalten in SELECT müssen in der GROUP BY-Klausel enthalten sein.', 1, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (39, 14, 'HAVING filtert Gruppen nach der Aggregation.', 1, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (40, 14, 'WHERE kann nur nach GROUP BY verwendet werden.', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (15, 1, 'TF', 'Ein Fremdschlüssel darf NULL sein, wenn die Beziehung optional ist.', NULL, NULL, 0, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (15, 11);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (41, 15, 'Wahr', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (42, 15, 'Falsch', 0, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (16, 1, 'TF', 'Die WHERE-Klausel wird nach der GROUP BY-Klausel ausgeführt und kann aggregierte Werte filtern.', NULL, NULL, 0, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (16, 10);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (43, 16, 'Wahr', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (44, 16, 'Falsch', 1, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (17, 1, 'TF', 'Ein INNER JOIN liefert nur Datensätze, für die in beiden Tabellen passende Zeilen existieren.', NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (17, 12);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (45, 17, 'Wahr', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (46, 17, 'Falsch', 0, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (18, 1, 'TF', 'Ein UNIQUE-Constraint erlaubt in SQL niemals mehrere NULL-Werte in derselben Spalte.', NULL, NULL, 0, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (18, 13);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (47, 18, 'Wahr', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (48, 18, 'Falsch', 1, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (19, 1, 'GAP', NULL, NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (19, 11);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (11, 19, 1, 'Ein', 'ist ein Attribut (oder eine Attributkombination), das einen Datensatz eindeutig identifiziert.
Ein');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (41, 11, 'Primärschlüssel', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (42, 11, 'Fremdschlüssel', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (43, 11, 'Beziehung', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (44, 11, 'id', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (12, 19, 2, 'ist ein Attribut (oder eine Attributkombination), das einen Datensatz eindeutig identifiziert.
Ein', 'verweist auf einen Datensatz in einer anderen Tabelle und stellt so eine');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (45, 12, 'Fremdschlüssel', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (46, 12, 'Primärschlüssel', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (47, 12, 'Beziehung', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (48, 12, 'id', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (13, 19, 3, 'verweist auf einen Datensatz in einer anderen Tabelle und stellt so eine', 'zwischen Tabellen her.');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (49, 13, 'Beziehung', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (50, 13, 'Primärschlüssel', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (51, 13, 'Fremdschlüssel', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (52, 13, 'id', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (20, 1, 'GAP', NULL, NULL, NULL, 0, 4);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (20, 12);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (14, 20, 1, 'Gegeben sind die Tabellen produkt(id, name, preis) und verkauf(id, produkt_id, menge).
Gesucht wird der Gesamtumsatz pro Produkt (menge * preis). Produkte ohne Verkäufe sollen trotzdem angezeigt werden.

SELECT p.', ', COALESCE(SUM(v.');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (53, 14, 'name', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (54, 14, 'menge', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (55, 14, 'preis', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (56, 14, 'produkt', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (15, 20, 2, ', COALESCE(SUM(v.', '* p.');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (57, 15, 'menge', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (58, 15, 'name', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (59, 15, 'preis', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (60, 15, 'produkt', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (16, 20, 3, '* p.', '), 0) AS umsatz
FROM');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (61, 16, 'preis', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (62, 16, 'name', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (63, 16, 'menge', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (64, 16, 'produkt', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (17, 20, 4, '), 0) AS umsatz
FROM', 'p
LEFT JOIN');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (65, 17, 'produkt', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (66, 17, 'name', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (67, 17, 'menge', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (68, 17, 'preis', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (18, 20, 5, 'p
LEFT JOIN', 'v ON p.id = v.produkt_id
GROUP BY p.');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (69, 18, 'verkauf', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (70, 18, 'name', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (71, 18, 'menge', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (72, 18, 'preis', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (19, 20, 6, 'v ON p.id = v.produkt_id
GROUP BY p.', ';');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (73, 19, 'name', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (74, 19, 'menge', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (75, 19, 'preis', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (76, 19, 'produkt', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (21, 1, 'MC', 'Welche SQL-Statements sind korrekt', NULL, NULL, 1, 4);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (21, 14);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (49, 21, 'CREATE DATABASE test;', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (50, 21, 'DROP TABLE kunde;', 1, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (51, 21, 'INSERT INTO kunde VALUES (''Max'');', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (52, 21, 'ALTER TABLE kunde ADD email VARCHAR(50);', 1, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (22, 1, 'MC', 'Welche Aussagen zu Datentypen sind korrekt?', NULL, NULL, 1, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (22, 13);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (53, 22, 'VARCHAR speichert Zeichenketten variabler Länge', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (54, 22, 'CHAR speichert Zeichenketten mit fixer Länge', 1, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (55, 22, 'DATE speichert Datum und Uhrzeit', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (56, 22, 'INT speichert Dezimalzahlen', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (23, 1, 'MC', 'Welche Aggregatfunktionen gibt es?', NULL, NULL, 1, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (23, 10);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (57, 23, 'ROUND', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (58, 23, 'COUNT', 1, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (59, 23, 'SUM', 1, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (60, 23, 'AVG', 1, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (24, 1, 'MC', 'Welche Aussage zu WHERE und HAVING ist korrekt?', NULL, NULL, 0, 3);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (24, 10);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (61, 24, 'WHERE kann aggregierte Werte filtern', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (62, 24, 'HAVING wird vor GROUP BY ausgeführt', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (63, 24, 'WHERE filtert Datensätze vor der Gruppierung', 1, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (64, 24, 'HAVING ersetzt die WHERE-Klausel vollständig', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (25, 1, 'TF', 'COALESCE gibt den ersten nicht-NULL Wert zurück', NULL, NULL, 0, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (25, 10);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (65, 25, 'Wahr', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (66, 25, 'Falsch', 0, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (26, 1, 'TF', 'COUNT(*) zählt nur Nicht-NULL Werte', NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (26, 10);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (67, 26, 'Wahr', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (68, 26, 'Falsch', 1, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (27, 1, 'TF', 'AVG berücksichtigt NULL-Werte bei der Durchschnittsberechnung', NULL, NULL, 0, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (27, 10);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (69, 27, 'Wahr', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (70, 27, 'Falsch', 1, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (28, 1, 'TF', 'LIMIT begrenzt die Anzahl der ausgegebenen Zeilen', NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (28, 10);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (71, 28, 'Wahr', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (72, 28, 'Falsch', 0, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (29, 1, 'GAP', NULL, NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (29, 10);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (20, 29, 1, 'LIKE verwendet', 'als Wildcard und');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (77, 20, '%', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (78, 20, '_', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (79, 20, '*', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (80, 20, '=', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (21, 29, 2, 'als Wildcard und', 'als Platzhalter für genau ein Zeichen');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (81, 21, '_', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (82, 21, '%', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (83, 21, '*', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (84, 21, '=', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (30, 1, 'GAP', NULL, NULL, NULL, 0, 4);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (30, 14);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (22, 30, 1, 'Vervollständige folgenden SQL-Trigger, der nach einem UPDATE die alten Werte eines Mitarbeiters in eine Log-Tabelle speichert.
CREATE', 'log_changes');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (85, 22, 'TRIGGER', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (86, 22, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (87, 22, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (88, 22, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (23, 30, 2, 'log_changes', 'UPDATE ON employees');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (89, 23, 'AFTER', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (90, 23, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (91, 23, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (92, 23, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (24, 30, 3, 'UPDATE ON employees', 'EACH ROW
BEGIN
    INSERT INTO employees_log (employee_id, name, action)
    VALUES (OLD.employee_id, OLD.name, ''updated'');
END;');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (93, 24, 'FOR', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (94, 24, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (95, 24, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (96, 24, 'INNER', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (31, 1, 'MC', 'Wofür steht ACID?', NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (31, 14);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (73, 31, 'Atomarität, Konsistenz, Isolation, Dauerhaftigkeit', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (74, 31, 'Ausführung, Konsistenz, Integrität, Datenschutz', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (75, 31, 'Atomarität, Commit, Indizierung, Datentyp', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (76, 31, 'Abfrage, Commit, Integrität, Datensatz', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (32, 1, 'MC', 'Welche Aussagen über Trigger in SQL sind zutreffend?', NULL, NULL, 1, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (32, 14);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (77, 32, 'Ein Trigger ist eine gespeicherte Prozedur, die automatisch bei einem Datenbank-Ereignis ausgeführt wird.', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (78, 32, 'Trigger werden hauptsächlich für komplexe SELECT-Abfragen verwendet.', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (79, 32, 'Ein Trigger kann nicht auf UPDATE-Ereignisse reagieren.', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (80, 32, 'Trigger können zur Sicherstellung komplexer Geschäftsregeln oder der referentiellen Integrität beitragen.', 1, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (33, 1, 'MC', 'Welche Eigenschaft kennzeichnet eine Transaktion im Datenbankkontext (ACID)?', NULL, NULL, 1, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (33, 14);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (81, 33, 'Atomarität: Eine Transaktion wird entweder ganz oder gar nicht ausgeführt', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (82, 33, 'Isolation: Alle Benutzer haben die gleichen Zugriffsrechte.', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (83, 33, 'Konsistenz: Die Transaktion überführt die Datenbank von einem konsistenten in einen anderen konsistenten Zustand.', 1, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (84, 33, 'Dauerhafigkeit:  Nach einem ROLLBACK sind die Daten dauerhaft verloren.', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (34, 1, 'MC', 'Was ist der Hauptunterschied zwischen DROP und DELETE?', NULL, NULL, 0, 3);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (34, 14);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (85, 34, 'DROP löscht Datensätze, DELETE löscht Tabellen.', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (86, 34, 'DROP entfernt Tabellen oder Datenbanken, DELETE löscht Datensätze.', 1, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (87, 34, 'DROP kann rückgängig gemacht werden, DELETE nicht.', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (88, 34, 'Es gibt keinen Unterschied.', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (35, 1, 'TF', 'Mit dem REVOKE-Befehl können einem Benutzer zuvor erteilte Rechte wieder entzogen werden.', NULL, NULL, 0, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (35, 13);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (89, 35, 'Wahr', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (90, 35, 'Falsch', 0, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (36, 1, 'TF', 'Ein INNER JOIN und ein WHERE-Join (verknüpfte Tabellen in der WHERE-Klausel) sind funktional identisch.', NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (36, 12);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (91, 36, 'Wahr', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (92, 36, 'Falsch', 0, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (37, 1, 'TF', 'Ein TRUNCATE TABLE-Befehl kann durch ein ROLLBACK rückgängig gemacht werden.', NULL, NULL, 0, 3);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (37, 14);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (93, 37, 'Wahr', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (94, 37, 'Falsch', 1, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (38, 1, 'TF', 'Ein DEFAULT-Wert in einer Tabellenspalte verhindert die Eingabe von NULL-Werten.', NULL, NULL, 0, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (38, 14);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (95, 38, 'Wahr', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (96, 38, 'Falsch', 1, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (39, 1, 'GAP', NULL, NULL, NULL, 0, 4);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (39, 12);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (25, 39, 1, 'Vervollständige das SQL-Statement: Zeige alle Kunden an, die noch nie eine Bestellung aufgegeben haben                                                                                       SELECT k.*
FROM kunde k', NULL);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (97, 25, 'LEFT', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (98, 25, 'RIGHT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (99, 25, 'INNER', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (100, 25, 'JOIN', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (26, 39, 2, NULL, NULL);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (101, 26, 'JOIN', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (102, 26, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (103, 26, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (104, 26, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (27, 39, 3, NULL, 'b ON k.id = b.kunden_id
WHERE b.id');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (105, 27, 'bestellung', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (106, 27, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (107, 27, 'JOIN', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (108, 27, 'IS', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (28, 39, 4, 'b ON k.id = b.kunden_id
WHERE b.id', NULL);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (109, 28, 'IS', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (110, 28, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (111, 28, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (112, 28, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (29, 39, 5, NULL, ';');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (113, 29, 'NULL', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (114, 29, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (115, 29, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (116, 29, 'INNER', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (40, 1, 'GAP', NULL, NULL, NULL, 0, 4);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (40, 14);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (30, 40, 1, 'Vervollständige das SQL-Statement: Erstelle eine Tabelle mitarbeiter mit den Spalten id (Primärschlüssel, automatisch hochzählend) und name (Pflichtfeld)', NULL);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (117, 30, 'CREATE', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (118, 30, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (119, 30, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (120, 30, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (31, 40, 2, NULL, 'mitarbeiter(
id');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (121, 31, 'TABLE', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (122, 31, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (123, 31, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (124, 31, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (32, 40, 3, 'mitarbeiter(
id', NULL);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (125, 32, 'INT', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (126, 32, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (127, 32, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (128, 32, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (33, 40, 4, NULL, NULL);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (129, 33, 'PRIMARY', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (130, 33, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (131, 33, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (132, 33, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (34, 40, 5, NULL, NULL);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (133, 34, 'KEY', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (134, 34, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (135, 34, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (136, 34, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (35, 40, 6, NULL, ',
name');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (137, 35, 'AUTO_INCREMENT', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (138, 35, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (139, 35, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (140, 35, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (36, 40, 7, ',
name', '(');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (141, 36, 'VARCHAR', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (142, 36, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (143, 36, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (144, 36, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (37, 40, 8, '(', ')');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (145, 37, '100', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (146, 37, '1', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (147, 37, '50', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (148, 37, '255', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (38, 40, 9, ')', NULL);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (149, 38, 'NOT', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (150, 38, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (151, 38, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (152, 38, 'INNER', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (39, 40, 10, NULL, ');');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (153, 39, 'NULL', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (154, 39, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (155, 39, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (156, 39, 'INNER', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (41, 1, 'MC', 'Welche Aussage zu GROUP BY ist korrekt?', NULL, NULL, 0, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (41, 10);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (97, 41, 'GROUP BY sortiert automatisch die Ergebnisse', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (98, 41, 'Alle nicht aggregierten Spalten müssen in GROUP BY stehen', 1, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (99, 41, 'GROUP BY kann nur mit COUNT verwendet werden', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (100, 41, 'GROUP BY ersetzt ORDER BY', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (42, 1, 'MC', 'Welche Aussagen zu Fremdschlüsseln sind korrekt?', NULL, NULL, 1, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (42, 11);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (101, 42, 'Ein Fremdschlüssel stellt referentielle Integrität sicher', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (102, 42, 'Ein Fremdschlüssel muss immer auf einen Primärschlüssel zeigen', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (103, 42, 'Ein Fremdschlüssel kann NULL sein, wenn die Beziehung optional ist', 1, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (104, 42, 'Ein Fremdschlüssel kann nicht in derselben Tabelle referenzieren', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (43, 1, 'MC', 'Welche Aussagen zu Normalformen sind korrekt?', NULL, NULL, 1, 5);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (43, 9);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (105, 43, 'Normalform verlangt atomare (unteilbare) Attribute.', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (106, 43, 'Normalform ist automatisch erfüllt, wenn ein Primärschlüssel existiert.', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (107, 43, 'Normalform verbietet partielle Abhängigkeiten bei zusammengesetzten Schlüsseln.', 1, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (108, 43, 'Normalform erlaubt transitive Abhängigkeiten.', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (44, 1, 'MC', 'Welche Aussagen zu Transaktionen sind korrekt?', NULL, NULL, 1, 4);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (44, 14);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (109, 44, 'Ein COMMIT kann durch ROLLBACK rückgängig gemacht werden.', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (110, 44, 'ROLLBACK setzt alle Änderungen seit BEGIN TRANSACTION zurück.', 1, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (111, 44, 'Jede einzelne SQL-Anweisung ist automatisch eine vollständige ACID-Transaktion ohne Ausnahme.', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (112, 44, 'Ohne COMMIT bleiben Änderungen in einer Transaktion nicht dauerhaft gespeichert.', 1, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (45, 1, 'MC', 'Welche Aussagen zu JOIN-Typen sind korrekt?', NULL, NULL, 1, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (45, 12);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (113, 45, 'Ein RIGHT JOIN liefert alle Datensätze der rechten Tabelle.', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (114, 45, 'Ein FULL OUTER JOIN liefert alle Datensätze beider Tabellen.', 1, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (115, 45, 'Ein INNER JOIN liefert immer alle Datensätze der linken Tabelle.', 0, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (116, 45, 'Ein CROSS JOIN benötigt zwingend eine ON-Bedingung.', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (46, 1, 'MC', 'Welche Aussagen zu Indizes sind korrekt?', NULL, NULL, 1, 3);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (46, 13);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (117, 46, 'Ein zusammengesetzter Index kann mehrere Spalten enthalten.', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (118, 46, 'Indizes verbessern ausschließlich INSERT-Operationen.', 0, 2);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (119, 46, 'Ein Index kann die Performance von WHERE- und ORDER BY-Klauseln verbessern.', 1, 3);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (120, 46, 'Indizes verändern automatisch die Sortierung einer Tabelle.', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (47, 1, 'TF', 'Eine Tabelle kann mehrere Fremdschlüssel besitzen.', NULL, NULL, 0, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (47, 11);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (121, 47, 'Wahr', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (122, 47, 'Falsch', 0, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (48, 1, 'TF', 'HAVING kann ohne GROUP BY verwendet werden.', NULL, NULL, 0, 2);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (48, 10);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (123, 48, 'Wahr', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (124, 48, 'Falsch', 0, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (49, 1, 'TF', 'Ein CHECK-Constraint kann Wertebereiche für eine Spalte einschränken.', NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (49, 14);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (125, 49, 'Wahr', 1, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (126, 49, 'Falsch', 0, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (50, 1, 'TF', 'Ein INDEX garantiert automatisch die Eindeutigkeit der Werte.', NULL, NULL, 0, 1);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (50, 13);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (127, 50, 'Wahr', 0, 1);

INSERT INTO `mc_answer` (`answer_id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES (128, 50, 'Falsch', 1, 2);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (51, 1, 'GAP', NULL, NULL, NULL, 0, 3);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (51, 10);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (40, 51, 1, 'Gegeben ist die Tabelle bestellung(id, kunden_id, betrag). Zeige alle kunden_id an, deren Gesamtbestellwert größer als 1000 ist.                        SELECT kunden_id, SUM(betrag)
FROM', 'GROUP BY');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (157, 40, 'bestellung', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (158, 40, 'kunden_id', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (159, 40, 'HAVING', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (160, 40, 'id', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (41, 51, 2, 'GROUP BY', NULL);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (161, 41, 'kunden_id', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (162, 41, 'bestellung', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (163, 41, 'HAVING', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (164, 41, 'id', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (42, 51, 3, NULL, 'SUM(betrag) > 1000;');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (165, 42, 'HAVING', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (166, 42, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (167, 42, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (168, 42, 'INNER', 0, 4);

INSERT INTO `question` (`question_id`, `question_set_id`, `question_type`, `start_text`, `image_url`, `end_text`, `allows_multiple`, `points`) VALUES (52, 1, 'GAP', NULL, NULL, NULL, 0, 3);

INSERT INTO `Question_Theme` (`question_id`, `theme_id`) VALUES (52, 11);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (43, 52, 1, 'Vervollständige das SQL-Statement. Beim Löschen eines Kunden sollen automatisch alle zugehörigen Bestellungen gelöscht werden.            CREATE TABLE bestellung (
id INT PRIMARY KEY,
kunden_id INT,
FOREIGN KEY (kunden_id)
REFERENCES', '(');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (169, 43, 'kunde', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (170, 43, 'id', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (171, 43, 'CASCADE', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (172, 43, 'name', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (44, 52, 2, '(', ')
ON DELETE');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (173, 44, 'id', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (174, 44, 'kunde', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (175, 44, 'CASCADE', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (176, 44, 'name', 0, 4);

INSERT INTO `gap_field` (`gap_id`, `question_id`, `gap_index`, `text_before`, `text_after`) VALUES (45, 52, 3, ')
ON DELETE', ');');

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (177, 45, 'CASCADE', 1, 1);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (178, 45, 'LEFT', 0, 2);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (179, 45, 'RIGHT', 0, 3);

INSERT INTO `gap_option` (`gap_option_id`, `gap_id`, `option_text`, `is_correct`, `option_order`) VALUES (180, 45, 'INNER', 0, 4);

COMMIT;

PRAGMA foreign_keys = ON;