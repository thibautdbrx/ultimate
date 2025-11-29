package org.ultimateam.apiultimate.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ultimateam.apiultimate.service.ClassementService;

@Tag(name = "Classement", description = "Endpoints pour gérer le classement")
@RestController
@RequestMapping("/api/classement")
public class ClassementController {

    private final ClassementService classementService;
    public ClassementController(ClassementService classementService) {
        this.classementService = classementService;
    }
    @GetMapping
    public void getClassement(){
        classementService.afficherClassement();
    }
}
