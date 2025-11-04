package org.ultimateam.apiultimate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.service.EquipeService;
import org.ultimateam.apiultimate.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;
    private final EquipeService equipeService;

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
    @PutMapping("/{id}/start")
    public Match startMatch(@PathVariable Long id) {
        Match match = matchService.getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        matchService.commencerMatch(match);
        return matchService.save(match);
    }

    @PutMapping("/{id}/pause")
    public Match pauseMatch(@PathVariable Long id) {
        Match match = matchService.getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        matchService.pauseMatch(match);
        return matchService.save(match);
    }
    @PutMapping("/{id}/resume")
    public Match resumeMatch(@PathVariable Long id) {
        Match match = matchService.getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        matchService.reprendreMatch(match);
        return matchService.save(match);
    }

    @PutMapping("/{id}/end")
    public Match endMatch(@PathVariable Long id) {
        Match match = matchService.getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        matchService.finMatch(match);
        return matchService.save(match);
    }

    @PutMapping("/{id}/addPoint/{id_equipe}")
    public Match addPoint(@PathVariable Long id, @PathVariable Long id_equipe) {
        Match match = matchService.getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Equipe equipe = equipeService.getById(id_equipe);
        if (equipe == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        matchService.ajouterPoint(match, equipe);
        return matchService.save(match);
    }

    @PutMapping("/{id}/removePoint/{id_equipe}")
    public Match removePoint(@PathVariable Long id, @PathVariable Long id_equipe) {
        Match match = matchService.getById(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Equipe equipe = equipeService.getById(id_equipe);
        if (equipe == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        matchService.retirerPoint(match, equipe);
        return matchService.save(match);
    }





    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id) { matchService.deleteById(id);}

}

