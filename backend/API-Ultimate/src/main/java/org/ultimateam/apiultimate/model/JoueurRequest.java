package org.ultimateam.apiultimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "joueur_request")
public class JoueurRequest {

    @EmbeddedId
    private JoueurRequestId id;


    public JoueurRequest(Joueur joueur, Equipe equipe) {
        this.id = new JoueurRequestId(joueur.getIdJoueur(), equipe.getIdEquipe());
    }
}
