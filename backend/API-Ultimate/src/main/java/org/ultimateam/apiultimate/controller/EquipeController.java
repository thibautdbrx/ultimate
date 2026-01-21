package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.EquipeNameDTO;
import org.ultimateam.apiultimate.DTO.Genre;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Indisponibilite;
import org.ultimateam.apiultimate.service.EquipeService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des entités {@link Equipe}.
 */
@RestController
@RequestMapping("/api/equipe")
@RequiredArgsConstructor
@Tag(name = "Equipe", description = "Endpoints pour gérer les équipes (Lecture publique, Modification Admin)")
public class EquipeController {

    private final EquipeService equipeService;

    /**
     * Récupère la liste complète de toutes les équipes enregistrées.
     *
     * @return Une liste de toutes les {@link Equipe}.
     */
    @Operation(summary = "Lister toutes les équipes", description = "Retourne la liste complète de toutes les équipes enregistrées en base de données.")
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Equipe> findAll() {
        return equipeService.findAll();
    }

    /**
     * Récupère une équipe par son identifiant.
     *
     * @param idEquipe Identifiant unique de l'équipe.
     * @return L'{@link Equipe} correspondant à l'identifiant fourni.
     */
    @Operation(summary = "Récupérer une équipe par ID", description = "Retourne l'équipe correspondant à l'identifiant fourni.")
    @GetMapping("/{idEquipe}")
    @PreAuthorize("permitAll()")
    public Equipe getById(@PathVariable long idEquipe) {
        return equipeService.getById(idEquipe);
    }

    /**
     * Récupère la liste des indisponibilités associées à une équipe.
     *
     * @param idEquipe Identifiant unique de l'équipe.
     * @return Une liste des {@link Indisponibilite} de l'équipe.
     */
    @Operation(summary = "Lister les indisponibilités", description = "Retourne la liste des indisponibilités associées à l'équipe.")
    @GetMapping("/{idEquipe}/indisponibilite")
    @PreAuthorize("permitAll()")
    public List<Indisponibilite> getIndisponibilites(@PathVariable long idEquipe) {
        return equipeService.getIndisponibilites(idEquipe);
    }

    /**
     * Récupère la liste des équipes correspondant à un genre spécifique.
     *
     * @param genre Genre des équipes à filtrer.
     * @return Une liste des {@link Equipe} correspondant au genre spécifié.
     */
    @Operation(summary = "Lister les équipes par genre", description = "Retourne la liste des équipes correspondant au genre spécifié (HOMME, FEMME, MIXTE).")
    @GetMapping("/genre")
    @PreAuthorize("permitAll()")
    public List<Equipe> getEquipeGenre(@RequestParam Genre genre) {
        return equipeService.getEquipeGenre(genre);
    }

    /**
     * Récupère le nombre total de joueurs appartenant à une équipe.
     *
     * @param idEquipe Identifiant unique de l'équipe.
     * @return Le nombre total de joueurs dans l'équipe.
     */
    @Operation(summary = "Nombre de joueurs", description = "Retourne le nombre total de joueurs appartenant à l'équipe.")
    @GetMapping("/{idEquipe}/nbjoueurs")
    @PreAuthorize("permitAll()")
    public int getNbJoueurs(@PathVariable long idEquipe) {
        return equipeService.getNbJoueurs(idEquipe);
    }

    /**
     * Crée une nouvelle équipe.
     * Accessible uniquement par l'ADMIN.
     *
     * @param equipe Objet {@link Equipe} à créer.
     * @return L'{@link Equipe} créée.
     */
    @Operation(summary = "Créer une équipe", description = "Crée une nouvelle équipe. Réservé aux administrateurs.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Equipe createEquipe(@RequestBody Equipe equipe) {
        return equipeService.save(equipe);
    }

    /**
     * Met à jour le nom et/ou la description d'une équipe.
     * Accessible uniquement par l'ADMIN.
     *
     * @param equipedto DTO contenant les modifications.
     * @param idEquipe Identifiant de l'équipe.
     * @return L'{@link Equipe} mise à jour.
     */
    @Operation(summary = "Modifier le nom/description", description = "Modifie le nom ou la description d'une équipe. Réservé aux administrateurs.")
    @PatchMapping("/{idEquipe}/name")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Equipe editNomEquipe(@RequestBody EquipeNameDTO equipedto, @PathVariable long idEquipe) {
        return equipeService.editName(equipedto, idEquipe);
    }

    /**
     * Supprime une équipe.
     * Accessible uniquement par l'ADMIN.
     *
     * @param id Identifiant de l'équipe à supprimer.
     */
    @Operation(summary = "Supprimer une équipe", description = "Supprime définitivement une équipe. Réservé aux administrateurs.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteById(@PathVariable long id) {
        equipeService.deleteById(id);
    }

    /**
     * Liste les équipes non complètes pour un joueur donné.
     *
     * @param idJoueur Identifiant du joueur cherchant une équipe.
     * @return Liste des équipes disponibles.
     */
    @Operation(summary = "Lister les équipes ouvertes", description = "Retourne les équipes qui ne sont pas encore complètes pour un joueur donné.")
    @GetMapping("/open")
    @PreAuthorize("permitAll()")
    public List<Equipe> openEquipe(@RequestParam Long idJoueur) {
        return equipeService.getNotFull(idJoueur);
    }
}