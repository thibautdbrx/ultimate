package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long idEquipe;

    private String nom_equipe;

    @OneToMany(mappedBy = "equipe", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Joueur> joueurs = new ArrayList<>();

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JsonBackReference
    private List<Indisponibilite> indisponibilites = new ArrayList<>();

    public Equipe(String nom_equipe) {
        this.nom_equipe = nom_equipe;
    }

    public void addJoueur(Joueur joueur) {
        joueur.setEquipe(this);
    }

    public void removeJoueur(Joueur joueur) {
        joueur.setEquipe(null);
    }
}
