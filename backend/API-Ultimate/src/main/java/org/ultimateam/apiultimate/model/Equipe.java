package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_equipe;

    private String nom_equipe;


    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Joueur> joueurs = new ArrayList<>();

    public Equipe() {}
    public Equipe(String nom_equipe) {
        this.nom_equipe = nom_equipe;
    }

    public Long getId_equipe() { return id_equipe; }
    public String getNom_equipe() { return nom_equipe; }


    public void setId_equipe(Long id_equipe) { this.id_equipe = id_equipe; }
    public void setNom_equipe(String nom_equipe) { this.nom_equipe = nom_equipe; }

    public void addJoueur(Joueur joueur) {
        joueur.setEquipe(this);
    }

    public void removeJoueur(Joueur joueur) {
        joueur.setEquipe(null);
    }
}
