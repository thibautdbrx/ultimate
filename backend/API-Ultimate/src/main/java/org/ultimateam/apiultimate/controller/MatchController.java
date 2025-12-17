package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.ActionTypeDTO;
import org.ultimateam.apiultimate.DTO.MatchDTO;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
import org.ultimateam.apiultimate.model.ActionMatch;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.service.ActionMatchService;
import org.ultimateam.apiultimate.service.MatchService;

import java.util.List;

@RestController
@Tag(name = "Match", description = "Endpoints pour gérer les matchs")
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
    public Match getById(@PathVariable Long id) {return matchService.getById(id);}

    @GetMapping("/started")
    public List<Match> getMatchStarted() {return matchService.getStarted();}

    @GetMapping("/notstarted")
    public List<Match> getMatchNotStarted() {return matchService.getNotStarted();}

    @PostMapping
    public Match createMatch(@RequestBody MatchDTO matchDTO) {
        return matchService.creerMatch(matchDTO);
    }

    /**
    @PostMapping("/test")
    public Match testMatch(){ return matchService.testMatch();}
    */

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

    @PatchMapping("{idMatch}/equipe/{idEquipe}/joueur/{idJoueur}/point")
    public Match addPoint(@PathVariable Long idMatch, @PathVariable Long idEquipe, @PathVariable Long idJoueur, @RequestBody MatchPointDTO matchPointDTO) {
        return matchService.ajouterPoint(idMatch,  idEquipe, matchPointDTO, idJoueur);
    }

    @PatchMapping("{idMatch}/equipe/{idEquipe}/joueur/{idJoueur}/faute")
    public Match addFaute(@PathVariable Long idMatch, @PathVariable Long idEquipe, @PathVariable Long idJoueur) {
        return matchService.ajouterFaute(idMatch, idEquipe, idJoueur);
    }


    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id) {
        matchService.deleteById(id);
    }

}
