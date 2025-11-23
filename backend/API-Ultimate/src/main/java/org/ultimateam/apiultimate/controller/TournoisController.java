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
@RequestMapping("/api/tournois")
public class TournoisController {


    private final TournoisService tournoisService;

    @Autowired
    public TournoisController(TournoisService tournoisService) {
        this.tournoisService = tournoisService;
    }

    @GetMapping
    public List<Tournois> findAll() { return (List<Tournois>) tournoisService.getAllTournois(); }

    @GetMapping("/{id}")
    public Tournois findById(@PathVariable Long id) { return tournoisService.getTournoisById(id); }

    @PostMapping
    public Tournois creerTournois(@RequestBody Tournois tournois) {return tournoisService.saveTournois(tournois); }

    @PutMapping("/{idTournoi}/create")
    public List<Equipe> genererMatchs(@PathVariable Long idTournoi) { return tournoisService.genererRoundRobin(idTournoi);}

    @PostMapping("/tournoi")
    public Tournois createTournois(@RequestBody Tournois Tournois) { return tournoisService.saveTournois(Tournois); }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) { tournoisService.deleteTournoisById(id); }

    /**
    @PostMapping("/Creation_tournois")
    public void genererTournois(Long idTournois){ tournoisService.genererTournois(idTournois);}

    */


}
