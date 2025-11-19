package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.EquipeNameDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.service.EquipeService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des entités Equipe.
 * Expose les points de terminaison (endpoints) de l'API pour les opérations CRUD sur les équipes.
 */
@RestController
@Tag(name = "Equipe", description = "Endpoints pour gérer les équipes")
@RequestMapping("/api/equipe")
public class EquipeController {

    private EquipeService equipeService;

    public EquipeController(EquipeService equipeService) { this.equipeService = equipeService; }

    @GetMapping
    public List<Equipe> findAll() { return (List<Equipe>) equipeService.findAll(); }

    @GetMapping("/{id}")
    public Equipe getById(@PathVariable long idEquipe) { return equipeService.getById(idEquipe); }

    @GetMapping("/{idEquipe}/indisponibilite")
    public List<Indisponibilite> getIndisponibilites(@PathVariable long idEquipe) {
        return equipeService.getIndisponibilites(idEquipe);
    }

    @PostMapping
    public Equipe createEquipe(@RequestBody Equipe equipe) { return equipeService.save(equipe); }

    @PatchMapping("/{idEquipe}/name")
    public Equipe editNomEquipe(@RequestBody EquipeNameDTO equipedto, @PathVariable long idEquipe) { return equipeService.editName(equipedto, idEquipe); }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) { equipeService.deleteById(id); }

}
