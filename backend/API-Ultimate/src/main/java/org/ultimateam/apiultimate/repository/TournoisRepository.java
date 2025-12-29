package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Tournoi;

@Repository
public interface TournoisRepository extends JpaRepository<Tournoi, Long> {
}
