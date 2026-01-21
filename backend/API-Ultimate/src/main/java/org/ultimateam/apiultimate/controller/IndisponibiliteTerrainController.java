package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.IndisponibiliteTerrain;
import org.ultimateam.apiultimate.service.IndisponibiliteTerrainService;

import java.util.List;

/**
 * Contrôleur REST responsable de la gestion des indisponibilités des terrains.
 *
 * <p>Ce contrôleur expose les points d'entrée (endpoints) permettant de consulter,
 * créer et supprimer les plages horaires durant lesquelles un terrain n'est pas disponible.</p>
 */
@RestController
@RequestMapping("/api/indisponibilites-terrain")
@Tag(name = "Indisponibilité Terrain", description = "Endpoints pour gérer les indisponibilités des terrains")
public class IndisponibiliteTerrainController {

    private final IndisponibiliteTerrainService service;

    /**
     * Constructeur pour l'injection du service IndisponibiliteTerrainService.
     *
     * @param service le service gérant la logique métier des indisponibilités
     */
    public IndisponibiliteTerrainController(IndisponibiliteTerrainService service) {
        this.service = service;
    }

    /**
     * Lister toutes les indisponibilités enregistrées.
     *
     * @return une liste de toutes les {@link IndisponibiliteTerrain}
     */
    @Operation(summary = "Lister toutes les indisponibilités", description = "Retourne la liste complète des indisponibilités de tous les terrains.")
    @GetMapping
    public List<IndisponibiliteTerrain> findAll() {
        return service.findAll();
    }

    /**
     * Rechercher les indisponibilités liées à un terrain spécifique.
     *
     * @param idTerrain l'identifiant unique du terrain
     * @return la liste des {@link IndisponibiliteTerrain} correspondant au terrain donné
     */
    @Operation(summary = "Lister les indisponibilités d'un terrain spécifique", description = "Filtre les indisponibilités pour un ID de terrain donné.")
    @GetMapping("/terrain/{idTerrain}")
    public List<IndisponibiliteTerrain> findByTerrainId(@PathVariable Long idTerrain) {
        return service.findByTerrainId(idTerrain);
    }

    /**
     * Créer une nouvelle plage d'indisponibilité pour un terrain.
     *
     * @param idTerrain l'identifiant du terrain à restreindre
     * @param indispo   les détails de l'indisponibilité (date, motif, etc.)
     * @return l'objet {@link IndisponibiliteTerrain} créé et persisté
     */
    @Operation(summary = "Ajouter une indisponibilité", description = "Crée une plage d'indisponibilité pour un terrain.")
    @PostMapping
    public IndisponibiliteTerrain create(
            @Parameter(description = "ID du terrain concerné", required = true)
            @RequestParam Long idTerrain,
            @RequestBody IndisponibiliteTerrain indispo) {
        return service.save(idTerrain, indispo);
    }

    /**
     * Supprimer une indisponibilité existante.
     *
     * @param id l'identifiant de l'indisponibilité à supprimer
     */
    @Operation(summary = "Supprimer une indisponibilité", description = "Supprime une indisponibilité par son ID.")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}