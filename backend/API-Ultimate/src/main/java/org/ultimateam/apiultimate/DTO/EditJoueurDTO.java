package org.ultimateam.apiultimate.DTO;

import lombok.Data;
import org.ultimateam.apiultimate.model.Joueur;

/**
 * DTO pour la mise à jour des informations d'un joueur.
 *
 * Ce DTO est utilisé pour modifier le nom, le prénom et/ou le genre d'un joueur existant.
 * Tous les champs sont optionnels : seuls les champs fournis seront mis à jour.
 *
 */

@Data
public class EditJoueurDTO {
    private String nomJoueur;
    private String prenomJoueur;
    private GenreJoueur genre;
}
