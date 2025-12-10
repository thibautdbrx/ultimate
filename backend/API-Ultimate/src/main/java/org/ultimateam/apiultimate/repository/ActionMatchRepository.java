package org.ultimateam.apiultimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ultimateam.apiultimate.model.ActionMatch;
import org.ultimateam.apiultimate.model.Joueur;
import org.ultimateam.apiultimate.model.Match;

import java.util.List;

public interface ActionMatchRepository extends JpaRepository<ActionMatch, Long> {
    List<ActionMatch> findByMatchAndJoueur(Match match, Joueur joueur);
}

