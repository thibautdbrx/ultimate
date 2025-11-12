package org.ultimateam.apiultimate.controller;

import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.service.EquipeService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des entités Equipe.
 * Expose les points de terminaison (endpoints) de l'API pour les opérations CRUD sur les équipes.
 */
@RestController
@RequestMapping("/api/equipe")
public class EquipeController {

    private EquipeService equipeService;

    /**
     * Constructeur pour l'injection de la dépendance EquipeService.
     *
     * @param equipeService Le service chargé de la logique métier pour les équipes.
     */
    public EquipeController(EquipeService equipeService) { this.equipeService = equipeService; }

    /**
     * Récupère la liste de toutes les équipes.
     * Mappe les requêtes HTTP GET sur /api/equipe.
     *
     * @return Une liste contenant toutes les équipes.
     */
    @GetMapping
    public List<Equipe> findAll() { return (List<Equipe>) equipeService.findAll(); }

    /**
     * Récupère une équipe spécifique par son identifiant.
     * Mappe les requêtes HTTP GET sur /api/equipe/{id}.
     *
     * @param id L'identifiant de l'équipe à récupérer (provient du chemin de l'URL).
     * @return L'équipe correspondant à l'ID, ou null si non trouvée.
     */
    @GetMapping("/{id}")
    public Equipe findById(@PathVariable Long id) { return equipeService.getById(id); }

    @GetMapping("/{id}/indisponibilite")
    public List<Indisponibilite> getIndisponibilites(@PathVariable Long id) {
        return equipeService.getIndisponibilites(id);
    }

    @PostMapping
    public Equipe createEquipe(@RequestBody Equipe equipe) { return equipeService.save(equipe); }

    /**
     * Supprime une équipe par son identifiant.
     * Mappe les requêtes HTTP DELETE sur /api/equipe/{id}.
     *
     * @param id L'identifiant de l'équipe à supprimer (provient du chemin de l'URL).
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) { equipeService.deleteById(id); }

}
