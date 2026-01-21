package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.*;
import org.ultimateam.apiultimate.service.ChampionnatService;
import org.ultimateam.apiultimate.service.CompetitionService;
import org.ultimateam.apiultimate.service.TournoisService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des entités {@link Competition}.
 *
 * Ce contrôleur expose des endpoints pour lister, créer, supprimer des compétitions,
 * ainsi que pour générer les matchs associés. Il permet également de gérer spécifiquement
 * les tournois et les championnats.
 */
@RestController
@RequestMapping("/api/competition")
@Tag(name = "Competition", description = "Endpoints pour gérer les compétitions")
public class CompetitionController {

    /** Service utilisé pour gérer les opérations générales sur les compétitions. */
    private final CompetitionService competitionService;

    /** Service utilisé pour gérer les opérations spécifiques aux tournois. */
    private final TournoisService tournoisService;

    /** Service utilisé pour gérer les opérations spécifiques aux championnats. */
    private final ChampionnatService championnatService;

    /**
     * Constructeur du contrôleur.
     *
     * @param competitionService Service injecté pour gérer les compétitions.
     * @param tournoisService Service injecté pour gérer les tournois.
     * @param championnatService Service injecté pour gérer les championnats.
     */
    @Autowired
    public CompetitionController(CompetitionService competitionService, TournoisService tournoisService, ChampionnatService championnatService) {
        this.competitionService = competitionService;
        this.tournoisService = tournoisService;
        this.championnatService = championnatService;
    }

    /**
     * Récupère la liste complète de toutes les compétitions enregistrées.
     *
     * @return Une liste de toutes les {@link Competition}.
     */
    @GetMapping
    @Operation(
            summary = "Lister toutes les compétitions",
            description = "Retourne la liste complète de toutes les compétitions."
    )
    public List<Competition> findAll() { return (List<Competition>) competitionService.getAllCompetition(); }

    /**
     * Récupère une compétition par son identifiant.
     *
     * @param id Identifiant unique de la compétition.
     * @return La {@link Competition} correspondant à l'identifiant fourni.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer une compétition par son identifiant",
            description = "Retourne la compétition correspondant à l'identifiant fourni."
    )
    @Parameter(
            name = "id",
            description = "Identifiant unique de la compétition.",
            required = true
    )
    public Competition findById(@PathVariable Long id) { return competitionService.getCompetitionById(id); }

    /**
     * Récupère la liste des matchs associés à une compétition spécifique.
     *
     * @param idCompetition Identifiant unique de la compétition.
     * @return Une liste de {@link Match} associés à la compétition.
     */
    @GetMapping("/{idCompetition}/matchs")
    @Operation(
            summary = "Lister les matchs d'une compétition",
            description = "Retourne la liste des matchs associés à la compétition identifiée par son id."
    )
    @Parameter(
            name = "idCompetition",
            description = "Identifiant unique de la compétition.",
            required = true
    )
    public List<Match> findMatches(@PathVariable Long idCompetition) { return competitionService.getMatchesByCompetition(idCompetition);}

    /**
     * Crée une nouvelle compétition à partir des informations fournies.
     *
     * @param competition Objet {@link Competition} contenant les informations de la compétition à créer.
     * @return La {@link Competition} nouvellement créée.
     */
    @PostMapping
    @Operation(
            summary = "Créer une compétition",
            description = "Crée une nouvelle compétition à partir des informations fournies."
    )
    public Competition creerCompetition(@RequestBody Competition competition) {return competitionService.saveCompetition(competition); }

    /**
     * Génère tous les matchs pour une compétition spécifique.
     *
     * @param idCompetition Identifiant unique de la compétition pour laquelle générer les matchs.
     * @return Une liste de {@link Match} générés pour la compétition.
     */
    @PutMapping("/{idCompetition}/create")
    @Operation(
            summary = "Générer les matchs d'une compétition",
            description = "Génère tous les matchs pour la compétition identifiée par son id."
    )
    @Parameter(
            name = "idCompetition",
            description = "Identifiant unique de la compétition.",
            required = true
    )
    public List<Match> genererMatchs(@PathVariable Long idCompetition) { return competitionService.genererCompetition(idCompetition);}

    /**
     * Supprime une compétition par son identifiant.
     *
     * @param id Identifiant unique de la compétition à supprimer.
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une compétition",
            description = "Supprime la compétition correspondant à l'identifiant fourni."
    )
    @Parameter(
            name = "id",
            description = "Identifiant unique de la compétition à supprimer.",
            required = true
    )
    public void deleteById(@PathVariable Long id) { competitionService.deleteCompetitionById(id); }

    /**
     * Récupère la liste complète de tous les tournois enregistrés.
     *
     * @return Une liste de tous les {@link Tournoi}.
     */
    @GetMapping("/tournoi")
    public List<Tournoi> findAllTournois() { return tournoisService.getAllTournois(); }

    /**
     * Crée un nouveau tournoi à partir des informations fournies.
     *
     * @param tournoi Objet {@link Tournoi} contenant les informations du tournoi à créer.
     * @return Le {@link Tournoi} nouvellement créé.
     */
    @PostMapping("/tournoi")
    public Tournoi creerTournois(@RequestBody Tournoi tournoi) {return tournoisService.saveTournois(tournoi); }

    /**
     * Récupère la liste complète de tous les championnats enregistrés.
     *
     * @return Une liste de tous les {@link Championnat}.
     */
    @GetMapping("/championnat")
    public List<Championnat> findAllChampionnat() { return championnatService.getAllChampionnat(); }

    /**
     * Crée un nouveau championnat à partir des informations fournies.
     *
     * @param championnat Objet {@link Championnat} contenant les informations du championnat à créer.
     * @return Le {@link Championnat} nouvellement créé.
     */
    @PostMapping("/championnat")
    public Championnat creerChampionnat(@RequestBody Championnat championnat) {return championnatService.saveChampionnat(championnat); }


}
