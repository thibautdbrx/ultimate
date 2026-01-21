package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Participation;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité {@link Participation}.
 *
 * Fournit des méthodes pour récupérer les participations d'équipes dans une compétition
 * ou les participations d'une équipe sur l'ensemble des compétitions.
 */
@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    /**
     * Récupère la liste des participations associées à une compétition donnée.
     *
     * Utile pour obtenir toutes les équipes inscrites à une compétition (par ex. pour
     * initialiser un classement ou générer un calendrier).
     *
     * @param idCompetition identifiant de la compétition
     * @return liste des {@link Participation} liées à la compétition (peut être vide)
     */
    List<Participation> findById_idCompetition(Long idCompetition);

    /**
     * Récupère toutes les participations correspondant à une équipe donnée.
     *
     * Permet par exemple de retrouver les compétitions auxquelles une équipe participe.
     *
     * @param idEquipe identifiant de l'équipe
     * @return liste des {@link Participation} associées à l'équipe (peut être vide)
     */
    List<Participation> findById_idEquipe(Long idEquipe);
}
