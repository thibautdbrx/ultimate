package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id_equipe")
    @JsonIdentityReference(alwaysAsId = true)  // sérialise juste l'id
    private Equipe equipe;


    /**
     * Constructeur par défaut pour créer une instance de Joueur vide.
     */
    public Joueur() {}

    /**
     * Constructeur pour créer une nouvelle instance de Joueur avec le nom, prénom et genre spécifiés.
     *
     * @param nom_joueur    Le nom de famille du joueur.
     * @param prenom_joueur Le prénom du joueur.
     * @param genre         Le genre du joueur (MALE ou FEMALE).
     */
    public Joueur(String nom_joueur, String prenom_joueur, Genre genre) {
        this.nom_joueur = nom_joueur;
        this.prenom_joueur = prenom_joueur;
        this.genre = genre;
    }

    /**
     * Constructeur pour créer une nouvelle instance de Joueur avec le nom, prénom, genre et l'équipe spécifiés.
     *
     * @param nom_joueur    Le nom de famille du joueur.
     * @param prenom_joueur Le prénom du joueur.
     * @param genre         Le genre du joueur (MALE ou FEMALE).
     * @param equipe        L'équipe à laquelle le joueur est associé.
     */
    public Joueur(String nom_joueur, String prenom_joueur, Genre genre, Equipe equipe) {
        this.nom_joueur = nom_joueur;
        this.prenom_joueur = prenom_joueur;
        this.genre = genre;
        this.equipe = equipe;
    }
}