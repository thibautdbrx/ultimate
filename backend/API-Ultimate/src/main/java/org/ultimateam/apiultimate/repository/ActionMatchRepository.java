package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ultimateam.apiultimate.DTO.ActionTypeDTO;
import org.ultimateam.apiultimate.model.ActionMatch;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité {@link ActionMatch}.
 *
 * Fournit des méthodes de recherche basées sur les associations (match, joueur) et le type d'action.
 * Ces méthodes sont utilisées par les services pour récupérer les actions liées à un match ou un joueur.
 */
@Repository
public interface ActionMatchRepository extends JpaRepository<ActionMatch, Long> {

    /**
     * Récupère la liste des {@link ActionMatch} associées à un match donné.
     *
     * @param matchId identifiant du match
     * @return liste des actions liées au match (peut être vide)
     */
    List<ActionMatch> findByMatch_IdMatch(Long matchId);

    /**
     * Récupère la liste des {@link ActionMatch} correspondant à un type d'action donné.
     *
     * @param type type d'action (DTO ou enum selon l'implémentation)
     * @return liste des actions du type spécifié
     */
    List<ActionMatch> findByType(ActionTypeDTO type);

    /**
     * Récupère la liste des {@link ActionMatch} d'un type donné pour un match précis.
     *
     * @param type    type d'action recherché
     * @param matchId identifiant du match
     * @return liste des actions correspondant au type et au match
     */
    List<ActionMatch> findByTypeAndMatch_IdMatch(ActionTypeDTO type, Long matchId);

    /**
     * Récupère les actions réalisées par un joueur sur un match donné.
     *
     * @param idJoueur identifiant du joueur
     * @param idMatch  identifiant du match
     * @return liste des actions du joueur sur le match
     */
    List<ActionMatch> findByJoueur_IdJoueurAndMatch_IdMatch(Long idJoueur, Long idMatch);

    /**
     * Récupère les actions d'un certain type effectuées par un joueur sur un match donné.
     *
     * @param typeDTO  type d'action recherché
     * @param idJoueur identifiant du joueur
     * @param idMatch  identifiant du match
     * @return liste des actions correspondant aux critères
     */
    List<ActionMatch> findByTypeAndJoueur_IdJoueurAndMatch_IdMatch(ActionTypeDTO typeDTO,Long idJoueur, Long idMatch);
}
