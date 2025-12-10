package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.EquipeNameDTO;
import org.ultimateam.apiultimate.DTO.Genre;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.repository.EquipeRepository;
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

    private final EquipeService equipeService;

    public EquipeController(EquipeService equipeService) { this.equipeService = equipeService;
    }

    @Operation(
            summary = "Lister toutes les équipes",
            description = "Retourne la liste complète des équipes enregistrés dans la base de données.")
    @GetMapping
    public List<Equipe> findAll() { return equipeService.findAll(); }

    @Operation(
            summary = "Récupère une équipe en fonction de son id",
            description = "Retourne l'équipe associé à l'id" +
                    "Renvoie une erreur si l'équipe n'existe pas")
    @GetMapping("/{idEquipe}")
    public Equipe getById(@PathVariable long idEquipe) { return equipeService.getById(idEquipe); }

    @Operation(
            summary = "Récupère les indisponibilités d'une équipe en fonction de son id | ",
            description = "Retourne les indisponibilités de l'équipe associé à l'id" +
                    "Renvoie une erreur si l'équipe n'existe pas")
    @GetMapping("/{idEquipe}/indisponibilite")
    public List<Indisponibilite> getIndisponibilites(@PathVariable long idEquipe) {
        return equipeService.getIndisponibilites(idEquipe);
    }

    @Operation(
            summary = "Récupère une liste d'équipes qui possède un même genre",
            description = "Retourne une liste d'équipe qui possède un certain genre | " +
                    "Renvoie une erreur si le genre n'existe pas")
    @GetMapping("/genre")
    public List<Equipe> getEquipeGenre(
           Genre genre
    ) {
        return equipeService.getEquipeGenre(genre);
    }

    @Operation(
            summary = "Renvoie le nombre de joueurs dans une équipe",
            description = "Retourne un entier correspondant au nombre de joueurs dans l'équipe" +
                    "Renvoie une erreur si l'équipe n'existe pas")
    @GetMapping("/{idEquipe}/nbjoueurs")
    public int getNbJoueurs(@PathVariable long idEquipe) { return equipeService.getNbJoueurs(idEquipe); }

    @Operation(
            summary = "Créer une équipe",
            description = "Envoyer un post avec le nom de l'équipe, description et genre")
    @PostMapping
    public Equipe createEquipe(@RequestBody Equipe equipe) { return equipeService.save(equipe); }

    @Operation(
            summary = "Modifie le nom et/ou la description de l'équipe",
            description = "Dans le json, remplir nom pour changer celui-ci et description pour changer celle-ci (pas obligatoire de remplir les deux)")
    @PatchMapping("/{idEquipe}/name")
    public Equipe editNomEquipe(@RequestBody EquipeNameDTO equipedto, @PathVariable long idEquipe) { return equipeService.editName(equipedto, idEquipe); }

    @Operation(
            summary = "Mettre à jour les genre des équipes",
            description = "Ne ")
    @PatchMapping("/updategenre")
    public void updateGenre(){
        equipeService.updateAllGenre(equipeService.findAll());
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) { equipeService.deleteById(id); }

}
