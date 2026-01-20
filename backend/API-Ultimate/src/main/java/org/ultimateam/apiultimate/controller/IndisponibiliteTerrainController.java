package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.IndisponibiliteTerrain;
import org.ultimateam.apiultimate.service.IndisponibiliteTerrainService;

import java.util.List;

@RestController
@RequestMapping("/api/indisponibilites-terrain")
@Tag(name = "Indisponibilité Terrain", description = "Endpoints pour gérer les indisponibilités des terrains")
public class IndisponibiliteTerrainController {

    private final IndisponibiliteTerrainService service;

    public IndisponibiliteTerrainController(IndisponibiliteTerrainService service) {
        this.service = service;
    }

    @Operation(summary = "Lister toutes les indisponibilités", description = "Retourne la liste complète des indisponibilités de tous les terrains.")
    @GetMapping
    public List<IndisponibiliteTerrain> findAll() {
        return service.findAll();
    }

    @Operation(summary = "Lister les indisponibilités d'un terrain spécifique", description = "Filtre les indisponibilités pour un ID de terrain donné.")
    @GetMapping("/terrain/{idTerrain}")
    public List<IndisponibiliteTerrain> findByTerrainId(@PathVariable Long idTerrain) {
        return service.findByTerrainId(idTerrain);
    }

    @Operation(summary = "Ajouter une indisponibilité", description = "Crée une plage d'indisponibilité pour un terrain.")
    @PostMapping
    public IndisponibiliteTerrain create(
            @Parameter(description = "ID du terrain concerné", required = true)
            @RequestParam Long idTerrain,
            @RequestBody IndisponibiliteTerrain indispo) {
        return service.save(idTerrain, indispo);
    }

    @Operation(summary = "Supprimer une indisponibilité", description = "Supprime une indisponibilité par son ID.")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}