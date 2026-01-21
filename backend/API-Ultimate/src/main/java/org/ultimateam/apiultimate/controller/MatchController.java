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

/**
 * Contrôleur REST pour la gestion des matchs.
 *
 * Ce contrôleur expose des endpoints pour lister, créer, démarrer, mettre en pause,
 * reprendre, terminer, et supprimer des matchs. Il permet également d'ajouter des points
 * ou des fautes à un match en cours.
 */
@RestController
@Tag(name = "Match", description = "Endpoints pour gérer les matchs")
@RequestMapping("/api/match")
public class MatchController {

    /** Service utilisé pour gérer les opérations liées aux matchs. */
    private final MatchService matchService;

    /**
     * Constructeur du contrôleur.
     *
     * @param matchService Service injecté pour gérer les matchs.
     */
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * Récupère la liste complète de tous les matchs enregistrés.
     *
     * @return Une liste de tous les {@link Match}.
     */
    @Operation(
            summary = "Lister tous les matchs",
            description = "Retourne la liste complète de tous les matchs enregistrés."
    )
    @GetMapping
    public List<Match> getAllMatch() {
        return (List<Match>) matchService.getAll();
    }
    /**
     * Récupère un match par son identifiant.
     *
     * @param id Identifiant unique du match.
     * @return Le {@link Match} correspondant à l'identifiant fourni.
     * @throws RuntimeException Si le match n'existe pas.
     */

    @Operation(
            summary = "Récupérer un match par son identifiant",
            description = "Retourne le match correspondant à l'identifiant fourni. Une erreur est renvoyée si le match n'existe pas."
    )
    @GetMapping("/{id}")
    public Match getById(@PathVariable Long id) {return matchService.getById(id);}

    /**
     * Récupère la liste des matchs dont l'état est 'commencé'.
     *
     * @return Une liste de {@link Match} en cours.
     */
    @Operation(
            summary = "Lister les matchs commencés",
            description = "Retourne la liste des matchs dont l'état est 'commencé'."
    )
    @GetMapping("/started")
    public List<Match> getMatchStarted() {return matchService.getStarted();}

    /**
     * Récupère la liste des matchs qui n'ont pas encore commencé.
     *
     * @return Une liste de {@link Match} non commencés.
     */
    @Operation(
            summary = "Lister les matchs non commencés",
            description = "Retourne la liste des matchs qui n'ont pas encore commencé."
    )
    @GetMapping("/notstarted")
    public List<Match> getMatchNotStarted() {return matchService.getNotStarted();}

    /**
     * Récupère la liste des matchs dont l'état est 'terminé'.
     *
     * @return Une liste de {@link Match} terminés.
     */
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

    /**
     * Crée un nouveau match à partir des informations fournies.
     *
     * @param matchDTO Objet contenant les informations nécessaires à la création du match (équipes, terrain, date, etc.).
     * @return Le {@link Match} nouvellement créé.
     */
    @Operation(
            summary = "Créer un nouveau match",
            description = "Crée un nouveau match à partir des informations fournies dans le corps de la requête (équipes, terrain, date, etc.)."
    )
    @PostMapping
    public Match createMatch(@RequestBody MatchDTO matchDTO) {
        return matchService.creerMatch(matchDTO);
    }

    /**
     * Méthode de test pour créer un match (actuellement commentée).
     * @return Un {@link Match} de test.
     */
    /*
    /**
    @PostMapping("/test")
    public Match testMatch(){ return matchService.testMatch();}
    */

    /**
     * Change l'état du match en 'commencé'.
     *
     * @param id Identifiant unique du match à démarrer.
     * @return Le {@link Match} mis à jour.
     * @throws RuntimeException Si le match est déjà démarré ou inexistant.
     */
    @Operation(
            summary = "Démarrer un match",
            description = "Change l'état du match en 'commencé'. Une erreur est renvoyée si le match est déjà démarré ou inexistant."
    )
    @PutMapping("/{id}/start")
    public Match startMatch(@PathVariable Long id) {
        return matchService.commencerMatch(id);
    }

    /**
     * Met le match en pause s'il est en cours.
     *
     * @param id Identifiant unique du match à mettre en pause.
     * @return Le {@link Match} mis à jour.
     * @throws RuntimeException Si le match n'est pas en cours.
     */
    @Operation(
            summary = "Mettre un match en pause",
            description = "Met le match en pause s'il est en cours. Une erreur est renvoyée si le match n'est pas en cours."
    )
    @PutMapping("/{id}/pause")
    public Match pauseMatch(@PathVariable Long id) {
        return matchService.mettreEnPause(id);
    }

    /**
     * Reprend un match précédemment mis en pause.
     *
     * @param id Identifiant unique du match à reprendre.
     * @return Le {@link Match} mis à jour.
     */
    @Operation(
            summary = "Reprendre un match",
            description = "Reprend un match précédemment mis en pause."
    )
    @PutMapping("/{id}/resume")
    public Match resumeMatch(@PathVariable Long id) {
        return matchService.reprendreMatch(id);
    }

    /**
     * Met fin au match et change son état en 'terminé'.
     *
     * @param id Identifiant unique du match à terminer.
     * @return Le {@link Match} mis à jour.
     */
    @Operation(
            summary = "Terminer un match",
            description = "Met fin au match et change son état en 'terminé'."
    )
    @PutMapping("/{id}/end")
    public Match endMatch(@PathVariable Long id) {
        return matchService.finirMatch(id);
    }

    /**
     * Ajoute un point à une équipe pour un match donné.
     *
     * @param idMatch Identifiant unique du match.
     * @param idEquipe Identifiant unique de l'équipe à qui ajouter le point.
     * @param matchPointDTO Objet contenant les informations sur le point à ajouter.
     * @return Le {@link Match} mis à jour.
     */
    @PatchMapping("{idMatch}/equipe/{idEquipe}/point")
    public Match addPoint(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestBody MatchPointDTO matchPointDTO) {
        return matchService.ajouterPoint(idMatch,  idEquipe, matchPointDTO);
    }

    /**
     * Ajoute une faute à une équipe pour un match donné.
     *
     * @param idMatch Identifiant unique du match.
     * @param idEquipe Identifiant unique de l'équipe à qui ajouter la faute.
     * @param matchFauteDTO Objet contenant les informations sur la faute à ajouter.
     * @return Le {@link Match} mis à jour.
     */
    @PatchMapping("{idMatch}/equipe/{idEquipe}/faute")
    public Match addFaute(@PathVariable Long idMatch, @PathVariable Long idEquipe, @RequestBody MatchFauteDTO matchFauteDTO) {
        return matchService.ajouterFaute(idMatch, idEquipe, matchFauteDTO);
    }

    /**
     * Supprime un match par son identifiant.
     *
     * @param id Identifiant unique du match à supprimer.
     * @throws RuntimeException Si le match n'existe pas.
     */
    @Operation(
            summary = "Supprimer un match",
            description = "Supprime le match correspondant à l'identifiant fourni. Une erreur est renvoyée si le match n'existe pas."
    )
    @DeleteMapping("/{id}")
    public void deleteMatch(@PathVariable Long id) {
        matchService.deleteById(id);
    }

}
