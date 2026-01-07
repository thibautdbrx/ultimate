package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ultimateam.apiultimate.DTO.ActionTypeDTO;
import org.ultimateam.apiultimate.model.ActionMatch;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionMatchRepository extends JpaRepository<ActionMatch, Long> {
    List<ActionMatch> findByMatch_IdMatch(Long matchId);
    List<ActionMatch> findByType(ActionTypeDTO type);
    List<ActionMatch> findByTypeAndMatch_IdMatch(ActionTypeDTO type, Long matchId);
    List<ActionMatch> findByJoueur_IdJoueurAndMatch_IdMatch(Long idJoueur, Long idMatch);
    List<ActionMatch> findByTypeAndJoueur_IdJoueurAndMatch_IdMatch(ActionTypeDTO typeDTO,Long idJoueur, Long idMatch);
}
