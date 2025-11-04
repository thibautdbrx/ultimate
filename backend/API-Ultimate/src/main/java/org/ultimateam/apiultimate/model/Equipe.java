package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Equipe {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_equipe;

    private String nom_equipe;

    //A compléter pour afficher la liste des joueurs
    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Joueur> joueurs = new ArrayList<>();

    public Equipe(String nom_equipe) {
        this.nom_equipe = nom_equipe;
    }

    public void addJoueur(Joueur joueur) {
        joueurs.add(joueur);
        joueur.setEquipe(this);
    }

    public void removeJoueur(Joueur joueur) {
        joueurs.remove(joueur);
        joueur.setEquipe(null);
    }
}
