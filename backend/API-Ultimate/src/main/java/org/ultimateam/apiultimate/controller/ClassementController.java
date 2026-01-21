package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Classement;
import org.ultimateam.apiultimate.service.ClassementService;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des classements.
 *
 * Ce contrôleur expose des endpoints pour lister et supprimer les classements,
 * notamment le classement trié d'une compétition spécifique.
 */
@RestController
@Tag(name = "Classement", description = "Endpoints pour gérer le classement")
@RequestMapping("/api/classement")
@Tag(name = "Classement", description = "Endpoints pour gérer les classements")
public class ClassementController {

    /** Service utilisé pour gérer les opérations liées aux classements. */
    private final ClassementService classementService;

    /**
     * Constructeur du contrôleur.
     *
     * @param classementService Service injecté pour gérer les classements.
     */
    public ClassementController(ClassementService classementService) {
        this.classementService = classementService;
    }

    /**
     * Récupère le classement trié pour une compétition spécifique.
     *
     * Le classement est trié selon les règles suivantes :
     * - Par nombre de points (descendant)
     * - En cas d'égalité, par différence de buts (descendant)
     * - Puis par nombre de buts marqués (descendant)
     *
     * @param idCompetition Identifiant unique de la compétition.
     * @return Une liste de {@link Classement} triée pour la compétition.
     */
    @GetMapping("/competition/{idCompetition}")
    @Operation(
            summary = "Lister le classement d'une compétition",
            description = "Retourne le classement trié pour la compétition identifiée par son id."
    )
    @Parameter(
            name = "idCompetition",
            description = "Identifiant unique de la compétition.",
            required = true
    )
    public List<Classement> getClassementByCompetition(@PathVariable Long idCompetition) {
        return classementService.triClassement(idCompetition);
    }

    /**
     * Récupère la liste complète de tous les classements enregistrés.
     *
     * @return Un objet {@link Iterable} contenant tous les {@link Classement}.
     */
    @GetMapping
    @Operation(
            summary = "Lister tous les classements",
            description = "Retourne la liste complète de tous les classements."
    )
    public Iterable<Classement> getAllClassement() {
        return classementService.getAll();
    }

    /**
     * Supprime tous les classements associés à une compétition spécifique.
     *
     * @param idCompetition Identifiant unique de la compétition dont les classements doivent être supprimés.
     */
    @DeleteMapping("/competition/{idCompetition}")
    public void deleteClassementCompetition(@PathVariable Long idCompetition) {
        classementService.deleteByIdCompetition(idCompetition);
    }

}
