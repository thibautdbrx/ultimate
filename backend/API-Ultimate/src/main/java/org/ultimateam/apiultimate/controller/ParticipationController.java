package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.ListEquipeDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.model.ParticipationId;
import org.ultimateam.apiultimate.service.ParticipationService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/participation")
@Tag(name = "Participation", description = "Endpoints pour gérer les participations")
public class ParticipationController {

    private final ParticipationService participationService;


    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    @GetMapping
    @Operation(
            summary = "Lister toutes les participations",
            description = "Retourne la liste complète de toutes les participations enregistrées."
    )
    public List<Participation> getAllParticipation() {
        return participationService.getAll();
    }

    @Operation(
            summary = "Lister les participations par compétition",
            description = "Retourne la liste des équipes participant à la compétition identifiée par son id."
    )
    @Parameter(
            name = "idCompetition",
            description = "Identifiant unique de la compétition.",
            required = true
    )
    @GetMapping("/competition/{idCompetition}")
    public List<Equipe> getParticipationByCompetitionId(@PathVariable Long idCompetition) {
        return participationService.getParticipationByCompetitionId(idCompetition);
    }

    @Operation(
            summary = "Lister les participations par équipe",
            description = "Retourne la liste des participations associées à l'équipe identifiée par son id."
    )
    @Parameter(
            name = "idEquipe",
            description = "Identifiant unique de l'équipe.",
            required = true
    )
    @GetMapping("/equipe/{idEquipe}")
    public List<Participation> getParticipationByEquipeId(@PathVariable Long idEquipe) {
        return participationService.getParticipationByEquipeId(idEquipe);
    }

    @Operation(
            summary = "Ajouter une participation",
            description = "Ajoute une participation unique à une compétition."
    )
    @PostMapping
    public Participation createParticipation(@RequestBody ParticipationId participationId) {
        return participationService.addParticipation(participationId);
    }

    @Operation(
            summary = "Ajouter plusieurs participations",
            description = "Ajoute une liste d'équipes à une compétition spécifique."
    )

    @PostMapping("/competition")
    public List<Participation> createParticipation(@RequestBody ListEquipeDTO listEquipeDTO) {
        return participationService.addListParticipation(listEquipeDTO);
    }

    @Operation(
            summary = "Supprimer une participation",
            description = "Supprime la participation correspondant aux informations fournies."
    )
    @DeleteMapping
    public List<Participation> deleteParticipation(@RequestBody ParticipationId participationId) {return participationService.deleteById(participationId);}


}
