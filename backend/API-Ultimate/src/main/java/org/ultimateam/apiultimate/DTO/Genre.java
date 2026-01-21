package org.ultimateam.apiultimate.DTO;

/**
 * Énumération des genres possibles pour une équipe ou une compétition.
 *
 * Cette énumération définit les différentes catégories de genre utilisées dans l'application,
 * allant des catégories classiques (HOMME, FEMME, MIXTE) aux compositions spécifiques
 * pour les équipes mixtes (ex : H3F2 pour 3 hommes et 2 femmes).
 * Elle est utilisée pour classer les équipes et les compétitions selon leur composition de genre.
 *
 * @see org.ultimateam.apiultimate.model.Equipe
 * @see org.ultimateam.apiultimate.model.Competition
 */

public enum Genre {
    FEMME, H3F2, H2F3, H3F4, H4F3, H4F4, OPEN
}
