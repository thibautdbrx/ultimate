package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Terrain;
import org.ultimateam.apiultimate.service.TerrainService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des terrains.
 */
@RestController
@RequestMapping("/api/terrain")
@RequiredArgsConstructor
@Tag(name = "Terrain", description = "Endpoints pour gérer les terrains et leur localisation")
public class TerrainController {

    private final TerrainService terrainService;

    /**
     * Récupère la liste complète des terrains.
     *
     * @return Une liste de tous les {@link Terrain} avec coordonnées GPS.
     */
    @Operation(summary = "Lister tous les terrains", description = "Retourne la liste complète des terrains avec leurs coordonnées GPS et villes associées.")
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Terrain> findAll() {
        return terrainService.getAll();
    }

    /**
     * Récupère un terrain par son identifiant.
     *
     * @param id Identifiant unique du terrain.
     * @return Le {@link Terrain} correspondant.
     */
    @Operation(summary = "Récupérer un terrain", description = "Retourne les détails d'un terrain spécifique.")
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public Terrain getById(@PathVariable long id) {
        return terrainService.getById(id);
    }

    /**
     * Crée ou met à jour un terrain.
     * Réservé aux administrateurs.
     *
     * @param terrain Objet {@link Terrain} contenant les infos (Ville ou GPS).
     * @return Le terrain sauvegardé avec les données complétées (GPS ou Ville).
     */
    @Operation(summary = "Créer un terrain", description = "Crée un terrain. Calcul automatique des coordonnées ou de la ville. Réservé aux administrateurs.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Terrain save(@RequestBody Terrain terrain) {
        return terrainService.saveTerrain(terrain);
    }

    /**
     * Supprime un terrain.
     * Réservé aux administrateurs.
     *
     * @param id Identifiant du terrain à supprimer.
     */
    @Operation(summary = "Supprimer un terrain", description = "Supprime définitivement un terrain de la base de données. Réservé aux administrateurs.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteById(@PathVariable long id) {
        terrainService.deleteById(id);
    }
}