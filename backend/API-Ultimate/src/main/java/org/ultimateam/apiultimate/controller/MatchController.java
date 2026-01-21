package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.MatchDTO;
import org.ultimateam.apiultimate.DTO.MatchFauteDTO;
import org.ultimateam.apiultimate.DTO.MatchPointDTO;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.service.MatchService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des matchs.
 */
@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
@Tag(name = "Match", description = "Endpoints pour gérer les matchs (Scores, Chronomètre, Création)")
public class MatchController {

    private final MatchService matchService;

    /**
     * Récupère la liste complète de tous les matchs.
     *
     * @return Une liste de tous les {@link Match}.
     */
    @Operation(summary = "Lister tous les matchs", description = "Retourne la liste complète de tous les matchs enregistrés.")
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Match> getAllMatch() {
        return (List<Match>) matchService.getAll();
    }

    /**
     * Récupère un match par son identifiant.
     *
     * @param id Identifiant unique du match.
     * @return Le {@link Match} correspondant.
     */
    @Operation(summary = "Récupérer un match", description = "Retourne le match correspondant à l'identifiant fourni.")
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public Match getById(@PathVariable Long id) {
        return matchService.getById(id);
    }

    /**
     * Récupère la liste des matchs 'commencés'.
     *
     * @return Une liste de {@link Match} en cours.
     */
    @Operation(summary = "Lister les matchs en cours", description = "Retourne la liste des matchs dont l'état est 'commencé'.")
    @GetMapping("/started")
    @PreAuthorize("permitAll()")
    public List<Match> getMatchStarted() {
        return matchService.getStarted();
    }

    /**
     * Récupère la liste des matchs non commencés.
     *
     * @return Une liste de {@link Match} à venir.
     */
    @Operation(summary = "Lister les matchs à venir", description = "Retourne la liste des matchs qui n'ont pas encore commencé.")
    @GetMapping("/notstarted")
    @PreAuthorize("permitAll()")
    public List<Match> getMatchNotStarted() {
        return matchService.getNotStarted();
    }

    /**
     * Récupère la liste des matchs terminés.
     *
     * @return Une liste de {@link Match} terminés.
     */
    @Operation(summary = "Lister les matchs terminés", description = "Retourne la liste des matchs dont l'état est 'terminé'.")
    @GetMapping("/finished")
    @PreAuthorize("permitAll()")
    public List<Match> getMatchFinished() {
        return matchService.getFinished();
    }

    /**
     * Récupère les matchs d'une équipe (via un joueur de l'équipe).
     *
     * @param idJoueur Identifiant d'un joueur de l'équipe.
     * @return Liste des matchs.
     */
    @Operation(summary = "Matchs d'une équipe", description = "Retourne les matchs associés à l'équipe d'un joueur donné.")
    @GetMapping("/joueur/{idJoueur}")
    @PreAuthorize("permitAll()")
    public List<Match> getMatchsEquipe(@PathVariable long idJoueur) {
        return matchService.getMatchesByEquipe(idJoueur);
    }

    /**
     * Récupère les matchs planifiés sur un terrain spécifique.
     *
     * @param idTerrain Identifiant du terrain.
     * @return Liste des matchs.
     */
    @Operation(summary = "Matchs d'un terrain", description = "Retourne les matchs planifiés sur un terrain spécifique.")
    @GetMapping("terrains/{idTerrain}")
    @PreAuthorize("permitAll()")
    public List<Match> getMatchsTerrains(@PathVariable long idTerrain) {
        return matchService.getMatchesByTerrain(idTerrain);
    }

    /**
     * Crée un nouveau match.
     * Réservé aux administrateurs.
     *
     * @param matchDTO Objet DTO de création.
     * @return Le {@link Match} créé.
     */
    @Operation(summary = "Créer un match", description = "Crée un nouveau match. Réservé aux administrateurs.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Match createMatch(@RequestBody MatchDTO matchDTO) {
        return matchService.creerMatch(matchDTO);
    }

    /**
     * Démarre un match.
     * Réservé aux administrateurs.
     *
     * @param id Identifiant du match.
     * @return Le {@link Match} mis à jour.
     */
    @Operation(summary = "Démarrer un match", description = "Passe l'état du match à 'commencé'. Réservé aux administrateurs.")
    @PutMapping("/{id}/start")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Match startMatch(@PathVariable Long id) {
        return matchService.commencerMatch(id);
    }

    /**
     * Met un match en pause.
     * Réservé aux administrateurs.
     *
     * @param id Identifiant du match.
     * @return Le {@link Match} mis à jour.
     */
    @Operation(summary = "Mettre en pause", description = "Met le match en pause. Réservé aux administrateurs.")
    @PutMapping("/{id}/pause")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Match pauseMatch(@PathVariable Long id) {
        return matchService.mettreEnPause(id);
    }

    /**
     * Reprend un match en pause.
     * Réservé aux administrateurs.
     *
     * @param id Identifiant du match.
     * @return Le {@link Match} mis à jour.
     */
    @Operation(summary = "Reprendre un match", description = "Reprend le chronomètre d'un match en pause. Réservé aux administrateurs.")
    @PutMapping("/{id}/resume")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Match resumeMatch(@PathVariable Long id) {
        return matchService.reprendreMatch(id);
    }

    /**
     * Termine un match.
     * Réservé aux administrateurs.
     *
     * @param id Identifiant du match.
     * @return Le {@link Match} mis à jour.
     */
    @Operation(summary = "Terminer un match", description = "Clôture le match définitivement. Réservé aux administrateurs.")
    @PutMapping("/{id}/end")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Match endMatch(@PathVariable Long id) {
        return matchService.finirMatch(id);
    }

    /**
     * Ajoute un point.
     * Réservé aux administrateurs.
     *
     * @param idMatch Identifiant du match.
     * @param idEquipe Identifiant de l'équipe.
     * @param matchPointDTO Infos du point.
     * @return Le {@link Match} mis à jour.
     */
    @Operation(summary = "Ajouter un point", description = "Ajoute un point au score. Réservé aux administrateurs.")
    @PatchMapping("{idMatch}/equipe/{idEquipe}/point")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Match addPoint(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestBody MatchPointDTO matchPointDTO) {
        return matchService.ajouterPoint(idMatch, idEquipe, matchPointDTO);
    }

    /**
     * Ajoute une faute.
     * Réservé aux administrateurs.
     *
     * @param idMatch Identifiant du match.
     * @param idEquipe Identifiant de l'équipe.
     * @param matchFauteDTO Infos de la faute.
     * @return Le {@link Match} mis à jour.
     */
    @Operation(summary = "Ajouter une faute", description = "Enregistre une faute. Réservé aux administrateurs.")
    @PatchMapping("{idMatch}/equipe/{idEquipe}/faute")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Match addFaute(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestBody MatchFauteDTO matchFauteDTO) {
        return matchService.ajouterFaute(idMatch, idEquipe, matchFauteDTO);
    }

    /**
     * Supprime un match.
     * Réservé aux administrateurs.
     *
     * @param id Identifiant du match.
     */
    @Operation(summary = "Supprimer un match", description = "Supprime le match de la base de données. Réservé aux administrateurs.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteMatch(@PathVariable Long id) {
        matchService.deleteById(id);
    }
}