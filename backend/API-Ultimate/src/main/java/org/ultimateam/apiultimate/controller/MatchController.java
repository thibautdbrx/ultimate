package org.ultimateam.apiultimate.controller;

import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public List<Match> getAllMatch() {
        return (List<Match>) matchService.getAll();
    }

    @GetMapping("/{id}")
    public Match getById(@PathVariable Long id) {
        return matchService.getById(id);
    }

    @GetMapping("/started")
    public List<Match> getMatchStarted() {return matchService.getStarted();}

    @GetMapping("/notstarted")
    public List<Match> getMatchNotStarted() {return matchService.getNotStarted();}

    @PostMapping
    public Match createMatch(@RequestParam Long idEquipe1, @RequestParam Long idEquipe2) {
        return matchService.creerMatch(idEquipe1, idEquipe2);
    }
    @PutMapping("/{id}/start")
    public Match startMatch(@PathVariable Long id) {
        return matchService.commencerMatch(id);
    }

    @PutMapping("/{id}/pause")
    public Match pauseMatch(@PathVariable Long id) {
        return matchService.mettreEnPause(id);
    }
    @PutMapping("/{id}/resume")
    public Match resumeMatch(@PathVariable Long id) {
        return matchService.reprendreMatch(id);
    }

    @PutMapping("/{id}/end")
    public Match endMatch(@PathVariable Long id) {
        return matchService.finirMatch(id);
    }

    @PutMapping("/{id_match}/addPoint/{id_equipe}")
    public Match addPoint(@PathVariable Long id_match, @PathVariable Long id_equipe) {
        return matchService.ajouterPoint(id_match, id_equipe);
    }

    @PutMapping("/{id_match}/removePoint/{id_equipe}")
    public Match removePoint(@PathVariable Long id_match, @PathVariable Long id_equipe) {
        return matchService.retirerPoint(id_match, id_equipe);
    }

    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id) {
        matchService.deleteById(id);
    }

}

