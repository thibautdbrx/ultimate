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

    @GetMapping("{id}")
    public Participation getParticipationById(@PathVariable Long id) {
        return participationService.getParticipationById(id);
    }

    @PostMapping
    public Participation createParticipation(@RequestBody Participation participation) {
        return participationService.save(participation);
    }

    @PostMapping("{id}")
    public void deleteParticipation(@PathVariable Long id) {participationService.deleteById(id);}
}
