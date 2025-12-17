package org.ultimateam.apiultimate.service;

import org.springframework.stereotype.Service;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.repository.CompetitionRepository;
import org.ultimateam.apiultimate.repository.MatchRepository;

import java.util.List;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final TournoisService tournoisService;
    private final ChampionnatService championnatService;
    private final MatchRepository matchRepository;

    public CompetitionService(TournoisService tournoisService, ChampionnatService championnatService, CompetitionRepository competitionRepository, MatchRepository matchRepository) {
        this.tournoisService = tournoisService;
        this.championnatService = championnatService;
        this.competitionRepository = competitionRepository;
        this.matchRepository = matchRepository;
    }

    public List<Competition> getAllCompetition() {
        return competitionRepository.findAll();
    }

    public Competition getCompetitionById(Long id) {
        return competitionRepository.findById(id).orElse(null);
    }

    public Competition saveCompetition(Competition Competition) {
        return competitionRepository.save(Competition);
    }

    public void deleteCompetitionById(Long id) {
        competitionRepository.deleteById(id);
    }


    public List<Equipe> genererCompetition(Long idCompetition) {
        Competition compet =  competitionRepository.findById(idCompetition).orElse(null);
        if (compet instanceof Tournois) {
            return tournoisService.genererRoundRobin(idCompetition);
        } else if (compet instanceof Championnat) {
            return List.of();
        } else {
            return List.of();
        }
    }

    public List<Match> getMatchesByCompetition(Long idCompetition) {
        return matchRepository.findByIdCompetition_IdCompetitionOrderByDateMatchAsc(idCompetition);
    }


}
