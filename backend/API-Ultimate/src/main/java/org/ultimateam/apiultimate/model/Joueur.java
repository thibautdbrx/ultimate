package org.ultimateam.apiultimate.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.ultimateam.apiultimate.DTO.GenreJoueur;

/**
 * Représente un joueur appartenant à une équipe.
 *
 * Cette entité contient les informations personnelles du joueur (nom, prénom, photo),
 * son genre et la référence vers l'équipe à laquelle il est associé. Les accesseurs
 * (getters/setters) sont fournis par Lombok (@Getter/@Setter).
 *
 * Règle de calcul des points utilisée dans les classements :
 * - victoire = +3 points
 * - égalité  = +1 point
 * - défaite  = +0 point
 */
@Getter
@Setter
@Entity
public class Joueur {
    /**
     * Identifiant unique du joueur (clé primaire).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long idJoueur;

    /**
     * Nom de famille du joueur.
     */
    private String nomJoueur;

    /**
     * Prénom du joueur.
     */
    private String prenomJoueur;

    /**
     * Genre du joueur (par exemple HOMME/FEMME selon {@link GenreJoueur}).
     */
    @Enumerated(EnumType.STRING)
    private GenreJoueur genre;

    /**
     * Équipe à laquelle le joueur est affecté.
     *
     * Association ManyToOne vers {@link Equipe}. Propriété ignorée pour éviter
     * les références cycliques lors de la sérialisation JSON.
     */
    @ManyToOne
    @JoinColumn(name = "idEquipe")
    @JsonIgnoreProperties({"joueurs"})
    private Equipe equipe;

    /**
     * Chemin ou nom du fichier de la photo du joueur.
     */
    private String photoJoueur;

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
    public Joueur(String nomJoueur, String prenomJoueur, GenreJoueur genre) {
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
    public Joueur(String nomJoueur, String prenomJoueur, GenreJoueur genre, Equipe equipe) {
        this.nomJoueur = nomJoueur;
        this.prenomJoueur = prenomJoueur;
        this.genre = genre;
        this.equipe = equipe;
    }
}