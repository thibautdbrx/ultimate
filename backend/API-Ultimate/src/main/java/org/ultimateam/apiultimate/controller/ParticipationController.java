package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.DTO.ListEquipeDTO;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.model.ParticipationId;
import org.ultimateam.apiultimate.service.ParticipationService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des participations aux compétitions.
 */
@RestController
@RequestMapping("/api/participation")
@RequiredArgsConstructor
@Tag(name = "Participation", description = "Endpoints pour gérer les participations aux compétitions")
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * Récupère la liste complète de toutes les participations enregistrées.
     *
     * @return Une liste de toutes les {@link Participation}.
     */
    @Operation(summary = "Lister toutes les participations", description = "Retourne la liste complète de toutes les participations enregistrées.")
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Participation> getAllParticipation() {
        return participationService.getAll();
    }

    /**
     * Récupère la liste des équipes participant à une compétition spécifique.
     *
     * @param idCompetition Identifiant unique de la compétition.
     * @return Une liste d'{@link Equipe}.
     */
    @Operation(summary = "Lister les participations par compétition", description = "Retourne la liste des équipes participant à la compétition identifiée.")
    @GetMapping("/competition/{idCompetition}")
    @PreAuthorize("permitAll()")
    public List<Equipe> getParticipationByCompetitionId(@PathVariable Long idCompetition) {
        return participationService.getParticipationByCompetitionId(idCompetition);
    }

    /**
     * Récupère la liste des participations associées à une équipe spécifique.
     *
     * @param idEquipe Identifiant unique de l'équipe.
     * @return Une liste de {@link Participation}.
     */
    @Operation(summary = "Lister les participations par équipe", description = "Retourne la liste des participations associées à une équipe donnée.")
    @GetMapping("/equipe/{idEquipe}")
    @PreAuthorize("permitAll()")
    public List<Participation> getParticipationByEquipeId(@PathVariable Long idEquipe) {
        return participationService.getParticipationByEquipeId(idEquipe);
    }

    /**
     * Ajoute une participation unique à une compétition.
     * Réservé aux administrateurs.
     *
     * @param participationId Identifiant composite (ID équipe + ID compétition).
     * @return La {@link Participation} créée.
     */
    @Operation(summary = "Ajouter une participation", description = "Inscrit une équipe à une compétition. Réservé aux administrateurs.")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Participation createParticipation(@RequestBody ParticipationId participationId) {
        return participationService.addParticipation(participationId);
    }

    /**
     * Ajoute plusieurs équipes à une compétition spécifique.
     * Réservé aux administrateurs.
     *
     * @param listEquipeDTO Objet contenant la liste des équipes à inscrire.
     * @return Une liste des participations créées.
     */
    @Operation(summary = "Ajouter plusieurs participations", description = "Inscrit une liste d'équipes à une compétition. Réservé aux administrateurs.")
    @PostMapping("/competition")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Participation> createParticipation(@RequestBody ListEquipeDTO listEquipeDTO) {
        return participationService.addListParticipation(listEquipeDTO);
    }

    /**
     * Supprime une participation existante.
     * Réservé aux administrateurs.
     *
     * @param participationId Identifiant composite de la participation à supprimer.
     * @return La liste mise à jour des participations.
     */
    @Operation(summary = "Supprimer une participation", description = "Désinscrit une équipe d'une compétition. Réservé aux administrateurs.")
    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Participation> deleteParticipation(@RequestBody ParticipationId participationId) {
        return participationService.deleteById(participationId);
    }
}