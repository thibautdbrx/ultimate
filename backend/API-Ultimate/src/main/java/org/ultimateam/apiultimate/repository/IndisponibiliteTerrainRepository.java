package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ultimateam.apiultimate.model.IndisponibiliteTerrain;
import org.ultimateam.apiultimate.model.Match;

import java.util.List;

public interface IndisponibiliteTerrainRepository extends JpaRepository<IndisponibiliteTerrain, Long> {
  List<IndisponibiliteTerrain> findByTerrain_IdTerrain(Long idTerrain);
  void deleteByMatch(Match match);
  IndisponibiliteTerrain findByMatch(Match match);
}