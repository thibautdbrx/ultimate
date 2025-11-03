package org.ultimateam.apiultimate.service;

import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.repository.EquipeRepository;
import org.ultimateam.apiultimate.repository.MatchRepository;
import org.ultimateam.apiultimate.model.Match;

@Service
public class MatchService {
    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {this.matchRepository = matchRepository;}
    public Iterable<Match> getAll() { return matchRepository.findAll();}
    public Match getById(Long id) { return matchRepository.findById(id).orElse(null);}
    public Match save(Match match) { return matchRepository.save(match);}
    public void deleteById(Long id) { matchRepository.deleteById(id);}
}



