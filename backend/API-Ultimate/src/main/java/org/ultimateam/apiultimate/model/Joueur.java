package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Joueur {
    public enum Genre {MALE, FEMALE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_joueur;

    private String nom_joueur;
    private String prenom_joueur;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "id_equipe")
    @JsonBackReference
    private Equipe equipe;

    public Joueur() {}

    public Joueur(String nom_joueur, String prenom_joueur, Genre genre) {
        this.nom_joueur = nom_joueur;
        this.prenom_joueur = prenom_joueur;
        this.genre = genre;
    }

    public Joueur(String nom_joueur, String prenom_joueur, Genre genre, Equipe equipe) {
        this.nom_joueur = nom_joueur;
        this.prenom_joueur = prenom_joueur;
        this.genre = genre;
        this.equipe = equipe;
    }
}
