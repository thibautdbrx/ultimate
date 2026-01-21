package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.EquipeNameDTO;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Tournoi;
import org.ultimateam.apiultimate.service.CompetitionService;
import org.ultimateam.apiultimate.service.TournoisService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des tournois.
 */
@RestController
@RequestMapping("/api/tournois")
@RequiredArgsConstructor
@Tag(name = "Tournoi", description = "Endpoints pour gérer les tournois")
public class TournoisController {

    private final TournoisService tournoisService;
    private final CompetitionService competitionService;

    /**
     * Récupère la liste complète de tous les tournois.
     *
     * @return liste de tous les {@link Tournoi}.
     */
    @Operation(summary = "Lister tous les tournois", description = "Retourne la liste complète de tous les tournois.")
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Tournoi> findAll() {
        return tournoisService.getAllTournois();
    }

    /**
     * Récupère un tournoi par son identifiant.
     *
     * @param id identifiant unique du tournoi.
     * @return le {@link Tournoi} correspondant.
     */
    @Operation(summary = "Récupérer un tournoi", description = "Retourne le tournoi correspondant à l'identifiant fourni.")
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public Tournoi findById(@PathVariable Long id) {
        return tournoisService.getTournoisById(id);
    }

    /**
     * Retourne la liste des matchs associés à un tournoi donné.
     *
     * @param idTournoi identifiant du tournoi.
     * @return liste des {@link Match} du tournoi.
     */
    @Operation(summary = "Lister les matchs d'un tournoi", description = "Retourne la liste de tous les matchs associés au tournoi.")
    @GetMapping("{idTournoi}/matchs")
    @PreAuthorize("permitAll()")
    public List<Match> findMatches(@PathVariable Long idTournoi) {
        return tournoisService.getMatchesByTournois(idTournoi);
    }

    /**
     * Crée un nouveau tournoi.
     * Réservé aux administrateurs.
     *
     * @param tournoi objet {@link Tournoi} à créer.
     * @return le {@link Tournoi} créé.
     */
    @Operation(summary = "Créer un tournoi", description = "Crée un nouveau tournoi. Réservé aux administrateurs.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Tournoi creerTournois(@RequestBody Tournoi tournoi) {
        return tournoisService.saveTournois(tournoi);
    }

    /**
     * Met à jour le nom d'un tournoi existant.
     * Réservé aux administrateurs.
     *
     * @param nameDTO objet contenant le nouveau nom.
     * @param idTournoi identifiant du tournoi à modifier.
     * @return le {@link Tournoi} modifié.
     */
    @Operation(summary = "Modifier le nom d'un tournoi", description = "Met à jour le nom d'un tournoi. Réservé aux administrateurs.")
    @PatchMapping("/{idTournoi}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Tournoi editTournoi(@RequestBody EquipeNameDTO nameDTO, @PathVariable long idTournoi) {
        return tournoisService.editTournois(nameDTO, idTournoi);
    }

    /**
     * Génère les matchs pour une compétition donnée.
     * Réservé aux administrateurs.
     *
     * @param idCompetition identifiant de la compétition.
     * @return liste des {@link Match} générés.
     */
    @Operation(summary = "Générer les matchs", description = "Génère le planning des matchs pour le tournoi. Réservé aux administrateurs.")
    @PutMapping("/{idCompetition}/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Match> genererMatchs(@PathVariable Long idCompetition) {
        return competitionService.genererCompetition(idCompetition);
    }

    /**
     * Supprime le tournoi identifié par l'id fourni.
     * Réservé aux administrateurs.
     *
     * @param id identifiant du tournoi à supprimer.
     */
    @Operation(summary = "Supprimer un tournoi", description = "Supprime le tournoi. Réservé aux administrateurs.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteById(@PathVariable Long id) {
        tournoisService.deleteTournoisById(id);
    }
}