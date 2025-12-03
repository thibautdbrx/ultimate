package org.ultimateam.apiultimate.DTO;

import lombok.Data;
import org.ultimateam.apiultimate.model.Joueur;

@Data
public class EditJoueurDTO {
    private String nomJoueur;
    private String prenomJoueur;
    private Joueur.Genre genre;
}
