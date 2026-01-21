package org.ultimateam.apiultimate.DTO;

import lombok.Data;

/**
 * DTO pour l'ajout de points à un joueur lors d'un match.
 *
 * Ce DTO est utilisé pour enregistrer les points marqués par un joueur spécifique
 * pendant un match. Il permet de préciser à la fois le joueur et le nombre de points marqués.
 *
 * @see org.ultimateam.apiultimate.controller.ActionMatchController#addPoint(Long, Long, MatchPointDTO)
 * @see org.ultimateam.apiultimate.controller.MatchController#addPoint(Long, Long, MatchPointDTO)
 */

@Data
public class MatchPointDTO {
    private long point;
    private long idJoueur;
}
