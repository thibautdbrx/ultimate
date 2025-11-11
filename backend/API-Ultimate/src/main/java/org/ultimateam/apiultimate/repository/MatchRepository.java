package org.ultimateam.apiultimate.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Match;

@Repository
public interface MatchRepository extends CrudRepository<Match, Long> {

        @Query("SELECT m FROM Match m WHERE m.id_equipe1 = :idEquipe OR m.id_equipe2 = :idEquipe")
        List<Match> findAllByEquipe(@Param("idEquipe") int idEquipe);
    }
