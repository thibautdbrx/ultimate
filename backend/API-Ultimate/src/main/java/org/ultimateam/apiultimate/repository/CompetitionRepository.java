package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Competition;
import org.ultimateam.apiultimate.model.Tournoi;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité {@link Competition}.
 *
 * Fournit l'accès aux opérations CRUD standard pour les compétitions via {@link JpaRepository}.
 * Cette interface expose également une méthode dérivée pour récupérer les tournois
 * associés à une compétition donnée.
 */
@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    /**
     * Retourne la liste des {@link Tournoi} associés à la compétition identifiée par {@code idCompetition}.
     *
     * Note : il s'agit d'une méthode dérivée basée sur le nom ; elle permet de
     * récupérer tous les tournois liés à une compétition donnée.
     *
     * @param idCompetition identifiant de la compétition
     * @return liste des tournois associés (peut être vide)
     */
    List<Tournoi> findAllByIdCompetition(long idCompetition);
}
