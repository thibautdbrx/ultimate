package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.MatchDTO;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.service.MatchService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des entités Match.
 * Expose les points de terminaison (endpoints) de l'API pour créer,
 * récupérer, supprimer et gérer le déroulement (démarrer, pause, etc.) des matchs.
 */
@RestController
@Tag(name = "Match", description = "Endpoints pour gérer les matchs")
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService matchService;

    /**
     * Constructeur pour l'injection de la dépendance MatchService.
     *
     * @param matchService Le service chargé de la logique métier pour les matchs.
     */
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * Récupère la liste de tous les matchs.
     * Mappe les requêtes HTTP GET sur /api/match.
     *
     * @return Une liste contenant tous les matchs.
     */
    @GetMapping
    public List<Match> getAllMatch() {
        return (List<Match>) matchService.getAll();
    }

    /**
     * Récupère un match spécifique par son identifiant.
     * Mappe les requêtes HTTP GET sur /api/match/{id}.
     *
     * @param id L'identifiant du match à récupérer.
     * @return Le match correspondant à l'ID, ou null si non trouvé.
     */
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
     * Démarre un match existant.
     * Mappe les requêtes HTTP PUT sur /api/match/{id}/start.
     *
     * @param id L'identifiant du match à démarrer.
     * @return Le match mis à jour (statut ONGOING).
     */
    @PutMapping("/{id}/start")
    public Match startMatch(@PathVariable Long id) {
        return matchService.commencerMatch(id);
    }

    /**
     * Met en pause un match en cours.
     * Mappe les requêtes HTTP PUT sur /api/match/{id}/pause.
     *
     * @param id L'identifiant du match à mettre en pause.
     * @return Le match mis à jour (statut PAUSED).
     */
    @PutMapping("/{id}/pause")
    public Match pauseMatch(@PathVariable Long id) {
        return matchService.mettreEnPause(id);
    }

    /**
     * Reprend un match qui était en pause.
     * Mappe les requêtes HTTP PUT sur /api/match/{id}/resume.
     *
     * @param id L'identifiant du match à reprendre.
     * @return Le match mis à jour (statut ONGOING).
     */
    @PutMapping("/{id}/resume")
    public Match resumeMatch(@PathVariable Long id) {
        return matchService.reprendreMatch(id);
    }

    /**
     * Termine un match (manuellement ou selon les règles).
     * Mappe les requêtes HTTP PUT sur /api/match/{id}/end.
     *
     * @param id L'identifiant du match à terminer.
     * @return Le match mis à jour (statut FINISHED).
     */
    @PutMapping("/{id}/end")
    public Match endMatch(@PathVariable Long id) {
        return matchService.finirMatch(id);
    }

    /**
     * Ajoute un point à une équipe lors d'un match.
     * Mappe les requêtes HTTP PUT sur /api/match/{id_match}/addPoint/{id_equipe}.
     *
     * @param id_match L'identifiant du match concerné.
     * @param id_equipe L'identifiant de l'équipe qui marque le point.
     * @return Le match mis à jour avec le score actualisé.
     */
    @PatchMapping("/{idMatch}/equipe/{idEquipe}/point")
    public Match addPoint(@PathVariable Long idMatch, @PathVariable Long idEquipe,@RequestBody MatchDTO matchDTO) {
        return matchService.ajouterPoint(idMatch, idEquipe, matchDTO);
    }


    /**
     * Supprime un match par son identifiant.
     * Mappe les requêtes HTTP DELETE sur /api/match/{id}.
     *
     * @param id L'identifiant du match à supprimer.
     */
    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id) {
        matchService.deleteById(id);
    }

}
