package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.model.Match;

import java.util.List;

@Repository
public interface IndisponibiliteRepository extends JpaRepository<Indisponibilite, Long> {
    List<Indisponibilite> findAllByEquipe_IdEquipe(Long idEquipe);
    List<Indisponibilite> findByEquipe(Equipe equipe);
    List<Indisponibilite> findByMatch(Match match);
}
