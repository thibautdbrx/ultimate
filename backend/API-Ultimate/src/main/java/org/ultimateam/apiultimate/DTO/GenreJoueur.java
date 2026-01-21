package org.ultimateam.apiultimate.DTO;

/**
 * Énumération des genres possibles pour un joueur.
 *
 * Cette énumération définit les catégories de genre utilisées pour identifier les joueurs
 * dans l'application. Elle est utilisée pour classer les joueurs et appliquer des règles
 * spécifiques selon le genre (ex : composition des équipes, statistiques).
 *
 * @see org.ultimateam.apiultimate.model.Joueur
 * @see org.ultimateam.apiultimate.DTO.RegisterRequest
 * @see org.ultimateam.apiultimate.DTO.EditJoueurDTO
 */

public enum GenreJoueur {
    HOMME, FEMME
}
