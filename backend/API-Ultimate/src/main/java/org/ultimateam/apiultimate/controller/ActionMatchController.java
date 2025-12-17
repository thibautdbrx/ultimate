package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.ActionTypeDTO;
import org.ultimateam.apiultimate.DTO.MatchFauteDTO;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
import org.ultimateam.apiultimate.model.ActionMatch;
import org.ultimateam.apiultimate.service.ActionMatchService;

import java.util.List;

@RestController
@Tag(name = "ActionMatch", description = "Endpoints pour gérer les points et fautes des joueurs")
@RequestMapping("/api/action-match")
public class ActionMatchController {

    private final ActionMatchService actionMatchService;

    public ActionMatchController(ActionMatchService actionMatchService) {
        this.actionMatchService = actionMatchService;
    }

    @GetMapping
    @Operation(summary = "Liste tout", description = "Récupère toutes les actions de tous les match")
    public List<ActionMatch> findAll() {
        return actionMatchService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouve par ID", description = "Récupère une action (faute ou point) avec son ID connu")
    public ActionMatch findById(@PathVariable Long id) {
        return actionMatchService.findById(id);
    }

    @GetMapping("/match/{matchId}")
    @Operation(summary = "Action d'un match", description = "Récupère toutes les actions pour un match avec son ID")
    public List<ActionMatch> findByMatchId(@PathVariable Long matchId) {
        return actionMatchService.findByMatchId(matchId);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Filtre par type", description = "Récupérer tous les point ou faute")
    public List<ActionMatch> findByActionType(@PathVariable ActionTypeDTO type) {
        return actionMatchService.findByActionType(type);
    }

    @GetMapping("/match/{matchId}/type/{type}")
    @Operation(summary = "Filtre par type dans un match", description = "Tous les point ou fautes du match ID")
    public List<ActionMatch> findByActionTypeAndMatchId(@PathVariable ActionTypeDTO type,
                                                        @PathVariable Long matchId) {
        return actionMatchService.findByActionTypeAndMatchId(type, matchId);
    }

    @GetMapping("/match/{matchId}/joueur/{joueurId}")
    @Operation(summary = "Actions d'un joueur dans un match", description = "Tout ce qu'a fait le joueur dans le match")
    public List<ActionMatch> findByJoueurAndMatchId(@PathVariable Long joueurId,
                                                    @PathVariable Long matchId) {
        return actionMatchService.findByJoueurAndMatchId(joueurId, matchId);
    }

    @GetMapping("/match/{matchId}/joueur/{joueurId}/type/{type}")
    @Operation(summary = "Filtre précis", description = "Tous les poits/fautes du joueur dans le match")
    public List<ActionMatch> findByActionAndJoueurAndMatchId(@PathVariable ActionTypeDTO type,
                                                             @PathVariable Long matchId,
                                                             @PathVariable Long joueurId) {
        return actionMatchService.findByActionAndJoueurAndMatchId(type, matchId, joueurId);
    }

    @PostMapping("/{idMatch}/equipe/{idEquipe}/point")
    public ActionMatch addPoint(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestParam MatchPointDTO matchPointDTO) {
        return actionMatchService.addPoint(idMatch, idEquipe, matchPointDTO);
    }

    @PostMapping("/{idMatch}/equipe/{idEquipe}/faute")
    public ActionMatch addFaute(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestBody MatchFauteDTO matchFauteDTO) {
        return actionMatchService.addFaute(idMatch, idEquipe, matchFauteDTO);
    }
}
