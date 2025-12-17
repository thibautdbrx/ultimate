package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Classement;
import org.ultimateam.apiultimate.service.ClassementService;

import java.util.List;
@RestController
@Tag(name = "Classement", description = "Endpoints pour gérer le classement")
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

    @DeleteMapping("/competition/{idCompetition}")
    public void deleteClassementCompetition(@PathVariable Long idCompetition) {
        classementService.deleteByIdCompetition(idCompetition);
    }

}
