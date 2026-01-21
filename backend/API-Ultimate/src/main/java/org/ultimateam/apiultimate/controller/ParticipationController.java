package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.ListEquipeDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.model.ParticipationId;
import org.ultimateam.apiultimate.service.ParticipationService;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des participations aux compétitions.
 *
 * Ce contrôleur expose des endpoints pour lister, ajouter et supprimer des participations.
 * Une participation représente l'association entre une équipe et une compétition.
 */
@RestController
@RequestMapping("/api/participation")
@Tag(name = "Participation", description = "Endpoints pour gérer les participations")
public class ParticipationController {

    /** Service utilisé pour gérer les opérations liées aux participations. */
    private final ParticipationService participationService;

    /**
     * Constructeur du contrôleur.
     *
     * @param participationService Service injecté pour gérer les participations.
     */
    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }
    /**
     * Récupère la liste complète de toutes les participations enregistrées.
     *
     * @return Une liste de toutes les {@link Participation}.
     */


    @GetMapping
    @Operation(
            summary = "Lister toutes les participations",
            description = "Retourne la liste complète de toutes les participations enregistrées."
    )
    public List<Participation> getAllParticipation() {
        return participationService.getAll();
    }

    /**
     * Récupère la liste des équipes participant à une compétition spécifique.
     *
     * @param idCompetition Identifiant unique de la compétition.
     * @return Une liste d'{@link Equipe} participant à la compétition.
     */
    @Operation(
            summary = "Lister les participations par compétition",
            description = "Retourne la liste des équipes participant à la compétition identifiée par son id."
    )
    @Parameter(
            name = "idCompetition",
            description = "Identifiant unique de la compétition.",
            required = true
    )
    @GetMapping("/competition/{idCompetition}")
    public List<Equipe> getParticipationByCompetitionId(@PathVariable Long idCompetition) {
        return participationService.getParticipationByCompetitionId(idCompetition);
    }
    /**
     * Récupère la liste des participations associées à une équipe spécifique.
     *
     * @param idEquipe Identifiant unique de l'équipe.
     * @return Une liste de {@link Participation} pour l'équipe donnée.
     */

    @Operation(
            summary = "Lister les participations par équipe",
            description = "Retourne la liste des participations associées à l'équipe identifiée par son id."
    )
    @Parameter(
            name = "idEquipe",
            description = "Identifiant unique de l'équipe.",
            required = true
    )
    @GetMapping("/equipe/{idEquipe}")
    public List<Participation> getParticipationByEquipeId(@PathVariable Long idEquipe) {
        return participationService.getParticipationByEquipeId(idEquipe);
    }

    /**
     * Ajoute une participation unique à une compétition.
     *
     * @param participationId Identifiant composite contenant l'id de l'équipe et l'id de la compétition.
     * @return La {@link Participation} nouvellement créée.
     */

    @Operation(
            summary = "Ajouter une participation",
            description = "Ajoute une participation unique à une compétition."
    )
    @PostMapping
    public Participation createParticipation(@RequestBody ParticipationId participationId) {
        return participationService.addParticipation(participationId);
    }

    /**
     * Ajoute plusieurs équipes à une compétition spécifique.
     *
     * @param idCompetition Identifiant unique de la compétition.
     * @param listEquipeDTO Objet contenant la liste des identifiants d'équipes à ajouter.
     * @return Une liste de {@link Participation} créées pour chaque équipe ajoutée.
     */
    @Operation(
            summary = "Ajouter plusieurs participations",
            description = "Ajoute une liste d'équipes à une compétition spécifique."
    )
    @Parameter(
            name = "idCompetition",
            description = "Identifiant unique de la compétition.",
            required = true
    )
    @PostMapping("/competition/{idCompetition}")
    public List<Participation> createParticipation(@RequestBody ListEquipeDTO listEquipeDTO) {
        return participationService.addListParticipation(listEquipeDTO);
    }

    /**
     * Supprime une participation existante.
     *
     * @param participationId Identifiant composite de la participation à supprimer.
     * @return La liste mise à jour des {@link Participation} après suppression.
     */
    @Operation(
            summary = "Supprimer une participation",
            description = "Supprime la participation correspondant aux informations fournies."
    )
    @DeleteMapping
    public List<Participation> deleteParticipation(@RequestBody ParticipationId participationId) {return participationService.deleteById(participationId);}


}
