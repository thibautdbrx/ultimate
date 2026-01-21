package org.ultimateam.apiultimate.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Représente la participation d'une {@link Equipe} à une {@link Competition}.
 *
 * Cette entité utilise une clé composite {@link ParticipationId} (embeddable) pour
 * identifier de manière unique la participation (idEquipe + idCompetition).
 * La logique métier (calcul de score, rang, etc.) se situe dans les services/entités
 * de classement et non ici.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Participation {

    /**
     * Clé composite identifiant la participation (idEquipe + idCompetition).
     */
    @EmbeddedId
    private ParticipationId id;

    /**
     * Crée une participation pour l'équipe et la compétition fournies.
     *
     * Ce constructeur initialise la clé composite {@code ParticipationId}
     * à partir des identifiants de l'équipe et de la compétition.
     *
     * @param equipe l'équipe participant à la compétition
     * @param competition la compétition concernée
     */
    public Participation(Equipe equipe, Competition competition) {
        this.id = new ParticipationId(equipe.getIdEquipe(), competition.getIdCompetition());
    }
}