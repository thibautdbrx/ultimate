package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Classement;
import org.ultimateam.apiultimate.model.Competition;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.ParticipationId;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassementRepository extends JpaRepository<Classement, ParticipationId>  {
    Classement findByCompetitionAndEquipe(Competition competition, Equipe equipe);

    @Query("SELECT c FROM Classement c WHERE c.competition.idCompetition = :idComp " +
            "ORDER BY c.score DESC, c.difference_points DESC, c.point_marque DESC")
    List<Classement> findAllByCompetitionIdOrderByRank(@Param("idComppetition") Long idCompetition);
}
