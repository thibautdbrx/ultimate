package org.ultimateam.apiultimate.controller;

import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.service.EquipeService;
import org.ultimateam.apiultimate.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;
    private EquipeService equipeService;

    public MatchController(MatchService matchService, EquipeService equipeService) {
        this.matchService = matchService;
        this.equipeService = equipeService;
    }

    @GetMapping
    public List<Match> getAllMatch() { return (List<Match>) matchService.getAll();}

    @GetMapping("/{id}")
    public Match getById(@PathVariable Long id) { return matchService.getById(id);}

    @PostMapping
    public Match createMatch(@RequestBody Match match) {

        //Récupère les équipes grâce aux id des équipes
        Equipe equipe1 = equipeService.getById(match.getEquipe1().getId_equipe());
        Equipe equipe2 = equipeService.getById(match.getEquipe2().getId_equipe());

        //Associe les équipes grâce à leurs id
        match.setEquipe1(equipe1);
        match.setEquipe2(equipe2);

        return matchService.save(match);
    }

    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id) { matchService.deleteById(id);}

}

