package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Championnat;
import org.ultimateam.apiultimate.model.Competition;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Tournoi;
import org.ultimateam.apiultimate.service.ChampionnatService;
import org.ultimateam.apiultimate.service.CompetitionService;
import org.ultimateam.apiultimate.service.TournoisService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des compétitions (Tournois et Championnats).
 */
@RestController
@RequestMapping("/api/competition")
@RequiredArgsConstructor
@Tag(name = "Competition", description = "Endpoints pour gérer les compétitions")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final TournoisService tournoisService;
    private final ChampionnatService championnatService;

    /**
     * Récupère la liste complète de toutes les compétitions.
     *
     * @return Une liste de toutes les compétitions.
     */
    @Operation(summary = "Lister toutes les compétitions", description = "Retourne la liste complète de toutes les compétitions.")
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Competition> findAll() {
        return (List<Competition>) competitionService.getAllCompetition();
    }

    /**
     * Récupère une compétition par son identifiant.
     *
     * @param id Identifiant unique de la compétition.
     * @return La compétition correspondante.
     */
    @Operation(summary = "Récupérer une compétition par ID", description = "Retourne la compétition correspondant à l'identifiant fourni.")
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public Competition findById(@PathVariable Long id) {
        return competitionService.getCompetitionById(id);
    }

    /**
     * Récupère la liste des matchs d'une compétition.
     *
     * @param idCompetition Identifiant unique de la compétition.
     * @return Une liste de matchs.
     */
    @Operation(summary = "Lister les matchs d'une compétition", description = "Retourne la liste des matchs associés à la compétition.")
    @GetMapping("/{idCompetition}/matchs")
    @PreAuthorize("permitAll()")
    public List<Match> findMatches(@PathVariable Long idCompetition) {
        return competitionService.getMatchesByCompetition(idCompetition);
    }

    /**
     * Crée une nouvelle compétition.
     * Réservé aux administrateurs.
     *
     * @param competition Objet compétition à créer.
     * @return La compétition créée.
     */
    @Operation(summary = "Créer une compétition", description = "Crée une nouvelle compétition. Réservé aux administrateurs.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Competition creerCompetition(@RequestBody Competition competition) {
        return competitionService.saveCompetition(competition);
    }

    /**
     * Ajoute un terrain à une compétition.
     * Réservé aux administrateurs.
     *
     * @param idCompetition Identifiant de la compétition.
     * @param idTerrain Identifiant du terrain.
     * @return La compétition mise à jour.
     */
    @Operation(summary = "Ajouter un terrain", description = "Lie un terrain existant à une compétition. Réservé aux administrateurs.")
    @PostMapping("/{idCompetition}/terrain/{idTerrain}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Competition ajouterTerrain(@PathVariable Long idCompetition, @PathVariable Long idTerrain) {
        return competitionService.ajouterTerrainACompetition(idCompetition, idTerrain);
    }

    /**
     * Génère les matchs d'une compétition.
     * Réservé aux administrateurs.
     *
     * @param idCompetition Identifiant de la compétition.
     * @return La liste des matchs générés.
     */
    @Operation(summary = "Générer les matchs", description = "Génère tous les matchs pour la compétition. Réservé aux administrateurs.")
    @PutMapping("/{idCompetition}/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Match> genererMatchs(@PathVariable Long idCompetition) {
        return competitionService.genererCompetition(idCompetition);
    }

    /**
     * Supprime une compétition.
     * Réservé aux administrateurs.
     *
     * @param id Identifiant de la compétition à supprimer.
     */
    @Operation(summary = "Supprimer une compétition", description = "Supprime la compétition. Réservé aux administrateurs.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteById(@PathVariable Long id) {
        competitionService.deleteCompetitionById(id);
    }

    /**
     * Liste tous les tournois.
     *
     * @return Une liste de tournois.
     */
    @Operation(summary = "Lister les tournois", description = "Retourne la liste complète des tournois.")
    @GetMapping("/tournoi")
    @PreAuthorize("permitAll()")
    public List<Tournoi> findAllTournois() {
        return tournoisService.getAllTournois();
    }

    /**
     * Crée un nouveau tournoi.
     * Réservé aux administrateurs.
     *
     * @param tournoi Objet tournoi à créer.
     * @return Le tournoi créé.
     */
    @Operation(summary = "Créer un tournoi", description = "Crée un nouveau tournoi. Réservé aux administrateurs.")
    @PostMapping("/tournoi")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Tournoi creerTournois(@RequestBody Tournoi tournoi) {
        return tournoisService.saveTournois(tournoi);
    }

    /**
     * Liste tous les championnats.
     *
     * @return Une liste de championnats.
     */
    @Operation(summary = "Lister les championnats", description = "Retourne la liste complète des championnats.")
    @GetMapping("/championnat")
    @PreAuthorize("permitAll()")
    public List<Championnat> findAllChampionnat() {
        return championnatService.getAllChampionnat();
    }

    /**
     * Crée un nouveau championnat.
     * Réservé aux administrateurs.
     *
     * @param championnat Objet championnat à créer.
     * @return Le championnat créé.
     */
    @Operation(summary = "Créer un championnat", description = "Crée un nouveau championnat. Réservé aux administrateurs.")
    @PostMapping("/championnat")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Championnat creerChampionnat(@RequestBody Championnat championnat) {
        return championnatService.saveChampionnat(championnat);
    }

    /**
     * Synchronise l'état de démarrage d'une compétition.
     * Réservé aux administrateurs.
     *
     * @param idCompetition Identifiant de la compétition.
     * @return La compétition mise à jour.
     */
    @Operation(summary = "Vérifier le démarrage", description = "Synchronise l'état de démarrage d'une compétition. Réservé aux administrateurs.")
    @PutMapping("/{idCompetition}/checkCommencer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Competition checkCommencer(@PathVariable Long idCompetition) {
        return competitionService.checkCommencer(idCompetition);
    }

    /**
     * Retire un terrain d'une compétition.
     * Réservé aux administrateurs.
     *
     * @param idCompetition Identifiant de la compétition.
     * @param idTerrain Identifiant du terrain.
     * @return La compétition mise à jour.
     */
    @Operation(summary = "Retirer un terrain", description = "Supprime le lien entre un terrain et une compétition. Réservé aux administrateurs.")
    @DeleteMapping("/{idCompetition}/terrain/{idTerrain}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Competition retirerTerrain(@PathVariable Long idCompetition, @PathVariable Long idTerrain) {
        return competitionService.retirerTerrainDeCompetition(idCompetition, idTerrain);
    }

    /**
     * Nettoie les matchs et indisponibilités d'une compétition.
     * Réservé aux administrateurs.
     *
     * @param idCompetition Identifiant de la compétition.
     */
    @Operation(summary = "Nettoyer une compétition", description = "Supprime tous les matchs et indisponibilités d'une compétition non commencée. Réservé aux administrateurs.")
    @DeleteMapping("/{idCompetition}/clean")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void nettoyerMatchsEtIndispos(@PathVariable Long idCompetition) {
        competitionService.nettoyerMatchsEtIndispos(idCompetition);
    }
}