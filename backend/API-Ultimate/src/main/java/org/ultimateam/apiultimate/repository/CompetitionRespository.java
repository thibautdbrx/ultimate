package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Competition;

@Repository
public interface CompetitionRespository extends JpaRepository<Competition, Long> {
}
