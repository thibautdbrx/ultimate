package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.IndisponibiliteDTO;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.service.IndisponibiliteService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Tag(name = "Indisponibilite", description = "Endpoints pour gérer les indisponibilités des équipes")
@RequestMapping("/api/indisponibilite")
public class IndisponibiliteController {

    private final IndisponibiliteService indisponibiliteService;

    public IndisponibiliteController(IndisponibiliteService indisponibiliteService) {
        this.indisponibiliteService = indisponibiliteService;
    }

    @Operation(
            summary = "Lister toutes les indisponibilités",
            description = "Retourne la liste complète de toutes les indisponibilités des équipes."
    )
    @GetMapping
    public List<IndisponibiliteDTO> getAllIndisponibilites() {
        return indisponibiliteService.getAllDTO();
    }


    @Operation(
            summary = "Récupérer une indisponibilité par son identifiant",
            description = "Retourne l'indisponibilité correspondant à l'identifiant fourni."
    )
    @Parameter(
            name = "id",
            description = "Identifiant unique de l'indisponibilité.",
            required = true
    )
    @GetMapping("/{id}")
    public IndisponibiliteDTO getIndisponibiliteById(@PathVariable Long id) {
        return indisponibiliteService.getByIdDTO(id);
    }


    @Operation(
            summary = "Lister les indisponibilités d'une équipe",
            description = "Retourne la liste des indisponibilités pour l'équipe identifiée par son id."
    )
    @Parameter(
            name = "id",
            description = "Identifiant unique de l'équipe.",
            required = true
    )
    @GetMapping("/equipe/{id}")
    public List<IndisponibiliteDTO> getEquipeIndisponibilite(@PathVariable Long id){
        return indisponibiliteService.getEquipeIndisponibilite(id);
    }


    @Operation(
            summary = "Créer une indisponibilité",
            description = "Crée une nouvelle indisponibilité pour une équipe à partir des informations fournies."
    )
    @PostMapping
    public IndisponibiliteDTO createIndisponibilite(@RequestBody IndisponibiliteDTO dto) {
        return indisponibiliteService.addIndisponibilite(dto);
    }

    //Json = "dateDebut" : "yyyy-MM-dd HH:mm", "dateFin" : "yyyy-MM-dd HH:mm"

    @Operation(
            summary = "Mettre à jour une indisponibilité",
            description = "Met à jour une indisponibilité existante identifiée par son id."
    )
    @Parameter(
            name = "idIndisponibilite",
            description = "Identifiant unique de l'indisponibilité à mettre à jour.",
            required = true
    )
    @PutMapping("/{idIndisponibilite}")
    public IndisponibiliteDTO updateIndisponibilite(@PathVariable Long idIndisponibilite, @RequestBody IndisponibiliteDTO dto) {
        return indisponibiliteService.updateIndisponibilite(dto, idIndisponibilite);
    }


    @Operation(
            summary = "Supprimer une indisponibilité",
            description = "Supprime l'indisponibilité correspondant à l'identifiant fourni."
    )
    @Parameter(
            name = "idIndisponibilite",
            description = "Identifiant unique de l'indisponibilité à supprimer.",
            required = true
    )
    @DeleteMapping("/{idIndisponibilite}")
    public void deleteIndisponibilite(@PathVariable Long idIndisponibilite) {
        indisponibiliteService.deleteIndisponibilite(idIndisponibilite);
    }


}
