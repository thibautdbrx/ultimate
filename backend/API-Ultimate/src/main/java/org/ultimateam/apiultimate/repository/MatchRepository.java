package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Match;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByDateDebutIsNull();
    List<Match> findByDateDebutIsNotNullAndDateFinIsNull();
    List<Match> findByIdCompetition_IdCompetitionOrderByDateMatchAsc(long competitionId);
    List<Match> findByDateFinIsNotNull();
}