package org.ultimateam.apiultimate.controller;

import org.springframework.web.bind.annotation.*;
import org.ultimateam.apiultimate.model.Tournois;
import org.ultimateam.apiultimate.service.TournoisService;

import java.util.List;

@RestController
@RequestMapping("/api/Tournois")
public class TournoisController {

    private TournoisService tournoisService;

    public TournoisController(TournoisController TournoisService) { this.tournoisService = tournoisService; }

    @GetMapping
    public List<Tournois> findAll() { return (List<Tournois>) tournoisService.getAllTournois(); }

    @GetMapping("/{id}")
    public Tournois findById(@PathVariable Long id) { return tournoisService.getTournoisById(id); }

    @PostMapping
    public Tournois createTournois(@RequestBody Tournois Tournois) { return tournoisService.saveTournois(Tournois); }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) { tournoisService.deleteTournoisById(id); }

    @PostMapping("/Creation_tournois")
    public void genererTournois(Long idTournois){ tournoisService.genererTournois(idTournois);}



}
