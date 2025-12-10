package org.ultimateam.apiultimate.controller;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Match;
import org.ultimateam.apiultimate.model.Tournois;
import org.ultimateam.apiultimate.service.TournoisService;

import java.util.List;

@RestController
@RequestMapping("/api/competition")
public class CompetitionController {

    private final CompetitionController competitionController;

    @Autowired
    public CompetitionController(CompetitionController competitionController) {
        this.competitionController = competitionController;
    }


}
