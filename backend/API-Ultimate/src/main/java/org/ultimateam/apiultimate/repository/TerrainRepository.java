package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ultimateam.apiultimate.model.Terrain;

public interface TerrainRepository extends JpaRepository<Terrain, Long> {
}