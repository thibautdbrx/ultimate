package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.EquipeNameDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Tournoi;
import org.ultimateam.apiultimate.service.CompetitionService;
import org.ultimateam.apiultimate.service.TournoisService;

import java.util.List;

@RestController
@RequestMapping("/api/tournois")
@Tag(name = "Tournoi", description = "Endpoints pour gérer les tournois")
public class TournoisController {


    private final TournoisService tournoisService;
    private final CompetitionService competitionService;

    @Autowired
    public TournoisController(TournoisService tournoisService, CompetitionService competitionService) {
        this.tournoisService = tournoisService;
        this.competitionService = competitionService;
    }

    @Operation(
            summary = "Lister tous les tournois",
            description = "Retourne la liste complète de tous les tournois."
    )
    @GetMapping
    public List<Tournoi> findAll() { return (List<Tournoi>) tournoisService.getAllTournois(); }

    @Operation(
            summary = "Récupérer un tournoi par son identifiant",
            description = "Retourne le tournoi correspondant à l'identifiant fourni."
    )
    @Parameter(
            name = "id",
            description = "Identifiant unique du tournoi.",
            required = true
    )
    @GetMapping("/{id}")
    public Tournoi findById(@PathVariable Long id) { return tournoisService.getTournoisById(id); }

    @Operation(
            summary = "Lister les matchs d'un tournoi",
            description = "Retourne la liste de tous les matchs associés au tournoi identifié par son id."
    )
    @Parameter(
            name = "idTournoi",
            description = "Identifiant unique du tournoi.",
            required = true
    )
    @GetMapping("{idTournoi}/matchs")
    public List<Match> findMatches(@PathVariable Long idTournoi) { return tournoisService.getMatchesByTournois(idTournoi);}

    @Operation(
            summary = "Créer un tournoi",
            description = "Crée un nouveau tournoi à partir des informations fournies."
    )
    @PostMapping
    public Tournoi creerTournois(@RequestBody Tournoi tournoi) {return tournoisService.saveTournois(tournoi); }

    @Operation(
            summary = "Modifier le nom d'un tournoi",
            description = "Met à jour le nom d'un tournoi identifié par son id."
    )
    @Parameter(
            name = "idTournoi",
            description = "Identifiant unique du tournoi à modifier.",
            required = true
    )
    @PatchMapping("/{idTournoi}")
    public Tournoi editTournoi(@RequestBody EquipeNameDTO nameDTO, @PathVariable long idTournoi) { return tournoisService.editTournois(nameDTO, idTournoi);}

    @Operation(
            summary = "Générer les matchs d'un tournoi",
            description = "Génère tous les matchs d'un tournoi pour le tournoi identifié par son id."
    )
    @Parameter(
            name = "idTournoi",
            description = "Identifiant unique du tournoi pour lequel générer les matchs.",
            required = true
    )
    @PutMapping("/{idCompetition}/create")
    public List<Match> genererMatchs(@PathVariable Long idCompetition) { return competitionService.genererCompetition(idCompetition);}

    @Operation(
            summary = "Supprimer un tournoi",
            description = "Supprime le tournoi correspondant à l'identifiant fourni."
    )
    @Parameter(
            name = "id",
            description = "Identifiant unique du tournoi à supprimer.",
            required = true
    )
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) { tournoisService.deleteTournoisById(id); }


}
