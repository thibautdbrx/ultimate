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
@RestController
@Tag(name = "Classement", description = "Endpoints pour gérer le classement")
@RequestMapping("/api/classement")
@Tag(name = "Classement", description = "Endpoints pour gérer les classements")
public class ClassementControllerTest {

    private ClassementService classementService;

    public ClassementControllerTest(ClassementService classementService) {
        this.classementService = classementService;
    }

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

    @GetMapping
    @Operation(
            summary = "Lister tous les classements",
            description = "Retourne la liste complète de tous les classements."
    )
    public Iterable<Classement> getAllClassement() {
        return classementService.getAll();
    }

    @DeleteMapping("/competition/{idCompetition}")
    public void deleteClassementCompetition(@PathVariable Long idCompetition) {
        classementService.deleteByIdCompetition(idCompetition);
    }

}
