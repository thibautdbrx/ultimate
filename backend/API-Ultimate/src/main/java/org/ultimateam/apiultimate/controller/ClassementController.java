package org.ultimateam.apiultimate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Classement;
import org.ultimateam.apiultimate.model.ParticipationId;
import org.ultimateam.apiultimate.service.ClassementService;

import java.util.List;
@RestController
@RequestMapping("/api/classement")
public class ClassementController {

    private ClassementService classementService;

    public ClassementController(ClassementService classementService) {
        this.classementService = classementService;
    }

    @GetMapping("/competition/{idCompetition}")
    public List<Classement> getClassementByCompetition(@PathVariable Long idCompetition) {
        return classementService.triClassement(idCompetition);
    }

    @GetMapping
    public Iterable<Classement> getAllClassement() {
        return classementService.getAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/competition/{idCompetition}/equipe/{idEquipe}")
    public void deleteClassement(@PathVariable Long idCompetition, @PathVariable Long idEquipe) {
        classementService.deleteById(new ParticipationId(idCompetition, idEquipe));
    }
}
