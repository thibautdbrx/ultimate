package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.MatchDTO;
import org.ultimateam.apiultimate.DTO.MatchFauteDTO;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
import org.ultimateam.apiultimate.model.Match;
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

    @Operation(
            summary = "Lister tous les matchs",
            description = "Retourne la liste complète de tous les matchs enregistrés."
    )
    @GetMapping
    public List<Match> getAllMatch() {
        return (List<Match>) matchService.getAll();
    }

    @Operation(
            summary = "Récupérer un match par son identifiant",
            description = "Retourne le match correspondant à l'identifiant fourni. Une erreur est renvoyée si le match n'existe pas."
    )
    @GetMapping("/{id}")
    public Match getById(@PathVariable Long id) {return matchService.getById(id);}

    @Operation(
            summary = "Lister les matchs commencés",
            description = "Retourne la liste des matchs dont l'état est 'commencé'."
    )
    @GetMapping("/started")
    public List<Match> getMatchStarted() {return matchService.getStarted();}

    @Operation(
            summary = "Lister les matchs non commencés",
            description = "Retourne la liste des matchs qui n'ont pas encore commencé."
    )
    @GetMapping("/notstarted")
    public List<Match> getMatchNotStarted() {return matchService.getNotStarted();}

    @Operation(
            summary = "Lister les matchs terminés",
            description = "Retourne la liste des matchs dont l'état est 'terminé'."
    )
    @GetMapping("/finished")
    public List<Match> getMatchFinished() {return matchService.getFinished();}

    @GetMapping("/joueur/{idJoueur}")
    public List<Match> getMatchsEquipe(@PathVariable long idJoueur) {return matchService.getMatchesByEquipe(idJoueur);}

    @GetMapping("terrains/{idTerrain}")
    public List<Match> getMatchsTerrains(@PathVariable long idTerrain) { return matchService.getMatchesByTerrain(idTerrain);}

    @Operation(
            summary = "Créer un nouveau match",
            description = "Crée un nouveau match à partir des informations fournies dans le corps de la requête (équipes, terrain, date, etc.)."
    )
    @PostMapping
    public Match createMatch(@RequestBody MatchDTO matchDTO) {
        return matchService.creerMatch(matchDTO);
    }

    /**
    @PostMapping("/test")
    public Match testMatch(){ return matchService.testMatch();}
    */

    @Operation(
            summary = "Démarrer un match",
            description = "Change l'état du match en 'commencé'. Une erreur est renvoyée si le match est déjà démarré ou inexistant."
    )
    @PutMapping("/{id}/start")
    public Match startMatch(@PathVariable Long id) {
        return matchService.commencerMatch(id);
    }


    @Operation(
            summary = "Mettre un match en pause",
            description = "Met le match en pause s'il est en cours. Une erreur est renvoyée si le match n'est pas en cours."
    )
    @PutMapping("/{id}/pause")
    public Match pauseMatch(@PathVariable Long id) {
        return matchService.mettreEnPause(id);
    }


    @Operation(
            summary = "Reprendre un match",
            description = "Reprend un match précédemment mis en pause."
    )
    @PutMapping("/{id}/resume")
    public Match resumeMatch(@PathVariable Long id) {
        return matchService.reprendreMatch(id);
    }


    @Operation(
            summary = "Terminer un match",
            description = "Met fin au match et change son état en 'terminé'."
    )
    @PutMapping("/{id}/end")
    public Match endMatch(@PathVariable Long id) {
        return matchService.finirMatch(id);
    }

    @PatchMapping("{idMatch}/equipe/{idEquipe}/point")
    public Match addPoint(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestBody MatchPointDTO matchPointDTO) {
        return matchService.ajouterPoint(idMatch,  idEquipe, matchPointDTO);
    }

    @PatchMapping("{idMatch}/equipe/{idEquipe}/faute")
    public Match addFaute(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestBody MatchFauteDTO matchFauteDTO) {
        return matchService.ajouterFaute(idMatch, idEquipe, matchFauteDTO);
    }


    @Operation(
            summary = "Supprimer un match",
            description = "Supprime le match correspondant à l'identifiant fourni. Une erreur est renvoyée si le match n'existe pas."
    )
    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id) {
        matchService.deleteById(id);
    }

}
