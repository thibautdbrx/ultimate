package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Classement;
import org.ultimateam.apiultimate.model.ParticipationId;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité {@link Classement}.
 *
 * Fournit des méthodes pour rechercher les classements par compétition ou par équipe
 * et une requête personnalisée pour obtenir le classement ordonné selon les règles
 * métier (score, nombre de rencontres, différence de points, points marqués).
 */
@Repository
public interface ClassementRepository extends JpaRepository<Classement, ParticipationId>  {

    /**
     * Récupère le classement d'une équipe pour une compétition donnée.
     *
     * @param idCompetition identifiant de la compétition
     * @param idEquipe identifiant de l'équipe
     * @return l'objet {@link Classement} correspondant (ou null si non trouvé)
     */
    Classement findClassementByCompetition_IdCompetitionAndEquipe_IdEquipe(long idCompetition, long idEquipe);

    /**
     * Récupère la liste des classements pour une compétition donnée triés selon le "rank" métier.
     *
     * L'ordre appliqué dans la requête est :
     * - par score décroissant,
     * - puis par (victoires + egalites + defaites) décroissant (nombre de rencontres jouées),
     * - puis par difference_points décroissant,
     * - puis par point_marque décroissant.
     *
     * Cette requête utilise une JPQL personnalisée pour garantir l'ordre exact attendu.
     *
     * @param idCompetition identifiant de la compétition
     * @return liste des {@link Classement} ordonnée selon les critères métier
     */
    @Query("SELECT c FROM Classement c WHERE c.competition.idCompetition = :idCompetition " +
            "ORDER BY c.score DESC, " +
            "(c.victoires + c.egalites + c.defaites) DESC, " +
            "c.difference_points DESC, " +
            "c.point_marque DESC")
    List<Classement> findAllByCompetitionIdOrderByRank(@Param("idCompetition") Long idCompetition);

    /**
     * Récupère tous les classements associés à une compétition (sans ordre particulier).
     *
     * @param idCompetition identifiant de la compétition
     * @return liste des {@link Classement} associés à la compétition
     */
    List<Classement> findAllByCompetition_IdCompetition(Long idCompetition);

    /**
     * Récupère tous les classements associés à une équipe donnée (sur toutes ses compétitions).
     *
     * @param idEquipe identifiant de l'équipe
     * @return liste des {@link Classement} contenant l'équipe
     */
    List<Classement> findAllByEquipe_IdEquipe(Long idEquipe);


}
