package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.IndisponibiliteTerrain;
import org.ultimateam.apiultimate.service.IndisponibiliteTerrainService;

import java.util.List;

/**
 * Contrôleur REST responsable de la gestion des indisponibilités des terrains.
 */
@RestController
@RequestMapping("/api/indisponibilites-terrain")
@RequiredArgsConstructor
@Tag(name = "Indisponibilité Terrain", description = "Endpoints pour gérer les indisponibilités des terrains")
public class IndisponibiliteTerrainController {

    private final IndisponibiliteTerrainService service;

    /**
     * Lister toutes les indisponibilités enregistrées.
     *
     * @return une liste de toutes les {@link IndisponibiliteTerrain}.
     */
    @Operation(summary = "Lister toutes les indisponibilités", description = "Retourne la liste complète des indisponibilités de tous les terrains.")
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<IndisponibiliteTerrain> findAll() {
        return service.findAll();
    }

    /**
     * Rechercher les indisponibilités liées à un terrain spécifique.
     *
     * @param idTerrain l'identifiant unique du terrain.
     * @return la liste des {@link IndisponibiliteTerrain} correspondant au terrain donné.
     */
    @Operation(summary = "Lister les indisponibilités d'un terrain", description = "Filtre les indisponibilités pour un ID de terrain donné.")
    @GetMapping("/terrain/{idTerrain}")
    @PreAuthorize("permitAll()")
    public List<IndisponibiliteTerrain> findByTerrainId(@PathVariable Long idTerrain) {
        return service.findByTerrainId(idTerrain);
    }

    /**
     * Créer une nouvelle plage d'indisponibilité pour un terrain.
     * Réservé aux administrateurs.
     *
     * @param idTerrain l'identifiant du terrain à restreindre.
     * @param indispo   les détails de l'indisponibilité (date, motif, etc.).
     * @return l'objet {@link IndisponibiliteTerrain} créé.
     */
    @Operation(summary = "Ajouter une indisponibilité", description = "Crée une plage d'indisponibilité pour un terrain. Réservé aux administrateurs.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public IndisponibiliteTerrain create(
            @Parameter(description = "ID du terrain concerné", required = true)
            @RequestParam Long idTerrain,
            @RequestBody IndisponibiliteTerrain indispo) {
        return service.save(idTerrain, indispo);
    }

    /**
     * Supprimer une indisponibilité existante.
     * Réservé aux administrateurs.
     *
     * @param id l'identifiant de l'indisponibilité à supprimer.
     */
    @Operation(summary = "Supprimer une indisponibilité", description = "Supprime une indisponibilité par son ID. Réservé aux administrateurs.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}