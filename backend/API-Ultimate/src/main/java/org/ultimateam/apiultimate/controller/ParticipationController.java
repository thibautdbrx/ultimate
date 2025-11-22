package org.ultimateam.apiultimate.controller;


import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Participation;
import org.ultimateam.apiultimate.service.ParticipationService;

import java.util.List;

@RestController
@RequestMapping("/api/participation")
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
    public List<Participation> getParticipationByCompetitionId(@PathVariable Long idCompetition) {
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
