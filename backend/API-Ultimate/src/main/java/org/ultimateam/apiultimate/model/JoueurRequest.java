package org.ultimateam.apiultimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ultimateam.apiultimate.service.JoueurService;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "joueur_request")
public class JoueurRequest {

    @EmbeddedId
    private JoueurRequestId id;

    @ManyToOne
    private Joueur joueur;

    @ManyToOne
    private Equipe equipe;

    public JoueurRequest(JoueurRequestId id, Joueur joueur, Equipe equipe) {
        this.id = id;
        this.joueur = joueur;
        this.equipe = equipe;
    }

    public JoueurRequest(Joueur joueur, Equipe equipe) {
        this.id = new JoueurRequestId(joueur.getIdJoueur(), equipe.getIdEquipe());
        this.joueur = joueur;
        this.equipe = equipe;
    }
}
