package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long idJoueur;

    private String nomJoueur;
    private String prenomJoueur;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "idEquipe")
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "idEquipe")
    @JsonIdentityReference(alwaysAsId = true)  // sérialise juste l'id
    private Equipe equipe;


    /**
     * Constructeur par défaut pour créer une instance de Joueur vide.
     */
    public Joueur() {}

    /**
     * Constructeur pour créer une nouvelle instance de Joueur avec le nom, prénom et genre spécifiés.
     *
     * @param nomJoueur    Le nom de famille du joueur.
     * @param prenomJoueur Le prénom du joueur.
     * @param genre         Le genre du joueur (MALE ou FEMALE).
     */
    public Joueur(String nomJoueur, String prenomJoueur, Genre genre) {
        this.nomJoueur = nomJoueur;
        this.prenomJoueur = prenomJoueur;
        this.genre = genre;
    }

    /**
     * Constructeur pour créer une nouvelle instance de Joueur avec le nom, prénom, genre et l'équipe spécifiés.
     *
     * @param nomJoueur    Le nom de famille du joueur.
     * @param prenomJoueur Le prénom du joueur.
     * @param genre         Le genre du joueur (MALE ou FEMALE).
     * @param equipe        L'équipe à laquelle le joueur est associé.
     */
    public Joueur(String nomJoueur, String prenomJoueur, Genre genre, Equipe equipe) {
        this.nomJoueur = nomJoueur;
        this.prenomJoueur = prenomJoueur;
        this.genre = genre;
        this.equipe = equipe;
    }
}