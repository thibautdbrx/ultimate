package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Championnat;

@Repository
public interface ChampionnatRepository extends JpaRepository<Championnat, Long> {
    }
