package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.ActionTypeDTO;
import org.ultimateam.apiultimate.DTO.MatchFauteDTO;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
import org.ultimateam.apiultimate.model.ActionMatch;
import org.ultimateam.apiultimate.service.ActionMatchService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des actions liées aux matchs (points et fautes).
 */
@RestController
@RequestMapping("/api/action-match")
@RequiredArgsConstructor
@Tag(name = "ActionMatch", description = "Endpoints pour gérer les points et fautes des joueurs")
public class ActionMatchController {

    private final ActionMatchService actionMatchService;

    /**
     * Récupère toutes les actions (points et fautes) de tous les matchs.
     *
     * @return Une liste de toutes les {@link ActionMatch}.
     */
    @Operation(summary = "Liste tout", description = "Récupère toutes les actions de tous les matchs.")
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<ActionMatch> findAll() {
        return actionMatchService.findAll();
    }

    /**
     * Récupère une action spécifique par son identifiant.
     *
     * @param id Identifiant unique de l'action.
     * @return L'{@link ActionMatch} correspondant à l'identifiant fourni.
     */
    @Operation(summary = "Trouve par ID", description = "Récupère une action (faute ou point) via son ID.")
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ActionMatch findById(@PathVariable Long id) {
        return actionMatchService.findById(id);
    }

    /**
     * Récupère toutes les actions (points et fautes) pour un match spécifique.
     *
     * @param matchId Identifiant unique du match.
     * @return Une liste de toutes les {@link ActionMatch} pour le match spécifié.
     */
    @Operation(summary = "Actions d'un match", description = "Récupère toutes les actions pour un match donné.")
    @GetMapping("/match/{matchId}")
    @PreAuthorize("permitAll()")
    public List<ActionMatch> findByMatchId(@PathVariable Long matchId) {
        return actionMatchService.findByMatchId(matchId);
    }

    /**
     * Récupère toutes les actions d'un type spécifique (point ou faute).
     *
     * @param type Type de l'action.
     * @return Une liste de toutes les {@link ActionMatch} du type spécifié.
     */
    @Operation(summary = "Filtre par type", description = "Récupère tous les points ou fautes.")
    @GetMapping("/type/{type}")
    @PreAuthorize("permitAll()")
    public List<ActionMatch> findByActionType(@PathVariable ActionTypeDTO type) {
        return actionMatchService.findByActionType(type);
    }

    /**
     * Récupère toutes les actions d'un type spécifique pour un match donné.
     *
     * @param type Type de l'action.
     * @param matchId Identifiant unique du match.
     * @return Une liste filtrée par type et match.
     */
    @Operation(summary = "Filtre par type dans un match", description = "Tous les points ou fautes d'un match spécifique.")
    @GetMapping("/match/{matchId}/type/{type}")
    @PreAuthorize("permitAll()")
    public List<ActionMatch> findByActionTypeAndMatchId(@PathVariable ActionTypeDTO type, @PathVariable Long matchId) {
        return actionMatchService.findByActionTypeAndMatchId(type, matchId);
    }

    /**
     * Récupère toutes les actions d'un joueur spécifique dans un match donné.
     *
     * @param joueurId Identifiant unique du joueur.
     * @param matchId Identifiant unique du match.
     * @return Une liste des actions du joueur dans ce match.
     */
    @Operation(summary = "Actions d'un joueur dans un match", description = "Tout ce qu'a fait le joueur dans le match.")
    @GetMapping("/match/{matchId}/joueur/{joueurId}")
    @PreAuthorize("permitAll()")
    public List<ActionMatch> findByJoueurAndMatchId(@PathVariable Long joueurId, @PathVariable Long matchId) {
        return actionMatchService.findByJoueurAndMatchId(joueurId, matchId);
    }

    /**
     * Récupère toutes les actions d'un type spécifique pour un joueur dans un match donné.
     *
     * @param type Type de l'action.
     * @param matchId Identifiant unique du match.
     * @param joueurId Identifiant unique du joueur.
     * @return Une liste filtrée par type, joueur et match.
     */
    @Operation(summary = "Filtre précis", description = "Tous les points/fautes du joueur dans le match.")
    @GetMapping("/match/{matchId}/joueur/{joueurId}/type/{type}")
    @PreAuthorize("permitAll()")
    public List<ActionMatch> findByActionAndJoueurAndMatchId(@PathVariable ActionTypeDTO type, @PathVariable Long matchId, @PathVariable Long joueurId) {
        return actionMatchService.findByActionAndJoueurAndMatchId(type, matchId, joueurId);
    }

    /**
     * Ajoute un point à une équipe pour un match spécifique.
     * Réservé aux administrateurs.
     *
     * @param idMatch Identifiant unique du match.
     * @param idEquipe Identifiant unique de l'équipe.
     * @param matchPointDTO Objet contenant les informations sur le point.
     * @return L'{@link ActionMatch} créée.
     */
    @Operation(summary = "Ajouter un point", description = "Ajoute un point au match. Réservé aux administrateurs.")
    @PostMapping("/{idMatch}/equipe/{idEquipe}/point")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ActionMatch addPoint(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestBody MatchPointDTO matchPointDTO) {
        return actionMatchService.addPoint(idMatch, idEquipe, matchPointDTO);
    }

    /**
     * Ajoute une faute à une équipe pour un match spécifique.
     * Réservé aux administrateurs.
     *
     * @param idMatch Identifiant unique du match.
     * @param idEquipe Identifiant unique de l'équipe.
     * @param matchFauteDTO Objet contenant les informations sur la faute.
     * @return L'{@link ActionMatch} créée.
     */
    @Operation(summary = "Ajouter une faute", description = "Ajoute une faute au match. Réservé aux administrateurs.")
    @PostMapping("/{idMatch}/equipe/{idEquipe}/faute")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ActionMatch addFaute(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestBody MatchFauteDTO matchFauteDTO) {
        return actionMatchService.addFaute(idMatch, idEquipe, matchFauteDTO);
    }
}