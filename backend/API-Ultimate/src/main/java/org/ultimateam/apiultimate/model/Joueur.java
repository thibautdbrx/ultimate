package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

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

    public long getId_joueur() { return id_joueur; }
    public String getNom_joueur() { return nom_joueur; }
    public String getPrenom_joueur() { return prenom_joueur; }
    public Genre getGenre() { return genre; }
    public Equipe getEquipe() { return equipe; }

    public void setNom_joueur(String nom_joueur) { this.nom_joueur = nom_joueur; }
    public void setPrenom_joueur(String prenom_joueur) { this.prenom_joueur = prenom_joueur; }
    public void  setGenre(Genre genre) { this.genre = genre; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }




}
