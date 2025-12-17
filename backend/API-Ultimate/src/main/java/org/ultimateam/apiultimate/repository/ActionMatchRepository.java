package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ultimateam.apiultimate.model.ActionMatch;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionMatchRepository extends JpaRepository<ActionMatch, Long> {
    List<ActionMatch> findByMatchId(Long matchId);
    List<ActionMatch> findByJoueurId(Long joueurId);
}
