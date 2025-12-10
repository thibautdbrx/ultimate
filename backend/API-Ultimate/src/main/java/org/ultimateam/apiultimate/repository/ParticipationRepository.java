package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.model.ParticipationId;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findById_idCompetition(Long idCompetition);

    List<Participation> findById_idEquipe(Long idEquipe);
}
