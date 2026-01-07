package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Classement;
import org.ultimateam.apiultimate.model.ParticipationId;

import java.util.List;

@Repository
public interface ClassementRepository extends JpaRepository<Classement, ParticipationId>  {
    Classement findClassementByCompetition_IdCompetitionAndEquipe_IdEquipe(long idCompetition, long idEquipe);

    @Query("SELECT c FROM Classement c WHERE c.competition.idCompetition = :idCompetition " +
            "ORDER BY c.score DESC, " +
            "(c.victoires + c.egalites + c.defaites) DESC, " +
            "c.difference_points DESC, " +
            "c.point_marque DESC")
    List<Classement> findAllByCompetitionIdOrderByRank(@Param("idCompetition") Long idCompetition);

    List<Classement> findAllByCompetition_IdCompetition(Long idCompetition);

    List<Classement> findAllByEquipe_IdEquipe(Long idEquipe);


}
