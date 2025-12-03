package org.ultimateam.apiultimate.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Equipe;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.service.ParticipationService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/participation")
@Tag(name = "Participation", description = "Endpoints pour gérer les participations")
public class ParticipationController {

    private final ParticipationService participationService;


    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    @GetMapping
    public List<Participation> getAllParticipation() {
        return participationService.getAll();
    }

    @GetMapping("/competition/{idCompetition}")
    public List<Equipe> getParticipationByCompetitionId(@PathVariable Long idCompetition) {
        return participationService.getParticipationByCompetitionId(idCompetition);
    }

    @GetMapping("/equipe/{idEquipe}")
    public List<Participation> getParticipationByEquipeId(@PathVariable Long idEquipe) {
        return participationService.getParticipationByEquipeId(idEquipe);
    }

    @PostMapping("/equipe/{idEquipe}/competition/{idCompetition}")
    public Participation createParticipation(@PathVariable Long idEquipe, @PathVariable Long idCompetition) {
        return participationService.addParticipation(idEquipe, idCompetition);
    }

    @DeleteMapping("/{id}")
    public void deleteParticipation(@PathVariable Long id) {participationService.deleteById(id);}


}
