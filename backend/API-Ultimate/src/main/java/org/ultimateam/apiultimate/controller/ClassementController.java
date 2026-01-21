package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Classement;
import org.ultimateam.apiultimate.service.ClassementService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des classements.
 */
@RestController
@RequestMapping("/api/classement")
@RequiredArgsConstructor
@Tag(name = "Classement", description = "Endpoints pour gérer les classements")
public class ClassementController {

    private final ClassementService classementService;

    /**
     * Récupère le classement trié pour une compétition spécifique.
     *
     * @param idCompetition Identifiant unique de la compétition.
     * @return Une liste de {@link Classement} triée pour la compétition.
     */
    @Operation(summary = "Lister le classement d'une compétition", description = "Retourne le classement trié (Points > Différence de buts > Buts marqués) pour une compétition donnée.")
    @GetMapping("/competition/{idCompetition}")
    @PreAuthorize("permitAll()")
    public List<Classement> getClassementByCompetition(@PathVariable Long idCompetition) {
        return classementService.triClassement(idCompetition);
    }

    /**
     * Récupère la liste complète de tous les classements enregistrés.
     *
     * @return Un objet {@link Iterable} contenant tous les {@link Classement}.
     */
    @Operation(summary = "Lister tous les classements", description = "Retourne la liste complète de tous les classements existants.")
    @GetMapping
    @PreAuthorize("permitAll()")
    public Iterable<Classement> getAllClassement() {
        return classementService.getAll();
    }

    /**
     * Supprime tous les classements associés à une compétition spécifique.
     * Réservé aux administrateurs.
     *
     * @param idCompetition Identifiant unique de la compétition.
     */
    @Operation(summary = "Supprimer le classement d'une compétition", description = "Supprime tous les classements associés à une compétition. Réservé aux administrateurs.")
    @DeleteMapping("/competition/{idCompetition}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteClassementCompetition(@PathVariable Long idCompetition) {
        classementService.deleteByIdCompetition(idCompetition);
    }
}