package org.ultimateam.apiultimate.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO pour la gestion des images ou avatars des joueurs.
 *
 * Cette classe est utilisée pour transmettre les données d'une image,
 * généralement sous forme d'URL ou de chemin d'accès, lors de la mise à jour
 * des informations d'un joueur
 *
 * @see org.ultimateam.apiultimate.controller.JoueurController#editImage(ImageDTO, long)
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private String image;
}
