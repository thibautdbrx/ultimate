package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Participation;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {


}
