package org.ultimateam.apiultimate.DTO;

import lombok.Data;

/**
 * DTO pour la mise à jour du nom et de la description d'une équipe.
 *
 * Ce DTO est utilisé pour modifier les informations principales d'une équipe existante.
 * Tous les champs sont optionnels : seuls les champs fournis seront mis à jour.
 *
 * @see org.ultimateam.apiultimate.controller.EquipeController#editNomEquipe(EquipeNameDTO, long)
 * @see org.ultimateam.apiultimate.controller.TournoisController#editTournoi(EquipeNameDTO, long)
 */

@Data
public class EquipeNameDTO {
   private String nom;
   private String description;
}
