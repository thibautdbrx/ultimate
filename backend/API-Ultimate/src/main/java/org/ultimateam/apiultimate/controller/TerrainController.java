package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Terrain;
import org.ultimateam.apiultimate.service.TerrainService;

import java.util.List;


@RestController
@Tag(name = "Terrain", description = "Endpoints pour gérer les terrains et leur localisation")
@RequestMapping("/api/terrain")
public class TerrainController {

    private final TerrainService terrainService;

    public TerrainController(TerrainService terrainService) {
        this.terrainService = terrainService;
    }

    @Operation(
            summary = "Lister tous les terrains",
            description = "Retourne la liste complète des terrains avec leurs coordonnées GPS et villes associées."
    )
    @GetMapping
    public List<Terrain> findAll() {
        return terrainService.getAll();
    }

    @Operation(
            summary = "Récupérer un terrain par son identifiant",
            description = "Retourne les détails d'un terrain spécifique."
    )
    @GetMapping("/{id}")
    public Terrain getById(@PathVariable long id) {
        return terrainService.getById(id);
    }

    @Operation(
            summary = "Créer ou mettre à jour un terrain",
            description = "Crée un terrain. Si seule la ville est fournie, les coordonnées GPS sont calculées. Si seules les coordonnées sont fournies, la ville est trouvée."
    )
    @PostMapping
    public Terrain save(@RequestBody Terrain terrain) {
        return terrainService.saveTerrain(terrain);
    }

    @Operation(
            summary = "Supprimer un terrain",
            description = "Supprime définitivement un terrain de la base de données."
    )
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        terrainService.deleteById(id);
    }
}