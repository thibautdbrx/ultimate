package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.ActionMatch;
import org.ultimateam.apiultimate.service.ActionMatchService;

@RestController
@Tag(name = "ActionMatch", description = "Endpoints pour gérer les points et fautes des joueurs")
@RequestMapping("/api/action-match")
public class ActionMatchController {

    private final ActionMatchService actionMatchService;

    public ActionMatchController(ActionMatchService actionMatchService) {
        this.actionMatchService = actionMatchService;
    }

    @PostMapping("/{idMatch}/equipe/{idEquipe}/joueur/{idJoueur}/point")
    public ActionMatch addPoint(@PathVariable Long idMatch, @PathVariable Long idEquipe, @PathVariable Long idJoueur) {
        return actionMatchService.addPoint(idMatch, idEquipe, idJoueur);
    }

    @PostMapping("/{idMatch}/equipe/{idEquipe}/joueur/{idJoueur}/faute")
    public ActionMatch addFaute(@PathVariable Long idMatch, @PathVariable Long idEquipe, @PathVariable Long idJoueur) {
        return actionMatchService.addFaute(idMatch, idEquipe, idJoueur);
    }
}
