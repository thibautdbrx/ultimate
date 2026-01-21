package org.ultimateam.apiultimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Représente le classement d'une équipe pour une compétition donnée.
 *
 * Cette entité utilise une clé composite {@link ParticipationId} (embedded id) liant
 * une {@link Equipe} et une {@link Competition}. Elle stocke les statistiques
 * utilisées pour calculer le rang et le score d'une équipe : points marqués,
 * points encaissés, victoires, défaites, égalités, etc.
 *
 * Règle de score métier (convention utilisée) :
 * - victoire = +3 points
 * - égalité = +1 point
 * - défaite = +0 point
 *
 * Les getters/setters sont fournis par Lombok (@Getter/@Setter).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Classement {

    /**
     * Clé composite identifiant le classement (id competition + id equipe).
     */
    @EmbeddedId
    private ParticipationId idClassement;

    /**
     * Équipe associée à ce classement.
     *
     * Association ManyToOne liée à {@code idEquipe} de la clé composite.
     */
    @ManyToOne
    @MapsId("idEquipe")
    @JoinColumn(name = "id_equipe")
    private Equipe equipe;

    /**
     * Compétition associée à ce classement.
     *
     * Association ManyToOne liée à {@code idCompetition} de la clé composite.
     */
    @ManyToOne
    @MapsId("idCompetition")
    @JoinColumn(name = "id_competition")
    private Competition competition;

    /**
     * Total des points marqués par l'équipe (somme des points inscrits lors des rencontres).
     */
    private long point_marque = 0;

    /**
     * Total des points encaissés par l'équipe.
     */
    private long point_encaisse = 0;

    /**
     * Différence de points (point_marque - point_encaisse).
     */
    private long difference_points = 0;

    /**
     * Nombre de victoires obtenues par l'équipe.
     */
    private long victoires = 0;

    /**
     * Nombre de défaites subies par l'équipe.
     */
    private long defaites = 0;

    /**
     * Nombre d'égalités.
     */
    private long egalites = 0;

    /**
     * Rang courant de l'équipe dans la compétition (1 = premier).
     */
    private long rang = 0;

    /**
     * Score calculé selon la règle métier (ex. 3*victoires + 1*egalites + 0*defaites).
     */
    private long score = 0; // +3 victoire, +1 égalité, +0 défaite

    /**
     * Date de début de la période de validité du classement (optionnel).
     */
    private LocalDateTime dateDebut;

    /**
     * Date de fin de la période de validité du classement (optionnel).
     */
    private LocalDateTime dateFin;

    /**
     * Constructeur minimal avec la clé composite du classement.
     *
     * @param idClassement clé composite identifiant ce classement (competition + equipe)
     */
    public Classement(ParticipationId idClassement) {
        this.idClassement = idClassement;
    }

}
