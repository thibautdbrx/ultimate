package org.ultimateam.apiultimate.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_equipe;

    private String nom_equipe;

    //A compléter pour afficher la liste des joueurs
    @OneToMany(mappedBy = "equipe")
    private List<Joueur> joueurs;

    public Equipe() {}
    public Equipe(String nom_equipe) {
        this.nom_equipe = nom_equipe;
    }

    public Long getId_equipe() { return id_equipe; }
    public String getNom_equipe() { return nom_equipe; }


    public void setId_equipe(Long id_equipe) { this.id_equipe = id_equipe; }
    public void setNom_equipe(String nom_equipe) { this.nom_equipe = nom_equipe; }

    public void setJoueurs(List<Joueur> joueurs) { this.joueurs = joueurs; }

}
