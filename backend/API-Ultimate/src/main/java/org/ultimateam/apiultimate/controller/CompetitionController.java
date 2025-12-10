package org.ultimateam.apiultimate.controller;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Competition;
import org.ultimateam.apiultimate.service.CompetitionService;

import java.util.List;

@RestController
@RequestMapping("/api/competition")
public class CompetitionController {

    private final CompetitionService competitionService;

    @Autowired
    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @GetMapping
    public List<Competition> findAll() { return (List<Competition>) competitionService.getAllCompetition(); }

    @GetMapping("/{id}")
    public Competition findById(@PathVariable Long id) { return competitionService.getCompetitionById(id); }

    @GetMapping("{idCompetition}/matchs")
    public List<Match> findMatches(@PathVariable Long idCompetition) { return competitionService.getMatchesByCompetition(idCompetition);}

    @PostMapping
    public Competition creerCompetition(@RequestBody Competition competition) {return competitionService.saveCompetition(competition); }

    @PutMapping("/{idCompetition}/create")
    public List<Equipe> genererMatchs(@PathVariable Long idCompetition) { return competitionService.genererCompetition(idCompetition);}

    @PostMapping("/tournoi")
    public Competition createCompetition(@RequestBody Competition competition) { return competitionService.saveCompetition(competition); }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) { competitionService.deleteCompetitionById(id); }

    /**
     @PostMapping("/Creation_Competition")
     public void genererCompetition(Long idCompetition){ CompetitionService.genererCompetition(idCompetition);}

     */


}
