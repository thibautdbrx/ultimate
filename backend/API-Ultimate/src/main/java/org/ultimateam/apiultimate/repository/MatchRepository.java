package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Match;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByDateDebutIsNull();
    List<Match> findByDateDebutIsNotNullAndDateFinIsNull();
    List<Match> findByIdCompetition_IdCompetitionOrderByDateMatchAsc(long competitionId);
    List<Match> findByDateMatchAfterAndDateDebutIsNull(LocalDateTime dateMatchBefore);
    List<Match> findByDateFinIsNotNull();
        @Query("""
        SELECT m
        FROM Match m
        WHERE m.equipe1.idEquipe = :idEquipe
           OR m.equipe2.idEquipe = :idEquipe
    """)
        List<Match> findMatchesByEquipe(@Param("idEquipe") long idEquipe);
    @Query("SELECT m FROM Match m WHERE m.terrain.idTerrain = :idTerrain")
    List<Match> findByTerrain_Id_terrain(@Param("idTerrain") Long idTerrain);

    List<Match>findByIdCompetition_IdCompetition(Long idCompetition);
}