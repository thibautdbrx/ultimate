-- ==============================================================================
-- 1. CRÉATION DES ÉQUIPES
-- ==============================================================================

INSERT INTO equipe (id_equipe, nom_equipe, description, genre, nb_joueurs) VALUES
                                                                               (1, 'Les Valkyries du Nord', 'Équipe 100% féminine compétitive', 0, 'CINQ'),
                                                                               (2, 'Les Amazones de Fer', 'Force et stratégie féminine', 0, 'CINQ'),
                                                                               (3, 'Les Titans du Crépuscule', 'Composition mixte 3H/2F', 1, 'CINQ'),
                                                                               (4, 'Les Spectres de la Tour', 'Défense de fer 3H/2F', 1, 'CINQ'),
                                                                               (5, 'Les Gardiens du Temple', 'Équilibre tactique 2H/3F', 2, 'CINQ'),
                                                                               (6, 'Les Faucons d’Argent', 'Vitesse et précision 2H/3F', 2, 'CINQ'),
                                                                               (7, 'Les Chimères des Abysses', 'Large effectif mixte 3H/4F', 3, 'SEPT'),
                                                                               (8, 'Les Hydres de Lave', 'Puissance de feu 3H/4F', 3, 'SEPT'),
                                                                               (9, 'Les Cyclopes de la Foudre', 'Attaque massive 4H/3F', 4, 'SEPT'),
                                                                               (10, 'Les Centaures de la Forêt', 'Solidité et nature 4H/3F', 4, 'SEPT'),
                                                                               (11, 'Les Vagabonds Galactiques', 'Équipe ouverte (Mixte)', 6, 'SEPT'),
                                                                               (12, 'Les Pirates du Néant', 'Aucune restriction (Mixte)', 6, 'SEPT');

-- COMMANDE H2 : On synchronise l'auto-incrément après les inserts manuels
ALTER TABLE equipe ALTER COLUMN id_equipe RESTART WITH 13;

-- ==============================================================================
-- 2. GÉNÉRATION DES JOUEURS
-- ==============================================================================

INSERT INTO joueur (id_joueur, nom_joueur, prenom_joueur, genre, id_equipe) VALUES
                                                                                (1, 'Bernard', 'Clara', 'FEMME', 1),
                                                                                (2, 'Dubois', 'Manon', 'FEMME', 1),
                                                                                (3, 'Petit', 'Julie', 'FEMME', 1),
                                                                                (4, 'Robert', 'Sophie', 'FEMME', 1),
                                                                                (5, 'Richard', 'Emma', 'FEMME', 1),
                                                                                (6, 'Moreau', 'Sarah', 'FEMME', 2),
                                                                                (7, 'Simon', 'Léa', 'FEMME', 2),
                                                                                (8, 'Laurent', 'Chloé', 'FEMME', 2),
                                                                                (9, 'Lefebvre', 'Juliette', 'FEMME', 2),
                                                                                (10, 'Michel', 'Inès', 'FEMME', 2),
                                                                                (11, 'Garcia', 'Lucas', 'HOMME', 3),
                                                                                (12, 'Thomas', 'Hugo', 'HOMME', 3),
                                                                                (13, 'Roux', 'Gabriel', 'HOMME', 3),
                                                                                (14, 'David', 'Charlotte', 'FEMME', 3),
                                                                                (15, 'Bertrand', 'Alice', 'FEMME', 3),
                                                                                (16, 'Fournier', 'Louis', 'HOMME', 4),
                                                                                (17, 'Morel', 'Arthur', 'HOMME', 4),
                                                                                (18, 'Girard', 'Jules', 'HOMME', 4),
                                                                                (19, 'Bonnet', 'Anna', 'FEMME', 4),
                                                                                (20, 'Dupont', 'Eva', 'FEMME', 4),
                                                                                (21, 'Lambert', 'Nathan', 'HOMME', 5),
                                                                                (22, 'Fontaine', 'Paul', 'HOMME', 5),
                                                                                (23, 'Rousseau', 'Manon', 'FEMME', 5),
                                                                                (24, 'Vincent', 'Laura', 'FEMME', 5),
                                                                                (25, 'Muller', 'Zoé', 'FEMME', 5),
                                                                                (26, 'Lefevre', 'Tom', 'HOMME', 6),
                                                                                (27, 'Faure', 'Noah', 'HOMME', 6),
                                                                                (28, 'Andre', 'Lisa', 'FEMME', 6),
                                                                                (29, 'Mercier', 'Rose', 'FEMME', 6),
                                                                                (30, 'Blanc', 'Jade', 'FEMME', 6),
                                                                                (31, 'Guerin', 'Léo', 'HOMME', 7),
                                                                                (32, 'B Boyer', 'Raphaël', 'HOMME', 7),
                                                                                (33, 'Garnier', 'Ethan', 'HOMME', 7),
                                                                                (34, 'Chevalier', 'Louise', 'FEMME', 7),
                                                                                (35, 'Francois', 'Ambre', 'FEMME', 7),
                                                                                (36, 'Legrand', 'Lina', 'FEMME', 7),
                                                                                (37, 'Gauthier', 'Nina', 'FEMME', 7),
                                                                                (38, 'Perrin', 'Sacha', 'HOMME', 8),
                                                                                (39, 'Robin', 'Maël', 'HOMME', 8),
                                                                                (40, 'Clement', 'Tiago', 'HOMME', 8),
                                                                                (41, 'Morin', 'Romane', 'FEMME', 8),
                                                                                (42, 'Nicolas', 'Julia', 'FEMME', 8),
                                                                                (43, 'Henry', 'Léna', 'FEMME', 8),
                                                                                (44, 'Roussel', 'Mathilde', 'FEMME', 8),
                                                                                (45, 'Mathieu', 'Liam', 'HOMME', 9),
                                                                                (46, 'Gautier', 'Gabin', 'HOMME', 9),
                                                                                (47, 'Masson', 'Adam', 'HOMME', 9),
                                                                                (48, 'Marchand', 'Enzo', 'HOMME', 9),
                                                                                (49, 'Duval', 'Agathe', 'FEMME', 9),
                                                                                (50, 'Denis', 'Lou', 'FEMME', 9),
                                                                                (51, 'Dumont', 'Jeanne', 'FEMME', 9),
                                                                                (52, 'Marie', 'Evan', 'HOMME', 10),
                                                                                (53, 'Lemaire', 'Nolan', 'HOMME', 10),
                                                                                (54, 'Noel', 'Timéo', 'HOMME', 10),
                                                                                (55, 'Meyer', 'Mohamed', 'HOMME', 10),
                                                                                (56, 'Dufour', 'Sarah', 'FEMME', 10),
                                                                                (57, 'Valentin', 'Clara', 'FEMME', 10),
                                                                                (58, 'Brun', 'Elise', 'FEMME', 10),
                                                                                (59, 'Colin', 'Victor', 'HOMME', 11),
                                                                                (60, 'Vidal', 'Mathis', 'HOMME', 11),
                                                                                (61, 'Caron', 'Marius', 'HOMME', 11),
                                                                                (62, 'Picard', 'Baptiste', 'HOMME', 11),
                                                                                (63, 'Roger', 'Maxence', 'HOMME', 11),
                                                                                (64, 'Fabre', 'Océane', 'FEMME', 11),
                                                                                (65, 'Lemoine', 'Luc', 'HOMME', 11),
                                                                                (66, 'Aubert', 'Corentin', 'HOMME', 12),
                                                                                (67, 'Lemoine', 'Alexis', 'HOMME', 12),
                                                                                (68, 'Renaud', 'Valentine', 'FEMME', 12),
                                                                                (69, 'Dumas', 'Apolline', 'FEMME', 12),
                                                                                (70, 'Lacroix', 'Héloïse', 'FEMME', 12),
                                                                                (71, 'Olivier', 'Constance', 'FEMME', 12),
                                                                                (72, 'Vasseur', 'Hugo', 'HOMME', 12);

-- COMMANDE H2 : On synchronise l'auto-incrément après les joueurs
ALTER TABLE joueur ALTER COLUMN id_joueur RESTART WITH 73;

-- ==============================================================================
-- 3. CRÉATION DE L'ADMINISTRATEUR
-- ==============================================================================

-- On insère l'admin (ID 1)
INSERT INTO users (id_user, email, password, role) VALUES
    (1, 'admin', '$2a$10$f/htVDo8T5ZOLgymYyWTX.iOe2pJ2mq5mL4avtFBn7rzasZcn8Psi', 'ROLE_ADMIN');

-- Synchronisation de la séquence d'ID pour la table users
ALTER TABLE users ALTER COLUMN id_user RESTART WITH 2;