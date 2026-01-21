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

/**
 * Contrôleur REST pour la gestion des actions liées aux matchs (points et fautes).
 *
 * Ce contrôleur expose des endpoints pour récupérer, filtrer et ajouter des actions
 * (points ou fautes) associées aux joueurs et aux matchs. Les actions sont identifiées
 * par leur type, leur association à un match et à un joueur.
 */
@RestController
@Tag(name = "ActionMatch", description = "Endpoints pour gérer les points et fautes des joueurs")
@RequestMapping("/api/action-match")
public class ActionMatchController {

    /** Service utilisé pour gérer les opérations liées aux actions de match. */
    private final ActionMatchService actionMatchService;

    /**
     * Constructeur du contrôleur.
     *
     * @param actionMatchService Service injecté pour gérer les actions de match.
     */
    public ActionMatchController(ActionMatchService actionMatchService) {
        this.actionMatchService = actionMatchService;
    }

    /**
     * Récupère toutes les actions (points et fautes) de tous les matchs.
     *
     * @return Une liste de toutes les {@link ActionMatch}.
     */
    @GetMapping
    @Operation(summary = "Liste tout", description = "Récupère toutes les actions de tous les match")
    public List<ActionMatch> findAll() {
        return actionMatchService.findAll();
    }

    /**
     * Récupère une action spécifique par son identifiant.
     *
     * @param id Identifiant unique de l'action.
     * @return L'{@link ActionMatch} correspondant à l'identifiant fourni.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Trouve par ID", description = "Récupère une action (faute ou point) avec son ID connu")
    public ActionMatch findById(@PathVariable Long id) {
        return actionMatchService.findById(id);
    }

    /**
     * Récupère toutes les actions (points et fautes) pour un match spécifique.
     *
     * @param matchId Identifiant unique du match.
     * @return Une liste de toutes les {@link ActionMatch} pour le match spécifié.
     */
    @GetMapping("/match/{matchId}")
    @Operation(summary = "Action d'un match", description = "Récupère toutes les actions pour un match avec son ID")
    public List<ActionMatch> findByMatchId(@PathVariable Long matchId) {
        return actionMatchService.findByMatchId(matchId);
    }

    /**
     * Récupère toutes les actions d'un type spécifique (point ou faute).
     *
     * @param type Type de l'action (point ou faute).
     * @return Une liste de toutes les {@link ActionMatch} du type spécifié.
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "Filtre par type", description = "Récupérer tous les point ou faute")
    public List<ActionMatch> findByActionType(@PathVariable ActionTypeDTO type) {
        return actionMatchService.findByActionType(type);
    }

    /**
     * Récupère toutes les actions d'un type spécifique pour un match donné.
     *
     * @param type Type de l'action (point ou faute).
     * @param matchId Identifiant unique du match.
     * @return Une liste de toutes les {@link ActionMatch} du type spécifié pour le match donné.
     */
    @GetMapping("/match/{matchId}/type/{type}")
    @Operation(summary = "Filtre par type dans un match", description = "Tous les point ou fautes du match ID")
    public List<ActionMatch> findByActionTypeAndMatchId(@PathVariable ActionTypeDTO type,
                                                        @PathVariable Long matchId) {
        return actionMatchService.findByActionTypeAndMatchId(type, matchId);
    }

    /**
     * Récupère toutes les actions d'un joueur spécifique dans un match donné.
     *
     * @param joueurId Identifiant unique du joueur.
     * @param matchId Identifiant unique du match.
     * @return Une liste de toutes les {@link ActionMatch} du joueur dans le match spécifié.
     */
    @GetMapping("/match/{matchId}/joueur/{joueurId}")
    @Operation(summary = "Actions d'un joueur dans un match", description = "Tout ce qu'a fait le joueur dans le match")
    public List<ActionMatch> findByJoueurAndMatchId(@PathVariable Long joueurId,
                                                    @PathVariable Long matchId) {
        return actionMatchService.findByJoueurAndMatchId(joueurId, matchId);
    }

    /**
     * Récupère toutes les actions d'un type spécifique pour un joueur dans un match donné.
     *
     * @param type Type de l'action (point ou faute).
     * @param matchId Identifiant unique du match.
     * @param joueurId Identifiant unique du joueur.
     * @return Une liste de toutes les {@link ActionMatch} du type spécifié pour le joueur dans le match donné.
     */
    @GetMapping("/match/{matchId}/joueur/{joueurId}/type/{type}")
    @Operation(summary = "Filtre précis", description = "Tous les poits/fautes du joueur dans le match")
    public List<ActionMatch> findByActionAndJoueurAndMatchId(@PathVariable ActionTypeDTO type,
                                                             @PathVariable Long matchId,
                                                             @PathVariable Long joueurId) {
        return actionMatchService.findByActionAndJoueurAndMatchId(type, matchId, joueurId);
    }

    /**
     * Ajoute un point à une équipe pour un match spécifique.
     *
     * @param idMatch Identifiant unique du match.
     * @param idEquipe Identifiant unique de l'équipe.
     * @param matchPointDTO Objet contenant les informations sur le point à ajouter.
     * @return L'{@link ActionMatch} nouvellement créée pour le point.
     */
    @PostMapping("/{idMatch}/equipe/{idEquipe}/point")
    public ActionMatch addPoint(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestParam MatchPointDTO matchPointDTO) {
        return actionMatchService.addPoint(idMatch, idEquipe, matchPointDTO);
    }

    /**
     * Ajoute une faute à une équipe pour un match spécifique.
     *
     * @param idMatch Identifiant unique du match.
     * @param idEquipe Identifiant unique de l'équipe.
     * @param matchFauteDTO Objet contenant les informations sur la faute à ajouter.
     * @return L'{@link ActionMatch} nouvellement créée pour la faute.
     */
    @PostMapping("/{idMatch}/equipe/{idEquipe}/faute")
    public ActionMatch addFaute(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestBody MatchFauteDTO matchFauteDTO) {
        return actionMatchService.addFaute(idMatch, idEquipe, matchFauteDTO);
    }
}
