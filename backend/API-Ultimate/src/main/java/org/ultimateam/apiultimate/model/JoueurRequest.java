package org.ultimateam.apiultimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Représente une demande d'affectation d'un {@link Joueur} vers une {@link Equipe}.
 *
 * Cette entité utilise une clé composite {@link JoueurRequestId} comme identifiant.
 * Elle permet de stocker la relation (demande) entre un joueur et une équipe —
 * par exemple lorsqu'un joueur demande à rejoindre une équipe.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "joueur_request")
public class JoueurRequest {

    /**
     * Clé composite identifiant la demande (idJoueur + idEquipe).
     */
    @EmbeddedId
    private JoueurRequestId id;

    /**
     * Joueur à l'origine de la demande.
     */
    @ManyToOne
    private Joueur joueur;

    /**
     * Équipe cible de la demande.
     */
    @ManyToOne
    private Equipe equipe;

    /**
     * Constructeur complet permettant d'initialiser explicitement la clé composite,
     * le joueur et l'équipe associés à la demande.
     *
     * @param id clé composite ({@link JoueurRequestId}) identifiant la demande
     * @param joueur joueur concerné par la demande
     * @param equipe équipe cible de la demande
     */
    public JoueurRequest(JoueurRequestId id, Joueur joueur, Equipe equipe) {
        this.id = id;
        this.joueur = joueur;
        this.equipe = equipe;
    }

    /**
     * Constructeur pratique qui crée automatiquement la clé composite à partir
     * des identifiants du {@link Joueur} et de l'{@link Equipe} fournis.
     *
     * Ce constructeur assigne également les références `joueur` et `equipe`.
     *
     * @param joueur joueur demandant l'affectation
     * @param equipe équipe cible de la demande
     */
    public JoueurRequest(Joueur joueur, Equipe equipe) {
        this.id = new JoueurRequestId(joueur.getIdJoueur(), equipe.getIdEquipe());
        this.joueur = joueur;
        this.equipe = equipe;
    }
}
