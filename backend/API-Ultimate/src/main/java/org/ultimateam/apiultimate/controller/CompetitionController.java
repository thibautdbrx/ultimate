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

@RestController
@RequestMapping("/api/competition")
@Tag(name = "Competition", description = "Endpoints pour gérer les compétitions")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final TournoisService tournoisService;
    private final ChampionnatService championnatService;

    @Autowired
    public CompetitionController(CompetitionService competitionService, TournoisService tournoisService, ChampionnatService championnatService) {
        this.competitionService = competitionService;
        this.tournoisService = tournoisService;
        this.championnatService = championnatService;
    }


    @GetMapping
    @Operation(
            summary = "Lister toutes les compétitions",
            description = "Retourne la liste complète de toutes les compétitions."
    )
    public List<Competition> findAll() { return (List<Competition>) competitionService.getAllCompetition(); }

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

    @PostMapping
    @Operation(
            summary = "Créer une compétition",
            description = "Crée une nouvelle compétition à partir des informations fournies."
    )
    public Competition creerCompetition(@RequestBody Competition competition) {return competitionService.saveCompetition(competition); }

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

    @GetMapping("/tournoi")
    public List<Tournoi> findAllTournois() { return tournoisService.getAllTournois(); }

    @PostMapping("/tournoi")
    public Tournoi creerTournois(@RequestBody Tournoi tournoi) {return tournoisService.saveTournois(tournoi); }

    @GetMapping("/championnat")
    public List<Championnat> findAllChampionnat() { return championnatService.getAllChampionnat(); }


    @PostMapping("/championnat")
    public Championnat creerChampionnat(@RequestBody Championnat championnat) {return championnatService.saveChampionnat(championnat); }


}
