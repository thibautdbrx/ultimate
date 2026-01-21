package org.ultimateam.apiultimate.DTO;

import lombok.Data;

/**
 * DTO pour l'ajout d'une faute à un joueur lors d'un match.
 *
 * Ce DTO est utilisé pour enregistrer une faute commise par un joueur spécifique
 * pendant un match. Il est transmis aux endpoints dédiés à la gestion des fautes.
 *
 * @see org.ultimateam.apiultimate.controller.ActionMatchController#addFaute(Long, Long, MatchFauteDTO)
 * @see org.ultimateam.apiultimate.controller.MatchController#addFaute(Long, Long, MatchFauteDTO)
 */

@Data
public class MatchFauteDTO {
    private long idJoueur;
}
