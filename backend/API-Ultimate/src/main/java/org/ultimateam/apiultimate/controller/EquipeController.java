package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
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
    private final EquipeRepository equipeRepository;

    public EquipeController(EquipeService equipeService, EquipeRepository equipeRepository) { this.equipeService = equipeService;
        this.equipeRepository = equipeRepository;
    }

    @Operation(
            summary = "Lister toutes les équipes",
            description = "Retourne la liste complète de toutes les équipes enregistrées en base de données."
    )
    @GetMapping
    public List<Equipe> findAll() { return equipeService.findAll(); }

    @Operation(
            summary = "Récupérer une équipe par son identifiant",
            description = "Retourne l'équipe correspondant à l'identifiant fourni. Une erreur est renvoyée si aucune équipe ne correspond à cet identifiant."
    )
    @GetMapping("/{idEquipe}")
    public Equipe getById(@PathVariable long idEquipe) { return equipeService.getById(idEquipe); }

    @Operation(
            summary = "Lister les indisponibilités d'une équipe",
            description = "Retourne la liste des indisponibilités associées à l'équipe identifiée par son id. Une erreur est renvoyée si l'équipe n'existe pas."
    )
    @GetMapping("/{idEquipe}/indisponibilite")
    public List<Indisponibilite> getIndisponibilites(@PathVariable long idEquipe) {
        return equipeService.getIndisponibilites(idEquipe);
    }

    @Operation(
            summary = "Lister les équipes par genre",
            description = "Retourne la liste des équipes correspondant au genre spécifié (ex: HOMME, FEMME, MIXTE). Une erreur est renvoyée si le genre est invalide."
    )
    @GetMapping("/genre")
    public List<Equipe> getEquipeGenre(
           Genre genre
    ) {
        return equipeService.getEquipeGenre(genre);
    }

    @Operation(
            summary = "Obtenir le nombre de joueurs d'une équipe",
            description = "Retourne le nombre total de joueurs appartenant à l'équipe identifiée par son id. Une erreur est renvoyée si l'équipe n'existe pas."
    )
    @GetMapping("/{idEquipe}/nbjoueurs")
    public int getNbJoueurs(@PathVariable long idEquipe) { return equipeService.getNbJoueurs(idEquipe); }

    @Operation(
            summary = "Créer une nouvelle équipe",
            description = "Crée une nouvelle équipe à partir des informations fournies dans le corps de la requête (nom, description, genre)."
    )
    @PostMapping
    public Equipe createEquipe(@RequestBody Equipe equipe) { return equipeService.save(equipe); }

    @Operation(
            summary = "Modifier le nom et/ou la description d'une équipe",
            description = "Permet de modifier le nom et/ou la description d'une équipe existante. Il n'est pas obligatoire de fournir les deux champs."
    )
    @PatchMapping("/{idEquipe}/name")
    public Equipe editNomEquipe(@RequestBody EquipeNameDTO equipedto, @PathVariable long idEquipe) { return equipeService.editName(equipedto, idEquipe); }

    @Operation(
            summary = "Mettre à jour le genre de toutes les équipes",
            description = "Met à jour automatiquement le genre de toutes les équipes existantes selon les règles définies dans le service."
    )
    @PatchMapping("/updategenre")
    public void updateGenre(){
        equipeService.updateAllGenre(equipeService.findAll());
    }

    @Operation(
            summary = "Supprimer une équipe",
            description = "Supprime l'équipe correspondant à l'identifiant fourni. Une erreur est renvoyée si l'équipe n'existe pas."
    )
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) { equipeService.deleteById(id); }

    @GetMapping("/test/{nbHomme}/{nbFemme}")
    public List<Equipe> test(@PathVariable int nbHomme, @PathVariable int nbFemme){return equipeRepository.findEquipesAvecHommesEtFemmes(nbHomme,nbFemme);}

    @GetMapping("/open")
    public List<Equipe> openEquipe() {return equipeService.getNotFull();}

}
