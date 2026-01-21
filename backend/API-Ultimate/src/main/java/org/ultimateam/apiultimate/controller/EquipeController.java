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
 * Contrôleur REST pour la gestion des entités {@link Equipe}.
 *
 * Ce contrôleur expose les endpoints de l'API pour les opérations CRUD sur les équipes,
 * ainsi que des fonctionnalités spécifiques comme la gestion des indisponibilités,
 * la récupération du nombre de joueurs, et la mise à jour du genre des équipes.
 */
@RestController
@Tag(name = "Equipe", description = "Endpoints pour gérer les équipes")
@RequestMapping("/api/equipe")
public class EquipeController {

    /** Service utilisé pour gérer les opérations liées aux équipes. */
    private final EquipeService equipeService;

    /** Repository utilisé pour accéder directement aux données des équipes. */
    private final EquipeRepository equipeRepository;

    /**
     * Constructeur du contrôleur.
     *
     * @param equipeService Service injecté pour gérer les équipes.
     * @param equipeRepository Repository injecté pour accéder aux données des équipes.
     */
    public EquipeController(EquipeService equipeService, EquipeRepository equipeRepository) { this.equipeService = equipeService;
        this.equipeRepository = equipeRepository;
    }

    /**
     * Récupère la liste complète de toutes les équipes enregistrées.
     *
     * @return Une liste de toutes les {@link Equipe}.
     */
    @Operation(
            summary = "Lister toutes les équipes",
            description = "Retourne la liste complète de toutes les équipes enregistrées en base de données."
    )
    @GetMapping
    public List<Equipe> findAll() { return equipeService.findAll(); }

    /**
     * Récupère une équipe par son identifiant.
     *
     * @param idEquipe Identifiant unique de l'équipe.
     * @return L'{@link Equipe} correspondant à l'identifiant fourni.
     * @throws RuntimeException Si aucune équipe ne correspond à cet identifiant.
     */
    @Operation(
            summary = "Récupérer une équipe par son identifiant",
            description = "Retourne l'équipe correspondant à l'identifiant fourni. Une erreur est renvoyée si aucune équipe ne correspond à cet identifiant."
    )
    @GetMapping("/{idEquipe}")
    public Equipe getById(@PathVariable long idEquipe) { return equipeService.getById(idEquipe); }

    /**
     * Récupère la liste des indisponibilités associées à une équipe.
     *
     * @param idEquipe Identifiant unique de l'équipe.
     * @return Une liste des {@link Indisponibilite} de l'équipe.
     * @throws RuntimeException Si l'équipe n'existe pas.
     */
    @Operation(
            summary = "Lister les indisponibilités d'une équipe",
            description = "Retourne la liste des indisponibilités associées à l'équipe identifiée par son id. Une erreur est renvoyée si l'équipe n'existe pas."
    )
    @GetMapping("/{idEquipe}/indisponibilite")
    public List<Indisponibilite> getIndisponibilites(@PathVariable long idEquipe) {
        return equipeService.getIndisponibilites(idEquipe);
    }

    /**
     * Récupère la liste des équipes correspondant à un genre spécifique.
     *
     * @param genre Genre des équipes à filtrer (ex: HOMME, FEMME, MIXTE).
     * @return Une liste des {@link Equipe} correspondant au genre spécifié.
     * @throws RuntimeException Si le genre est invalide.
     */
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

    /**
     * Récupère le nombre total de joueurs appartenant à une équipe.
     *
     * @param idEquipe Identifiant unique de l'équipe.
     * @return Le nombre total de joueurs dans l'équipe.
     * @throws RuntimeException Si l'équipe n'existe pas.
     */
    @Operation(
            summary = "Obtenir le nombre de joueurs d'une équipe",
            description = "Retourne le nombre total de joueurs appartenant à l'équipe identifiée par son id. Une erreur est renvoyée si l'équipe n'existe pas."
    )
    @GetMapping("/{idEquipe}/nbjoueurs")
    public int getNbJoueurs(@PathVariable long idEquipe) { return equipeService.getNbJoueurs(idEquipe); }

    /**
     * Crée une nouvelle équipe à partir des informations fournies.
     *
     * @param equipe Objet {@link Equipe} contenant les informations de l'équipe à créer (nom, description, genre).
     * @return L'{@link Equipe} nouvellement créée.
     */
    @Operation(
            summary = "Créer une nouvelle équipe",
            description = "Crée une nouvelle équipe à partir des informations fournies dans le corps de la requête (nom, description, genre)."
    )
    @PostMapping
    public Equipe createEquipe(@RequestBody Equipe equipe) { return equipeService.save(equipe); }

    /**
     * Met à jour le nom et/ou la description d'une équipe existante.
     *
     * @param equipedto Objet {@link EquipeNameDTO} contenant le nouveau nom et/ou la nouvelle description.
     * @param idEquipe Identifiant unique de l'équipe à mettre à jour.
     * @return L'{@link Equipe} mise à jour.
     */
    @Operation(
            summary = "Modifier le nom et/ou la description d'une équipe",
            description = "Permet de modifier le nom et/ou la description d'une équipe existante. Il n'est pas obligatoire de fournir les deux champs."
    )
    @PatchMapping("/{idEquipe}/name")
    public Equipe editNomEquipe(@RequestBody EquipeNameDTO equipedto, @PathVariable long idEquipe) { return equipeService.editName(equipedto, idEquipe); }

    /**
     * Met à jour automatiquement le genre de toutes les équipes existantes.
     *
     * Cette méthode applique les règles définies dans le service pour recalculer
     * le genre de chaque équipe en fonction de la composition actuelle des joueurs.
     */
    @Operation(
            summary = "Mettre à jour le genre de toutes les équipes",
            description = "Met à jour automatiquement le genre de toutes les équipes existantes selon les règles définies dans le service."
    )
    @PatchMapping("/updategenre")
    public void updateGenre(){
        equipeService.updateAllGenre(equipeService.findAll());
    }

    /**
     * Supprime une équipe par son identifiant.
     *
     * @param id Identifiant unique de l'équipe à supprimer.
     * @throws RuntimeException Si l'équipe n'existe pas.
     */
    @Operation(
            summary = "Supprimer une équipe",
            description = "Supprime l'équipe correspondant à l'identifiant fourni. Une erreur est renvoyée si l'équipe n'existe pas."
    )
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) { equipeService.deleteById(id); }

    /**
     * Méthode de test pour récupérer les équipes ayant un nombre spécifique de joueurs hommes et femmes.
     *
     * @param nbHomme Nombre minimum de joueurs hommes requis.
     * @param nbFemme Nombre minimum de joueuses femmes requises.
     * @return Une liste d'{@link Equipe} correspondant aux critères.
     */
    @GetMapping("/test/{nbHomme}/{nbFemme}")
    public List<Equipe> test(@PathVariable int nbHomme, @PathVariable int nbFemme){return equipeRepository.findEquipesAvecHommesEtFemmes(nbHomme,nbFemme);}

}
